package net.swedz.extended_industrialization.lootmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.item.ElectricToolItem;

public record ElectricToolModePredicate(boolean fortune) implements ItemSubPredicate
{
	public static final Codec<ElectricToolModePredicate> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(Codec.BOOL.fieldOf("fortune").forGetter(ElectricToolModePredicate::fortune))
			.apply(instance, ElectricToolModePredicate::new));
	
	@Override
	public boolean matches(ItemStack stack)
	{
		return stack != null &&
			   ElectricToolItem.isFortune(stack) == fortune;
	}
}
