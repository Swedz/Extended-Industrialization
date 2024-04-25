package net.swedz.extended_industrialization.machines.blockentities.brewery;

import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.AutoExtract;
import aztech.modern_industrialization.machines.guicomponents.ProgressBar;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.util.Simulation;
import aztech.modern_industrialization.util.Tickable;
import com.google.common.collect.Maps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.swedz.extended_industrialization.api.MachineInventoryHelper;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;
import org.apache.commons.compress.utils.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BreweryMachineBlockEntity extends MachineBlockEntity implements Tickable
{
	protected static final int WATER_COST = 1000;
	protected static final int POTIONS_PER_RECIPE = 4;
	protected static final int BREW_TIME = 5 * 20;
	
	protected static final int STEAM_SLOT_X = 143;
	protected static final int STEAM_SLOT_Y = 36;
	
	protected static final int BLAZING_ESSENCE_SLOT_X = 8;
	protected static final int BLAZING_ESSENCE_SLOT_Y = 17;
	
	protected static final int WATER_SLOT_X = 26;
	protected static final int WATER_SLOT_Y = 39;
	
	protected static final int INPUT_BOTTLE_SLOTS_X = 8;
	protected static final int INPUT_BOTTLE_SLOTS_Y = 64;
	
	protected static final int INPUT_REAGENT_SLOTS_X = 53;
	protected static final int INPUT_REAGENT_SLOTS_Y = 36;
	
	protected static final int OUTPUT_SLOTS_X = 116;
	protected static final int OUTPUT_SLOTS_Y = 64;
	
	protected final long euCost;
	protected final int  capacity;
	
	protected SlotRange<ConfigurableItemStack> slotsBlazePowder;
	protected SlotRange<ConfigurableItemStack> slotsBottle;
	protected SlotRange<ConfigurableItemStack> slotsReagent;
	protected SlotRange<ConfigurableItemStack> slotsOutput;
	
	protected SlotRange<ConfigurableFluidStack> slotsBlazingEssence;
	protected SlotRange<ConfigurableFluidStack> slotsWater;
	
	protected BrewingPickRecipeResult processResult;
	protected int                     processTicks;
	
	protected IsActiveComponent isActiveComponent;
	
	public BreweryMachineBlockEntity(BEP bep, String blockName, long euCost, int capacity)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(blockName, true).backgroundHeight(215).build(),
				new OrientationComponent.Params(true, true, false)
		);
		
		this.euCost = euCost;
		this.capacity = capacity;
		
		this.isActiveComponent = new IsActiveComponent();
		this.registerGuiComponent(new ProgressBar.Server(
				new ProgressBar.Parameters(78, 80, "triple_arrow"),
				() -> (float) processTicks / BREW_TIME
		));
		
		this.registerComponents(isActiveComponent);
		
		this.registerGuiComponent(new AutoExtract.Server(orientation));
	}
	
	protected abstract long consumeEu(long max);
	
	protected boolean hasEnoughBlazingEssence()
	{
		return MachineInventoryHelper.hasFluid(this.getInventory().getFluidStacks(), EIFluids.BLAZING_ESSENCE, 1);
	}
	
	protected boolean hasEnoughWater()
	{
		return MachineInventoryHelper.hasFluid(this.getInventory().getFluidStacks(), Fluids.WATER, WATER_COST);
	}
	
	private Optional<BrewingPickRecipeResult> pick()
	{
		// Make sure there is blazing essence in the machine
		if(!this.hasEnoughBlazingEssence())
		{
			return Optional.empty();
		}
		
		// Find the slots that can be used for outputting
		List<ConfigurableItemStack> resultOutputSlotsToAddTo = Lists.newArrayList();
		for(ConfigurableItemStack slot : this.slotsOutput.slots(this.getInventory().getItemStacks()))
		{
			if(slot.isEmpty())
			{
				resultOutputSlotsToAddTo.add(slot);
			}
		}
		if(resultOutputSlotsToAddTo.size() < POTIONS_PER_RECIPE)
		{
			return Optional.empty();
		}
		
		// Check all of the slots
		// TODO optimize this by not checking the same slot with item in it
		for(ConfigurableItemStack slot : this.slotsBottle.slots(this.getInventory().getItemStacks()))
		{
			// Get this bottle input
			ItemStack slotStack = slot.toStack();
			if(slot.isEmpty() || slotStack.isEmpty())
			{
				continue;
			}
			
			// Make sure there's enough of the bottles in this input
			Map<ConfigurableItemStack, Integer> resultBottleSlotsToRemoveFrom = Maps.newHashMap();
			int count = 0;
			boolean hasEnoughBottles = false;
			for(ConfigurableItemStack otherSlot : this.slotsBottle.slots(this.getInventory().getItemStacks()))
			{
				ItemStack otherSlotStack = otherSlot.toStack();
				if(ItemStack.isSameItemSameTags(slotStack, otherSlotStack))
				{
					int amountToRemove = Math.min((int) otherSlot.getAmount(), POTIONS_PER_RECIPE - count);
					count += amountToRemove;
					if(amountToRemove > 0)
					{
						resultBottleSlotsToRemoveFrom.put(otherSlot, amountToRemove);
					}
					if(count >= POTIONS_PER_RECIPE)
					{
						hasEnoughBottles = true;
						break;
					}
				}
			}
			if(!hasEnoughBottles)
			{
				continue;
			}
			
			// Transform this bottle stack and check if we need to consume water to do so
			// This is done *after* the comparisons to make sure that the item matches work properly
			// TODO allow mismatch potions, like 2 glass bottles and 2 water bottles and it'll only consume half the water
			boolean resultConsumeWater = false;
			if(slotStack.is(Items.GLASS_BOTTLE))
			{
				slotStack = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
				resultConsumeWater = true;
				if(!MachineInventoryHelper.hasFluid(this.getInventory().getFluidStacks(), Fluids.WATER, WATER_COST))
				{
					continue;
				}
			}
			
			// Check if there's a recipe for these reagents given this bottle input
			// If there are multiple reagents, it will iterate over them and find the final one and generate the output itemstack for it
			List<ConfigurableItemStack> resultReagentSlotsToRemoveFrom = Lists.newArrayList();
			ItemStack resultOutput = slotStack.copy();
			boolean foundRecipe = false;
			for(ConfigurableItemStack slotReagent : this.slotsReagent.slots(this.getInventory().getItemStacks()))
			{
				ItemStack slotReagentStack = slotReagent.toStack();
				if(slotReagent.isEmpty() || slotReagentStack.isEmpty())
				{
					continue;
				}
				for(IBrewingRecipe otherRecipe : BrewingRecipeRegistry.getRecipes())
				{
					if(otherRecipe.isInput(resultOutput) && otherRecipe.isIngredient(slotReagentStack))
					{
						foundRecipe = true;
						resultReagentSlotsToRemoveFrom.add(slotReagent);
						resultOutput = otherRecipe.getOutput(resultOutput, slotReagentStack);
						break;
					}
				}
			}
			if(!foundRecipe)
			{
				continue;
			}
			
			return Optional.of(new BrewingPickRecipeResult(
					resultConsumeWater,
					resultBottleSlotsToRemoveFrom, resultReagentSlotsToRemoveFrom,
					resultOutput, POTIONS_PER_RECIPE,
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
		
		Optional<BrewingPickRecipeResult> pick = this.pick();
		if(pick.isEmpty())
		{
			return false;
		}
		processResult = pick.get();
		
		MachineInventoryHelper.consumeFluid(this.getInventory().getFluidStacks(), EIFluids.BLAZING_ESSENCE, 1, Simulation.ACT);
		if(processResult.consumeWater())
		{
			MachineInventoryHelper.consumeFluid(this.getInventory().getFluidStacks(), Fluids.WATER, WATER_COST, Simulation.ACT);
		}
		for(Map.Entry<ConfigurableItemStack, Integer> entry : processResult.bottleSlotsToRemoveFrom().entrySet())
		{
			entry.getKey().decrement(entry.getValue());
		}
		for(ConfigurableItemStack slot : processResult.reagentSlotsToRemoveFrom())
		{
			slot.decrement(1);
		}
		
		return true;
	}
	
	private void doBlazeEssenceStuff()
	{
		ConfigurableItemStack slotPowder = slotsBlazePowder.slot(this.getInventory().getItemStacks());
		if(slotPowder.getAmount() == 0)
		{
			return;
		}
		
		ConfigurableFluidStack slotEssence = slotsBlazingEssence.slot(this.getInventory().getFluidStacks());
		if(slotEssence.getAmount() > 0)
		{
			return;
		}
		
		slotPowder.decrement(1);
		slotEssence.increment(20);
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide)
		{
			return;
		}
		
		this.doBlazeEssenceStuff();
		
		if(!this.pickAndStart())
		{
			this.updateActive(false);
			return;
		}
		
		long eu = this.consumeEu(euCost);
		boolean active = eu > 0;
		processTicks += active ? 1 : 0;
		this.updateActive(active);
		
		// TODO use overclocking for electric machine
		if(processTicks == BREW_TIME)
		{
			processResult.push();
			
			processResult = null;
			processTicks = 0;
		}
		
		if(orientation.extractItems)
		{
			this.getInventory().autoExtractItems(level, worldPosition, orientation.outputDirection);
		}
		this.setChanged();
	}
	
	private void updateActive(boolean active)
	{
		isActiveComponent.updateActive(active, this);
	}
	
	@Override
	public List<Component> getTooltips()
	{
		return Collections.emptyList();
	}
	
	public record SlotRange<T extends AbstractConfigurableStack>(int start, int end)
	{
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
	
	public record BrewingPickRecipeResult(
			boolean consumeWater,
			Map<ConfigurableItemStack, Integer> bottleSlotsToRemoveFrom,
			List<ConfigurableItemStack> reagentSlotsToRemoveFrom,
			ItemStack output, int outputCount,
			List<ConfigurableItemStack> outputSlotsToAddTo
	)
	{
		public void push()
		{
			// TODO output in a way where it supports mods who make potions stackable
			for(int i = 0; i < outputCount; i++)
			{
				ConfigurableItemStack slot = outputSlotsToAddTo.get(i);
				slot.setKey(ItemVariant.of(output.copy()));
				slot.setAmount(1);
			}
		}
	}
}