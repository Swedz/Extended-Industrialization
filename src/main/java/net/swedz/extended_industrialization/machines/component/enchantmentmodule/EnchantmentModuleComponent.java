package net.swedz.extended_industrialization.machines.component.enchantmentmodule;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.datamap.EnchantmentModule;
import net.swedz.tesseract.neoforge.compat.mi.component.SimpleItemStackComponent;

import java.util.Optional;

public final class EnchantmentModuleComponent extends SimpleItemStackComponent
{
	public static boolean is(ItemStack stack)
	{
		return EnchantmentModule.getFor(stack.getItem()) != null;
	}
	
	public EnchantmentModuleComponent(UpdatedCallback callback)
	{
		super("enchantment_module_stack", callback);
	}
	
	public EnchantmentModuleComponent()
	{
		this(null);
	}
	
	public Optional<EnchantmentModule> getActiveEnchantment()
	{
		return Optional.ofNullable(EnchantmentModule.getFor(stack.getItem()));
	}
	
	public ItemInteractionResult onUse(MachineBlockEntity blockEntity, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		if(!is(stack))
		{
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		if(this.getStack().isEmpty())
		{
			ItemStack copy = stack.copyWithCount(1);
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
