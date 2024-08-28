package net.swedz.extended_industrialization.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.swedz.extended_industrialization.EIComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DyedItemColor.class)
public class RemoveRainbowWhenDyeingMixin
{
	@WrapOperation(
			method = "applyDyes",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"
			)
	)
	private static Object removeRainbow(ItemStack stack, DataComponentType dataComponentType, Operation<Object> original)
	{
		stack.remove(EIComponents.RAINBOW);
		return original.call(stack, dataComponentType);
	}
}
