package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.registry.items.EIItems;

public final class CommonRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public CommonRecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		addMachineRecipe(
				"mixer", "mulch", MIMachineRecipeTypes.MIXER,
				2, 5 * 20,
				(r) -> r
						.addItemInput(Items.DIRT, 1)
						.addItemInput(MIItem.WOOD_PULP, 6)
						.addItemOutput(EIItems.MULCH, 1),
				output
		);
	}
}
