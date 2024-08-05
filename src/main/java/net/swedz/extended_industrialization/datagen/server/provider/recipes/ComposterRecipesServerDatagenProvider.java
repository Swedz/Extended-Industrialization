package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EIMachines;

public final class ComposterRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public ComposterRecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
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
