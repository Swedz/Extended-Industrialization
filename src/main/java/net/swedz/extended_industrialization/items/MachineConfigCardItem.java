package net.swedz.extended_industrialization.items;

import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.swedz.extended_industrialization.mixin.mi.accessor.ConfigurableItemStackAccessor;
import net.swedz.extended_industrialization.EIText;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;

import static aztech.modern_industrialization.MITooltips.*;

public final class MachineConfigCardItem extends Item
{
	private static final String TAG = "machine_config";
	
	public MachineConfigCardItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		Player player = context.getPlayer();
		if(player != null)
		{
			InteractionHand usedHand = context.getHand();
			ItemStack itemStack = player.getItemInHand(usedHand);
			BlockEntity hitBlockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
			if(hitBlockEntity instanceof MachineBlockEntity machine)
			{
				if(!context.getLevel().isClientSide())
				{
					if(player.isShiftKeyDown())
					{
						MachineConfig config = readFromMachine(machine);
						itemStack.getOrCreateTag().put(TAG, config.serialize());
						player.displayClientMessage(EIText.MACHINE_CONFIG_CARD_SAVE.text(), true);
					}
					else
					{
						CompoundTag machineConfigTag = itemStack.getTagElement(TAG);
						if(machineConfigTag != null)
						{
							MachineConfig config = readFromNBT(machineConfigTag);
							
							if(writeToMachine(machine, config, Simulation.SIMULATE))
							{
								writeToMachine(machine, config, Simulation.ACT);
								player.displayClientMessage(EIText.MACHINE_CONFIG_CARD_APPLY_SUCCESS.text(), true);
							}
							else
							{
								player.displayClientMessage(EIText.MACHINE_CONFIG_CARD_APPLY_FAILED.text(), true);
							}
						}
					}
				}
				return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
			}
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		if(player.isShiftKeyDown())
		{
			player.getItemInHand(usedHand).removeTagKey(TAG);
			player.displayClientMessage(EIText.MACHINE_CONFIG_CARD_CLEAR.text(), true);
			return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
		}
		return super.use(level, player, usedHand);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		CompoundTag configTag = stack.getTagElement(TAG);
		if(configTag != null)
		{
			Block machineBlock = BuiltInRegistries.BLOCK.get(new ResourceLocation(configTag.getString("machine_block")));
			tooltipComponents.add(EIText.MACHINE_CONFIG_CARD_CONFIGURED.text(ITEM_PARSER.parse(machineBlock.asItem())));
		}
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		CompoundTag configTag = stack.getTagElement(TAG);
		if(configTag != null)
		{
			Block machineBlock = BuiltInRegistries.BLOCK.get(new ResourceLocation(configTag.getString("machine_block")));
			Item item = machineBlock.asItem();
			return Optional.of(new TooltipData(item.getDefaultInstance()));
		}
		return Optional.empty();
	}
	
	public record TooltipData(ItemStack machineItemStack) implements TooltipComponent
	{
	}
	
	private static MachineConfig readFromMachine(MachineBlockEntity machine)
	{
		Block block = machine.getBlockState().getBlock();
		
		MIInventory inventory = machine.getInventory();
		
		List<MachineConfig.Slot> slots = Lists.newArrayList();
		
		int itemIndex = 0;
		for(ConfigurableItemStack itemStack : inventory.getItemStacks())
		{
			slots.add(new MachineConfig.ItemSlot(itemIndex, itemStack.getAdjustedCapacity(), itemStack.getLockedInstance()));
			itemIndex++;
		}
		
		int fluidIndex = 0;
		for(ConfigurableFluidStack fluidStack : inventory.getFluidStacks())
		{
			slots.add(new MachineConfig.FluidSlot(fluidIndex, fluidStack.getLockedInstance()));
			fluidIndex++;
		}
		
		return new MachineConfig(block, slots, itemIndex, fluidIndex);
	}
	
	private static MachineConfig readFromNBT(CompoundTag tag)
	{
		Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(tag.getString("machine_block")));
		
