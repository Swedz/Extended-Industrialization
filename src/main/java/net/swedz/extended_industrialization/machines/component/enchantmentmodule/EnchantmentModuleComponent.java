package net.swedz.extended_industrialization.machines.component.enchantmentmodule;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.datamap.EnchantmentModule;
import net.swedz.tesseract.neoforge.compat.mi.component.SimpleItemStackComponent;

import java.util.Optional;

public final class EnchantmentModuleComponent extends SimpleItemStackComponent
{
	private final TagKey<Item> tag;
	
	public EnchantmentModuleComponent(TagKey<Item> tag, UpdatedCallback callback)
	{
		super("enchantment_module_stack", callback);
		this.tag = tag;
	}
	
	public EnchantmentModuleComponent(TagKey<Item> tag)
	{
		this(tag, null);
	}
	
	public boolean is(ItemStack stack)
	{
		return stack.is(tag);
	}
	
	public Optional<EnchantmentModule> getActiveEnchantment()
	{
		return Optional.ofNullable(EnchantmentModule.getFor(stack.getItem()));
	}
	
	public long getAdditionalEuCost(CableTier tier)
	{
		return this.getActiveEnchantment().map((enchantment) -> enchantment.getEuCost(tier)).orElse(0L);
	}
	
	public ItemInteractionResult onUse(MachineBlockEntity blockEntity, Player player, InteractionHand hand)
	{
		var stack = player.getItemInHand(hand);
		if(!this.is(stack))
		{
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		if(this.getStack().isEmpty())
		{
			var copy = stack.copyWithCount(1);
			stack.consume(1, player);
			if(!blockEntity.getLevel().isClientSide())
			{
				this.setStackServer(blockEntity, copy);
			}
			return ItemInteractionResult.sidedSuccess(blockEntity.getLevel().isClientSide());
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
}
