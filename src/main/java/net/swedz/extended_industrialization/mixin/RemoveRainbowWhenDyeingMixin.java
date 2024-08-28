package net.swedz.extended_industrialization.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.swedz.extended_industrialization.EIComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DyedItemColor.class)
public class RemoveRainbowWhenDyeingMixin
{
	@Inject(
			method = "applyDyes",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;",
					ordinal = 0
			)
	)
	private static void removeRainbow(ItemStack originalStack, List<DyeItem> dyes,
									  CallbackInfoReturnable<ItemStack> callback,
									  @Local(name = "itemstack") ItemStack newStack)
	{
		newStack.remove(EIComponents.RAINBOW);
	}
}
