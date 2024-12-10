package net.swedz.extended_industrialization.machines.recipe;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.machines.recipe.ProxyableMachineRecipeType;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import com.google.common.collect.Lists;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.datagen.api.RecipeHelper;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class BreweryMachineRecipeType extends ProxyableMachineRecipeType
{
	private static String id(ResourceLocation location)
	{
		return "%s/%s".formatted(location.getNamespace(), location.getPath());
	}
	
	private static String id(Item item)
	{
		return id(BuiltInRegistries.ITEM.getKey(item));
	}
	
	private static String idItem(Holder<Item> item)
	{
		return id(item.value());
	}
	
	private static String id(ItemStack stack)
	{
		return id(stack.getItem());
	}
	
	private static String id(Ingredient ingredient)
	{
		return id(ingredient.getItems()[0]);
	}
	
	private static String idPotion(Holder<Potion> potion)
	{
		return id(potion.getKey().location());
	}
	
	public BreweryMachineRecipeType(ResourceLocation id)
	{
		super(id);
	}
	
	private RecipeHolder<MachineRecipe> generate(ResourceLocation id, Ingredient inputIngredient, Ingredient reagentIngredient, ItemStack outputStack)
	{
		MachineRecipeBuilder recipe = new MachineRecipeBuilder(this, 4, 5 * 20);
		
		recipe.addItemInput(inputIngredient, 4, 1f);
		recipe.addItemInput(reagentIngredient, 1, 1f);
		recipe.addFluidInput(EIFluids.BLAZING_ESSENCE, 1);
		recipe.addItemOutput(ItemVariant.of(outputStack), 4, 1f);
		
		return new RecipeHolder<>(id, RecipeHelper.getActualRecipe(recipe));
	}
	
	private RecipeHolder<MachineRecipe> generate(ResourceLocation id, ItemStack inputStack, Ingredient reagentIngredient, ItemStack outputStack)
	{
		return this.generate(id, DataComponentIngredient.of(false, inputStack), reagentIngredient, outputStack);
	}
	
	private RecipeHolder<MachineRecipe> generateMix(ItemStack stack, PotionBrewing.Mix<Potion> mix)
	{
		ItemStack inputStack = stack.copy();
		inputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(mix.from()));
		Ingredient reagentIngredient = mix.ingredient();
		ItemStack outputStack = stack.copy();
		outputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(mix.to()));
		
		ResourceLocation id = EI.id("brewery/generated/mix/%s/%s/%s/%s".formatted(
				id(stack),
				idPotion(mix.from()),
				id(reagentIngredient),
				idPotion(mix.to())
		));
		
		return this.generate(id, inputStack, reagentIngredient, outputStack);
	}
	
	private List<RecipeHolder<MachineRecipe>> buildMixes(Level level, PotionBrewing potionBrewing)
	{
		List<RecipeHolder<MachineRecipe>> recipes = Lists.newArrayList();
		
		for(Ingredient allowedContainer : potionBrewing.containers)
		{
			for(ItemStack stack : allowedContainer.getItems())
			{
				for(PotionBrewing.Mix<Potion> mix : potionBrewing.potionMixes)
				{
					if(mix.ingredient().getItems().length > 0)
					{
						recipes.add(this.generateMix(stack, mix));
					}
				}
			}
		}
		
		return recipes;
	}
	
	private RecipeHolder<MachineRecipe> generateContainer(PotionBrewing.Mix<Item> mix, Holder<Potion> potion)
	{
		ItemStack inputStack = new ItemStack(mix.from().value());
		inputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
		Ingredient reagentIngredient = mix.ingredient();
		ItemStack outputStack = new ItemStack(mix.to().value());
		outputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
		
		ResourceLocation id = EI.id("brewery/generated/container/%s/%s/%s/%s".formatted(
				id(potion.unwrapKey().orElseThrow().location()),
				idItem(mix.from()),
				id(reagentIngredient),
				idItem(mix.to())
		));
		
		return this.generate(id, inputStack, reagentIngredient, outputStack);
	}
	
	private List<RecipeHolder<MachineRecipe>> buildContainers(Level level, PotionBrewing potionBrewing)
	{
		List<RecipeHolder<MachineRecipe>> recipes = Lists.newArrayList();
		
		for(PotionBrewing.Mix<Item> mix : potionBrewing.containerMixes)
		{
			if(mix.ingredient().getItems().length > 0)
			{
				Consumer<Holder<Potion>> recipeGen = (entry) ->
				{
					Potion potion = entry.value();
					if(potion == null || !potionBrewing.isBrewablePotion(entry))
					{
						return;
					}
					recipes.add(this.generateContainer(mix, entry));
				};
				
				if(mix.from().value() instanceof PotionItem)
				{
					BuiltInRegistries.POTION.holders().forEach(recipeGen);
				}
				else
				{
					recipeGen.accept(BuiltInRegistries.POTION.wrapAsHolder(Potions.AWKWARD.value()));
				}
			}
		}
		
		return recipes;
	}
	
	private RecipeHolder<MachineRecipe> generateModded(ItemStack inputStack, Ingredient reagentIngredient, ItemStack outputStack)
	{
		ResourceLocation id = EI.id("brewery/generated/modded_improper/%s/%s/%s".formatted(
				id(inputStack),
				id(reagentIngredient),
				id(outputStack)
		));
		
		if(inputStack.has(DataComponents.POTION_CONTENTS) && outputStack.has(DataComponents.POTION_CONTENTS))
		{
			Optional<Holder<Potion>> inputPotion = inputStack.get(DataComponents.POTION_CONTENTS).potion();
			Optional<Holder<Potion>> outputPotion = outputStack.get(DataComponents.POTION_CONTENTS).potion();
			if(inputPotion.isPresent() && outputPotion.isPresent())
			{
				id = EI.id("brewery/generated/modded/%s/%s/%s".formatted(
						idPotion(inputPotion.get()),
						id(reagentIngredient),
						idPotion(outputPotion.get())
				));
			}
		}
		
		return this.generate(id, inputStack, reagentIngredient, outputStack);
	}
	
	private List<RecipeHolder<MachineRecipe>> generateModded(BrewingRecipe brewingRecipe)
	{
		List<RecipeHolder<MachineRecipe>> recipes = Lists.newArrayList();
		
		for(ItemStack inputStack : brewingRecipe.getInput().getItems())
		{
			Ingredient reagentIngredient = brewingRecipe.getIngredient();
			ItemStack outputStack = brewingRecipe.getOutput(inputStack, reagentIngredient.getItems()[0]);
			recipes.add(this.generateModded(inputStack, reagentIngredient, outputStack));
		}
		
		return recipes;
	}
	
	private List<RecipeHolder<MachineRecipe>> buildModded(Level level, PotionBrewing potionBrewing)
	{
		List<RecipeHolder<MachineRecipe>> recipes = Lists.newArrayList();
		
		for(IBrewingRecipe entry : potionBrewing.getRecipes())
		{
			if(entry instanceof BrewingRecipe brewingRecipe)
			{
				recipes.addAll(this.generateModded(brewingRecipe));
			}
		}
		
		return recipes;
	}
	
	@Override
	protected void fillRecipeList(Level level, List<RecipeHolder<MachineRecipe>> recipeList)
	{
		recipeList.addAll(this.getManagerRecipes(level));
		
		recipeList.addAll(this.buildMixes(level, level.potionBrewing()));
		recipeList.addAll(this.buildContainers(level, level.potionBrewing()));
		recipeList.addAll(this.buildModded(level, level.potionBrewing()));
	}
}
