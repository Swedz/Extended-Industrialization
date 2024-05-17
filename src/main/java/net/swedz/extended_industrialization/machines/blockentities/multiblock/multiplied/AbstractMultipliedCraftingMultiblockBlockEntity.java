package net.swedz.extended_industrialization.machines.blockentities.multiblock.multiplied;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.machine.component.CrafterAccess;
import aztech.modern_industrialization.api.machine.holder.CrafterComponentHolder;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.ReiSlotLocking;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.util.TextHelper;
import net.minecraft.world.level.Level;
import net.swedz.extended_industrialization.machines.components.craft.ModularCrafterAccessBehavior;
import net.swedz.extended_industrialization.machines.components.craft.multiplied.MultipliedCrafterComponent;
import net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock.ModularMultiblockGui;
import net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock.ModularMultiblockGuiLine;
import net.swedz.extended_industrialization.machines.multiblock.BasicMultiblockMachineBlockEntity;
import net.swedz.extended_industrialization.text.EIText;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class AbstractMultipliedCraftingMultiblockBlockEntity extends BasicMultiblockMachineBlockEntity implements CrafterComponentHolder, ModularCrafterAccessBehavior
{
	protected final Supplier<MachineRecipeType> recipeTypeGetter;
	protected final Supplier<Integer>           maxMultiplierGetter;
	
	protected final MultipliedCrafterComponent crafter;
	
	public AbstractMultipliedCraftingMultiblockBlockEntity(BEP bep, String name, ShapeTemplate[] shapeTemplates,
														   Supplier<MachineRecipeType> recipeTypeGetter, Supplier<Integer> maxMultiplierGetter)
	{
		super(bep, new MachineGuiParameters.Builder(name, false).backgroundHeight(200).build(), shapeTemplates);
		
		this.recipeTypeGetter = recipeTypeGetter;
		this.maxMultiplierGetter = maxMultiplierGetter;
		
		this.crafter = new MultipliedCrafterComponent(
				this, inventory, this,
				this::getRecipeType, this::getMaxMultiplier
		);
		
		this.registerComponents(crafter);
		
		this.registerGuiComponent(new ReiSlotLocking.Server(crafter::lockRecipe, () -> operatingState != OperatingState.NOT_MATCHED));
		
		this.registerGuiComponent(new ModularMultiblockGui.Server(ModularMultiblockGui.H, () ->
		{
			List<ModularMultiblockGuiLine> text = Lists.newArrayList();
			
			boolean shapeValid = this.isShapeValid();
			boolean active = isActive.isActive;
			
			text.add(shapeValid ? new ModularMultiblockGuiLine(EIText.MULTIBLOCK_SHAPE_VALID.text()) : new ModularMultiblockGuiLine(EIText.MULTIBLOCK_SHAPE_INVALID.text(), 0xFF0000));
			if(shapeValid)
			{
				text.add(active ? new ModularMultiblockGuiLine(EIText.MULTIBLOCK_STATUS_ACTIVE.text()) : new ModularMultiblockGuiLine(EIText.MULTIBLOCK_STATUS_INACTIVE.text(), 0xFF0000));
				
				if(crafter.hasActiveRecipe())
				{
					text.add(new ModularMultiblockGuiLine(MIText.Progress.text(String.format("%.1f", crafter.getProgress() * 100) + " %")));
					
					if(crafter.getEfficiencyTicks() != 0 || crafter.getMaxEfficiencyTicks() != 0)
					{
						text.add(new ModularMultiblockGuiLine(MIText.EfficiencyTicks.text(crafter.getEfficiencyTicks(), crafter.getMaxEfficiencyTicks())));
					}
					
					text.add(new ModularMultiblockGuiLine(MIText.BaseEuRecipe.text(TextHelper.getEuTextTick(crafter.getBaseRecipeEu() * crafter.getRecipeMultiplier()))));
					
					text.add(new ModularMultiblockGuiLine(MIText.CurrentEuRecipe.text(TextHelper.getEuTextTick(crafter.getCurrentRecipeEu()))));
				}
			}
			
			return text;
		}));
	}
	
	protected MachineRecipeType getRecipeType()
	{
		return recipeTypeGetter.get();
	}
	
	protected int getMaxMultiplier()
	{
		return maxMultiplierGetter.get();
	}
	
	@Override
	public CrafterAccess getCrafterComponent()
	{
		return crafter;
	}
	
	@Override
	public Level getCrafterWorld()
	{
		return level;
	}
	
	@Override
	public UUID getOwnerUuid()
	{
		return placedBy.placerId;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if(!level.isClientSide)
		{
			boolean newActive = false;
			
			if(operatingState == OperatingState.TRYING_TO_RESUME)
			{
				if(crafter.tryContinueRecipe())
				{
					operatingState = OperatingState.NORMAL_OPERATION;
				}
			}
			
			if(operatingState == OperatingState.NORMAL_OPERATION)
			{
				if(crafter.tickRecipe())
				{
					newActive = true;
				}
			}
			else
			{
				crafter.decreaseEfficiencyTicks();
			}
			
			this.updateActive(newActive);
		}
		
		this.tickExtra();
	}
	
	public void tickExtra()
	{
	}
}