		List<MachineConfig.Slot> slots = Lists.newArrayList();
		int itemSlotCount = 0;
		int fluidSlotCount = 0;
		ListTag slotsTag = tag.getList("slots", Tag.TAG_COMPOUND);
		for(Tag slotTagTag : slotsTag)
		{
			CompoundTag slotTag = (CompoundTag) slotTagTag;
			int index = slotTag.getInt("index");
			String typeId = slotTag.getString("type");
			if(typeId.equals("item"))
			{
				int capacity = slotTag.getInt("capacity");
				Item lock = BuiltInRegistries.ITEM.get(new ResourceLocation(slotTag.getString("lock")));
				slots.add(new MachineConfig.ItemSlot(index, capacity, lock));
				itemSlotCount++;
			}
			else if(typeId.equals("fluid"))
			{
				Fluid lock = BuiltInRegistries.FLUID.get(new ResourceLocation(slotTag.getString("lock")));
				slots.add(new MachineConfig.FluidSlot(index, lock));
				fluidSlotCount++;
			}
			else
			{
				throw new IllegalArgumentException("Malformed machine config nbt: %s".formatted(tag.toString()));
			}
		}
		
		return new MachineConfig(block, slots, itemSlotCount, fluidSlotCount);
	}
	
	private static boolean writeToMachine(MachineBlockEntity machine, MachineConfig config, Simulation simulation)
	{
		if(!config.matches(machine))
		{
			return false;
		}
		
		boolean success = true;
		
		for(MachineConfig.Slot slot : config.slots())
		{
			AbstractConfigurableStack stack = slot.stack(machine.getInventory());
			if(slot.lock() != null && stack.canPlayerLock())
			{
				if(!stack.playerLock(slot.lock(), simulation))
				{
					success = false;
				}
			}
			if(slot.capacity() >= 0 && slot instanceof MachineConfig.ItemSlot)
			{
				ConfigurableItemStack itemStack = (ConfigurableItemStack) stack;
				if(itemStack.getAmount() <= slot.capacity())
				{
					if(simulation == Simulation.ACT)
					{
						ConfigurableItemStackAccessor capacityAccessor = (ConfigurableItemStackAccessor) stack;
						capacityAccessor.setAdjustedCapacity(slot.capacity());
					}
				}
				else
				{
					success = false;
				}
			}
		}
		
		return success;
	}
	
	private record MachineConfig(Block machineBlock, List<Slot> slots, int itemSlotCount, int fluidSlotCount)
	{
		public boolean matches(MachineBlockEntity machine)
		{
			return machine.getBlockState().getBlock() == machineBlock &&
					machine.getInventory().getItemStacks().size() == itemSlotCount &&
					machine.getInventory().getFluidStacks().size() == fluidSlotCount;
		}
		
		public CompoundTag serialize()
		{
			CompoundTag tag = new CompoundTag();
			
			tag.putString("machine_block", BuiltInRegistries.BLOCK.getKey(machineBlock).toString());
			
			ListTag slotsTag = new ListTag();
			for(Slot slot : slots)
			{
				slotsTag.add(slot.serialize());
			}
			tag.put("slots", slotsTag);
			
			return tag;
		}
		
		private record ItemSlot(
				int index, int capacity, Item lock
		) implements MachineConfig.Slot<Item, ConfigurableItemStack>
		{
			@Override
			public ConfigurableItemStack stack(MIInventory inventory)
			{
				return inventory.getItemStacks().get(index);
			}
			
			@Override
			public CompoundTag serialize()
			{
				CompoundTag tag = new CompoundTag();
				
				tag.putInt("index", index);
				tag.putString("type", "item");
				
				tag.putString("lock", BuiltInRegistries.ITEM.getKey(lock).toString());
				tag.putInt("capacity", capacity);
				
				return tag;
			}
		}
		
		private record FluidSlot(int index, Fluid lock) implements MachineConfig.Slot<Fluid, ConfigurableFluidStack>
		{
			@Override
			public int capacity()
			{
				return -1;
			}
			
			@Override
			public ConfigurableFluidStack stack(MIInventory inventory)
			{
				return inventory.getFluidStacks().get(index);
			}
			
			@Override
			public CompoundTag serialize()
			{
				CompoundTag tag = new CompoundTag();
				
				tag.putInt("index", index);
				tag.putString("type", "fluid");
				
				tag.putString("lock", BuiltInRegistries.FLUID.getKey(lock).toString());
				
				return tag;
			}
		}
		
		interface Slot<T, S extends AbstractConfigurableStack>
		{
			int index();
			
			int capacity();
			
			T lock();
			
			S stack(MIInventory inventory);
			
			CompoundTag serialize();
		}
	}
}
