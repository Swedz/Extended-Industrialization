package net.swedz.extended_industrialization.lootmodifier;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

public record ElectricToolModePredicate(ElectricToolItem.Mode mode) implements ItemSubPredicate
{
	public static final Codec<ElectricToolModePredicate> CODEC = CodecHelper.forLowercaseEnum(ElectricToolItem.Mode.class)
			.xmap(ElectricToolModePredicate::new, ElectricToolModePredicate::mode);
	
	@Override
	public boolean matches(ItemStack stack)
	{
		return stack != null &&
			   ElectricToolItem.getMode(stack) == mode;
	}
}
