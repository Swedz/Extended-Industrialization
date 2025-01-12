package net.swedz.extended_industrialization.machines.blockentity.tesla;

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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaPlasmaBehavior;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaPlasmaBehaviorHolder;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiver;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiverComponent;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiverState;
import net.swedz.extended_industrialization.machines.guicomponent.teslanetwork.TeslaNetworkBar;

import java.util.List;
import java.util.Optional;

import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.*;

public final class TeslaReceiverHatchBlockEntity extends HatchBlockEntity implements EnergyComponentHolder, CableTierHolder, TeslaReceiver.Delegate, TeslaPlasmaBehaviorHolder
{
	private final CableTier tier;
	
	private final IsActiveComponent isActive;
	
	private final EnergyComponent energy;
	private final MIEnergyStorage insertable;
	
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
		
		receiver = new TeslaReceiverComponent(this, insertable, () -> true, () -> tier);
		
		this.registerComponents(isActive, energy, receiver);
		
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
	}
	
	private void onCasingUpdate(CableTier from, CableTier to)
	{
		if(level != null && !level.isClientSide())
		{
			receiver.addToNetwork();
		}
	}
	
	@Override
	public TeslaPlasmaBehavior getTeslaPlasmaBehavior()
	{
		return new TeslaPlasmaBehavior()
		{
			@Override
			public boolean shouldRender()
			{
				return isActive.isActive;
			}
			
			@Override
			public Vec3 getOffset()
			{
				return new Vec3(-0.05f / 2f, -0.05f / 2f, -0.05f / 2f);
			}
			
			@Override
			public ResourceLocation getModelLocation()
			{
				return EI.id("tesla_plasma/tesla_hatch");
			}
			
			@Override
			public float getModelScale()
			{
				return 1.05f;
			}
			
			@Override
			public float getSpeed()
			{
				return 100f;
			}
			
			@Override
			public float getTextureScale()
			{
				return 48f;
			}
		};
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
	public List<Component> getTooltips()
	{
		return List.of(
				line(EIText.TESLA_RECEIVER_HELP_1),
				line(EIText.TESLA_RECEIVER_HELP_2)
		);
	}
}
