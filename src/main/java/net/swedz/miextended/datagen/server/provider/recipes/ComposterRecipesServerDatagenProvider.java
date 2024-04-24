package net.swedz.miextended.datagen.server.provider.recipes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.mi.hook.MIMachineHook;
import net.swedz.miextended.registry.fluids.MIEFluids;
import net.swedz.miextended.registry.items.MIEItems;

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
						"composter/standard/%s".formatted(id.getNamespace()), id.getPath(), MIMachineHook.RecipeTypes.COMPOSTER,
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
				"composter/fertilizer", "composted_manure", MIMachineHook.RecipeTypes.COMPOSTER,
				4, 5 * 20,
				(r) -> r
						.addFluidInput(MIEFluids.MANURE, 150)
						.addItemInput(Items.BONE_MEAL, 1)
						.addItemInput(MIEItems.MULCH, 1)
						.addFluidOutput(MIEFluids.COMPOSTED_MANURE, 200),
				output
		);
	}
}
