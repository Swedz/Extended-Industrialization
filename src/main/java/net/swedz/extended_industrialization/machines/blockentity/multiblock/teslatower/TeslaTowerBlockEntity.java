package net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.blockentities.hatches.EnergyHatch;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaBehavior;
import net.swedz.extended_industrialization.machines.component.itemslot.TeslaTowerUpgradeComponent;
import net.swedz.extended_industrialization.machines.component.tesla.AestheticTeslaCoilComponent;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaTransferLimits;
import net.swedz.extended_industrialization.machines.component.tesla.network.transmitter.TeslaTransmitter;
import net.swedz.extended_industrialization.machines.component.tesla.network.transmitter.TeslaTransmitterComponent;
import net.swedz.extended_industrialization.machines.guicomponent.EIModularSlotPanelSlots;
import net.swedz.extended_industrialization.proxy.EIProxy;
import net.swedz.tesseract.neoforge.api.WorldPos;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.configurationpanel.ConfigurationPanelBuilder;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGui;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.slotpanel.ModularSlotPanel;
import net.swedz.tesseract.neoforge.compat.mi.machine.blockentity.multiblock.BasicMultiblockMachineBlockEntity;
import net.swedz.tesseract.neoforge.compat.mi.machine.multiblock.matcher.SameCableTierShapeMatcher;
import net.swedz.tesseract.neoforge.proxy.Proxies;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

import static net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine.*;

public final class TeslaTowerBlockEntity extends BasicMultiblockMachineBlockEntity implements EnergyListComponentHolder, TeslaTransmitter.Delegate, TeslaBehavior
{
	private final RedstoneControlComponent   redstoneControl;
	private final TeslaTowerUpgradeComponent upgrade;
	
	private final List<EnergyComponent> energyInputs = Lists.newArrayList();
	
	private final TeslaTransmitterComponent transmitter;
	
	private final AestheticTeslaCoilComponent aesthetic;
	
	private boolean   hasMismatchingHatches;
	private CableTier cableTier;
	private long      lastEnergyTransmitted;
	
	public TeslaTowerBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("tesla_tower"), false).backgroundHeight(200).build(),
				SHAPES.shapeTemplates()
		);
		
		redstoneControl = new RedstoneControlComponent();
		upgrade = new TeslaTowerUpgradeComponent(this::onUpgradeUpdate);
		
		transmitter = new TeslaTransmitterComponent(
				this,
				energyInputs,
				() -> TeslaTransferLimits.of(cableTier, SHAPES.tiers().get(activeShape.getActiveShapeIndex())),
				() -> new WorldPos(level, this.getTopLoadPosition())
		);
		
		aesthetic = new AestheticTeslaCoilComponent();
		
		this.registerComponents(redstoneControl, upgrade, transmitter, aesthetic);
		
		this.registerGuiComponent(new ModularMultiblockGui(0, ModularMultiblockGui.HEIGHT, (content) ->
		{
			content.add((this.isShapeValid() ? MIText.MultiblockShapeValid : MIText.MultiblockShapeInvalid).text(), this.isShapeValid() ? WHITE : RED);
			
			if(this.isShapeValid())
			{
				if(this.hasNetwork())
				{
					TeslaNetwork network = this.getNetwork();
					if(network.isTransmitterLoaded())
					{
						content.add(EI.text().teslaNetworkTransmitterReceivers(network.receiverCount()));
						
						content.add(EI.text().teslaNetworkTransmitterTransmitting(lastEnergyTransmitted, network.getCableTier()));
						long drain = this.getPassiveDrain();
						content.add(EI.text().teslaNetworkTransmitterDrain(drain));
						content.add(EI.text().teslaNetworkTransmitterConsuming(lastEnergyTransmitted + drain));
					}
					else if(this.getCableTier() == null)
					{
						content.add(EI.text().teslaTowerNoEnergyHatches(), RED);
					}
				}
			}
			else
			{
				if(hasMismatchingHatches)
				{
					content.add(EI.text().teslaTowerMismatchingHatches(), RED, true);
				}
			}
		}));
		
		this.registerGuiComponent(new ModularSlotPanel(this, 0)
				.withRedstoneModule(redstoneControl)
				.with(EIModularSlotPanelSlots.TESLA_TOWER_UPGRADE, upgrade));
		
		var configPanel = new ConfigurationPanelBuilder(
				EI.text().configurationPanel(),
				EI.text().configurationPanelDescription(),
				(lineIndex, delta) -> this.sync()
		);
		SHAPES.appendConfigurationPanel(configPanel, this, activeShape, true);
		aesthetic.appendSelectionPanel(this, configPanel);
		this.registerGuiComponent(configPanel.build());
	}
	
	private void onUpgradeUpdate(ItemStack from, ItemStack to)
	{
		if(level != null && !level.isClientSide())
		{
			transmitter.getNetwork().updateAll();
		}
	}
	
	public BlockPos getTopLoadPosition()
	{
		var facing = orientation.facingDirection;
		return worldPosition
				.relative(facing, -3)
				.above(14);
	}
	
	public BlockPos getTopLoadPositionRelative()
	{
		return this.getTopLoadPosition().subtract(worldPosition);
	}
	
	@Override
	public boolean shouldTeslaRender()
	{
		return isActive.isActive;
	}
	
	@Override
	public ResourceLocation getTeslaModelLocation()
	{
		return EI.id("tesla/tesla_tower");
	}
	
	@Override
	public Vector3f getTeslaColor()
	{
		return aesthetic.getColor();
	}
	
	@Override
	public List<? extends EnergyAccess> getEnergyComponents()
	{
		return energyInputs;
	}
	
	@Override
	protected void onRematch(ShapeMatcher shapeMatcher)
	{
		super.onRematch(shapeMatcher);
		
		if(shapeMatcher.isMatchSuccessful())
		{
			CableTier cableTier = null;
			energyInputs.clear();
			for(var hatch : shapeMatcher.getMatchedHatches())
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
		else
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
	protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face)
	{
		var result = super.useItemOn(player, hand, face);
		if(!result.consumesAction())
		{
			result = redstoneControl.onUse(this, player, hand);
		}
		if(!result.consumesAction())
		{
			result = upgrade.onUse(this, player, hand);
		}
		if(!result.consumesAction())
		{
			result = aesthetic.onUse(this, player, hand);
		}
		return result;
	}
	
	@Override
	public ShapeMatcher createShapeMatcher()
	{
		return new SameCableTierShapeMatcher(
				level, worldPosition, orientation.facingDirection,
				this.getActiveShape(), (value) -> hasMismatchingHatches = value
		);
	}
	
	@Override
	public void setRemoved()
	{
		super.setRemoved();
		
		if(level.isClientSide())
		{
			Proxies.get(EIProxy.class).removeTesla(worldPosition);
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
	
	@Override
	public void tick()
	{
		super.tick();
		
		if(level.isClientSide())
		{
			Proxies.get(EIProxy.class).tickTesla(worldPosition);
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
	public List<Component> getTooltips()
	{
		return List.of(
				EI.text().teslaTowerHelp1(),
				EI.text().teslaTowerHelp2(),
				EI.text().teslaTowerHelp3()
		);
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
