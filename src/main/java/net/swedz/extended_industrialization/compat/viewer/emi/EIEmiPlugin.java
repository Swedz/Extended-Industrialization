package net.swedz.extended_industrialization.compat.viewer.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import net.swedz.extended_industrialization.EIItems;

@EmiEntrypoint
public final class EIEmiPlugin implements EmiPlugin
{
	@Override
	public void register(EmiRegistry registry)
	{
		registry.addWorkstation(VanillaEmiRecipeCategories.BREWING, EmiStack.of(EIItems.valueOf("steel_brewery")));
		registry.addWorkstation(VanillaEmiRecipeCategories.BREWING, EmiStack.of(EIItems.valueOf("electric_brewery")));
	}
}
