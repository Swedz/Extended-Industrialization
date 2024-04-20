package net.swedz.miextended.machines.components.farmer;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class FarmerComponentPlantableStacks
{
	private final FarmerComponent farmer;
	
	private final Map<ConfigurableItemStack, PlantableConfigurableItemStack> listeners = Maps.newHashMap();
	
	private final List<PlantableConfigurableItemStack> items = Lists.newArrayList();
	
	FarmerComponentPlantableStacks(FarmerComponent farmer)
	{
		this.farmer = farmer;
	}
	
	public FarmerComponent getFarmer()
	{
		return farmer;
	}
	
	public List<PlantableConfigurableItemStack> getItems()
	{
		return new ArrayList<>(items);
	}
	
	public void update(List<ConfigurableItemStack> stacks)
	{
		for(ConfigurableItemStack stack : stacks)
		{
			PlantableConfigurableItemStack listener = listeners.remove(stack);
			if(listener != null)
			{
				stack.removeListener(listener);
			}
		}
		listeners.clear();
		items.clear();
		for(ConfigurableItemStack stack : stacks)
		{
			PlantableConfigurableItemStack listener = new PlantableConfigurableItemStack(stack);
			listener.listenAll(List.of(stack), null);
			listeners.put(stack, listener);
			items.add(listener);
			listener.onChange();
		}
	}
}
