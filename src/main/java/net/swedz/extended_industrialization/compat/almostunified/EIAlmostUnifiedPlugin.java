package net.swedz.extended_industrialization.compat.almostunified;

import aztech.modern_industrialization.compat.almostunified.MIRecipeUnifier;
import com.almostreliable.unified.api.plugin.AlmostUnifiedNeoPlugin;
import com.almostreliable.unified.api.plugin.AlmostUnifiedPlugin;
import com.almostreliable.unified.api.unification.recipe.RecipeUnifierRegistry;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;

@AlmostUnifiedNeoPlugin
public final class EIAlmostUnifiedPlugin implements AlmostUnifiedPlugin
{
	@Override
	public ResourceLocation getPluginId()
	{
		return EI.id("almost_unified");
	}
	
	@Override
	public void registerRecipeUnifiers(RecipeUnifierRegistry registry)
	{
		registry.registerForModId(EI.ID, new MIRecipeUnifier());
	}
}
