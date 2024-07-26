package net.swedz.extended_industrialization.machines.components.farmer;

import aztech.modern_industrialization.inventory.ChangeListener;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.world.item.Item;
import net.swedz.extended_industrialization.machines.components.farmer.plantinghandler.PlantingHandler;

import java.util.Optional;

public final class PlantableConfigurableItemStack extends ChangeListener
{
	private final FarmerComponentPlantableStacks parent;
	private final ConfigurableItemStack          stack;
	
	private Item lastUpdateItem;
	
	private Optional<PlantingHandler> plantingHandler = Optional.empty();
	
	PlantableConfigurableItemStack(FarmerComponentPlantableStacks parent, ConfigurableItemStack stack)
	{
		this.parent = parent;
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
		return plantingHandler.isPresent();
	}
	
	public PlantingHandler asPlantable()
	{
		return plantingHandler.orElseThrow(() -> new IllegalStateException("Tried to get plantable of non-plantable stack"));
	}
	
	@Override
	protected void onChange()
	{
		ItemVariant itemVariant = this.getItemVariant();
		Item item = itemVariant.getItem();
		if(lastUpdateItem != item)
		{
			plantingHandler = parent.getFarmer().getPlantingHandlersHolder().getHandler(stack.toStack());
		}
		lastUpdateItem = item;
	}
	
	@Override
	protected boolean isValid(Object token)
	{
		return true;
	}
}
