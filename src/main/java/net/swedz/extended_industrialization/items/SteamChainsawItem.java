package net.swedz.extended_industrialization.items;

import aztech.modern_industrialization.MIComponents;
import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.blocks.storage.StorageBehaviour;
import aztech.modern_industrialization.items.ContainerItem;
import aztech.modern_industrialization.items.DynamicToolItem;
import aztech.modern_industrialization.items.ItemContainingItemHelper;
import aztech.modern_industrialization.items.ItemHelper;
import aztech.modern_industrialization.items.SteamDrillFuel;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.util.Simulation;
import aztech.modern_industrialization.util.TextHelper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.apache.commons.lang3.mutable.Mutable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Most of this code was directly copied from {@link aztech.modern_industrialization.items.SteamDrillItem} but adapted to be an axe-type tool instead and without the 3x3.
 */
public final class SteamChainsawItem extends Item implements DynamicToolItem, ItemContainingItemHelper
{
	public static final StorageBehaviour<ItemVariant> BEHAVIOR = new StorageBehaviour<>()
	{
		@Override
		public long getCapacityForResource(ItemVariant resource)
		{
			return resource.getMaxStackSize();
		}
		
		public boolean canInsert(ItemVariant item)
		{
			return item.toStack().getBurnTime(null) > 0;
		}
	};
	
	private static final int FULL_WATER = 18000;
	
	public SteamChainsawItem(Item.Properties settings)
	{
		super(settings.stacksTo(1).rarity(Rarity.UNCOMMON));
	}
	
	private static boolean isNotSilkTouch(ItemStack stack)
	{
		return !stack.getOrDefault(MIComponents.SILK_TOUCH, true);
	}
	
