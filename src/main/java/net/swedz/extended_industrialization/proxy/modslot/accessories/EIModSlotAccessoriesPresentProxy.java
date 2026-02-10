package net.swedz.extended_industrialization.proxy.modslot.accessories;

import com.google.common.collect.Lists;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;
import net.swedz.tesseract.neoforge.proxy.ProxyEnvironment;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@ProxyEntrypoint(environment = ProxyEnvironment.MOD, modid = "accessories")
public class EIModSlotAccessoriesPresentProxy extends EIModSlotAccessoriesProxy
{
	@Override
	public boolean isLoaded()
	{
		return true;
	}
	
	@Override
	public List<ItemStack> getContents(Player player, Predicate<ItemStack> filter)
	{
		List<ItemStack> contents = Lists.newArrayList();
		
		AccessoriesCapability.getOptionally(player).ifPresent((capability) ->
		{
			for(var entry : capability.getEquipped(filter))
			{
				var stack = entry.stack();
				contents.add(stack);
			}
		});
		
		return Collections.unmodifiableList(contents);
	}
}
