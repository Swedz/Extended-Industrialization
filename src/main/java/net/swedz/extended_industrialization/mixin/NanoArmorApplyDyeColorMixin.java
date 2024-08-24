package net.swedz.extended_industrialization.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.swedz.extended_industrialization.EITags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DyedItemColor.class)
public class NanoArmorApplyDyeColorMixin
{
	@Redirect(
			method = "applyDyes",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/DyeColor;getTextureDiffuseColor()I"
			)
	)
	private static int getDyeColor(DyeColor dyeColor,
								   @Local(argsOnly = true) ItemStack stack)
	{
		return stack.is(EITags.NANO_ARMOR) ? dyeColor.getTextColor() : dyeColor.getTextureDiffuseColor();
	}
}
