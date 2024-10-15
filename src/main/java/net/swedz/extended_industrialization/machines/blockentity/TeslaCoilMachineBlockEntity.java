package net.swedz.extended_industrialization.machines.blockentity;

import aztech.modern_industrialization.MICapabilities;
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
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIClientConfig;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaArcBehavior;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaArcBehaviorHolder;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaArcs;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaTransferLimits;
import net.swedz.extended_industrialization.machines.component.tesla.transmitter.TeslaTransmitter;
import net.swedz.extended_industrialization.machines.component.tesla.transmitter.TeslaTransmitterComponent;
import net.swedz.extended_industrialization.machines.guicomponent.modularslots.ModularSlotPanel;

import java.util.List;
import java.util.Set;

public final class TeslaCoilMachineBlockEntity extends MachineBlockEntity implements TeslaTransmitter.Delegate, Tickable, EnergyComponentHolder, TeslaArcBehaviorHolder
{
	private final IsActiveComponent isActive;
	
	private final RedstoneControlComponent redstoneControl;
	private final CasingComponent          casing;
	
	private final EnergyComponent energy;
	private final MIEnergyStorage insertable;
	
	private final TeslaTransmitterComponent transmitter;
	
	private final TeslaArcs arcs;
	
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
		casing = new CasingComponent()
		{
			@Override
			protected void setCasingStack(ItemStack stack)
			{
				super.setCasingStack(stack);
				
				if(level != null && !level.isClientSide())
				{
					transmitter.getNetwork().updateAll();
				}
			}
		};
		
		energy = new EnergyComponent(this, casing::getEuCapacity);
		insertable = energy.buildInsertable(casing::canInsertEu);
		
		transmitter = new TeslaTransmitterComponent(
				this, List.of(energy),
				() ->
				{
					CableTier tier = casing.getCableTier();
					long maxTransfer = tier.getMaxTransfer();
					return TeslaTransferLimits.of(tier, maxTransfer, 32, 2);
				}
		);
		
		arcs = new TeslaArcs(
				0.25f, 3, 3, 1, 3, 2, 5,
				() ->
				{
					double radius = 0.35;
					boolean side = TeslaArcs.RANDOM.nextBoolean();
					double x = (side ? radius : radius * TeslaArcs.RANDOM.nextDouble()) * (TeslaArcs.RANDOM.nextBoolean() ? 1 : -1);
					double z = (!side ? radius : radius * TeslaArcs.RANDOM.nextDouble()) * (TeslaArcs.RANDOM.nextBoolean() ? 1 : -1);
					return Vec3.upFromBottomCenterOf(Vec3i.ZERO, 1).add(x, -0.2, z);
				},
				Set.of(Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)
		);
		
		this.registerComponents(isActive, redstoneControl, casing, energy, transmitter);
		
		this.registerGuiComponent(new EnergyBar.Server(new EnergyBar.Parameters(81, 34), energy::getEu, energy::getCapacity));
		this.registerGuiComponent(new ModularSlotPanel.Server(this, 0)
				.withRedstoneModule(redstoneControl)
				.withCasings(casing));
	}
	
	@Override
	public TeslaArcBehavior getTeslaArcBehavior()
	{
		return new TeslaArcBehavior()
		{
			@Override
			public boolean shouldRender()
			{
				return isActive.isActive;
			}
			
			@Override
			public TeslaArcs getArcs()
			{
				return arcs;
			}
		};
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
	public void setLevel(Level level)
	{
		super.setLevel(level);
		
		this.setNetwork(this.getPosition());
		this.getNetwork().loadTransmitter(transmitter);
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
			if(EIClientConfig.renderTeslaAnimations)
			{
				arcs.tick();
			}
			return;
		}
		
		lastEnergyTransmitted = 0;
		boolean active = false;
		
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
		
		isActive.updateActive(active, this);
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) -> ((TeslaCoilMachineBlockEntity) be).insertable));
	}
}
