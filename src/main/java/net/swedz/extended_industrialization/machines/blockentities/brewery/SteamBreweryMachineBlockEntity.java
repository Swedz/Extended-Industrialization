package net.swedz.extended_industrialization.machines.blockentities.brewery;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.helper.SteamHelper;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;

public final class SteamBreweryMachineBlockEntity extends BreweryMachineBlockEntity
{
	private final MIInventory inventory;
	
	public SteamBreweryMachineBlockEntity(BEP bep, boolean bronze)
	{
		super(bep, (bronze ? "bronze" : "steel") + "_brewery", bronze ? 2 : 4, (bronze ? 8 : 16) * FluidType.BUCKET_VOLUME);
		
		List<ConfigurableItemStack> itemStacks = Lists.newArrayList();
		itemStacks.add(ConfigurableItemStack.lockedInputSlot(Items.BLAZE_POWDER));
		for(int i = 0; i < 9; i++)
		{
			itemStacks.add(ConfigurableItemStack.standardInputSlot());
		}
		itemStacks.add(ConfigurableItemStack.standardInputSlot());
		for(int i = 0; i < 9; i++)
		{
			itemStacks.add(new PotionConfigurableItemStack());
		}
		SlotPositions itemPositions = new SlotPositions.Builder()
				.addSlot(BLAZING_ESSENCE_SLOT_X, BLAZING_ESSENCE_SLOT_Y)
				.addSlots(INPUT_BOTTLE_SLOTS_X, INPUT_BOTTLE_SLOTS_Y, 3, 3)
				.addSlot(INPUT_REAGENT_SLOTS_X + 18 + 9, INPUT_REAGENT_SLOTS_Y)
				.addSlots(OUTPUT_SLOTS_X, OUTPUT_SLOTS_Y, 3, 3)
				.build();
		slotsBlazePowder = new SlotRange<>(0);
		slotsBottle = new SlotRange<>(1, 9);
		slotsReagent = new SlotRange<>(10);
		slotsOutput = new SlotRange<>(11, 19);
		
		List<ConfigurableFluidStack> fluidStacks = Arrays.asList(
				ConfigurableFluidStack.lockedInputSlot(capacity, MIFluids.STEAM.asFluid()),
				ConfigurableFluidStack.lockedInputSlot(20, EIFluids.BLAZING_ESSENCE.asFluid()),
				ConfigurableFluidStack.lockedInputSlot(capacity, Fluids.WATER)
		);
		SlotPositions fluidPositions = new SlotPositions.Builder()
				.addSlot(STEAM_SLOT_X, STEAM_SLOT_Y)
				.addSlot(BLAZING_ESSENCE_SLOT_X + 18, BLAZING_ESSENCE_SLOT_Y)
				.addSlot(WATER_SLOT_X, WATER_SLOT_Y)
				.build();
		slotsBlazingEssence = new SlotRange<>(1);
		slotsWater = new SlotRange<>(2);
		
		this.inventory = new MIInventory(itemStacks, fluidStacks, itemPositions, fluidPositions);
		
		this.registerComponents(inventory);
	}
	
	@Override
	protected long consumeEu(long max)
	{
		return SteamHelper.consumeSteamEu(inventory.getFluidStacks(), max, Simulation.ACT);
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
}
