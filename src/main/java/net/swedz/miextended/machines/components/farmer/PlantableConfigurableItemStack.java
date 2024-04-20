package net.swedz.miextended.machines.components.farmer;

import aztech.modern_industrialization.inventory.ChangeListener;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.PlantType;

final class PlantableConfigurableItemStack extends ChangeListener implements Comparable<PlantableConfigurableItemStack>
{
	private final FarmerComponentPlantableStacks farmerComponentPlantableStacks;
	private final ConfigurableItemStack          stack;
	
	private Item lastUpdateItem;
	
	private PlantableType plantable = PlantableType.NOT_PLANTABLE;
	
	PlantableConfigurableItemStack(FarmerComponentPlantableStacks farmerComponentPlantableStacks, ConfigurableItemStack stack)
	{
		this.farmerComponentPlantableStacks = farmerComponentPlantableStacks;
		this.stack = stack;
	}
	
	public ConfigurableItemStack getStack()
	{
		return stack;
	}
	
	public Item getItem()
	{
		return stack.getResource().getItem();
	}
	
	public boolean isPlantable()
	{
		return plantable != PlantableType.NOT_PLANTABLE;
	}
	
	public BlockState getPlant(BlockPos pos)
	{
		if(plantable == PlantableType.VANILLA_BLOCK)
		{
			return ((BlockItem) this.getItem()).getBlock().defaultBlockState();
		}
		else if(plantable == PlantableType.NEOFORGE_PLANTABLE)
		{
			return ((IPlantable) this.getItem()).getPlant(farmerComponentPlantableStacks.getFarmer().getLevel(), pos);
		}
		throw new IllegalStateException("Tried to get plant of non-plantable");
	}
	
	@Override
	protected void onChange()
	{
		ItemStack itemStack = stack.getResource().toStack();
		Item item = itemStack.getItem();
		if(lastUpdateItem != item)
		{
			if(itemStack.isEmpty())
			{
				plantable = PlantableType.NOT_PLANTABLE;
			}
			else
			{
				if(itemStack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS) && item instanceof BlockItem)
				{
					plantable = PlantableType.VANILLA_BLOCK;
				}
				else if(item instanceof IPlantable plant && plant.getPlantType(farmerComponentPlantableStacks.getFarmer().getLevel(), null) == PlantType.CROP)
				{
					plantable = PlantableType.NEOFORGE_PLANTABLE;
				}
				else
				{
					plantable = PlantableType.NOT_PLANTABLE;
				}
			}
		}
		lastUpdateItem = item;
	}
	
	@Override
	protected boolean isValid(Object token)
	{
		return true;
	}
	
	@Override
	public int compareTo(PlantableConfigurableItemStack other)
	{
		return BuiltInRegistries.ITEM.getKey(this.getItem()).compareTo(BuiltInRegistries.ITEM.getKey(other.getItem()));
	}
}
