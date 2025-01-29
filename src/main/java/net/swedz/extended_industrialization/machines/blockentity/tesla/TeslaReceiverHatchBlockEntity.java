package net.swedz.extended_industrialization.machines.blockentity.tesla;

import aztech.modern_industrialization.MITooltips;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.CableTierHolder;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyComponentHolder;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaBehavior;
import net.swedz.extended_industrialization.machines.component.tesla.AestheticTeslaCoilComponent;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.network.receiver.TeslaReceiver;
import net.swedz.extended_industrialization.machines.component.tesla.network.receiver.TeslaReceiverComponent;
import net.swedz.extended_industrialization.machines.component.tesla.network.receiver.TeslaReceiverState;
import net.swedz.extended_industrialization.machines.guicomponent.teslanetwork.TeslaNetworkBar;
import net.swedz.extended_industrialization.proxy.EIProxy;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.configurationpanel.ConfigurationPanelBuilder;
import net.swedz.tesseract.neoforge.proxy.Proxies;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.*;

public final class TeslaReceiverHatchBlockEntity extends HatchBlockEntity implements EnergyComponentHolder, CableTierHolder, TeslaReceiver.Delegate, TeslaBehavior
{
	private final CableTier tier;
	
	private final IsActiveComponent isActive;
	
	private final EnergyComponent energy;
	private final MIEnergyStorage insertable;
	
	private final AestheticTeslaCoilComponent aesthetic;
	
	private final TeslaReceiverComponent receiver;
	
	public TeslaReceiverHatchBlockEntity(BEP bep, CableTier tier)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("tesla_receiver_hatch"), false).build(),
				new OrientationComponent.Params(false, false, false)
		);
		
		this.tier = tier;
		
		isActive = new IsActiveComponent();
		
		energy = new EnergyComponent(this, () -> 30 * 20 * tier.getEu());
		insertable = energy.buildInsertable((other) -> other == tier);
		
		aesthetic = new AestheticTeslaCoilComponent();
		
		receiver = new TeslaReceiverComponent(this, insertable, () -> true, () -> tier);
		
		this.registerComponents(isActive, energy, aesthetic, receiver);
		
		this.registerGuiComponent(new EnergyBar.Server(new EnergyBar.Parameters(61, 34), energy::getEu, energy::getCapacity));
		
		this.registerGuiComponent(new TeslaNetworkBar.Server(
				new TeslaNetworkBar.Parameters(101, 34),
				() ->
				{
					if(this.hasNetwork())
					{
						TeslaNetwork network = this.getNetwork();
						if(network.isTransmitterLoaded())
						{
							TeslaReceiverState state = this.checkReceiveFrom(network);
							return Optional.of(new TeslaNetworkBar.ReceiverData(state, Optional.of(this.getNetworkKey()), Optional.of(network.getCableTier())));
						}
						else
						{
							return Optional.of(new TeslaNetworkBar.ReceiverData(TeslaReceiverState.UNLOADED_TRANSMITTER, Optional.of(this.getNetworkKey()), Optional.empty()));
						}
					}
					else
					{
						return Optional.of(new TeslaNetworkBar.ReceiverData(TeslaReceiverState.NO_LINK, Optional.empty(), Optional.empty()));
					}
				}
		));
		
		var configPanel = new ConfigurationPanelBuilder(
				EIText.CONFIGURATION_PANEL.text(),
				EIText.CONFIGURATION_PANEL_DESCRIPTION.text().withStyle(MITooltips.DEFAULT_STYLE.withItalic(true)),
				(lineIndex, delta) -> this.sync()
		);
		aesthetic.appendSelectionPanel(this, configPanel);
		this.registerGuiComponent(configPanel.build());
	}
	
	private void onCasingUpdate(CableTier from, CableTier to)
	{
		if(level != null && !level.isClientSide())
		{
			receiver.addToNetwork();
		}
	}
	
	@Override
	public boolean shouldTeslaRender()
	{
		return isActive.isActive;
	}
	
	@Override
	public ResourceLocation getTeslaModelLocation()
	{
		return EI.id("tesla/tesla_hatch");
	}
	
	@Override
	public Vector3f getTeslaColor()
	{
		return aesthetic.getColor();
	}
	
	@Override
	public TeslaReceiver getDelegateReceiver()
	{
		return receiver;
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = super.getMachineModelData();
		data.isActive = isActive.isActive;
		return data;
	}
	
	@Override
	public CableTier getCableTier()
	{
		return tier;
	}
	
	@Override
	public EnergyAccess getEnergyComponent()
	{
		return energy;
	}
	
	@Override
	public HatchType getHatchType()
	{
		return HatchType.ENERGY_INPUT;
	}
	
	@Override
	public boolean upgradesToSteel()
	{
		return false;
	}
	
	@Override
	public MIInventory getInventory()
	{
		return MIInventory.EMPTY;
	}
	
	@Override
	public void appendEnergyInputs(List<EnergyComponent> list)
	{
		list.add(energy);
	}
	
	@Override
	public void setLevel(Level level)
	{
		super.setLevel(level);
		
		if(level.isClientSide())
		{
			return;
		}
		
		receiver.addToNetwork();
	}
	
	@Override
	public void setRemoved()
	{
		super.setRemoved();
		
		if(level.isClientSide())
		{
			return;
		}
		
		receiver.removeFromNetwork();
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
		
		if(this.hasNetwork() && this.getNetwork().isTransmitterLoaded())
		{
			TeslaNetwork network = this.getNetwork();
			isActive.updateActive(network.isTransmitterLoaded() && this.checkReceiveFrom(network).isSuccess(), this);
		}
		else
		{
			isActive.updateActive(false, this);
		}
	}
	
	@Override
	protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face)
	{
		ItemInteractionResult result = super.useItemOn(player, hand, face);
		if(!result.consumesAction())
		{
			result = aesthetic.onUse(this, player, hand);
		}
		return result;
	}
	
	@Override
	public List<Component> getTooltips()
	{
		return List.of(
				line(EIText.TESLA_RECEIVER_HELP_1),
				line(EIText.TESLA_RECEIVER_HELP_2)
		);
	}
}
