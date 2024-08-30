package net.swedz.extended_industrialization.item;

import aztech.modern_industrialization.MIComponents;
import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.items.DynamicToolItem;
import aztech.modern_industrialization.items.ItemHelper;
import aztech.modern_industrialization.util.GeometryHelper;
import com.google.common.collect.Lists;
import dev.technici4n.grandpower.api.ISimpleEnergyItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.EIText;
import net.swedz.tesseract.neoforge.helper.ColorHelper;
import net.swedz.tesseract.neoforge.item.DynamicDyedItem;
import net.swedz.tesseract.neoforge.proxy.ProxyManager;
import net.swedz.tesseract.neoforge.proxy.builtin.TesseractProxy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

@EventBusSubscriber(modid = EI.ID)
public class ElectricToolItem extends Item implements DynamicToolItem, ISimpleEnergyItem, DynamicDyedItem, ToggleableItem
{
	public static final  int SPEED_MIN        = 1;
	public static final  int SPEED_MAX        = 10;
	private static final int SPEED_MULTIPLIER = 10;
	
	public static final long ENERGY_COST = 2048;
	
	public enum Type
	{
		DRILL(60 * 20 * CableTier.HV.getMaxTransfer(), 8, false, true),
		CHAINSAW(60 * 20 * CableTier.HV.getMaxTransfer(), 10, true, false),
		ULTIMATE(60 * 20 * CableTier.EV.getMaxTransfer(), 20, true, true);
		
		private final long    energyCapacity;
		private final int     damage;
		private final boolean includeLooting;
		private final boolean canDo3by3;
		
