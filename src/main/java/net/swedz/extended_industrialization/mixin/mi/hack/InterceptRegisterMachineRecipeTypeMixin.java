package net.swedz.extended_industrialization.mixin.mi.hack;

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.hook.mi.tracker.MIHookTracker;
import net.swedz.extended_industrialization.EIOtherRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(MIMachineRecipeTypes.class)
public class InterceptRegisterMachineRecipeTypeMixin
{
	@Inject(
			method = "create(Ljava/lang/String;Ljava/util/function/Function;)Laztech/modern_industrialization/machines/recipe/MachineRecipeType;",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void create(String name, Function<ResourceLocation, MachineRecipeType> creator,
							   CallbackInfoReturnable<MachineRecipeType> callback)
	{
		if(MIHookTracker.isOpen())
		{
			MachineRecipeType type = creator.apply(EI.id(name));
			EIOtherRegistries.RECIPE_SERIALIZERS.register(name, () -> type);
			EIOtherRegistries.RECIPE_TYPES.register(name, () -> type);
			// recipeTypes.add(type); - this is only used for kjs, we don't need this
			callback.setReturnValue(type);
		}
	}
}
