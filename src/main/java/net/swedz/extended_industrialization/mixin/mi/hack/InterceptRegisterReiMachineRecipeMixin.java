package net.swedz.extended_industrialization.mixin.mi.hack;

import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.hook.mi.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ReiMachineRecipes.class)
public class InterceptRegisterReiMachineRecipeMixin
{
	@ModifyArg(
			method = "registerRecipeCategoryForMachine(Ljava/lang/String;Ljava/lang/String;Laztech/modern_industrialization/compat/rei/machines/ReiMachineRecipes$MachineScreenPredicate;)V",
			at = @At(
					value = "INVOKE",
					target = "Laztech/modern_industrialization/compat/rei/machines/ReiMachineRecipes$ClickAreaCategory;<init>(Lnet/minecraft/resources/ResourceLocation;Laztech/modern_industrialization/compat/rei/machines/ReiMachineRecipes$MachineScreenPredicate;)V"
			)
	)
	private static ResourceLocation registerRecipeCategoryForMachine(ResourceLocation category)
	{
		if(MIHookTracker.isOpen())
		{
			return EI.id(category.getPath());
		}
		return category;
	}
}
