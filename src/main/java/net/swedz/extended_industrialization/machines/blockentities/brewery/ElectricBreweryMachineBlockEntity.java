package net.swedz.extended_industrialization.machines.blockentities.brewery;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.MachineInventoryComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.util.Simulation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.machines.components.craft.PotionCrafterComponent;
import net.swedz.extended_industrialization.machines.components.craft.PotionCrafterComponent.SlotRange;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;

public final class ElectricBreweryMachineBlockEntity extends BreweryMachineBlockEntity
{
	private final EnergyComponent energy;
	private final MIEnergyStorage insertable;
	private final RedstoneControlComponent redstoneControl;
	private final CasingComponent casing;
	private final UpgradeComponent upgrades;
	
	public ElectricBreweryMachineBlockEntity(BEP bep)
	{
		super(bep, "electric_brewery", MachineTier.LV, 32 * FluidType.BUCKET_VOLUME);
		
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
		
		this.registerComponents(energy, redstoneControl, casing, upgrades);
	}
	
	@Override
	protected Pair<MachineInventoryComponent, PotionCrafterComponent.Params> buildInventory()
	{
		List<ConfigurableItemStack> itemInputs = Lists.newArrayList();
		List<ConfigurableItemStack> itemOutputs = Lists.newArrayList();
		itemInputs.add(ConfigurableItemStack.lockedInputSlot(Items.BLAZE_POWDER));
		for(int i = 0; i < 9; i++)
		{
			itemInputs.add(ConfigurableItemStack.standardInputSlot());
		}
		for(int i = 0; i < 4; i++)
		{
			itemInputs.add(ConfigurableItemStack.standardInputSlot());
		}
		for(int i = 0; i < 9; i++)
		{
			itemOutputs.add(new PotionConfigurableItemStack());
		}
		SlotPositions itemPositions = new SlotPositions.Builder()
				.addSlot(BLAZING_ESSENCE_SLOT_X, BLAZING_ESSENCE_SLOT_Y)
				.addSlots(INPUT_BOTTLE_SLOTS_X, INPUT_BOTTLE_SLOTS_Y, 3, 3)
				.addSlots(INPUT_REAGENT_SLOTS_X, INPUT_REAGENT_SLOTS_Y, 4, 1)
				.addSlots(OUTPUT_SLOTS_X, OUTPUT_SLOTS_Y, 3, 3)
				.build();
		SlotRange<ConfigurableItemStack> slotsBlazePowder = SlotRange.item(0);
		SlotRange<ConfigurableItemStack> slotsBottle = SlotRange.item(1, 9);
		SlotRange<ConfigurableItemStack> slotsReagent = SlotRange.item(10, 13);
		SlotRange<ConfigurableItemStack> slotsOutput = SlotRange.item(0, 8);
		
		List<ConfigurableFluidStack> fluidInputs = Arrays.asList(
				ConfigurableFluidStack.lockedInputSlot(20, EIFluids.BLAZING_ESSENCE.asFluid()),
				ConfigurableFluidStack.lockedInputSlot(capacity, Fluids.WATER)
		);
		SlotPositions fluidPositions = new SlotPositions.Builder()
				.addSlot(BLAZING_ESSENCE_SLOT_X + 18, BLAZING_ESSENCE_SLOT_Y)
				.addSlot(WATER_SLOT_X, WATER_SLOT_Y)
				.build();
		SlotRange<ConfigurableFluidStack> slotsBlazingEssence = SlotRange.fluid(0);
		SlotRange<ConfigurableFluidStack> slotsWater = SlotRange.fluid(1);
		
		return Pair.of(
				new MachineInventoryComponent(itemInputs, itemOutputs, fluidInputs, List.of(), itemPositions, fluidPositions),
				new PotionCrafterComponent.Params(
						slotsBlazePowder, slotsBottle, slotsReagent, slotsOutput,
						slotsBlazingEssence, slotsWater
				)
		);
	}
	
	@Override
	public long consumeEu(long max, Simulation simulation)
	{
		return redstoneControl.doAllowNormalOperation(this) ? energy.consumeEu(max, simulation) : 0;
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) -> ((ElectricBreweryMachineBlockEntity) be).insertable));
	}
}
