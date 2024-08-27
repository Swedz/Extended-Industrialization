package net.swedz.extended_industrialization.item;

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
import net.minecraft.world.item.DyeColor;
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
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.Tags;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.tesseract.neoforge.helper.ColorHelper;
import net.swedz.tesseract.neoforge.item.DynamicDyedItem;

import java.util.List;
import java.util.Map;

public class ElectricToolItem extends Item implements DynamicToolItem, ISimpleEnergyItem, DynamicDyedItem
{
	public static final  int SPEED_MIN        = 1;
	public static final  int SPEED_MAX        = 10;
	private static final int SPEED_MULTIPLIER = 10;
	
	public static final long ENERGY_COST = 2048;
	
	public enum Type
	{
		DRILL(60 * 20 * CableTier.HV.getMaxTransfer(), 8, false, false),
		CHAINSAW(60 * 20 * CableTier.HV.getMaxTransfer(), 10, true, false),
		ULTIMATE(60 * 20 * CableTier.EV.getMaxTransfer(), 20, true, true);
		
		private final long    energyCapacity;
		private final int     damage;
		private final boolean includeLooting;
		private final boolean worksForAllBlocks;
		
		Type(long energyCapacity, int damage, boolean includeLooting, boolean worksForAllBlocks)
		{
			this.energyCapacity = energyCapacity;
			this.damage = damage;
			this.includeLooting = includeLooting;
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
		
		public boolean includeLooting()
		{
			return includeLooting;
		}
		
		public boolean worksForAllBlocks()
		{
			return worksForAllBlocks;
		}
	}
	
	private final Type toolType;
	
	public ElectricToolItem(Properties properties, Type toolType)
	{
		super(properties.stacksTo(1).rarity(Rarity.UNCOMMON));
		this.toolType = toolType;
	}
	
	public Type getToolType()
	{
		return toolType;
	}
	
	public static int getToolSpeed(ItemStack stack)
	{
		if(!(stack.getItem() instanceof ElectricToolItem item))
		{
			throw new IllegalArgumentException("Cannot get tool speed for a non electric tool item");
		}
		return stack.getOrDefault(EIComponents.ELECTRIC_TOOL_SPEED, SPEED_MAX);
	}
	
	public static void setToolSpeed(ItemStack stack, int speed)
	{
		speed = Mth.clamp(speed, SPEED_MIN, SPEED_MAX);
		stack.set(EIComponents.ELECTRIC_TOOL_SPEED, speed);
	}
	
	public static boolean isFortune(ItemStack stack)
	{
		return !stack.getOrDefault(MIComponents.SILK_TOUCH, false);
	}
	
	public static void setFortune(ItemStack stack, boolean fortune)
	{
		stack.set(MIComponents.SILK_TOUCH, !fortune);
	}
	
	@Override
	public int getDyeColor(DyeColor dyeColor)
	{
		if(toolType == Type.ULTIMATE)
		{
			return ColorHelper.getVibrantColor(dyeColor);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
	
	@Override
	public int getDefaultDyeColor()
	{
		if(toolType == Type.ULTIMATE)
		{
			return 0xFFFF0000;
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
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
		if((toolType.worksForAllBlocks() || this.isSupportedBlock(stack, state)) &&
		   this.getStoredEnergy(stack) > 0 && !state.is(Tiers.NETHERITE.getIncorrectBlocksForDrops()))
		{
			return true;
		}
		return super.isCorrectToolForDrops(stack, state);
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state)
	{
		if((toolType.worksForAllBlocks() || this.isSupportedBlock(stack, state)) &&
		   this.getStoredEnergy(stack) > 0)
		{
			return getToolSpeed(stack) * SPEED_MULTIPLIER;
		}
		return 1;
	}
	
	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
	{
		return this.getStoredEnergy(stack) > 0 ? ItemHelper.getToolModifiers(toolType.damage()) : ItemAttributeModifiers.EMPTY;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return !stack.getOrDefault(EIComponents.HIDE_BAR, false);
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return (int) Math.round(this.getStoredEnergy(stack) / (double) toolType.energyCapacity() * 13);
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		float hue = Math.max(0, (float) this.getStoredEnergy(stack) / toolType.energyCapacity());
		return Mth.hsvToRgb(hue / 3, 1, 1);
	}
	
	@Override
	public long getEnergyCapacity(ItemStack stack)
	{
		return toolType.energyCapacity();
	}
	
	@Override
	public long getEnergyMaxInput(ItemStack stack)
	{
		return toolType.energyCapacity();
	}
	
	@Override
	public long getEnergyMaxOutput(ItemStack stack)
	{
		return toolType.energyCapacity();
	}
	
	private static Component enchantmentFullNameComponent(HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry,
														  ResourceKey<Enchantment> enchantment)
	{
		Holder.Reference<Enchantment> fortune = enchantmentRegistry.getOrThrow(enchantment);
		return Enchantment.getFullname(fortune, fortune.value().getMaxLevel());
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
	{
		if(this.getStoredEnergy(stack) > 0)
		{
			HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = context.registries().lookupOrThrow(Registries.ENCHANTMENT);
			if(isFortune(stack))
			{
				tooltip.add(enchantmentFullNameComponent(enchantmentRegistry, Enchantments.FORTUNE));
				if(toolType.includeLooting())
				{
					tooltip.add(enchantmentFullNameComponent(enchantmentRegistry, Enchantments.LOOTING));
				}
			}
			else
			{
				tooltip.add(enchantmentFullNameComponent(enchantmentRegistry, Enchantments.SILK_TOUCH));
			}
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
	
	private static void includeEnchantment(HolderLookup.RegistryLookup<Enchantment> lookup,
										   ItemEnchantments.Mutable enchantments,
										   ResourceKey<Enchantment> enchantment)
	{
		lookup.get(enchantment).ifPresent((ench) -> enchantments.set(ench, ench.value().getMaxLevel()));
	}
	
	@Override
	public ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup)
	{
		ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(super.getAllEnchantments(stack, lookup));
		if(this.getStoredEnergy(stack) > 0)
		{
			if(isFortune(stack))
			{
				includeEnchantment(lookup, enchantments, Enchantments.FORTUNE);
				if(toolType.includeLooting())
				{
					includeEnchantment(lookup, enchantments, Enchantments.LOOTING);
				}
			}
			else
			{
				includeEnchantment(lookup, enchantments, Enchantments.SILK_TOUCH);
			}
		}
		return enchantments.toImmutable();
	}
	
	@Override
	public boolean isFoil(ItemStack stack)
	{
		return this.getStoredEnergy(stack) > 0;
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
