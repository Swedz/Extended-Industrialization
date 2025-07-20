package net.swedz.extended_industrialization.lootmodifier;

import com.mojang.serialization.Codec;
import dev.technici4n.grandpower.api.ISimpleEnergyItem;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.world.item.ItemStack;

// TODO move to tesseract
public record SimpleEnergyItemHasChargePredicate() implements ItemSubPredicate
{
	public static final Codec<SimpleEnergyItemHasChargePredicate> CODEC = Codec.unit(SimpleEnergyItemHasChargePredicate::new);
	
	@Override
	public boolean matches(ItemStack stack)
	{
		return stack != null &&
			   stack.getItem() instanceof ISimpleEnergyItem item &&
			   item.getStoredEnergy(stack) > 0;
	}
}
