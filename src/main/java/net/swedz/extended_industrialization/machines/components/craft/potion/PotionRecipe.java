package net.swedz.extended_industrialization.machines.components.craft.potion;

import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.storage.StorageView;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import net.swedz.extended_industrialization.datamaps.PotionBrewingCosts;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class PotionRecipe
{
	private final ResourceLocation id;
	private final ItemStack        input;
	private final Ingredient       reagent;
	private final ItemStack        output;
	private final Potion           potion;
	
	private PotionBrewingCosts costs;
	private List<PotionRecipe> chain;
	
	public PotionRecipe(ResourceLocation id, ItemStack input, Ingredient reagent, ItemStack output, Potion potion)
	{
		this.id = id;
		this.input = input;
		this.reagent = reagent;
		this.output = output;
		this.potion = potion;
	}
	
	public ResourceLocation id()
	{
		return id;
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
	
	public List<PotionRecipe> subchain(List<StorageView<ItemVariant>> truncatedReagentItems)
	{
		// We can skip any logic if the chain is too small to match the reagents
		if(this.chain().size() < truncatedReagentItems.size())
		{
			return List.of();
		}
		
		// Grab the tail end of the recipe's chain
		int subchainStartIndex = this.chain().size() - truncatedReagentItems.size();
		int subchainEndIndex = this.chain().size();
		List<PotionRecipe> subchain = this.chain().subList(subchainStartIndex, subchainEndIndex);
		
		// Compare all of the reagent inputs with this recipe's chain
		for(int reagentIndex = 0; reagentIndex < truncatedReagentItems.size(); reagentIndex++)
		{
			StorageView<ItemVariant> reagent = truncatedReagentItems.get(reagentIndex);
			ItemStack reagentStack = reagent.getResource().toStack();
			PotionRecipe subrecipe = subchain.get(reagentIndex);
			if(!subrecipe.reagent().test(reagentStack))
			{
				return List.of();
			}
		}
		
		return Collections.unmodifiableList(subchain);
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
	
	private static Map<ResourceLocation, PotionRecipe> RECIPE_MAP = Maps.newHashMap();
	private static List<PotionRecipe>                  RECIPES    = Lists.newArrayList();
	
	private static String subId(ResourceLocation location)
	{
		return "%s/%s".formatted(location.getNamespace(), location.getPath());
	}
	
	private static String subId(Item item)
	{
		return subId(BuiltInRegistries.ITEM.getKey(item));
	}
	
	private static String subId(ItemStack stack)
	{
		return subId(stack.getItem());
	}
	
	private static String subId(Ingredient ingredient)
	{
		return subId(ingredient.getItems()[0]);
	}
	
	private static String subId(Potion potion)
	{
		return subId(BuiltInRegistries.POTION.getKey(potion));
	}
	
	private static Map<ResourceLocation, PotionRecipe> fetchRecipes()
	{
		Map<ResourceLocation, PotionRecipe> recipes = Maps.newHashMap();
		
		// Vanilla potion recipes
		for(Ingredient allowedContainer : PotionBrewing.ALLOWED_CONTAINERS)
		{
			for(ItemStack stack : allowedContainer.getItems())
			{
				String stackId = subId(stack);
				for(PotionBrewing.Mix<Potion> mix : PotionBrewing.POTION_MIXES)
				{
					if(mix.ingredient.getItems().length == 0)
					{
						continue;
					}
					
					String reagentId = subId(mix.ingredient);
					String inputId = subId(mix.from);
					String outputId = subId(mix.to);
					
					ResourceLocation id = EI.id("brewing/container/%s/%s/%s/%s".formatted(stackId, reagentId, inputId, outputId));
					if(recipes.containsKey(id))
					{
						throw new IllegalStateException("Generated duplicate potion recipe id %s".formatted(id));
					}
					recipes.put(id, new PotionRecipe(
							id,
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
			
			String reagentId = subId(mix.ingredient);
			String inputId = subId(mix.from);
			String outputId = subId(mix.to);
			
			Consumer<Holder<Potion>> recipeGen = (entry) ->
			{
				Potion potion = entry.value();
				if(potion == Potions.EMPTY || !PotionBrewing.isBrewablePotion(potion))
				{
					return;
				}
				ResourceLocation id = EI.id("brewing/item/%s/%s/%s/%s".formatted(subId(entry.unwrapKey().orElseThrow().location()), reagentId, inputId, outputId));
				if(recipes.containsKey(id))
				{
					throw new IllegalStateException("Generated duplicate potion recipe id %s".formatted(id));
				}
				recipes.put(id, new PotionRecipe(
						id,
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
				
				String reagentId = subId(recipe.getIngredient());
				String inputId = subId(stack);
				String outputId = subId(output);
				
				ResourceLocation id = EI.id("brewing/neoforge/%s/%s/%s".formatted(inputId, reagentId, outputId));
				if(recipes.containsKey(id))
				{
					throw new IllegalStateException("Generated duplicate potion recipe id %s".formatted(id));
				}
				recipes.put(id, new PotionRecipe(
						id,
						stack.copy(),
						recipe.getIngredient(),
						output,
						potion
				));
			}
		}
		
		return recipes;
	}
	
	public static List<PotionRecipe> getRecipes()
	{
		return RECIPES;
	}
	
	public static PotionRecipe getRecipe(ResourceLocation id)
	{
		return RECIPE_MAP.get(id);
	}
	
	public static void init()
	{
		RECIPE_MAP = fetchRecipes();
		RECIPES = Collections.unmodifiableList(Lists.newArrayList(RECIPE_MAP.values()));
		
		EI.LOGGER.info("Generated {} potion recipes with their chains successfully", getRecipes().size());
		
		// printDebug();
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
