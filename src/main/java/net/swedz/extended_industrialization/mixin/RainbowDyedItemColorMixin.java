package net.swedz.extended_industrialization.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.component.RainbowDataComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DyedItemColor.class)
public class RainbowDyedItemColorMixin
{
	@Inject(
			method = "getOrDefault",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void getOrDefault(ItemStack stack, int defaultValue,
									 CallbackInfoReturnable<Integer> callback)
	{
		if(stack.is(EITags.Items.RAINBOW_DYEABLE))
		{
			RainbowDataComponent rainbow = stack.get(EIComponents.RAINBOW);
			if(rainbow != null && rainbow.value())
			{
				int color = RainbowDataComponent.getCurrentRainbowColor();
				callback.setReturnValue(color);
			}
		}
	}
}
