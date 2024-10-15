package net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.blockentities.hatches.EnergyHatch;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIClientConfig;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.api.WorldPos;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaArcGenerator;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaArcs;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaPlasmaGenerator;
import net.swedz.extended_industrialization.machines.component.itemslot.TeslaTowerUpgradeComponent;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaTransferLimits;
import net.swedz.extended_industrialization.machines.component.tesla.transmitter.TeslaTransmitter;
import net.swedz.extended_industrialization.machines.component.tesla.transmitter.TeslaTransmitterComponent;
import net.swedz.extended_industrialization.machines.guicomponent.EIModularSlotPanelSlots;
import net.swedz.extended_industrialization.machines.guicomponent.modularslots.ModularSlotPanel;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGui;
import net.swedz.tesseract.neoforge.compat.mi.machine.blockentity.multiblock.BasicMultiblockMachineBlockEntity;

import java.util.List;
import java.util.Map;

import static aztech.modern_industrialization.MITooltips.*;
import static net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine.*;
import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MIParser.*;

public final class TeslaTowerBlockEntity extends BasicMultiblockMachineBlockEntity implements EnergyListComponentHolder, TeslaTransmitter.Delegate, TeslaArcGenerator, TeslaPlasmaGenerator
{
	private final RedstoneControlComponent redstoneControl;
	private final TeslaTowerUpgradeComponent upgrade;
	
	private final List<EnergyComponent> energyInputs = Lists.newArrayList();
	
	private final TeslaTransmitterComponent transmitter;
	
	private final TeslaArcs arcs;
	
	private CableTier cableTier;
	private long lastEnergyTransmitted;
	
