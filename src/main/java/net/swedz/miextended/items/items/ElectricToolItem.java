package net.swedz.miextended.items.items;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.items.DynamicToolItem;
import aztech.modern_industrialization.items.ItemHelper;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.technici4n.grandpower.api.ISimpleEnergyItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.TierSortingRegistry;

import java.util.List;
import java.util.Map;

public class ElectricToolItem extends Item implements Vanishable, DynamicToolItem, ISimpleEnergyItem
{
	private static final long ENERGY_CAPACITY = 60 * 20 * CableTier.HV.getMaxTransfer();
	private static final long ENERGY_COST     = 2048;
	
	public ElectricToolItem(Properties properties)
	{
		super(properties.stacksTo(1).rarity(Rarity.UNCOMMON));
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		return enchantment.category.canEnchant(stack.getItem()) ||
			   (enchantment.category == EnchantmentCategory.DIGGER && enchantment != Enchantments.SILK_TOUCH && enchantment != Enchantments.BLOCK_FORTUNE);
	}
	
	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner)
	{
		if(state.getDestroySpeed(level, pos) != 0)
		{
			this.tryUseEnergy(stack, ENERGY_COST);
		}
		return true;
	}
	
	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		this.tryUseEnergy(stack, ENERGY_COST);
		return true;
	}
	
	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state)
	{
		if(this.isSupportedBlock(stack, state) && this.getStoredEnergy(stack) > 0 && TierSortingRegistry.isCorrectTierForDrops(Tiers.NETHERITE, state))
		{
			return true;
		}
		return super.isCorrectToolForDrops(stack, state);
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state)
	{
		if(this.isSupportedBlock(stack, state) && this.getStoredEnergy(stack) > 0)
		{
			return Tiers.NETHERITE.getSpeed();
		}
		return 1;
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack)
	{
		if(slot == EquipmentSlot.MAINHAND && this.getStoredEnergy(stack) > 0)
		{
			return ItemHelper.createToolModifiers(8);
		}
		return ImmutableMultimap.of();
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return (int) Math.round(this.getStoredEnergy(stack) / (double) ENERGY_CAPACITY * 13);
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		float hue = Math.max(0, (float) this.getStoredEnergy(stack) / ENERGY_CAPACITY);
		return Mth.hsvToRgb(hue / 3, 1, 1);
	}
	
	@Override
	public long getEnergyCapacity(ItemStack stack)
	{
		return ENERGY_CAPACITY;
	}
	
	@Override
	public long getEnergyMaxInput(ItemStack stack)
	{
		return ENERGY_CAPACITY;
	}
	
	@Override
	public long getEnergyMaxOutput(ItemStack stack)
	{
		return ENERGY_CAPACITY;
	}
	
	private static boolean isFortune(ItemStack stack)
	{
		CompoundTag tag = stack.getTag();
		return tag != null && tag.getBoolean("fortune");
	}
	
	private static void setFortune(ItemStack stack, boolean fortune)
	{
		if(fortune)
		{
			stack.getOrCreateTag().putBoolean("fortune", true);
		}
		else
		{
			stack.removeTagKey("fortune");
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context)
	{
		if(this.getStoredEnergy(stack) > 0)
		{
			Enchantment enchantment = isFortune(stack) ? Enchantments.BLOCK_FORTUNE : Enchantments.SILK_TOUCH;
			tooltip.add(enchantment.getFullname(enchantment.getMaxLevel()));
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand)
	{
		if(hand == InteractionHand.MAIN_HAND && user.isShiftKeyDown())
		{
			ItemStack stack = user.getItemInHand(hand);
			setFortune(stack, !isFortune(stack));
			if(!world.isClientSide)
			{
				user.displayClientMessage(
						isFortune(stack) ? MIText.ToolSwitchedFortune.text() : MIText.ToolSwitchedSilkTouch.text(),
						true
				);
			}
			return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
		}
		return super.use(world, user, hand);
	}
	
	@Override
	public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment)
	{
		return this.getAllEnchantments(stack).getOrDefault(enchantment, 0);
	}
	
	@Override
	public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack)
	{
		Map<Enchantment, Integer> map = EnchantmentHelper.deserializeEnchantments(stack.getEnchantmentTags());
		if(this.getStoredEnergy(stack) > 0)
		{
			if(!isFortune(stack))
			{
				map.put(Enchantments.SILK_TOUCH, Enchantments.SILK_TOUCH.getMaxLevel());
			}
			else
			{
				map.put(Enchantments.BLOCK_FORTUNE, Enchantments.BLOCK_FORTUNE.getMaxLevel());
			}
		}
		return map;
	}
	
	@Override
	public boolean isFoil(ItemStack pStack)
	{
		return !this.getAllEnchantments(pStack).isEmpty();
	}
}
