package net.swedz.extended_industrialization.machines.blockentity.tesla;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyComponentHolder;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EISounds;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaBehavior;
import net.swedz.extended_industrialization.machines.component.tesla.SingingTeslaCoilComponent;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaBuzzingComponent;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaTransferLimits;
import net.swedz.extended_industrialization.machines.component.tesla.network.transmitter.TeslaTransmitter;
import net.swedz.extended_industrialization.machines.component.tesla.network.transmitter.TeslaTransmitterComponent;
import net.swedz.extended_industrialization.machines.guicomponent.teslanetwork.TeslaNetworkBar;
import net.swedz.extended_industrialization.proxy.EIProxy;
import net.swedz.tesseract.neoforge.capabilities.CapabilitiesListeners;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.slotpanel.ModularSlotPanel;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.List;
import java.util.Optional;

import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.*;

public final class TeslaCoilMachineBlockEntity extends MachineBlockEntity implements TeslaTransmitter.Delegate, Tickable, EnergyComponentHolder, TeslaBehavior
{
	private final IsActiveComponent isActive;
	
	private final RedstoneControlComponent redstoneControl;
	private final CasingComponent          casing;
	
	private final EnergyComponent energy;
	private final MIEnergyStorage insertable;
	
	private final SingingTeslaCoilComponent singing;
	private final TeslaBuzzingComponent     buzzing;
	
	private final TeslaTransmitterComponent transmitter;
	
	private long lastEnergyTransmitted;
	
	public TeslaCoilMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("tesla_coil"), false).build(),
				new OrientationComponent.Params(false, false, false)
		);
		
		isActive = new IsActiveComponent();
		
		redstoneControl = new RedstoneControlComponent();
		casing = new CasingComponent(this::onCasingUpdate);
		
		energy = new EnergyComponent(this, () -> 30 * 20 * casing.getCableTier().eu);
		insertable = energy.buildInsertable(casing::canInsertEu);
		
		singing = new SingingTeslaCoilComponent(this);
		buzzing = new TeslaBuzzingComponent(
				this,
				EISounds.TESLA_COIL_SINGING.get(), SoundSource.RECORDS,
				() -> singing.hasNote() && isActive.isActive,
				singing::getPitch
		);
		
		transmitter = new TeslaTransmitterComponent(
				this,
				List.of(energy),
				() ->
				{
					CableTier tier = casing.getCableTier();
					long maxTransfer = tier.getMaxTransfer();
					return TeslaTransferLimits.of(tier, maxTransfer, EI.config().teslaCoilRange(), singing.hasNote() ? 1 : tier.eu / 16);
				}
		);
		
		this.registerComponents(isActive, redstoneControl, casing, energy, transmitter, singing, buzzing);
		
		this.registerGuiComponent(new EnergyBar.Server(new EnergyBar.Parameters(61, 34), energy::getEu, energy::getCapacity));
		
		this.registerGuiComponent(new TeslaNetworkBar.Server(
				new TeslaNetworkBar.Parameters(101, 34),
				() ->
				{
					if(singing.hasNote())
					{
						return Optional.of(new TeslaNetworkBar.SingingData(singing.getNote(), this.getPassiveDrain()));
					}
					else if(this.hasNetwork())
					{
						TeslaNetwork network = this.getNetwork();
						if(network.isTransmitterLoaded())
						{
							long drain = this.getPassiveDrain();
							return Optional.of(new TeslaNetworkBar.TransmitterData(
									network.receiverCount(),
									lastEnergyTransmitted,
									network.getCableTier(),
									drain,
									lastEnergyTransmitted + drain
							));
						}
					}
					return Optional.empty();
				}
		));
		
		this.registerGuiComponent(new ModularSlotPanel.Server(this, 0)
				.withRedstoneModule(redstoneControl)
				.withCasings(casing));
	}
	
	private void onCasingUpdate(CableTier from, CableTier to)
	{
		if(level != null && !level.isClientSide())
		{
			transmitter.getNetwork().updateAll();
		}
	}
	
	public SingingTeslaCoilComponent getSingingComponent()
	{
		return singing;
	}
	
	@Override
	public boolean shouldTeslaRender()
	{
		return isActive.isActive;
	}
	
	@Override
	public ResourceLocation getTeslaModelLocation()
	{
		return EI.id("tesla/tesla_coil");
	}
	
	@Override
	public TeslaTransmitter getDelegateTransmitter()
	{
		return transmitter;
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData(casing.getCasing());
		data.isActive = isActive.isActive;
		orientation.writeModelData(data);
		return data;
	}
	
	@Override
	public MIInventory getInventory()
	{
		return MIInventory.EMPTY;
	}
	
	@Override
	public EnergyAccess getEnergyComponent()
	{
		return energy;
	}
	
	@Override
	public void setLevel(Level level)
	{
		super.setLevel(level);
		
		this.setNetwork(this.getPosition());
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
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			Proxies.get(EIProxy.class).tickTesla(worldPosition);
			buzzing.tick();
			return;
		}
		
		// TODO only update when the noteblock below is updated...
		if(singing.updateNote())
		{
			this.sync(false);
		}
		
		TeslaNetwork network = this.getNetwork();
		if(!network.hasTransmitter())
		{
			network.loadTransmitter(transmitter);
		}
		
		lastEnergyTransmitted = 0;
		boolean active = false;
		
		if(redstoneControl.doAllowNormalOperation(this))
		{
			long amountToDrain = this.getPassiveDrain();
			long drained = this.extractEnergy(amountToDrain, false);
			if(drained == amountToDrain)
			{
				if(!singing.hasNote())
				{
					lastEnergyTransmitted = this.transmitEnergy(this.getMaxTransfer());
				}
				active = true;
			}
		}
		
		isActive.updateActive(active, this);
	}
	
	@Override
	protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face)
	{
		ItemInteractionResult result = super.useItemOn(player, hand, face);
		if(!result.consumesAction())
		{
			result = redstoneControl.onUse(this, player, hand);
		}
		if(!result.consumesAction())
		{
			result = casing.onUse(this, player, hand);
		}
		return result;
	}
	
	@Override
	public List<Component> getTooltips()
	{
		return List.of(
				line(EIText.TESLA_COIL_HELP_1).arg(EI.config().teslaCoilRange()),
				line(EIText.TESLA_COIL_HELP_2)
		);
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		CapabilitiesListeners.register(EI.ID, (event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) -> ((TeslaCoilMachineBlockEntity) be).insertable));
	}
}
