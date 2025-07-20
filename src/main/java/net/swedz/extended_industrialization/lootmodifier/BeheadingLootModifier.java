package net.swedz.extended_industrialization.lootmodifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public final class BeheadingLootModifier extends LootModifier
{
	public static final MapCodec<BeheadingLootModifier> CODEC = RecordCodecBuilder.mapCodec((instance) -> LootModifier.codecStart(instance)
			.and(ItemStack.CODEC.fieldOf("drop").forGetter(BeheadingLootModifier::drop))
			.apply(instance, BeheadingLootModifier::new));
	
	private final ItemStack drop;
	
	public BeheadingLootModifier(LootItemCondition[] conditions, ItemStack drop)
	{
		super(conditions);
		this.drop = drop;
	}
	
	public ItemStack drop()
	{
		return drop;
	}
	
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> items, LootContext context)
	{
		// Don't add to a drop where the drop item already exists
		for(var existing : items)
		{
			if(ItemStack.isSameItem(existing, drop))
			{
				return items;
			}
		}
		
		items.add(drop.copy());
		return items;
	}
	
	@Override
	public MapCodec<? extends IGlobalLootModifier> codec()
	{
		return CODEC;
	}
}
