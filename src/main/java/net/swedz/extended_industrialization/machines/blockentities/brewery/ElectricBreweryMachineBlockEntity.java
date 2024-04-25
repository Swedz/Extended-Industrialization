package net.swedz.extended_industrialization.machines.blockentities.brewery;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;

public final class ElectricBreweryMachineBlockEntity extends BreweryMachineBlockEntity
{
	private final MIInventory inventory;
	
	private final EnergyComponent          energy;
	private final MIEnergyStorage          insertable;
	private final RedstoneControlComponent redstoneControl;
	private final CasingComponent          casing;
	private final UpgradeComponent         upgrades;
	
	public ElectricBreweryMachineBlockEntity(BEP bep)
	{
		super(bep, "electric_brewery", 8, 32 * FluidType.BUCKET_VOLUME);
		
		List<ConfigurableItemStack> itemStacks = Lists.newArrayList();
		itemStacks.add(ConfigurableItemStack.lockedInputSlot(Items.BLAZE_POWDER));
		for(int i = 0; i < 9; i++)
		{
			itemStacks.add(ConfigurableItemStack.standardInputSlot());
		}
		for(int i = 0; i < 4; i++)
		{
			itemStacks.add(ConfigurableItemStack.standardInputSlot());
		}
		for(int i = 0; i < 9; i++)
		{
			itemStacks.add(new PotionConfigurableItemStack());
		}
		SlotPositions itemPositions = new SlotPositions.Builder()
				.addSlot(BLAZING_ESSENCE_SLOT_X, BLAZING_ESSENCE_SLOT_Y)
				.addSlots(INPUT_BOTTLE_SLOTS_X, INPUT_BOTTLE_SLOTS_Y, 3, 3)
				.addSlots(INPUT_REAGENT_SLOTS_X, INPUT_REAGENT_SLOTS_Y, 4, 1)
				.addSlots(OUTPUT_SLOTS_X, OUTPUT_SLOTS_Y, 3, 3)
				.build();
		slotsBlazePowder = new SlotRange<>(0);
		slotsBottle = new SlotRange<>(1, 9);
		slotsReagent = new SlotRange<>(10, 13);
		slotsOutput = new SlotRange<>(14, 22);
		
		List<ConfigurableFluidStack> fluidStacks = Arrays.asList(
				ConfigurableFluidStack.lockedInputSlot(20, EIFluids.BLAZING_ESSENCE.asFluid()),
				ConfigurableFluidStack.lockedInputSlot(capacity, Fluids.WATER)
		);
		SlotPositions fluidPositions = new SlotPositions.Builder()
				.addSlot(BLAZING_ESSENCE_SLOT_X + 18, BLAZING_ESSENCE_SLOT_Y)
				.addSlot(WATER_SLOT_X, WATER_SLOT_Y)
				.build();
		slotsBlazingEssence = new SlotRange<>(0);
		slotsWater = new SlotRange<>(1);
		
		this.inventory = new MIInventory(itemStacks, fluidStacks, itemPositions, fluidPositions);
		
		this.energy = new EnergyComponent(this, 3200);
		this.insertable = energy.buildInsertable((tier) -> tier == CableTier.LV);
		this.redstoneControl = new RedstoneControlComponent();
		this.casing = new CasingComponent();
		this.upgrades = new UpgradeComponent();
		
		this.registerGuiComponent(new EnergyBar.Server(new EnergyBar.Parameters(STEAM_SLOT_X + 1, STEAM_SLOT_Y - 1), energy::getEu, energy::getCapacity));
		// TODO this.registerGuiComponent(new RecipeEfficiencyBar.Server(efficiencyBarParams, crafter));
		this.registerGuiComponent(new SlotPanel.Server(this)
				.withRedstoneControl(redstoneControl)
				.withUpgrades(upgrades)
				.withCasing(casing));
		
		this.registerComponents(inventory, energy, redstoneControl, casing, upgrades);
	}
	
	@Override
	protected long consumeEu(long max)
	{
		return redstoneControl.doAllowNormalOperation(this) ? energy.consumeEu(max, Simulation.ACT) : 0;
	}
	
	@Override
	public MIInventory getInventory()
	{
		return inventory;
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData();
		data.isActive = isActiveComponent.isActive;
		orientation.writeModelData(data);
		return data;
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) -> ((ElectricBreweryMachineBlockEntity) be).insertable));
	}
}
