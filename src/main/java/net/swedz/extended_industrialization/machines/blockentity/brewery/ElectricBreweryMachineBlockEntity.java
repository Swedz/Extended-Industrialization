package net.swedz.extended_industrialization.machines.blockentity.brewery;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyComponentHolder;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.LubricantHelper;
import aztech.modern_industrialization.machines.components.MachineInventoryComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.RecipeEfficiencyBar;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Lists;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.EIFluids;

import java.util.List;

public final class ElectricBreweryMachineBlockEntity extends BreweryMachineBlockEntity implements EnergyComponentHolder
{
	private static final int ENERGY_BAR_X = 7;
	private static final int ENERGY_BAR_Y = 44;
	
	private static final int EFFICIENCY_BAR_X = 57;
	private static final int EFFICIENCY_BAR_Y = 86;
	
	private final EnergyComponent          energy;
	private final MIEnergyStorage          insertable;
	private final RedstoneControlComponent redstoneControl;
	private final CasingComponent          casing;
	private final UpgradeComponent         upgrades;
	
	public ElectricBreweryMachineBlockEntity(BEP bep)
	{
		super(bep, "electric_brewery", MachineTier.LV, 32 * FluidType.BUCKET_VOLUME);
		
		this.redstoneControl = new RedstoneControlComponent();
		this.casing = new CasingComponent();
		this.upgrades = new UpgradeComponent();
		
		this.energy = new EnergyComponent(this, casing::getEuCapacity);
		this.insertable = energy.buildInsertable(casing::canInsertEu);
		
		this.registerGuiComponent(new EnergyBar.Server(new EnergyBar.Parameters(ENERGY_BAR_X, ENERGY_BAR_Y), energy::getEu, energy::getCapacity));
		this.registerGuiComponent(new RecipeEfficiencyBar.Server(new RecipeEfficiencyBar.Parameters(EFFICIENCY_BAR_X, EFFICIENCY_BAR_Y), crafter));
		this.registerGuiComponent(new SlotPanel.Server(this)
				.withRedstoneControl(redstoneControl)
				.withUpgrades(upgrades)
				.withCasing(casing));
		
		this.registerComponents(energy, redstoneControl, casing, upgrades);
	}
	
	@Override
	protected MachineInventoryComponent buildInventory()
	{
		List<ConfigurableItemStack> itemInputs = Lists.newArrayList();
		List<ConfigurableItemStack> itemOutputs = Lists.newArrayList();
		for(int i = 0; i < 9; i++)
		{
			itemInputs.add(ConfigurableItemStack.standardInputSlot());
		}
		for(int i = 0; i < 9; i++)
		{
			itemOutputs.add(ConfigurableItemStack.standardOutputSlot());
		}
		SlotPositions itemPositions = new SlotPositions.Builder()
				.addSlots(INPUT_SLOTS_X, INPUT_SLOTS_Y, 3, 3)
				.addSlots(OUTPUT_SLOTS_X, OUTPUT_SLOTS_Y, 3, 3)
				.build();
		
		List<ConfigurableFluidStack> fluidInputs = List.of(
				ConfigurableFluidStack.lockedInputSlot(capacity, EIFluids.BLAZING_ESSENCE.asFluid())
		);
		SlotPositions fluidPositions = new SlotPositions.Builder()
				.addSlot(BLAZING_ESSENCE_SLOT_X, BLAZING_ESSENCE_SLOT_Y)
				.build();
		
		return new MachineInventoryComponent(itemInputs, itemOutputs, fluidInputs, List.of(), itemPositions, fluidPositions);
	}
	
	@Override
	public boolean isEnabled()
	{
		return redstoneControl.doAllowNormalOperation(this);
	}
	
	@Override
	public long consumeEu(long max, Simulation simulation)
	{
		return energy.consumeEu(max, simulation);
	}
	
	@Override
	public long getMaxRecipeEu()
	{
		return tier.getMaxEu() + upgrades.getAddMaxEUPerTick();
	}
	
	@Override
	public EnergyAccess getEnergyComponent()
	{
		return energy;
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
		if(!result.consumesAction())
		{
			result = upgrades.onUse(this, player, hand);
		}
		if(!result.consumesAction())
		{
			result = LubricantHelper.onUse(crafter, player, hand);
		}
		return result;
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData(casing.getCasing());
		orientation.writeModelData(data);
		data.isActive = isActiveComponent.isActive;
		return data;
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) -> ((ElectricBreweryMachineBlockEntity) be).insertable));
	}
}
