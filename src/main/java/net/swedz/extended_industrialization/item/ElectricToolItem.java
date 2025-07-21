package net.swedz.extended_industrialization.item;

import aztech.modern_industrialization.MIComponents;
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
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
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
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIArmorMaterials;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.EISounds;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.component.RainbowDataComponent;
import net.swedz.extended_industrialization.entity.NanoSaberSweepEntity;
import net.swedz.extended_industrialization.proxy.EIProxy;
import net.swedz.tesseract.neoforge.api.Assert;
import net.swedz.tesseract.neoforge.helper.ColorHelper;
import net.swedz.tesseract.neoforge.item.DynamicDyedItem;
import net.swedz.tesseract.neoforge.proxy.Proxies;
import net.swedz.tesseract.neoforge.proxy.builtin.TesseractProxy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

import static aztech.modern_industrialization.MITooltips.*;
import static net.swedz.extended_industrialization.EITooltips.*;
import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.line;

@EventBusSubscriber(modid = EI.ID)
public class ElectricToolItem extends Item implements DynamicToolItem, ISimpleEnergyItem, DynamicDyedItem, ToggleableItem
{
	public static final  int SPEED_MIN        = 1;
	public static final  int SPEED_MAX        = 10;
	private static final int SPEED_MULTIPLIER = 10;
	
	public static final long ENERGY_COST = 2048;
	
	public enum Type
	{
		DRILL(
				60 * 20 * CableTier.HV.getMaxTransfer(),
				8,
				true, true,
				EIText.ELECTRIC_TOOL_HELP_2_FORTUNE_SILK_TOUCH,
				Mode.SILK_TOUCH, Mode.FORTUNE
		),
		CHAINSAW(
				60 * 20 * CableTier.HV.getMaxTransfer(),
				10,
				true, false,
				EIText.ELECTRIC_TOOL_HELP_2_FORTUNE_LOOTING,
				Mode.SILK_TOUCH, Mode.FORTUNE_LOOTING
		),
		SABER(
				60 * 20 * CableTier.HV.getMaxTransfer(),
				14,
				false, false,
				EIText.ELECTRIC_TOOL_HELP_2_LOOTING_BEHEADING,
				Mode.LOOTING, Mode.BEHEADING
		),
		ULTIMATE(
				60 * 20 * CableTier.EV.getMaxTransfer(),
				20,
				true, true,
				EIText.ELECTRIC_TOOL_HELP_2_FORTUNE_LOOTING,
				Mode.SILK_TOUCH, Mode.FORTUNE_LOOTING
		);
		
		private final long       energyCapacity;
		private final int        damage;
		private final boolean    adjustableSpeed;
		private final boolean    canDo3by3;
		private final EIText     helpText;
		private final List<Mode> modes;
		
		Type(long energyCapacity, int damage, boolean adjustableSpeed, boolean canDo3by3, EIText helpText, Mode... modes)
		{
			Assert.that(modes.length > 0, "Electric tool type must have at least one mode");
			this.energyCapacity = energyCapacity;
			this.damage = damage;
			this.adjustableSpeed = adjustableSpeed;
			this.canDo3by3 = canDo3by3;
			this.helpText = helpText;
			this.modes = List.of(modes);
		}
		
		public long energyCapacity()
		{
			return energyCapacity;
		}
		
		public int damage()
		{
			return damage;
		}
		
		public boolean hasAdjustableSpeed()
		{
			return adjustableSpeed;
		}
		
		public boolean canDo3by3()
		{
			return canDo3by3;
		}
		
		public EIText helpText()
		{
			return helpText;
		}
		
		public List<Mode> modes()
		{
			return modes;
		}
		
		public Mode nextMode(Mode current)
		{
			Assert.notNull(current);
			int index = modes.indexOf(current);
			Assert.that(index >= 0, "The type does not support the mode " + current);
			int nextIndex = index + 1;
			return nextIndex < modes.size() ? modes.get(nextIndex) : modes.getFirst();
		}
		
		public Mode defaultMode()
		{
			return modes.getFirst();
		}
	}
	
	public enum Mode
	{
		SILK_TOUCH(
				new Text(
						EIText.TOOL_MODE_SILK_TOUCH,
						EIText.TOOL_SWITCHED_SILK_TOUCH
				),
				Enchantments.SILK_TOUCH
		),
		FORTUNE(
				new Text(
						EIText.TOOL_MODE_FORTUNE,
						EIText.TOOL_SWITCHED_FORTUNE
				),
				Enchantments.FORTUNE
		),
		FORTUNE_LOOTING(
				new Text(
						EIText.TOOL_MODE_FORTUNE_LOOTING,
						EIText.TOOL_SWITCHED_FORTUNE
				),
				Enchantments.FORTUNE, Enchantments.LOOTING
		),
		LOOTING(
				new Text(
						EIText.TOOL_MODE_LOOTING,
						EIText.TOOL_SWITCHED_LOOTING
				),
				Enchantments.LOOTING
		),
		BEHEADING(
				new Text(
						EIText.TOOL_MODE_BEHEADING,
						EIText.TOOL_SWITCHED_BEHEADING
				)
		);
		
		private final Text                       text;
		private final ResourceKey<Enchantment>[] enchantments;
		
		@SafeVarargs
		Mode(Text text,
			 ResourceKey<Enchantment>... enchantments)
		{
			this.text = text;
			this.enchantments = enchantments;
		}
		
		public Text text()
		{
			return text;
		}
		
		public ResourceKey<Enchantment>[] enchantments()
		{
			return enchantments;
		}
		
		public record Text(
				EIText name,
				EIText switched
		)
		{
		}
	}
	
	private final Type toolType;
	
