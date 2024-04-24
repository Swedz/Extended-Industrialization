package net.swedz.miextended.mixin.mihack;

import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import net.minecraft.resources.ResourceLocation;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;
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
			return MIExtended.id(category.getPath());
		}
		return category;
	}
}