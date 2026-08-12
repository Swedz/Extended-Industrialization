package net.swedz.extended_industrialization.machines.blockentity.multiblock.beacon;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Lists;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.beacon.BeaconBeamComponent;
import net.swedz.extended_industrialization.machines.component.beacon.BeaconEffectComponent;
import net.swedz.extended_industrialization.machines.guicomponent.beacon.BeaconEffectsView;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.slotpanel.ModularSlotPanel;
import net.swedz.tesseract.neoforge.compat.mi.machine.blockentity.multiblock.BasicMultiblockMachineBlockEntity;

import java.util.List;

public final class ElectricBeaconMachineBlockEntity extends BasicMultiblockMachineBlockEntity implements EnergyListComponentHolder
{
	private final RedstoneControlComponent redstoneControl;
	
	private final List<EnergyComponent> energyInputs = Lists.newArrayList();
	
	private final BeaconBeamComponent   beam;
	private final BeaconEffectComponent effect;
	
	public ElectricBeaconMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("electric_beacon"), false)
						.backgroundHeight(160)
						.build(),
				SHAPE_TEMPLATES
		);
		
		redstoneControl = new RedstoneControlComponent();
		beam = new BeaconBeamComponent();
		effect = new BeaconEffectComponent();
		
		this.registerComponents(redstoneControl, beam, effect);
		
		this.registerGuiComponent(new BeaconEffectsView(
				this::isShapeValid,
				beam::isActive,
				effect::getActiveEffects,
				effect::getTotalTicks,
				effect::getRemainingTicks,
				this::getEuCost
		));
		
		this.registerGuiComponent(new ModularSlotPanel(this, 0)
				.withRedstoneModule(redstoneControl));
		
		List<ShapeSelection.LineInfo> lines = Lists.newArrayList();
		List<Component> sizes = Lists.newArrayList();
		for(int i = 0; i < 4 && i < SHAPE_TEMPLATES.length; i++)
		{
			sizes.add(SHAPE_TRANSLATIONS[i]);
		}
		lines.add(new ShapeSelection.LineInfo(sizes, true));
		this.registerGuiComponent(new ShapeSelection(
				new ShapeSelection.Behavior()
				{
					@Override
					public void handleClick(int line, int delta)
					{
						if(line == 0)
						{
							activeShape.incrementShape(ElectricBeaconMachineBlockEntity.this, delta);
						}
					}
					
					@Override
					public int getCurrentIndex(int line)
					{
						if(line == 0)
						{
							return activeShape.getActiveShapeIndex();
						}
						throw new IllegalStateException();
					}
				},
				lines.toArray(new ShapeSelection.LineInfo[0])
		));
	}
	
	public boolean isActive()
	{
		return isActive.isActive;
	}
	
	public List<BeaconBeamComponent.Section> getBeamSections()
	{
		return beam.getBeamSections();
	}
	
	private long consumeEu(long max)
	{
		long total = 0;
		for(var energyComponent : energyInputs)
		{
			total += energyComponent.consumeEu(max - total, Simulation.ACT);
		}
		return total;
	}
	
	private long getEuCostPerEffect()
	{
		return 256;
	}
	
	private long getEuCost()
	{
		return this.getEuCostPerEffect() * effect.getActiveEffects().size();
	}
	
	@Override
	public List<? extends EnergyAccess> getEnergyComponents()
	{
		return energyInputs;
	}
	
	@Override
	public ShapeMatcher createShapeMatcher()
	{
		return new ElectricBeaconShapeMatcher(level, worldPosition, orientation.facingDirection, getActiveShape(), shapeValid, beam);
	}
	
	@Override
	protected void onRematch(ShapeMatcher shapeMatcher)
	{
		super.onRematch(shapeMatcher);
		
		if(shapeMatcher.isMatchSuccessful())
		{
			energyInputs.clear();
			for(var hatch : shapeMatcher.getMatchedHatches())
			{
				hatch.appendEnergyInputs(energyInputs);
			}
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if(level.isClientSide())
		{
			return;
		}
		
		boolean activeBefore = this.isActive();
		boolean active = beam.isActive();
		if(redstoneControl.doAllowNormalOperation(this) && this.isShapeValid())
		{
			if(beam.needsRematch())
			{
				beam.rematch(level, worldPosition);
				this.sync(false);
			}
			
			if(active)
			{
				effect.tryConsumePotion(inventory);
				
				long euCost = this.getEuCost();
				long eu = this.consumeEu(euCost);
				active = eu == euCost;
			}
			
			if(active)
			{
				if(level.getGameTime() % (4 * 20) == 0)
				{
					level.playSound(null, worldPosition, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 1, 1);
				}
				
				effect.tryApplyEffects(level, worldPosition, activeShape.getActiveShapeIndex() + 1);
			}
		}
		else
		{
			active = false;
		}
		this.updateActive(active);
		
		if(activeBefore != active)
		{
			level.playSound(null, worldPosition, active ? SoundEvents.BEACON_ACTIVATE : SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1, 1);
		}
	}
	
	@Override
	protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face)
	{
		var result = super.useItemOn(player, hand, face);
		if(!result.consumesAction())
		{
			result = redstoneControl.onUse(this, player, hand);
		}
		return result;
	}
	
	@Override
	public List<Component> getTooltips()
	{
		List<Component> lines = Lists.newArrayList();
		lines.add(EI.text().electricBeaconHelp(this.getEuCostPerEffect()));
		return lines;
	}
	
	private static final int MAX_LAYERS = 4;
	
	private static final ShapeTemplate[] SHAPE_TEMPLATES;
	
	private static final Component[] SHAPE_TRANSLATIONS = new Component[]
			{
					MIText.ShapeTextSmall.text(),
					MIText.ShapeTextMedium.text(),
					MIText.ShapeTextLarge.text(),
					MIText.ShapeTextExtreme.text()
			};
	
	static
	{
		var shapeTemplates = new ShapeTemplate[MAX_LAYERS];
		
		var casing = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("steel_machine_casing")));
		var pipe = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("steel_machine_casing_pipe")));
		var metal = SimpleMember.forBlockTag(() -> Blocks.IRON_BLOCK, BlockTags.BEACON_BASE_BLOCKS);
		
		var hatch = new HatchFlags.Builder().with(HatchTypes.ENERGY_INPUT, HatchTypes.ITEM_INPUT, HatchTypes.ITEM_OUTPUT).build();
		
		for(int index = 0; index < MAX_LAYERS; index++)
		{
			int size = index + 1;
			var builder = new ShapeTemplate.Builder(MachineCasings.STEEL);
			for(int layer = 0; layer < size + 1; layer++)
			{
				int y = -layer - 1;
				boolean isBottom = layer == size;
				int layerSize = layer + 1;
				for(int x = -layerSize; x <= layerSize; x++)
				{
					for(int z = -layerSize; z <= layerSize; z++)
					{
						boolean isCenter = x == 0 && z == 0;
						builder.add(x, y, z, isBottom ? casing : (isCenter ? pipe : metal), isBottom ? hatch : null);
					}
				}
			}
			shapeTemplates[index] = builder.build();
		}
		
		SHAPE_TEMPLATES = shapeTemplates;
	}
	
	public static void registerReiShapes()
	{
		int index = 0;
		for(var shapeTemplate : SHAPE_TEMPLATES)
		{
			ReiMachineRecipes.registerMultiblockShape(EI.id("electric_beacon"), shapeTemplate, "" + index);
			index++;
		}
	}
}
