package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EIMachines;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIItems;

public final class ComposterRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public ComposterRecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event);
	}
	
	private static void addStandardCompostingRecipes(RecipeOutput output)
	{
		for(Item item : BuiltInRegistries.ITEM)
		{
			ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
			float chance = ComposterBlock.getValue(item.getDefaultInstance());
			if(chance > 0)
			{
				int amountNeeded = Math.max(1, (int) Math.floor((8 / chance) / 2));
				addMachineRecipe(
						"composter/standard/%s".formatted(id.getNamespace()), id.getPath(), EIMachines.RecipeTypes.COMPOSTER,
						2, 5 * 20,
						(r) -> r
								.addItemInput(item, amountNeeded)
								.addItemOutput(Items.BONE_MEAL, 1),
						output
				);
			}
		}
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		addStandardCompostingRecipes(output);
		
		addMachineRecipe(
				"composter/fertilizer", "composted_manure", EIMachines.RecipeTypes.COMPOSTER,
				4, 5 * 20,
				(r) -> r
						.addFluidInput(EIFluids.MANURE, 150)
						.addItemInput(Items.BONE_MEAL, 1)
						.addItemInput(EIItems.MULCH, 1)
						.addFluidOutput(EIFluids.COMPOSTED_MANURE, 200),
				output
		);
	}
}
