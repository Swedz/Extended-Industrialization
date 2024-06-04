package net.swedz.extended_industrialization.mixin.mi.hack;

import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import aztech.modern_industrialization.machines.init.MultiblockMachines;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import com.llamalad7.mixinextras.sugar.Local;
import net.swedz.extended_industrialization.machines.recipe.condition.EBFCoilProcessCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(
		value = MultiblockMachines.class,
		remap = false
)
public class InterceptRegisterEBFReiCategoriesMixin
{
	@ModifyArg(
			method = "registerEbfReiCategories",
			at = @At(
					value = "INVOKE",
					target = "Laztech/modern_industrialization/machines/init/MultiblockMachines$Rei;extraTest(Ljava/util/function/Predicate;)Laztech/modern_industrialization/machines/init/MultiblockMachines$Rei;"
			)
	)
	private static Predicate<MachineRecipe> extraTest(Predicate<MachineRecipe> extraTest,
													  @Local ElectricBlastFurnaceBlockEntity.Tier tier,
													  @Local(name = "previousMax") long previousMax,
													  @Local(name = "currentMax") long currentMax)
	{
		return (recipe) ->
		{
			Optional<EBFCoilProcessCondition> optionalCoilCondition = recipe.conditions.stream()
					.filter((c) -> c instanceof EBFCoilProcessCondition)
					.map((c) -> (EBFCoilProcessCondition) c)
					.findFirst();
			if(optionalCoilCondition.isPresent())
			{
				EBFCoilProcessCondition coilCondition = optionalCoilCondition.get();
				return coilCondition.coilTier().maxBaseEu() <= tier.maxBaseEu();
			}
			return previousMax < recipe.eu && recipe.eu <= currentMax;
		};
	}
}
