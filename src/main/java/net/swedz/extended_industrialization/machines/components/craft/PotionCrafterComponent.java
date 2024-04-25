package net.swedz.extended_industrialization.machines.components.craft;

import aztech.modern_industrialization.api.machine.component.CrafterAccess;
import aztech.modern_industrialization.api.machine.component.InventoryAccess;
import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.components.MachineInventoryComponent;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Maps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.api.MachineInventoryHelper;
import net.swedz.extended_industrialization.datamaps.PotionBrewing;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class PotionCrafterComponent implements IComponent.ServerOnly, CrafterAccess
{
	private final Params params;
	private final MachineInventoryComponent inventory;
	private final Behavior behavior;
	
	private BrewingPickRecipeResult processResult;
	
	private long usedEnergy;
	
	private int efficiencyTicks;
	private int maxEfficiencyTicks;
	
	public PotionCrafterComponent(Params params, MachineInventoryComponent inventory, Behavior behavior)
	{
		this.params = params;
		this.inventory = inventory;
		this.behavior = behavior;
	}
	
	public interface Behavior
	{
		default boolean isEnabled()
		{
			return true;
		}
		
		long consumeEu(long max, Simulation simulation);
		
		default boolean canConsumeEu(long amount)
		{
			return this.consumeEu(amount, Simulation.SIMULATE) == amount;
		}
		
		long getBaseRecipeEu();
		
		long getMaxRecipeEu();
	}
	
	@Override
	public InventoryAccess getInventory()
	{
		return inventory;
	}
	
	@Override
	public boolean hasActiveRecipe()
	{
		return processResult != null;
	}
	
	@Override
	public float getProgress()
	{
		return processResult != null ? (float) usedEnergy / processResult.recipe().totalEuCost() : 0;
	}
	
	@Override
	public int getEfficiencyTicks()
	{
		return efficiencyTicks;
	}
	
	@Override
	public int getMaxEfficiencyTicks()
	{
		return maxEfficiencyTicks;
	}
	
	public void decreaseEfficiencyTicks()
	{
		efficiencyTicks = Math.max(efficiencyTicks - 1, 0);
		this.clearActiveRecipeIfPossible();
	}
	
	public void increaseEfficiencyTicks(int increment)
	{
		efficiencyTicks = Math.min(efficiencyTicks + increment, maxEfficiencyTicks);
	}
	
	@Override
	public long getBaseRecipeEu()
	{
		return processResult.recipe().euCost();
	}
	
	@Override
	public long getCurrentRecipeEu()
	{
		return processResult.recipe().totalEuCost();
	}
	
	private void clearActiveRecipeIfPossible()
	{
		if(efficiencyTicks == 0 && usedEnergy == 0)
		{
			processResult = null;
		}
	}
	
	private Optional<BrewingPickRecipeResult> pick()
	{
		// Check if there's a recipe for these reagents and capture all of the recipe steps
		List<IBrewingRecipe> recipeSteps = Lists.newArrayList();
		List<ItemStack> recipeReagentStacks = Lists.newArrayList();
		List<ConfigurableItemStack> resultReagentSlotsToRemoveFrom = Lists.newArrayList();
		for(ConfigurableItemStack slotReagent : params.reagent().slots(inventory.getItemInputs()))
		{
			ItemStack slotReagentStack = slotReagent.toStack();
			if(slotReagent.isEmpty() || slotReagentStack.isEmpty())
			{
				continue;
			}
			for(IBrewingRecipe otherRecipe : BrewingRecipeRegistry.getRecipes())
			{
				if(otherRecipe.isIngredient(slotReagentStack))
				{
					recipeSteps.add(otherRecipe);
					recipeReagentStacks.add(slotReagentStack);
					resultReagentSlotsToRemoveFrom.add(slotReagent);
					break;
				}
			}
		}
		if(recipeSteps.size() == 0)
		{
			return Optional.empty();
		}
		
		// Find the slots that can be used for outputting
		List<ConfigurableItemStack> resultOutputSlotsToAddTo = Lists.newArrayList();
		for(ConfigurableItemStack slot : params.output().slots(inventory.getItemOutputs()))
		{
			if(slot.isEmpty())
			{
				resultOutputSlotsToAddTo.add(slot);
			}
		}
		
		// Check all of the slots
		outer:
		for(ConfigurableItemStack slot : params.bottle().slots(inventory.getItemInputs()))
		{
			// Get this bottle input
			ItemStack slotStack = slot.toStack();
			if(slot.isEmpty() || slotStack.isEmpty())
			{
				continue;
			}
			
			// Transform this bottle stack and check if we need to consume water to do so
			ItemStack slotStackConverted;
			boolean resultConsumeWater = false;
			if(slotStack.is(Items.GLASS_BOTTLE))
			{
				slotStackConverted = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
				resultConsumeWater = true;
			}
			else
			{
				slotStackConverted = slotStack.copy();
			}
			
			// Check if there's a recipe for the reagents given this bottle input
			// If there are multiple reagents, it will iterate over them and find the final one and generate the output itemstack for it
			// If there is a reagent that does not have a valid recipe with this bottle input in the correct sequential order, the recipe will fail to be picked
			ItemStack resultOutput = slotStackConverted.copy();
			int recipeIndex = 0;
			for(IBrewingRecipe recipe : recipeSteps)
			{
				ItemStack slotReagentStack = recipeReagentStacks.get(recipeIndex);
				if(recipe.isInput(resultOutput))
				{
					resultOutput = recipe.getOutput(resultOutput, slotReagentStack);
				}
				else
				{
					continue outer;
				}
				recipeIndex++;
			}
			Potion potion = PotionUtils.getPotion(resultOutput);
			PotionBrewing potionBrewingData = PotionBrewing.getFor(potion);
			if(potionBrewingData == null)
			{
				potionBrewingData = PotionBrewing.getFor(Potions.EMPTY);
				if(potionBrewingData == null)
				{
					EI.LOGGER.warn("Failed to fetch potion brewing data from datamap for default potion! Perhaps the default was overridden with nothing? (operating on potion {})", BuiltInRegistries.POTION.getKey(potion));
					continue;
				}
			}
			
			// Make sure there are enough output slots for this recipe
			if(resultOutputSlotsToAddTo.size() < potionBrewingData.bottles())
			{
				continue;
			}
			
			// Make sure there is enough water for this recipe
			if(resultConsumeWater && potionBrewingData.water() > 0 && !MachineInventoryHelper.hasFluid(inventory.getFluidInputs(), Fluids.WATER, potionBrewingData.water()))
			{
				continue;
			}
			
			// Make sure there is enough blazing essence in the machine
			if(potionBrewingData.blazingEssence() > 0 && !MachineInventoryHelper.hasFluid(inventory.getFluidInputs(), EIFluids.BLAZING_ESSENCE, potionBrewingData.blazingEssence()))
			{
				continue;
			}
			
			// Make sure there's enough of the bottles in this input
			Map<Integer, Integer> resultBottleSlotsToRemoveFrom = Maps.newHashMap();
			int count = 0;
			boolean hasEnoughBottles = false;
			int bottleIndex = 0;
			for(ConfigurableItemStack otherSlot : params.bottle().slots(inventory.getItemInputs()))
			{
				ItemStack otherSlotStack = otherSlot.toStack();
				// We compare with the original stack so that other stacks are converted as well if need be
				if(ItemStack.isSameItemSameTags(slotStack, otherSlotStack))
				{
					int amountToRemove = Math.min((int) otherSlot.getAmount(), potionBrewingData.bottles() - count);
					count += amountToRemove;
					if(amountToRemove > 0)
					{
						resultBottleSlotsToRemoveFrom.put(bottleIndex, amountToRemove);
					}
					if(count >= potionBrewingData.bottles())
					{
						hasEnoughBottles = true;
						break;
					}
				}
				bottleIndex++;
			}
			if(!hasEnoughBottles)
			{
				continue;
			}
			
			return Optional.of(new BrewingPickRecipeResult(
					potionBrewingData,
					resultConsumeWater,
					resultBottleSlotsToRemoveFrom, resultReagentSlotsToRemoveFrom,
					resultOutput,
					resultOutputSlotsToAddTo
			));
		}
		
		return Optional.empty();
	}
	
	private boolean pickAndStart()
	{
		if(processResult != null)
		{
			return true;
		}
		
		if(!behavior.canConsumeEu(1))
		{
			return false;
		}
		
		Optional<BrewingPickRecipeResult> pick = this.pick();
		if(pick.isEmpty())
		{
			return false;
		}
		processResult = pick.get();
		
		if(processResult.recipe().blazingEssence() > 0)
		{
			MachineInventoryHelper.consumeFluid(inventory.getFluidInputs(), EIFluids.BLAZING_ESSENCE, processResult.recipe().blazingEssence(), Simulation.ACT);
		}
		if(processResult.consumeWater())
		{
			MachineInventoryHelper.consumeFluid(inventory.getFluidInputs(), Fluids.WATER, processResult.recipe().water(), Simulation.ACT);
		}
		List<ConfigurableItemStack> bottleSlots = params.bottle().slots(inventory.getItemInputs());
		for(Map.Entry<Integer, Integer> entry : processResult.bottleSlotsToRemoveFrom().entrySet())
		{
			bottleSlots.get(entry.getKey()).decrement(entry.getValue());
		}
		for(ConfigurableItemStack slot : processResult.reagentSlotsToRemoveFrom())
		{
			slot.decrement(1);
		}
		
		return true;
	}
	
	private void doBlazeEssenceStuff()
	{
		ConfigurableItemStack slotPowder = params.blazePowder().slot(inventory.getItemInputs());
		if(slotPowder.getAmount() == 0)
		{
			return;
		}
		
		ConfigurableFluidStack slotEssence = params.blazingEssence().slot(inventory.getFluidInputs());
		if(slotEssence.getAmount() > 0)
		{
			return;
		}
		
		slotPowder.decrement(1);
		slotEssence.increment(20);
	}
	
	public boolean tickRecipe()
	{
		this.doBlazeEssenceStuff();
		
		if(!this.pickAndStart())
		{
			return false;
		}
		
		boolean active = false;
		long amountToAdd = Math.min(this.getCurrentRecipeEu() - usedEnergy, processResult.recipe().euCost()); // TODO use overclocking
		if(behavior.canConsumeEu(amountToAdd))
		{
			usedEnergy += behavior.consumeEu(amountToAdd, Simulation.ACT);
			active = true;
		}
		
		if(usedEnergy == this.getCurrentRecipeEu())
		{
			processResult.push();
			
			processResult = null;
			usedEnergy = 0;
		}
		
		return active;
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
		tag.putLong("usedEnergy", usedEnergy);
		// TODO store active recipe
		/*if(activeRecipe != null)
		{
			tag.putString("activeRecipe", this.activeRecipe.id().toString());
		}
		else if(delayedActiveRecipe != null)
		{
			tag.putString("activeRecipe", this.delayedActiveRecipe.toString());
		}*/
		tag.putInt("efficiencyTicks", efficiencyTicks);
		tag.putInt("maxEfficiencyTicks", maxEfficiencyTicks);
	}
	
	@Override
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
		usedEnergy = tag.getInt("usedEnergy");
		// TODO read active recipe
		/*this.delayedActiveRecipe = tag.contains("activeRecipe") ? new ResourceLocation(tag.getString("activeRecipe")) : null;
		if(delayedActiveRecipe == null && usedEnergy > 0)
		{
			usedEnergy = 0;
			MI.LOGGER.error("Had to set the usedEnergy of CrafterComponent to 0, but that should never happen!");
		}*/
		efficiencyTicks = tag.getInt("efficiencyTicks");
		maxEfficiencyTicks = tag.getInt("maxEfficiencyTicks");
	}
	
	private record BrewingPickRecipeResult(
			PotionBrewing recipe,
			boolean consumeWater,
			Map<Integer, Integer> bottleSlotsToRemoveFrom,
			List<ConfigurableItemStack> reagentSlotsToRemoveFrom,
			ItemStack output,
			List<ConfigurableItemStack> outputSlotsToAddTo
	)
	{
		public void push()
		{
			// TODO use PotionCrafterComponent.inventory.getItemOutputs() so that each potion isnt forced to go to its own slot if they're stackable
			for(int i = 0; i < recipe.bottles(); i++)
			{
				ConfigurableItemStack slot = outputSlotsToAddTo.get(i);
				slot.setKey(ItemVariant.of(output.copy()));
				slot.setAmount(1);
			}
		}
	}
	
	public record SlotRange<T extends AbstractConfigurableStack>(int start, int end)
	{
		public static SlotRange<ConfigurableItemStack> item(int start, int end)
		{
			return new SlotRange<>(start, end);
		}
		
		public static SlotRange<ConfigurableItemStack> item(int slot)
		{
			return new SlotRange<>(slot);
		}
		
		public static SlotRange<ConfigurableFluidStack> fluid(int start, int end)
		{
			return new SlotRange<>(start, end);
		}
		
		public static SlotRange<ConfigurableFluidStack> fluid(int slot)
		{
			return new SlotRange<>(slot);
		}
		
		public SlotRange(int slot)
		{
			this(slot, slot);
		}
		
		public List<T> slots(List<T> slots)
		{
			return slots.subList(start, end + 1);
		}
		
		public T slot(List<T> slots)
		{
			return this.slots(slots).get(0);
		}
	}
	
	public record Params(
			SlotRange<ConfigurableItemStack> blazePowder,
			SlotRange<ConfigurableItemStack> bottle,
			SlotRange<ConfigurableItemStack> reagent,
			// TODO replace with inventory.getItemOutputs()
			SlotRange<ConfigurableItemStack> output,
			SlotRange<ConfigurableFluidStack> blazingEssence,
			SlotRange<ConfigurableFluidStack> water
	)
	{
	}
}
