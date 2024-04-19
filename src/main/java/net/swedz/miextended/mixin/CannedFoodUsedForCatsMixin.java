package net.swedz.miextended.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.item.ItemStack;
import net.swedz.miextended.items.MIEItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Cat.class, Ocelot.class})
public class CannedFoodUsedForCatsMixin
{
	@ModifyReturnValue(
			method = "isFood",
			at = @At("RETURN")
	)
	private boolean isFood(boolean original, ItemStack stack)
	{
		return original || stack.is(MIEItems.CANNED_FOOD);
	}
}
