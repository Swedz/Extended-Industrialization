package net.swedz.extended_industrialization.machines.blockentities;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyComponentHolder;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.swedz.extended_industrialization.machines.components.WirelessChargingComponent;

import java.util.function.BiPredicate;

public final class WirelessChargerMachineBlockEntity extends MachineBlockEntity implements EnergyComponentHolder, Tickable
{
	private final RedstoneControlComponent redstoneControl;
	
	private final EnergyComponent energy;
	private final MIEnergyStorage insertable;
	
	private final WirelessChargingComponent wirelessCharging;
	
	public WirelessChargerMachineBlockEntity(BEP bep, String blockName, CableTier tier, BiPredicate<MachineBlockEntity, Player> filter)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(blockName, false).build(),
				new OrientationComponent.Params(false, false, false)
		);
		
		redstoneControl = new RedstoneControlComponent();
		
		energy = new EnergyComponent(this, () -> tier.eu * 100);
		insertable = energy.buildInsertable((otherTier) -> otherTier == tier);
		
		wirelessCharging = new WirelessChargingComponent(this, energy, filter, () -> tier.eu * 8);
		
		this.registerGuiComponent(new EnergyBar.Server(new EnergyBar.Parameters(76, 39), energy::getEu, energy::getCapacity));
		
		this.registerGuiComponent(new SlotPanel.Server(this)
				.withRedstoneControl(redstoneControl));
		
		this.registerComponents(energy, redstoneControl, wirelessCharging);
	}
	
	@Override
	public EnergyAccess getEnergyComponent()
	{
		return energy;
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
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			return;
		}
		
		if(redstoneControl.doAllowNormalOperation(this))
		{
			wirelessCharging.tick();
		}
	}
	
	@Override
	protected InteractionResult onUse(Player player, InteractionHand hand, Direction face)
	{
		InteractionResult result = super.onUse(player, hand, face);
		if(!result.consumesAction())
		{
			result = redstoneControl.onUse(this, player, hand);
		}
		return result;
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) ->
						((WirelessChargerMachineBlockEntity) be).insertable));
	}
}
