package net.swedz.extended_industrialization.proxy.accessories;

import aztech.modern_industrialization.api.energy.EnergyApi;
import dev.technici4n.grandpower.api.ILongEnergyStorage;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories.impl.ExpandedSimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;
import net.swedz.tesseract.neoforge.proxy.ProxyEnvironment;

import java.util.Optional;

@ProxyEntrypoint(environment = ProxyEnvironment.MOD, modid = "accessories")
public class EIAccessoriesLoadedProxy extends EIAccessoriesProxy
{
	@Override
	public boolean isLoaded()
	{
		return true;
	}
	
	@Override
	public long chargeAccessories(Player player, long maxEu)
	{
		long eu = 0;
		
		Optional<AccessoriesCapability> capabilityOptional = AccessoriesCapability.getOptionally(player);
		if(capabilityOptional.isPresent())
		{
			AccessoriesCapability capability = capabilityOptional.get();
			
			for(SlotType slotType : AccessoriesAPI.getUsedSlotsFor(player))
			{
				AccessoriesContainer container = capability.getContainer(slotType);
				
				if(container != null)
				{
					ExpandedSimpleContainer content = container.getAccessories();
					
					for(int index = 0; index < content.getContainerSize(); index++)
					{
						ItemStack stack = content.getItem(index);
						
						ILongEnergyStorage energy = stack.getCapability(EnergyApi.ITEM);
						if(energy != null)
						{
							long received = energy.receive(Math.max(0, maxEu - eu), false);
							if(received > 0)
							{
								eu += received;
								if(eu == maxEu)
								{
									return eu;
								}
							}
						}
					}
				}
			}
		}
		
		return eu;
	}
}
