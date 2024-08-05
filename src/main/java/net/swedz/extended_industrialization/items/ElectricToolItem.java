package net.swedz.extended_industrialization.items;

import aztech.modern_industrialization.MIComponents;
import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.items.DynamicToolItem;
import aztech.modern_industrialization.items.ItemHelper;
import dev.technici4n.grandpower.api.ISimpleEnergyItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.Tags;
import net.swedz.extended_industrialization.EIDataComponents;

import java.util.List;
import java.util.Map;

public class ElectricToolItem extends Item implements DynamicToolItem, ISimpleEnergyItem
{
	public enum Type
	{
		DRILL(60 * 20 * CableTier.HV.getMaxTransfer(), 9, 8, false),
		CHAINSAW(60 * 20 * CableTier.HV.getMaxTransfer(), 9, 10, false),
		ULTIMATE(60 * 20 * CableTier.EV.getMaxTransfer(), 12, 20, true);
		
		private final long    energyCapacity;
		private final float   speed;
		private final int     damage;
		private final boolean worksForAllBlocks;
		
		Type(long energyCapacity, float speed, int damage, boolean worksForAllBlocks)
		{
			this.energyCapacity = energyCapacity;
			this.speed = speed;
			this.damage = damage;
			this.worksForAllBlocks = worksForAllBlocks;
		}
		
		public long energyCapacity()
		{
			return energyCapacity;
		}
		
		public int damage()
		{
			return damage;
		}
		
		public boolean worksForAllBlocks()
		{
			return worksForAllBlocks;
		}
	}
	
	private static final long ENERGY_COST = 2048;
	
	private final Type type;
	
	public ElectricToolItem(Properties properties, Type type)
	{
		super(properties.stacksTo(1).rarity(Rarity.UNCOMMON));
		this.type = type;
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return true;
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
		if((type.worksForAllBlocks() || this.isSupportedBlock(stack, state)) &&
		   this.getStoredEnergy(stack) > 0 && !state.is(Tiers.NETHERITE.getIncorrectBlocksForDrops()))
		{
			return true;
		}
		return super.isCorrectToolForDrops(stack, state);
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state)
	{
		if((type.worksForAllBlocks() || this.isSupportedBlock(stack, state)) &&
		   this.getStoredEnergy(stack) > 0)
		{
			return Tiers.NETHERITE.getSpeed();
		}
		return 1;
	}
	
	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
	{
		return this.getStoredEnergy(stack) > 0 ? ItemHelper.getToolModifiers(type.damage()) : ItemAttributeModifiers.EMPTY;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return !stack.getOrDefault(EIDataComponents.HIDE_BAR, false);
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return (int) Math.round(this.getStoredEnergy(stack) / (double) type.energyCapacity() * 13);
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		float hue = Math.max(0, (float) this.getStoredEnergy(stack) / type.energyCapacity());
		return Mth.hsvToRgb(hue / 3, 1, 1);
	}
	
	@Override
	public long getEnergyCapacity(ItemStack stack)
	{
		return type.energyCapacity();
	}
	
	@Override
	public long getEnergyMaxInput(ItemStack stack)
	{
		return type.energyCapacity();
	}
	
	@Override
	public long getEnergyMaxOutput(ItemStack stack)
	{
		return type.energyCapacity();
	}
	
	private static boolean isFortune(ItemStack stack)
	{
		return !stack.getOrDefault(MIComponents.SILK_TOUCH, false);
	}
	
	private static void setFortune(ItemStack stack, boolean fortune)
	{
		stack.set(MIComponents.SILK_TOUCH, !fortune);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
	{
		if(this.getStoredEnergy(stack) > 0)
		{
			HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = context.registries().lookupOrThrow(Registries.ENCHANTMENT);
			ResourceKey<Enchantment> enchantmentKey = isFortune(stack) ? Enchantments.FORTUNE : Enchantments.SILK_TOUCH;
			Holder.Reference<Enchantment> enchantment = enchantmentRegistry.getOrThrow(enchantmentKey);
			tooltip.add(Enchantment.getFullname(enchantment, enchantment.value().getMaxLevel()));
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
	public InteractionResult useOn(UseOnContext context)
	{
		ItemStack stack = context.getItemInHand();
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Player player = context.getPlayer();
		if(this.getStoredEnergy(stack) > 0)
		{
			if(stack.is(ItemTags.AXES))
			{
				Block newBlock = StrippingAccess.getStrippedBlocks().get(state.getBlock());
				if(newBlock != null)
				{
					level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1, 1);
					if(!level.isClientSide)
					{
						level.setBlock(pos, newBlock.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)), 11);
						this.tryUseEnergy(stack, ENERGY_COST);
					}
					return InteractionResult.sidedSuccess(level.isClientSide);
				}
			}
			if(stack.is(ItemTags.SHOVELS))
			{
				BlockState newState = PathingAccess.getPathStates().get(state.getBlock());
				if(newState != null)
				{
					level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1, 1);
					if(!level.isClientSide)
					{
						level.setBlock(pos, newState, 11);
						this.tryUseEnergy(stack, ENERGY_COST);
					}
					return InteractionResult.sidedSuccess(level.isClientSide);
				}
			}
		}
		return super.useOn(context);
	}
	
	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand)
	{
		Level level = interactionTarget.level();
		BlockPos blockPos = interactionTarget.blockPosition();
		if(this.getStoredEnergy(stack) > 0 &&
		   stack.is(Tags.Items.TOOLS_SHEAR) && interactionTarget instanceof IShearable shearable)
		{
			if(!level.isClientSide && shearable.isShearable(player, stack, level, blockPos))
			{
				this.tryUseEnergy(stack, ENERGY_COST);
				shearable.onSheared(player, stack, level, blockPos)
						.forEach((drop) -> shearable.spawnShearedDrop(level, blockPos, drop));
				interactionTarget.gameEvent(GameEvent.SHEAR, player);
				return InteractionResult.SUCCESS;
			}
			else
			{
				return InteractionResult.CONSUME;
			}
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment)
	{
		return this.getAllEnchantments(stack, enchantment.unwrapLookup()).getLevel(enchantment);
	}
	
	@Override
	public ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup)
	{
		ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(super.getAllEnchantments(stack, lookup));
		if(this.getStoredEnergy(stack) > 0)
		{
			lookup.get(isFortune(stack) ? Enchantments.FORTUNE : Enchantments.SILK_TOUCH)
					.ifPresent((enchantment) -> enchantments.set(enchantment, enchantment.value().getMaxLevel()));
		}
		return enchantments.toImmutable();
	}
	
	@Override
	public boolean isFoil(ItemStack stack)
	{
		return this.getAllEnchantments(stack, CommonHooks.resolveLookup(Registries.ENCHANTMENT)).size() > (this.getStoredEnergy(stack) > 0 ? 1 : 0);
	}
	
	@Override
	public DataComponentType<Long> getEnergyComponent()
	{
		return MIComponents.ENERGY.get();
	}
	
	private static class StrippingAccess extends AxeItem
	{
		private StrippingAccess(Tier material, Properties properties)
		{
			super(material, properties);
		}
		
		public static Map<Block, Block> getStrippedBlocks()
		{
			return AxeItem.STRIPPABLES;
		}
	}
	
	private static class PathingAccess extends ShovelItem
	{
		private PathingAccess(Tier material, Properties properties)
		{
			super(material, properties);
		}
		
		public static Map<Block, BlockState> getPathStates()
		{
			return ShovelItem.FLATTENABLES;
		}
	}
}
