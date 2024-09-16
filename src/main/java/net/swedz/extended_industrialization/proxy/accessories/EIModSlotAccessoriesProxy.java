package net.swedz.extended_industrialization.proxy.accessories;

import com.google.common.collect.Lists;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;
import net.swedz.tesseract.neoforge.proxy.ProxyEnvironment;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@ProxyEntrypoint(priority = 1, environment = ProxyEnvironment.MOD, modid = "accessories")
public class EIModSlotAccessoriesProxy extends EIModSlotProxy
{
	@Override
	public boolean isLoaded()
	{
		return true;
	}
	
	@Override
	public List<ItemStack> getContents(Player player, Predicate<ItemStack> filter)
	{
		List<ItemStack> accessories = Lists.newArrayList();
		
		AccessoriesCapability.getOptionally(player).ifPresent((capability) ->
		{
			for(SlotEntryReference entry : capability.getEquipped(filter))
			{
				ItemStack stack = entry.stack();
				accessories.add(stack);
			}
		});
		
		return Collections.unmodifiableList(accessories);
	}
}
