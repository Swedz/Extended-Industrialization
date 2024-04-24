package net.swedz.miextended.mixin.mihack;

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.minecraft.resources.ResourceLocation;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;
import net.swedz.miextended.registry.MIEOtherRegistries;
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
			MachineRecipeType type = creator.apply(MIExtended.id(name));
			MIEOtherRegistries.RECIPE_SERIALIZERS.register(name, () -> type);
			MIEOtherRegistries.RECIPE_TYPES.register(name, () -> type);
			// recipeTypes.add(type); - this is only used for kjs, we don't need this
			callback.setReturnValue(type);
		}
	}
}
