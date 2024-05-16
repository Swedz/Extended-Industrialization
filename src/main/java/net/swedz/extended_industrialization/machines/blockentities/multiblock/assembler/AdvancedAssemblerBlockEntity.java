package net.swedz.extended_industrialization.machines.blockentities.multiblock.assembler;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.machine.component.CrafterAccess;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.CrafterComponentHolder;
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.ReiSlotLocking;
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import aztech.modern_industrialization.util.Simulation;
import aztech.modern_industrialization.util.TextHelper;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.swedz.extended_industrialization.api.EILubricantHelper;
import net.swedz.extended_industrialization.machines.components.craft.ModularCrafterAccessBehavior;
import net.swedz.extended_industrialization.machines.components.craft.advancedassembler.AdvancedAssemblerCrafterComponent;
import net.swedz.extended_industrialization.machines.components.craft.advancedassembler.AdvancedAssemblerMachineComponent;
import net.swedz.extended_industrialization.machines.guicomponents.advancedassemblermachineslot.AdvancedAssemblerMachineSlot;
import net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock.ModularMultiblockGui;
import net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock.ModularMultiblockGuiLine;
import net.swedz.extended_industrialization.machines.multiblock.BasicMultiblockMachineBlockEntity;
import net.swedz.extended_industrialization.machines.multiblock.members.PredicateSimpleMember;
import net.swedz.extended_industrialization.registry.tags.EITags;
import net.swedz.extended_industrialization.text.EIText;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public final class AdvancedAssemblerBlockEntity extends BasicMultiblockMachineBlockEntity implements EnergyListComponentHolder, CrafterComponentHolder, ModularCrafterAccessBehavior
{
	private final UpgradeComponent         upgrades;
	private final RedstoneControlComponent redstoneControl;
	
	private final AdvancedAssemblerMachineComponent machines;
	private final AdvancedAssemblerCrafterComponent crafter;
	
	private final List<EnergyComponent> energyInputs = Lists.newArrayList();
	
	public AdvancedAssemblerBlockEntity(BEP bep)
	{
		super(bep, new MachineGuiParameters.Builder("advanced_assembler", false).backgroundHeight(200).build(), SHAPE_TEMPLATES);
		
		this.machines = new AdvancedAssemblerMachineComponent();
		this.crafter = new AdvancedAssemblerCrafterComponent(this, machines, inventory, this);
		
		this.upgrades = new UpgradeComponent();
		this.redstoneControl = new RedstoneControlComponent();
		
		this.registerComponents(machines, crafter, upgrades, redstoneControl);
		
		this.registerGuiComponent(new SlotPanel.Server(this)
				.withRedstoneControl(redstoneControl)
				.withUpgrades(upgrades));
		
		this.registerGuiComponent(new ReiSlotLocking.Server(crafter::lockRecipe, () -> operatingState != OperatingState.NOT_MATCHED));
		
		this.registerGuiComponent(new AdvancedAssemblerMachineSlot.Server(
				this,
				() -> this.getMachineStackSize(activeShape.getActiveShapeIndex()),
				machines
		));
		
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
		
		this.registerGuiComponent(new ShapeSelection.Server(
				new ShapeSelection.Behavior()
				{
					@Override
					public void handleClick(int line, int delta)
					{
						int newShapeIndex = Mth.clamp(activeShape.getActiveShapeIndex() + delta, 0, SHAPE_TEMPLATES.length - 1);
						int newMachineStackSize = AdvancedAssemblerBlockEntity.this.getMachineStackSize(newShapeIndex);
						if(newMachineStackSize < machines.getMachines().getCount())
						{
							return;
						}
						activeShape.incrementShape(AdvancedAssemblerBlockEntity.this, delta);
					}
					
					@Override
					public int getCurrentIndex(int line)
					{
						return activeShape.getActiveShapeIndex();
					}
				},
				new ShapeSelection.LineInfo(
						SPLIT,
						IntStream.range(0, SPLIT).map(this::getMachineStackSize).mapToObj(EIText.ADVANCED_ASSEMBLER_SIZE::text).toList(),
						false
				)
		));
	}
	
	private int getMachineStackSize(int sizeIndex)
	{
		return (int) (BASE_MACHINES * Math.pow(MULT_MACHINES, sizeIndex));
	}
	
	@Override
	public List<? extends EnergyAccess> getEnergyComponents()
	{
		return energyInputs;
	}
	
	@Override
	public void onSuccessfulMatch(ShapeMatcher shapeMatcher)
	{
		energyInputs.clear();
		for(HatchBlockEntity hatch : shapeMatcher.getMatchedHatches())
		{
			hatch.appendEnergyInputs(energyInputs);
		}
	}
	
	@Override
	protected InteractionResult onUse(Player player, InteractionHand hand, Direction face)
	{
		InteractionResult result = super.onUse(player, hand, face);
		if(!result.consumesAction())
		{
			result = EILubricantHelper.onUse(crafter, player, hand);
		}
		if(!result.consumesAction())
		{
			result = mapComponentOrDefault(UpgradeComponent.class, upgrade -> upgrade.onUse(this, player, hand), result);
		}
		if(!result.consumesAction())
		{
			result = redstoneControl.onUse(this, player, hand);
		}
		return result;
	}
	
	@Override
	public CrafterAccess getCrafterComponent()
	{
		return crafter;
	}
	
	@Override
	public boolean isEnabled()
	{
		return redstoneControl.doAllowNormalOperation(this);
	}
	
	@Override
	public long consumeEu(long max, Simulation simulation)
	{
		long total = 0;
		for(EnergyComponent energyComponent : energyInputs)
		{
			total += energyComponent.consumeEu(max - total, simulation);
		}
		return total;
	}
	
	@Override
	public long getBaseRecipeEu()
	{
		return MachineTier.LV.getBaseEu();
	}
	
	@Override
	public long getMaxRecipeEu()
	{
		return MachineTier.LV.getMaxEu() + upgrades.getAddMaxEUPerTick();
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
		
		if(level.isClientSide)
		{
			return;
		}
		
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
	
	private static final int MAX_MACHINES  = 64;
	private static final int SPLIT         = 4;
	private static final int BASE_MACHINES = 8;
	private static final int MULT_MACHINES = 2;
	
	private static final ShapeTemplate[] SHAPE_TEMPLATES;
	
	static
	{
		SHAPE_TEMPLATES = new ShapeTemplate[SPLIT];
		
		SimpleMember casing = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("clean_stainless_steel_machine_casing")));
		SimpleMember pipe = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("stainless_steel_machine_casing_pipe")));
		SimpleMember glass = new PredicateSimpleMember((state) -> state.is(EITags.blockForge("glass")), Blocks.GLASS);
		HatchFlags front = new HatchFlags.Builder().with(HatchType.ENERGY_INPUT).build();
		HatchFlags top = new HatchFlags.Builder().with(HatchType.ITEM_INPUT, HatchType.FLUID_INPUT).build();
		HatchFlags bottom = new HatchFlags.Builder().with(HatchType.ITEM_OUTPUT, HatchType.FLUID_OUTPUT).build();
		
		for(int
			i = 0, size = 3, machines = BASE_MACHINES;
			i < SPLIT && machines <= MAX_MACHINES;
			i++, size += 2, machines *= MULT_MACHINES)
		{
			ShapeTemplate.Builder builder = new ShapeTemplate.Builder(MachineCasings.CLEAN_STAINLESS_STEEL);
			for(int z = 0; z < size; z++)
			{
				boolean isFront = z == 0;
				for(int x = -1; x <= 1; x++)
				{
					for(int y = -1; y <= 1; y++)
					{
						boolean isTop = y == 1;
						boolean isBottom = y == -1;
						boolean isCenter = x == 0 && y == 0;
						boolean isGlass = x != 0 && y == 0;
						builder.add(x, y, z, isCenter ? pipe : isGlass ? glass : casing, isFront ? front : isTop ? top : isBottom ? bottom : null);
					}
				}
			}
			SHAPE_TEMPLATES[i] = builder.build();
		}
	}
	
	public static void registerReiShapes()
	{
		for(ShapeTemplate shapeTemplate : SHAPE_TEMPLATES)
		{
			ReiMachineRecipes.registerMultiblockShape("advanced_assembler", shapeTemplate);
		}
	}
}
