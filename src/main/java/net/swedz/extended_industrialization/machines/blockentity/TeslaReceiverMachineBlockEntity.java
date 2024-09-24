package net.swedz.extended_industrialization.machines.blockentity;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.TeslaNetworkComponent;
import net.swedz.extended_industrialization.mixin.mi.accessor.CasingComponentAccessor;
import net.swedz.tesseract.neoforge.helper.transfer.InputOutputDirectionalBlockCapabilityCache;

import java.util.Optional;

public final class TeslaReceiverMachineBlockEntity extends MachineBlockEntity
{
	private final RedstoneControlComponent redstoneControl;
	private final CasingComponent          casing;
	
	private final InputOutputDirectionalBlockCapabilityCache<MIEnergyStorage> energyOutputCache;
	private final MIEnergyStorage                                             insertable;
	
	private final TeslaNetworkComponent teslaNetwork;
	
	public TeslaReceiverMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("tesla_receiver"), true).backgroundHeight(180).build(),
				new OrientationComponent.Params(true, false, false)
		);
		
		redstoneControl = new RedstoneControlComponent();
		casing = new CasingComponent();
		
		energyOutputCache = new InputOutputDirectionalBlockCapabilityCache<>(EnergyApi.SIDED);
		insertable = new MIEnergyStorage.NoExtract()
		{
			@Override
			public boolean canConnect(CableTier cableTier)
			{
				return false;
			}
			
			@Override
			public long receive(long maxReceive, boolean simulate)
			{
				if(!redstoneControl.doAllowNormalOperation(TeslaReceiverMachineBlockEntity.this))
				{
					return 0;
				}
				MIEnergyStorage target = energyOutputCache.output(level, worldPosition, orientation.outputDirection);
				return target != null && target.canConnect(((CasingComponentAccessor) casing).getCurrentTier()) ? target.receive(maxReceive, simulate) : 0;
			}
			
			@Override
			public long getAmount()
			{
				MIEnergyStorage target = energyOutputCache.output(level, worldPosition, orientation.outputDirection);
				return target != null ? target.getAmount() : 0;
			}
			
			@Override
			public long getCapacity()
			{
				MIEnergyStorage target = energyOutputCache.output(level, worldPosition, orientation.outputDirection);
				return target != null ? target.getCapacity() : 0;
			}
			
			@Override
			public boolean canReceive()
			{
				return redstoneControl.doAllowNormalOperation(TeslaReceiverMachineBlockEntity.this);
			}
		};
		
		teslaNetwork = new TeslaNetworkComponent(Optional.of(() -> insertable), Optional.empty());
		
		this.registerComponents(redstoneControl, casing, teslaNetwork);
		
		this.registerGuiComponent(new SlotPanel.Server(this)
				.withRedstoneControl(redstoneControl)
				.withCasing(casing));
	}
	
	@Override
	public MIInventory getInventory()
	{
		return MIInventory.EMPTY;
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData();
		orientation.writeModelData(data);
		return data;
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) ->
				{
					TeslaReceiverMachineBlockEntity machine = (TeslaReceiverMachineBlockEntity) be;
					return machine.orientation.outputDirection == direction ? null : machine.insertable;
				}));
	}
}