	public ElectricToolItem(Properties properties, Type toolType)
	{
		super(properties
				.stacksTo(1)
				.rarity(Rarity.UNCOMMON)
				.component(EIComponents.HIDE_BAR, false)
				.component(EIComponents.ELECTRIC_TOOL_MODE, toolType.defaultMode())
				.component(EIComponents.ELECTRIC_TOOL_SPEED, SPEED_MAX)
				.component(MIComponents.ENERGY, 0L));
		this.toolType = toolType;
	}
	
	public Type getToolType()
	{
		return toolType;
	}
	
	public static Mode getMode(ItemStack stack)
	{
		if(!(stack.getItem() instanceof ElectricToolItem item))
		{
			throw new IllegalArgumentException("Cannot get tool mode for a non electric tool item");
		}
		return stack.getOrDefault(EIComponents.ELECTRIC_TOOL_MODE, item.getToolType().defaultMode());
	}
	
	public static void setMode(ItemStack stack, Mode mode)
	{
		Assert.notNull(mode);
		stack.set(EIComponents.ELECTRIC_TOOL_MODE, mode);
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
	
	@Override
	public int getDyeColor(DyeColor dyeColor)
	{
		return switch (toolType)
		{
			case SABER, ULTIMATE -> ColorHelper.getVibrantColor(dyeColor);
			default -> throw new UnsupportedOperationException();
		};
	}
	
	@Override
	public int getDefaultDyeColor()
	{
		return switch (toolType)
		{
			case SABER -> EIArmorMaterials.NANO_COLOR;
			case ULTIMATE -> 0xFFFF0000;
			default -> throw new UnsupportedOperationException();
		};
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
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access)
	{
		if(toolType.canDo3by3() && action == ClickAction.SECONDARY && other.isEmpty())
		{
			this.setActivated(player, stack, !this.isActivated(stack));
			if(player.level().isClientSide())
			{
				player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
			}
			return true;
		}
		return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
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
		
		public void drop(Level level, Player player, Area area)
		{
			BlockPos pos = player.blockPosition();
			
			totalDrops.forEach((drop) -> ItemHandlerHelper.giveItemToPlayer(player, drop));
			
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
				LAST_CLICKED_FACE.put(player, new ClickedBlock(event.getPos().immutable(), event.getFace()));
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
				MERGED_DROPS.drop(level, player, area);
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
				
				Optional<Player> player = Proxies.get(TesseractProxy.class).findUserWithItem(EquipmentSlot.MAINHAND, stack);
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
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return !newStack.is(this) ||
			   slotChanged;
	}
	
	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack)
	{
		return !newStack.is(this) ||
			   this.getStoredEnergy(newStack) == 0 ||
			   Proxies.get(EIProxy.class).shouldCauseElectricToolBreakReset();
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
		if(toolType.hasAdjustableSpeed())
		{
			tooltip.add(line(EIText.TOOL_MINING_SPEED)
					.arg((float) ElectricToolItem.getToolSpeed(stack) / ElectricToolItem.SPEED_MAX, SPACED_PERCENTAGE_PARSER));
		}
		
		if(toolType.canDo3by3())
		{
			tooltip.add(line(EIText.TOOL_MINING_AREA)
					.arg((this.isActivated(stack) ? EIText.TOOL_MINING_AREA_3_BY_3 : EIText.TOOL_MINING_AREA_1_BY_1).text().withStyle(NUMBER_TEXT)));
		}
		
		if(context.registries() != null)
		{
			tooltip.add(line(EIText.TOOL_MODE).arg(getMode(stack).text().name().text().withStyle(NUMBER_TEXT)));
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		var stack = player.getItemInHand(hand);
		if(hand == InteractionHand.MAIN_HAND && player.isShiftKeyDown())
		{
			var mode = toolType.nextMode(getMode(stack));
			setMode(stack, mode);
			if(!level.isClientSide())
			{
				player.displayClientMessage(mode.text().switched().text(), true);
			}
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
		}
		else if(toolType == Type.SABER && this.getStoredEnergy(stack) > 0)
		{
			if(!player.getCooldowns().isOnCooldown(this))
			{
				level.playSound(player, player.blockPosition(), EISounds.NANO_SABER_SWEEP_SWING.get(), SoundSource.PLAYERS, 1, 1);
				
				if(!level.isClientSide())
				{
					this.tryUseEnergy(stack, ENERGY_COST * 4);
					
					var color = stack.get(DataComponents.DYED_COLOR);
					int colorRGB = color == null ? this.getDefaultDyeColor() : color.rgb();
					boolean rainbow = stack.getOrDefault(EIComponents.RAINBOW, new RainbowDataComponent(false, true)).value();
					
					Vec3 look = player.getLookAngle().normalize();
					Vec3 spawnPos = player.position().add(0, player.getEyeHeight() / 2f, 0).add(look.multiply(0.25, 0.25, 0.25));
					Vec3 target = player.getEyePosition().add(look.multiply(100, 100, 100));
					Vec3 motion = target.subtract(spawnPos).normalize();
					NanoSaberSweepEntity sweep = new NanoSaberSweepEntity(level, player, motion, colorRGB, rainbow, toolType.damage(), getMode(stack) == Mode.BEHEADING);
					sweep.setPos(spawnPos.x(), spawnPos.y(), spawnPos.z());
					level.addFreshEntity(sweep);
					
					player.getCooldowns().addCooldown(this, 20);
				}
				
				return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
			}
			else
			{
				return InteractionResultHolder.consume(stack);
			}
		}
		return super.use(level, player, hand);
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
			for(var enchantment : getMode(stack).enchantments())
			{
				includeEnchantment(lookup, enchantments, enchantment);
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
	
	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player)
	{
		return toolType != Type.SABER || !player.isCreative();
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
