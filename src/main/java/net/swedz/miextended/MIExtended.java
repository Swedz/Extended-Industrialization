package net.swedz.miextended;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.swedz.miextended.capabilities.BottleFluidHandler;
import net.swedz.miextended.datagen.DatagenDelegator;
import net.swedz.miextended.fluids.MIEFluids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MIExtended.ID)
public final class MIExtended
{
	public static final String ID = "miextended";
	
	public static ResourceLocation id(String name)
	{
		return new ResourceLocation(ID, name);
	}
	
	public static final Logger LOGGER = LoggerFactory.getLogger("MI Extended");
	
	public MIExtended(IEventBus modBus)
	{
		modBus.register(new DatagenDelegator());
		
		modBus.addListener(RegisterCapabilitiesEvent.class, (event) ->
		{
			event.registerItem(
					Capabilities.FluidHandler.ITEM,
					(stack, __) -> new BottleFluidHandler(
							stack,
							PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER), new ItemStack(Items.GLASS_BOTTLE),
							Fluids.WATER, 250
					),
					Items.GLASS_BOTTLE, Items.POTION
			);
			
			event.registerItem(
					Capabilities.FluidHandler.ITEM,
					(stack, __) -> new BottleFluidHandler(
							stack,
							Items.HONEY_BOTTLE, Items.GLASS_BOTTLE,
							MIEFluids.HONEY.asFluid(), 250
					),
					Items.GLASS_BOTTLE, Items.HONEY_BOTTLE
			);
		});
	}
}
