package net.swedz.extended_industrialization.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.swedz.extended_industrialization.EIArmorMaterials;
import net.swedz.extended_industrialization.EITags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(HumanoidArmorLayer.class)
public class NanoArmorRenderLayerColorMixin
{
	@ModifyConstant(
			method = "renderArmorPiece",
			constant = @Constant(intValue = DyedItemColor.LEATHER_COLOR)
	)
	private int getDefaultDyeColor(int leatherColor,
								   @Local ItemStack stack)
	{
		return stack.is(EITags.NANO_ARMOR) ? EIArmorMaterials.NANO_COLOR : leatherColor;
	}
}
