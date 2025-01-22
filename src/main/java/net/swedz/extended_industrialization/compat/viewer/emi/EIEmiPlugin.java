package net.swedz.extended_industrialization.compat.viewer.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EIText;

import java.util.List;

@EmiEntrypoint
public final class EIEmiPlugin implements EmiPlugin
{
	@Override
	public void register(EmiRegistry registry)
	{
		registry.addWorkstation(VanillaEmiRecipeCategories.BREWING, EmiStack.of(EIItems.valueOf("steel_brewery")));
		registry.addWorkstation(VanillaEmiRecipeCategories.BREWING, EmiStack.of(EIItems.valueOf("electric_brewery")));
		
		registry.addRecipe(new EmiInfoRecipe(
				List.of(EmiStack.of(EIFluids.BLAZING_ESSENCE.asFluid())),
				List.of(
						EIText.BLAZING_ESSENCE_USES_1.text(),
						Component.literal(" "),
						EIText.BLAZING_ESSENCE_USES_2.text()
				),
				EI.id("emi_info/blazing_essence")
		));
	}
}
