package net.swedz.extended_industrialization.proxy.modslot;

import com.google.common.collect.Lists;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;
import net.swedz.tesseract.neoforge.proxy.ProxyEnvironment;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@ProxyEntrypoint(environment = ProxyEnvironment.MOD, modid = "curios")
public class EIModSlotCuriosProxy extends EIModSlotProxy
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
		
		CuriosApi.getCuriosInventory(player).ifPresent((inventory) ->
				inventory.getCurios().forEach((identifier, slot) ->
				{
					IDynamicStackHandler stacks = slot.getStacks();
					for(int index = 0; index < stacks.getSlots(); index++)
					{
						ItemStack stack = stacks.getStackInSlot(index);
						if(filter.test(stack))
						{
							contents.add(stack);
						}
					}
				}));
		
		return Collections.unmodifiableList(contents);
	}
}
