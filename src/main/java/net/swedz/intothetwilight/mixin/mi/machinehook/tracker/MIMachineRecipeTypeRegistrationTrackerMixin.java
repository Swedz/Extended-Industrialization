package net.swedz.intothetwilight.mixin.mi.machinehook.tracker;

import aztech.modern_industrialization.compat.rei.machines.MachineCategoryParams;
import aztech.modern_industrialization.machines.init.SingleBlockCraftingMachines;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.swedz.intothetwilight.mi.machines.MIMachineHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SingleBlockCraftingMachines.class)
public class MIMachineRecipeTypeRegistrationTrackerMixin
{
	@Inject(
			method = "registerReiTiers",
			at = @At("HEAD")
	)
	private static void registerReiTiers(String englishName, String machine,
										 MachineRecipeType recipeType, MachineCategoryParams categoryParams,
										 int tiers,
										 CallbackInfo callback)
	{
		if(MIMachineHookTracker.isOpen())
		{
			int previousMaxEu = 0;
			for(int i = 0; i < 3; i++)
			{
				if(((tiers >> i) & 1) > 0)
				{
					int minEu = previousMaxEu + 1;
					int maxEu = i == 0 ? 2 : i == 1 ? 4 : Integer.MAX_VALUE;
					
					String prefix = i == 0 ? "bronze_" : i == 1 ? "steel_" : tiers == SingleBlockCraftingMachines.TIER_ELECTRIC ? "" : "electric_";
					String itemId = prefix + machine;
					String englishPrefix = i == 0 ? "Bronze " : i == 1 ? "Steel " : "Electric ";
					String fullEnglishName = tiers == SingleBlockCraftingMachines.TIER_ELECTRIC || previousMaxEu == 0 ? englishName : englishPrefix + englishName;
					
					MIMachineHookTracker.addMachineRecipeTypeLanguageEntry(itemId, fullEnglishName);
					
					previousMaxEu = maxEu;
				}
			}
		}
	}
}
