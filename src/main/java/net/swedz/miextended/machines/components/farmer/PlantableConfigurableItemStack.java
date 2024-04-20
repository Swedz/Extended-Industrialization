package net.swedz.miextended.machines.components.farmer;

import aztech.modern_industrialization.inventory.ChangeListener;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.IPlantable;

public final class PlantableConfigurableItemStack extends ChangeListener
{
	private final ConfigurableItemStack stack;
	
	private Item lastUpdateItem;
	
	private boolean plantable;
	
	PlantableConfigurableItemStack(ConfigurableItemStack stack)
	{
		this.stack = stack;
	}
	
	public ConfigurableItemStack getStack()
	{
		return stack;
	}
	
	public ItemVariant getItemVariant()
	{
		return (stack.isPlayerLocked() || stack.isMachineLocked()) && stack.getResource().isBlank() ?
				ItemVariant.of(stack.getLockedInstance()) :
				stack.getResource();
	}
	
	public Item getItem()
	{
		return this.getItemVariant().getItem();
	}
	
	public boolean isPlantable()
	{
		return plantable;
	}
	
	public IPlantable asPlantable()
	{
		if(!plantable)
		{
			throw new IllegalStateException("Tried to get plantable of non-plantable stack");
		}
		return (IPlantable) ((BlockItem) this.getItem()).getBlock();
	}
	
	public BlockState getPlant(Level level, BlockPos pos)
	{
		return this.asPlantable().getPlant(level, pos);
	}
	
	@Override
	protected void onChange()
	{
		ItemVariant itemVariant = this.getItemVariant();
		Item item = itemVariant.getItem();
		if(lastUpdateItem != item)
		{
			plantable = item instanceof BlockItem blockItem &&
						blockItem.getBlock() instanceof IPlantable;
		}
		lastUpdateItem = item;
	}
	
	@Override
	protected boolean isValid(Object token)
	{
		return true;
	}
}