		Type(long energyCapacity, int damage, boolean includeLooting, boolean canDo3by3)
		{
			this.energyCapacity = energyCapacity;
			this.damage = damage;
			this.includeLooting = includeLooting;
			this.canDo3by3 = canDo3by3;
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
		
		public boolean canDo3by3()
		{
			return canDo3by3;
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
	public void setActivated(Player player, ItemStack stack, boolean activated)
	{
		if(toolType.canDo3by3())
		{
			ToggleableItem.super.setActivated(player, stack, activated);
			
			if(!player.level().isClientSide())
			{
				player.displayClientMessage((activated ? EIText.ELECTRIC_TOOL_3_BY_3_TOGGLED_ON : EIText.ELECTRIC_TOOL_3_BY_3_TOGGLED_OFF).text(), true);
			}
		}
	}
	
	public boolean should3By3(ItemStack stack, Player player)
	{
		return toolType.canDo3by3() && this.isActivated(stack) && !player.isShiftKeyDown();
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}
	
	private static MergedDrops MERGED_DROPS = null;
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	private static void mergeDrops(BlockDropsEvent event)
	{
		if(MERGED_DROPS == null)
		{
			return;
		}
		MERGED_DROPS.addAll(event.getDrops(), event.getDroppedExperience());
		event.getDrops().clear();
	}
	
	private record MergedDrops(List<ItemStack> totalDrops)
	{
		private MergedDrops()
		{
			this(Lists.newArrayList());
		}
		
		private void addAll(List<ItemEntity> droppedItems, int droppedExperience)
		{
			outer:
			for(ItemEntity drop : droppedItems)
			{
				ItemStack dropItem = drop.getItem();
				for(ItemStack totalDrop : totalDrops)
				{
					if(ItemStack.isSameItemSameComponents(dropItem, totalDrop))
					{
						totalDrop.grow(dropItem.getCount());
						continue outer;
					}
				}
				totalDrops.add(dropItem);
			}
		}
		
		public void drop(Level level, BlockPos pos, Area area)
		{
			totalDrops.forEach((drop) -> Block.popResource(level, pos, drop));
			
			level.getEntitiesOfClass(
							ExperienceOrb.class,
							new AABB(Vec3.atLowerCornerOf(area.cornerFirst()), Vec3.atLowerCornerOf(area.cornerSecond())).inflate(1)
					)
					.forEach((orb) -> orb.teleportTo(pos.getX(), pos.getY(), pos.getZ()));
		}
	}
	
	private static final WeakHashMap<Player, ClickedBlock> LAST_CLICKED_FACE = new WeakHashMap<>();
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	private static void onLeftClick(PlayerInteractEvent.LeftClickBlock event)
	{
		Player player = event.getEntity();
		if(!player.level().isClientSide())
		{
			PlayerInteractEvent.LeftClickBlock.Action action = event.getAction();
			if(action == PlayerInteractEvent.LeftClickBlock.Action.START ||
			   action == PlayerInteractEvent.LeftClickBlock.Action.STOP)
			{
				LAST_CLICKED_FACE.put(player, new ClickedBlock(event.getPos(), event.getFace()));
			}
			else if(action == PlayerInteractEvent.LeftClickBlock.Action.ABORT)
			{
				LAST_CLICKED_FACE.remove(player);
			}
		}
	}
	
	private record ClickedBlock(BlockPos pos, Direction face)
	{
	}
	
	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner)
	{
		if(state.getDestroySpeed(level, pos) > 0)
		{
			if(miner instanceof Player player)
			{
				Optional<Area> optionalArea = this.getArea(level, player, stack, false);
				if(optionalArea.isEmpty())
				{
					this.tryUseEnergy(stack, ENERGY_COST);
					return true;
				}
				LAST_CLICKED_FACE.remove(player);
				Area area = optionalArea.get();
				
				MERGED_DROPS = new MergedDrops();
				forEachMineableBlock(level, area, player, (minedPos, minedState) ->
				{
					Block minedBlock = minedState.getBlock();
					BlockEntity minedBlockEntity = level.getBlockEntity(minedPos);
					BlockEvent.BreakEvent event = CommonHooks.fireBlockBreak(
							level,
							((ServerPlayer) player).gameMode.getGameModeForPlayer(),
							(ServerPlayer) player,
							minedPos, minedState
					);
					if(!event.isCanceled() && minedBlock.onDestroyedByPlayer(minedState, level, minedPos, player, true, minedState.getFluidState()))
					{
						minedBlock.destroy(level, minedPos, minedState);
						Block.dropResources(minedState, level, minedPos, minedBlockEntity, miner, stack);
					}
				});
				MERGED_DROPS.drop(level, miner.blockPosition(), area);
				MERGED_DROPS = null;
				
				this.tryUseEnergy(stack, ENERGY_COST * 3);
			}
			else
			{
				this.tryUseEnergy(stack, ENERGY_COST);
			}
		}
		return true;
	}
	
	private static HitResult rayTraceSimple(BlockGetter level, Player player, float partialTicks)
	{
		double blockReachDistance = player.blockInteractionRange();
		Vec3 eyePos = player.getEyePosition(partialTicks);
		Vec3 viewVector = player.getViewVector(partialTicks);
		Vec3 target = eyePos.add(viewVector.x * blockReachDistance, viewVector.y * blockReachDistance, viewVector.z * blockReachDistance);
		return level.clip(new ClipContext(eyePos, target, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
	}
	
	public record Area(BlockPos center, BlockPos cornerFirst, BlockPos cornerSecond)
	{
		public static Area of(BlockPos pos, Direction hitFace)
		{
			int face = hitFace.get3DDataValue();
			Vec3 right = GeometryHelper.FACE_RIGHT[face];
			int rightX = (int) right.x();
			int rightY = (int) right.y();
			int rightZ = (int) right.z();
			Vec3 up = GeometryHelper.FACE_UP[face];
			int upX = (int) up.x();
			int upY = (int) up.y();
			int upZ = (int) up.z();
			return new Area(
					pos,
					pos.offset(rightX + upX, rightY + upY, rightZ + upZ),
					pos.offset(-rightX - upX, -rightY - upY, -rightZ - upZ)
			);
		}
	}
	
	public Optional<Area> getArea(BlockGetter level, Player player, ItemStack stack, boolean rayTraceOnly)
	{
		if(!this.should3By3(stack, player))
		{
			return Optional.empty();
		}
		if(!rayTraceOnly)
		{
			ClickedBlock clickedBlock = LAST_CLICKED_FACE.get(player);
			if(clickedBlock != null)
			{
				return Optional.of(Area.of(clickedBlock.pos(), clickedBlock.face()));
			}
		}
		HitResult rayTraceResult = rayTraceSimple(level, player, 0);
		if(rayTraceResult.getType() == HitResult.Type.BLOCK)
		{
			BlockHitResult blockResult = (BlockHitResult) rayTraceResult;
			Direction facing = blockResult.getDirection();
			return Optional.of(Area.of(blockResult.getBlockPos(), facing));
		}
		return Optional.empty();
	}
	
	private boolean isMineableBlock(ItemStack stack, BlockState state, BlockGetter level, BlockPos pos)
	{
		return !state.isAir() &&
			   state.getDestroySpeed(level, pos) > 0 &&
			   this.isValidForBlock(stack, state);
	}
	
	public static void forEachMineableBlock(BlockGetter level, Area area, LivingEntity miner, BiConsumer<BlockPos, BlockState> callback)
	{
		if(miner instanceof Player player)
		{
			ItemStack stack = player.getMainHandItem();
			if(stack.getItem() instanceof ElectricToolItem tool)
			{
				BlockState centerState = level.getBlockState(area.center());
				if(!tool.isMineableBlock(stack, centerState, level, area.center()))
				{
					return;
				}
				callback.accept(area.center(), centerState);
				
				BlockPos.betweenClosed(area.cornerFirst(), area.cornerSecond()).forEach((pos) ->
				{
					if(level.getBlockEntity(pos) == null && !area.center().equals(pos))
					{
						BlockState state = level.getBlockState(pos);
						if(tool.isMineableBlock(stack, state, level, pos))
						{
							callback.accept(pos, state);
						}
					}
				});
			}
		}
	}
	
	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		this.tryUseEnergy(stack, ENERGY_COST);
		return true;
	}
	
	private boolean isValidForBlock(ItemStack stack, BlockState state)
	{
		return this.isSupportedBlock(stack, state) &&
			   !state.is(Tiers.NETHERITE.getIncorrectBlocksForDrops());
	}
	
	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state)
	{
		return (this.getStoredEnergy(stack) > 0 && this.isValidForBlock(stack, state)) ||
			   super.isCorrectToolForDrops(stack, state);
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state)
	{
		if(this.getStoredEnergy(stack) > 0)
		{
			if(this.isValidForBlock(stack, state))
			{
				float speed = getToolSpeed(stack) * SPEED_MULTIPLIER;
				
				Optional<Player> player = ProxyManager.get(TesseractProxy.class).findUserWithItem(EquipmentSlot.MAINHAND, stack);
				if(player.isPresent() && this.should3By3(stack, player.get()))
				{
					speed /= 4;
				}
				
				return speed;
			}
			else
			{
				return 1;
			}
		}
		return 0;
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
		return 0xFF0000;
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