	public TeslaTowerBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("tesla_tower"), false).backgroundHeight(200).build(),
				SHAPES.shapeTemplates()
		);
		
		redstoneControl = new RedstoneControlComponent();
		upgrade = new TeslaTowerUpgradeComponent()
		{
			@Override
			public void setStackServer(MachineBlockEntity machine, ItemStack stack)
			{
				super.setStackServer(machine, stack);
				
				if(level != null && !level.isClientSide())
				{
					transmitter.getNetwork().updateAll();
				}
			}
		};
		
		transmitter = new TeslaTransmitterComponent(
				this, energyInputs,
				() -> TeslaTransferLimits.of(cableTier, SHAPES.tiers().get(activeShape.getActiveShapeIndex())),
				() -> new WorldPos(level, this.getTopLoadPosition())
		);
		
		arcs = new TeslaArcs(
				1f, 3, 6, 4, 8, 15, 10,
				() -> Vec3.atCenterOf(this.getTopLoadPosition())
		);
		
		this.registerComponents(redstoneControl, upgrade, transmitter);
		
		this.registerGuiComponent(new ModularMultiblockGui.Server(0, ModularMultiblockGui.HEIGHT, (content) ->
		{
			content.add((this.isShapeValid() ? MIText.MultiblockShapeValid : MIText.MultiblockShapeInvalid).text(), this.isShapeValid() ? WHITE : RED);
			
			if(this.isShapeValid())
			{
				if(this.hasNetwork())
				{
					TeslaNetwork network = this.getNetwork();
					
					if(network.isTransmitterLoaded())
					{
						content.add(EIText.TESLA_TRANSMITTER_RECEIVERS.arg(network.receiverCount()));
						
						content.add(EIText.TESLA_TRANSMITTER_TRANSMITTING.arg(lastEnergyTransmitted, EU_PER_TICK_PARSER).arg(network.getCableTier(), CABLE_TIER_SHORT));
						long drain = this.getPassiveDrain();
						content.add(EIText.TESLA_TRANSMITTER_DRAIN.arg(drain, EU_PER_TICK_PARSER));
						content.add(EIText.TESLA_TRANSMITTER_CONSUMING.arg(lastEnergyTransmitted + drain, EU_PER_TICK_PARSER));
					}
					else
					{
						if(this.getCableTier() == null)
						{
							content.add(EIText.TESLA_TRANSMITTER_NO_ENERGY_HATCHES, RED);
						}
						else
						{
							content.add(EIText.TESLA_TRANSMITTER_NO_NETWORK, RED);
						}
					}
				}
				else
				{
					content.add(EIText.TESLA_TRANSMITTER_NO_NETWORK, RED);
				}
			}
			else
			{
				if(this.getShapeMatcher() != null && this.getShapeMatcher().hasMismatchingHatches())
				{
					content.add(EIText.TESLA_TRANSMITTER_MISMATCHING_HATCHES, RED, true);
				}
			}
		}));
		
		this.registerGuiComponent(SHAPES.createShapeSelectionGuiComponent(this, activeShape, true));
		
		this.registerGuiComponent(new ModularSlotPanel.Server(this, 0)
				.withRedstoneModule(redstoneControl)
				.with(EIModularSlotPanelSlots.TESLA_TOWER_UPGRADE, upgrade));
	}
	
	public BlockPos getTopLoadPosition()
	{
		Direction facing = orientation.facingDirection;
		BlockPos topLoadCenter = worldPosition
				.relative(facing.getAxis(), -3)
				.above(14);
		return topLoadCenter.subtract(worldPosition);
	}
	
	@Override
	public TeslaArcs getTeslaArcs()
	{
		return arcs;
	}
	
	@Override
	public boolean shouldRenderTeslaArcs()
	{
		return isActive.isActive;
	}
	
	@Override
	public Vec3 getTeslaPlasmaOffset()
	{
		return Vec3.atLowerCornerOf(this.getTopLoadPosition());
	}
	
	@Override
	public boolean shouldRenderTeslaPlasma()
	{
		return this.shouldRenderTeslaArcs();
	}
	
	@Override
	public List<? extends EnergyAccess> getEnergyComponents()
	{
		return energyInputs;
	}
	
	@Override
	public void onMatchSuccessful()
	{
		super.onMatchSuccessful();
		
		CableTier cableTier = null;
		energyInputs.clear();
		for(HatchBlockEntity hatch : shapeMatcher.getMatchedHatches())
		{
			hatch.appendEnergyInputs(energyInputs);
			if(cableTier == null && hatch instanceof EnergyHatch energyHatch)
			{
				cableTier = energyHatch.getCableTier();
			}
		}
		this.cableTier = cableTier;
		if(this.hasNetwork())
		{
			if(this.getCableTier() != null)
			{
				this.getNetwork().loadTransmitter(transmitter);
			}
		}
		else
		{
			EI.LOGGER.error("Failed to load transmitter into the network because no network was set yet");
		}
	}
	
	@Override
	protected void onMatchFailure()
	{
		if(this.hasNetwork())
		{
			this.getNetwork().unloadTransmitter();
		}
		else
		{
			EI.LOGGER.error("Failed to unload transmitter into the network because no network was set yet");
		}
	}
	
	@Override
	public TeslaTransmitter getDelegateTransmitter()
	{
		return transmitter;
	}
	
	@Override
	public void setLevel(Level level)
	{
		super.setLevel(level);
		
		this.setNetwork(new WorldPos(level, worldPosition));
	}
	
	@Override
	public ShapeMatcher createShapeMatcher()
	{
		return new SameCableTierShapeMatcher(
				level, worldPosition, orientation.facingDirection,
				this.getActiveShape()
		);
	}
	
	@Override
	public SameCableTierShapeMatcher getShapeMatcher()
	{
		return (SameCableTierShapeMatcher) shapeMatcher;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if(level.isClientSide())
		{
			if(EIClientConfig.renderTeslaAnimations)
			{
				arcs.tick();
			}
			return;
		}
		
		lastEnergyTransmitted = 0;
		boolean active = false;
		
		if(shapeValid.shapeValid)
		{
			if(redstoneControl.doAllowNormalOperation(this))
			{
				long amountToDrain = this.getPassiveDrain();
				long drained = this.extractEnergy(amountToDrain, false);
				if(drained == amountToDrain)
				{
					lastEnergyTransmitted = this.transmitEnergy(this.getMaxTransfer());
					active = true;
				}
			}
		}
		
		this.updateActive(active);
	}
	
	@Override
	public void setRemoved()
	{
		super.setRemoved();
		
		if(level.isClientSide())
		{
			return;
		}
		
		if(this.hasNetwork())
		{
			this.getNetwork().unloadTransmitter();
		}
		else
		{
			EI.LOGGER.error("Failed to unload transmitter into the network because no network was set yet");
		}
	}
	
	private static final TeslaTowerShapes SHAPES = new TeslaTowerShapes();
	
	public static void registerTieredShapes()
	{
		SHAPES.register();
	}
	
	public static Map<ResourceLocation, TeslaTowerTier> getTiersByWinding()
	{
		return SHAPES.tiersByBlock();
	}
}
