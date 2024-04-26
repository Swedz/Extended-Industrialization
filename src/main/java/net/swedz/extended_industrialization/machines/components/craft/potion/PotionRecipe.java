package net.swedz.extended_industrialization.machines.components.craft.potion;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIItemStorage;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.storage.StorageView;
import com.google.common.collect.Lists;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.api.MachineInventoryHelper;
import net.swedz.extended_industrialization.datamaps.PotionBrewingCosts;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class PotionRecipe
{
	private final ItemStack  input;
	private final Ingredient reagent;
	private final ItemStack  output;
	private final Potion     potion;
	
	private PotionBrewingCosts costs;
	private List<PotionRecipe> chain;
	
	public PotionRecipe(ItemStack input, Ingredient reagent, ItemStack output, Potion potion)
	{
		this.input = input;
		this.reagent = reagent;
		this.output = output;
		this.potion = potion;
	}
	
	public ItemStack input()
	{
		return input;
	}
	
	public Ingredient reagent()
	{
		return reagent;
	}
	
	public ItemStack output()
	{
		return output;
	}
	
	public Potion potion()
	{
		return potion;
	}
	
	private PotionBrewingCosts costs()
	{
		if(costs == null)
		{
			costs = PotionBrewingCosts.getFor(potion);
		}
		return costs;
	}
	
	public int bottles()
	{
		return this.costs().bottles();
	}
	
	public int water()
	{
		return this.costs().water();
	}
	
	public int blazingEssence()
	{
		return this.costs().blazingEssence();
	}
	
	public int time()
	{
		return this.costs().time();
	}
	
	public int euCost()
	{
		return this.costs().euCost();
	}
	
	public int totalEuCost()
	{
		return this.costs().totalEuCost();
	}
	
	private List<PotionRecipe> generateChain(List<PotionRecipe> recipes)
	{
		recipes.add(0, this);
		for(PotionRecipe parent : getRecipes())
		{
			if(ItemStack.isSameItemSameTags(input, parent.output))
			{
				return parent.generateChain(recipes);
			}
		}
		return recipes;
	}
	
	private List<PotionRecipe> generateChain()
	{
		return Collections.unmodifiableList(this.generateChain(Lists.newArrayList()));
	}
	
	public List<PotionRecipe> chain()
	{
		if(chain == null)
		{
			chain = this.generateChain();
		}
		return chain;
	}
	
	public boolean chainMatchesReagentsExactly(MIItemStorage storage)
	{
		List<StorageView<ItemVariant>> storageList = Lists.newArrayList(storage.iterator());
		
		// Remove trailing empty slots
		ListIterator<StorageView<ItemVariant>> reverseStorageIterator = storageList.listIterator(storageList.size());
		while(reverseStorageIterator.hasPrevious())
		{
			ConfigurableItemStack item = (ConfigurableItemStack) reverseStorageIterator.previous();
			if(MachineInventoryHelper.isActuallyJustAir(item))
			{
				reverseStorageIterator.remove();
			}
			else
			{
				break;
			}
		}
		
		// Make sure the reagent list is the same size as the chain list
		if(storageList.size() != this.chain().size())
		{
			return false;
		}
		
		// Check to make sure the reagents match the recipe chain's reagents
		Iterator<StorageView<ItemVariant>> storageIterator = storageList.iterator();
		for(PotionRecipe recipe : this.chain())
		{
			if(!storageIterator.hasNext())
			{
				return false;
			}
			
			ConfigurableItemStack item = (ConfigurableItemStack) storageIterator.next();
			if(MachineInventoryHelper.isActuallyJustAir(item))
			{
				return false;
			}
			ItemStack reagent = MachineInventoryHelper.toActualItemStack(item);
			
			if(!recipe.reagent().test(reagent))
			{
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this) return true;
		if(obj == null || obj.getClass() != this.getClass()) return false;
		PotionRecipe other = (PotionRecipe) obj;
		return ItemStack.isSameItemSameTags(input, other.input) &&
			   Objects.equals(reagent, other.reagent) &&
			   ItemStack.isSameItemSameTags(output, other.output);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(input, reagent, output);
	}
	
	private static List<PotionRecipe> RECIPES = Lists.newArrayList();
	
	private static List<PotionRecipe> fetchRecipes()
	{
		List<PotionRecipe> recipes = Lists.newArrayList();
		
		// Vanilla potion recipes
		for(Ingredient allowedContainer : PotionBrewing.ALLOWED_CONTAINERS)
		{
			for(ItemStack stack : allowedContainer.getItems())
			{
				for(PotionBrewing.Mix<Potion> mix : PotionBrewing.POTION_MIXES)
				{
					if(mix.ingredient.getItems().length == 0)
					{
						continue;
					}
					
					recipes.add(new PotionRecipe(
							PotionUtils.setPotion(stack.copy(), mix.from),
							mix.ingredient,
							PotionUtils.setPotion(stack.copy(), mix.to),
							mix.to
					));
				}
			}
		}
		
		// Vanilla container (like splash, lingering, etc.) recipes
		for(PotionBrewing.Mix<Item> mix : PotionBrewing.CONTAINER_MIXES)
		{
			if(mix.ingredient.getItems().length == 0)
			{
				continue;
			}
			
			Consumer<Holder<Potion>> recipeGen = (entry) ->
			{
				Potion potion = entry.value();
				if(potion == Potions.EMPTY || !PotionBrewing.isBrewablePotion(potion))
				{
					return;
				}
				recipes.add(new PotionRecipe(
						PotionUtils.setPotion(new ItemStack(mix.from), potion),
						mix.ingredient,
						PotionUtils.setPotion(new ItemStack(mix.to), potion),
						potion
				));
			};
			
			if(mix.from instanceof PotionItem)
			{
				BuiltInRegistries.POTION.holders().forEach(recipeGen);
			}
			else
			{
				recipeGen.accept(BuiltInRegistries.POTION.wrapAsHolder(Potions.AWKWARD));
			}
		}
		
		// Modded recipes
		for(IBrewingRecipe brewingRecipe : BrewingRecipeRegistry.getRecipes())
		{
			if(!(brewingRecipe instanceof BrewingRecipe recipe))
			{
				continue;
			}
			
			for(ItemStack stack : recipe.getInput().getItems())
			{
				ItemStack output = recipe.getOutput(stack, recipe.getIngredient().getItems()[0]);
				Potion potion = PotionUtils.getPotion(output);
				if(potion == Potions.EMPTY)
				{
					EI.LOGGER.warn("Found modded potion recipe with invalid potion output");
					continue;
				}
				recipes.add(new PotionRecipe(
						stack.copy(),
						recipe.getIngredient(),
						output,
						potion
				));
			}
		}
		
		// Sort the recipes so that viewing debug info is easier
		recipes.sort(Comparator.comparing((recipe) -> BuiltInRegistries.POTION.getKey(recipe.potion())));
		
		return recipes;
	}
	
	public static List<PotionRecipe> getRecipes()
	{
		return RECIPES;
	}
	
	public static void init()
	{
		RECIPES = Collections.unmodifiableList(fetchRecipes());
		
		printDebug();
	}
	
	private static void printDebug()
	{
		Function<Potion, ResourceLocation> potionId = BuiltInRegistries.POTION::getKey;
		EI.LOGGER.info("Cached {} recipes into the potion recipe cache", getRecipes().size());
		for(PotionRecipe recipe : getRecipes())
		{
			EI.LOGGER.info(
					"- {}: input=({}), reagent=({}), output=({}), chain={}",
					potionId.apply(recipe.potion()),
					potionId.apply(PotionUtils.getPotion(recipe.input())),
					recipe.reagent().getItems()[0],
					potionId.apply(PotionUtils.getPotion(recipe.output())),
					recipe.chain().size()
			);
		}
	}
}
