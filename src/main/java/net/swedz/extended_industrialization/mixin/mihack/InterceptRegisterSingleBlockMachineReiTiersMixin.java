package net.swedz.extended_industrialization.mixin.mihack;

import aztech.modern_industrialization.compat.rei.machines.MachineCategoryParams;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.compat.rei.machines.SteamMode;
import aztech.modern_industrialization.machines.init.SingleBlockCraftingMachines;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.mi.hook.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SingleBlockCraftingMachines.class)
public class InterceptRegisterSingleBlockMachineReiTiersMixin
{
	@Inject(
			method = "registerReiTiers",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void registerReiTiers(String englishName, String machine,
										 MachineRecipeType recipeType, MachineCategoryParams categoryParams, int tiers,
										 CallbackInfo callback)
	{
		if(MIHookTracker.isOpen())
		{
			List<MachineCategoryParams> previousCategories = new ArrayList<>();
			int previousMaxEu = 0;
			for(int i = 0; i < 3; ++i)
			{
				if(((tiers >> i) & 1) > 0)
				{
					int minEu = previousMaxEu + 1;
					int maxEu = i == 0 ? 2 : i == 1 ? 4 : Integer.MAX_VALUE;
					String prefix = i == 0 ? "bronze_" : i == 1 ? "steel_" : tiers == SingleBlockCraftingMachines.TIER_ELECTRIC ? "" : "electric_";
					String itemId = prefix + machine;
					String englishPrefix = i == 0 ? "Bronze " : i == 1 ? "Steel " : "Electric ";
					String fullEnglishName = tiers == SingleBlockCraftingMachines.TIER_ELECTRIC || previousMaxEu == 0 ? englishName
							: englishPrefix + englishName;
					MachineCategoryParams category = new MachineCategoryParams(fullEnglishName, itemId, categoryParams.itemInputs,
							categoryParams.itemOutputs,
							categoryParams.fluidInputs, categoryParams.fluidOutputs, categoryParams.progressBarParams,
							recipe -> recipe.getType() == recipeType && minEu <= recipe.eu && recipe.eu <= maxEu, false,
							i < 2 ? SteamMode.BOTH : SteamMode.ELECTRIC_ONLY
					);
					MIHookTracker.addMachineRecipeTypeLanguageEntry(itemId, fullEnglishName);
					ReiMachineRecipes.registerCategory(itemId, category);
					ReiMachineRecipes.registerMachineClickArea(itemId, categoryParams.progressBarParams.toRectangle());
					previousCategories.add(category);
					for(MachineCategoryParams param : previousCategories)
					{
						param.workstations.add(EI.id(itemId));
						ReiMachineRecipes.registerRecipeCategoryForMachine(itemId, param.category);
					}
					previousMaxEu = maxEu;
				}
			}
			
			callback.cancel();
		}
	}
}
