package net.swedz.extended_industrialization.machines.component;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.DropableComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public final class TransformerTierComponent implements IComponent.ServerOnly, DropableComponent
{
	private final boolean isFrom;
	
	private ItemStack stack = ItemStack.EMPTY;
	private CableTier tier  = CableTier.LV;
	
	public TransformerTierComponent(boolean isFrom)
	{
		this.isFrom = isFrom;
	}
	
	public ItemStack getStack()
	{
		return stack;
	}
	
	public CableTier getTier()
	{
		return tier;
	}
	
	public boolean canInsertEu(CableTier tier)
	{
		return this.tier == tier;
	}
	
	private void setCasingInternal(ItemStack stack)
	{
		this.stack = stack;
		tier = getTierFromCasing(stack);
		if(tier == null)
		{
			tier = CableTier.LV;
		}
	}
	
	private String nbtKey()
	{
		return "casing_" + (isFrom ? "from" : "to");
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.put(this.nbtKey(), stack.saveOptional(registries));
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
		this.setCasingInternal(ItemStack.parseOptional(registries, tag.getCompound(this.nbtKey())));
	}
	
	@Override
	public ItemStack getDrop()
	{
		return this.getStack();
	}
	
	@SuppressWarnings("deprecation")
	private void playCasingPlaceSound(MachineBlockEntity blockEntity)
	{
		ResourceLocation blockKey = tier.itemKey;
		if(blockKey == null)
		{
			return;
		}
		
		BuiltInRegistries.BLOCK.getOptional(blockKey).ifPresent((block) ->
		{
			BlockState casingState = block.defaultBlockState();
			SoundType group = casingState.getSoundType();
			SoundEvent sound = group.getBreakSound();
			blockEntity.getLevel().playSound(null, blockEntity.getBlockPos(), sound, SoundSource.BLOCKS, (group.getVolume() + 1.0F) / 4.0F, group.getPitch() * 0.8F);
		});
	}
	
	public void setCasing(MachineBlockEntity blockEntity, ItemStack stack)
	{
		this.setCasingInternal(stack);
		blockEntity.setChanged();
		blockEntity.sync();
		blockEntity.getLevel().updateNeighborsAt(blockEntity.getBlockPos(), Blocks.AIR);
		this.playCasingPlaceSound(blockEntity);
	}
	
	public static CableTier getTierFromCasing(ItemStack stack)
	{
		if(stack.isEmpty())
		{
			return null;
		}
		ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
		for(var tier : CableTier.allTiers())
		{
			if(tier.itemKey != null && tier.itemKey.equals(itemKey))
			{
				return tier;
			}
		}
		return null;
	}
}