	private static void setSilkTouch(ItemStack stack, boolean silkTouch)
	{
		stack.set(MIComponents.SILK_TOUCH, silkTouch);
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return !newStack.is(this) || slotChanged;
	}
	
	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack)
	{
		return !newStack.is(this) || !this.canUse(newStack);
	}
	
	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state)
	{
		if(this.isSupportedBlock(stack, state) && this.canUse(stack) && !state.is(Tiers.NETHERITE.getIncorrectBlocksForDrops()))
		{
			return true;
		}
		return super.isCorrectToolForDrops(stack, state);
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state)
	{
		if(this.canUse(stack))
		{
			if(isCorrectToolForDrops(stack, state))
			{
				return Tiers.DIAMOND.getSpeed();
			}
			else
			{
				return 1;
			}
		}
		else
		{
			return 0;
		}
	}
	
	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
	{
		return this.canUse(stack) ? ItemHelper.getToolModifiers(7) : ItemAttributeModifiers.EMPTY;
	}
	
	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner)
	{
		this.useFuel(stack, miner);
		return true;
	}
	
	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		this.useFuel(stack, attacker);
		return true;
	}
	
	private void useFuel(ItemStack stack, LivingEntity entity)
	{
		if(stack.getOrDefault(MIComponents.WATER, 0) > 0 &&
		   stack.getOrDefault(MIComponents.STEAM_DRILL_FUEL, SteamDrillFuel.EMPTY).burnTicks() == 0)
		{
			int burnTicks = this.consumeFuel(stack, Simulation.ACT);
			
			stack.set(MIComponents.STEAM_DRILL_FUEL, new SteamDrillFuel(burnTicks, burnTicks));
			
			if(burnTicks > 0 && entity != null)
			{
				entity.level().playSound(
						null,
						entity.getX(), entity.getY(), entity.getZ(),
						SoundEvents.FIRE_AMBIENT, SoundSource.PLAYERS,
						1f, 1f
				);
			}
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand)
	{
		// Enable or disable silk touch
		if(hand == InteractionHand.MAIN_HAND && user.isShiftKeyDown())
		{
			ItemStack stack = user.getItemInHand(hand);
			setSilkTouch(stack, isNotSilkTouch(stack));
			if(!level.isClientSide)
			{
				user.displayClientMessage(
						isNotSilkTouch(stack) ? MIText.ToolSwitchedNoSilkTouch.text() : MIText.ToolSwitchedSilkTouch.text(), true);
			}
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
		}
		
		// Refill water
		ItemStack itemStack = user.getItemInHand(hand);
		BlockHitResult hitResult = getPlayerPOVHitResult(level, user, ClipContext.Fluid.ANY);
		if(hitResult.getType() != HitResult.Type.BLOCK)
			return InteractionResultHolder.pass(itemStack);
		FluidState fluidState = level.getFluidState(hitResult.getBlockPos());
		if(fluidState.getType() == Fluids.WATER || fluidState.getType() == Fluids.FLOWING_WATER)
		{
			this.fillWater(user, itemStack);
			return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
		}
		
		return super.use(level, user, hand);
	}
	
	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand)
	{
		Level level = interactionTarget.level();
		BlockPos blockPos = interactionTarget.blockPosition();
		if(this.canUse(stack) &&
		   stack.is(Tags.Items.TOOLS_SHEAR) && interactionTarget instanceof IShearable shearable)
		{
			if(!level.isClientSide && shearable.isShearable(player, stack, level, blockPos))
			{
				this.useFuel(stack, player);
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
	
	private void fillWater(Player player, ItemStack stack)
	{
		if(stack.getOrDefault(MIComponents.WATER, 0) != FULL_WATER)
		{
			stack.set(MIComponents.WATER, FULL_WATER);
			player.playNotifySound(SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1f, 1f);
		}
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected)
	{
		SteamDrillFuel fuel = stack.getOrDefault(MIComponents.STEAM_DRILL_FUEL, SteamDrillFuel.EMPTY);
		if(fuel.burnTicks() > 0)
		{
			stack.set(MIComponents.STEAM_DRILL_FUEL, new SteamDrillFuel(Math.max(0, fuel.burnTicks() - 5), fuel.maxBurnTicks()));
			stack.update(MIComponents.WATER, 0, (water) -> Math.max(0, water - 5));
		}
		if(fuel.burnTicks() == 0)
		{
			stack.remove(MIComponents.STEAM_DRILL_FUEL);
		}
		if(stack.getOrDefault(MIComponents.WATER, 0) == 0 &&
		   entity instanceof Player player)
		{
			Inventory inv = player.getInventory();
			for(int i = 0; i < inv.getContainerSize(); ++i)
			{
				if(this.tryFillWater(player, stack, inv.getItem(i)))
				{
					break;
				}
			}
		}
	}
	
	public boolean canUse(ItemStack stack)
	{
		if(stack.getOrDefault(MIComponents.WATER, 0) == 0)
		{
			return false;
		}
		return stack.getOrDefault(MIComponents.STEAM_DRILL_FUEL, SteamDrillFuel.EMPTY).burnTicks() > 0 ||
			   this.consumeFuel(stack, Simulation.SIMULATE) > 0;
	}
	
	private int consumeFuel(ItemStack stack, Simulation simulation)
	{
		int burnTicks = this.getResource(stack).toStack().getBurnTime(null);
		if(burnTicks > 0)
		{
			if(simulation.isActing())
			{
				ItemStack burnt = getResource(stack).toStack();
				setAmount(stack, getAmount(stack) - 1);
				
				if(burnt.hasCraftingRemainingItem())
				{
					new ContainerItem.ItemHandler(stack, this)
							.insertItem(0, burnt.getCraftingRemainingItem(), false, true, true);
				}
			}
			return burnTicks;
		}
		return 0;
	}
	
	@Override
	public int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment)
	{
		return this.getAllEnchantments(stack, enchantment.unwrapLookup()).getLevel(enchantment);
	}
	
	@Override
	public ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup)
	{
		ItemEnchantments.Mutable map = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
		if(!isNotSilkTouch(stack))
		{
			lookup.get(Enchantments.SILK_TOUCH).ifPresent((enchantment) -> map.set(enchantment, 1));
		}
		return map.toImmutable();
	}
	
	@Override
	public boolean isFoil(ItemStack stack)
	{
		return !isNotSilkTouch(stack);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		ItemStack stack = context.getItemInHand();
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Player player = context.getPlayer();
		if(this.canUse(stack))
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
						this.useFuel(stack, player);
					}
					return InteractionResult.sidedSuccess(level.isClientSide);
				}
			}
		}
		return super.useOn(context);
	}
	
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		SteamDrillFuel fuel = stack.getOrDefault(MIComponents.STEAM_DRILL_FUEL, SteamDrillFuel.EMPTY);
		return Optional.of(new SteamChainsawTooltipData(
				stack.getOrDefault(MIComponents.WATER, 0) * 100 / 18000,
				fuel.burnTicks(),
				Math.max(1, fuel.maxBurnTicks()),
				this.getResource(stack),
				this.getAmount(stack)
		));
	}
	
	@Override
	public boolean overrideStackedOnOther(ItemStack stackBarrel, Slot slot, ClickAction clickType, Player player)
	{
		return handleStackedOnOther(stackBarrel, slot, clickType, player);
	}
	
	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stackBarrel, ItemStack itemStack, Slot slot, ClickAction clickType, Player player,
											SlotAccess cursorStackReference)
	{
		return handleOtherStackedOnMe(stackBarrel, itemStack, slot, clickType, player, cursorStackReference);
	}
	
	@Override
	public boolean handleClick(Player player, ItemStack barrelLike, Mutable<ItemStack> otherStack)
	{
		// Try to refill water first if it's contained in the other stack
		if(this.tryFillWater(player, barrelLike, otherStack.getValue()))
		{
			return true;
		}
		
		return ItemContainingItemHelper.super.handleClick(player, barrelLike, otherStack);
	}
	
	private boolean tryFillWater(Player player, ItemStack barrelLike, ItemStack fillSource)
	{
		IFluidHandlerItem otherStorage = fillSource.getCapability(Capabilities.FluidHandler.ITEM);
		
		if(otherStorage != null)
		{
			long totalWater = 0;
			for(int tank = 0; tank < otherStorage.getTanks(); ++tank)
			{
				if(otherStorage.getFluidInTank(tank).getFluid() == Fluids.WATER)
				{
					totalWater += otherStorage.getFluidInTank(tank).getAmount();
				}
			}
			
			if(totalWater * fillSource.getCount() >= FluidType.BUCKET_VOLUME)
			{
				this.fillWater(player, barrelLike);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
	{
		SteamChainsawTooltipData data = (SteamChainsawTooltipData) this.getTooltipImage(stack).orElseThrow();
		
		// Water %
		tooltip.add(MIText.WaterPercent.text(data.waterLevel).setStyle(TextHelper.WATER_TEXT));
		int barWater = (int) Math.ceil(data.waterLevel / 5d);
		int barVoid = 20 - barWater;
		// Water bar
		tooltip.add(Component.literal("|".repeat(barWater)).setStyle(TextHelper.WATER_TEXT)
				.append(Component.literal("|".repeat(barVoid)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6b6b6b)))));
		// Fuel left
		if(data.burnTicks > 0)
		{
			tooltip.add(MIText.SecondsLeft.text(data.burnTicks / 100).setStyle(TextHelper.GRAY_TEXT));
		}
		
		// Enchantments
		if(context.registries() != null)
		{
			for(Object2IntMap.Entry<Holder<Enchantment>> entry : this.getAllEnchantments(stack, context.registries().lookupOrThrow(Registries.ENCHANTMENT)).entrySet())
			{
				tooltip.add(Enchantment.getFullname(entry.getKey(), entry.getIntValue()));
			}
		}
	}
	
	@Override
	public StorageBehaviour<ItemVariant> getBehaviour()
	{
		return BEHAVIOR;
	}
	
	public record SteamChainsawTooltipData(
			int waterLevel, int burnTicks, int maxBurnTicks, ItemVariant variant, long amount
	)
			implements TooltipComponent
	{
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
}
