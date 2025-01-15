package net.swedz.extended_industrialization;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.recipe.RainbowableDyeRecipe;

import java.util.function.Supplier;

public final class EIRecipeTypes
{
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, EI.ID);
	
	public static final Supplier<RecipeSerializer<RainbowableDyeRecipe>> RAINBOWABLE_DYE_SERIALIZER = RECIPE_SERIALIZERS.register(
			"crafting_special_rainbowable_dye",
			() -> new SimpleCraftingRecipeSerializer<>(RainbowableDyeRecipe::new)
	);
	
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, EI.ID);
	
	public static void init(IEventBus bus)
	{
		RECIPE_SERIALIZERS.register(bus);
		RECIPE_TYPES.register(bus);
	}
}
