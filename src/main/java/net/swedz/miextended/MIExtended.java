package net.swedz.miextended;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import net.swedz.miextended.api.MCIdentifiable;
import net.swedz.miextended.api.capabilities.CapabilitiesListeners;
import net.swedz.miextended.api.isolatedlistener.IsolatedListeners;
import net.swedz.miextended.datagen.DatagenDelegator;
import net.swedz.miextended.datamaps.MIEDataMaps;
import net.swedz.miextended.registry.MIEOtherRegistries;
import net.swedz.miextended.registry.blocks.BlockHolder;
import net.swedz.miextended.registry.blocks.MIEBlocks;
import net.swedz.miextended.registry.fluids.FluidHolder;
import net.swedz.miextended.registry.fluids.MIEFluids;
import net.swedz.miextended.registry.items.ItemHolder;
import net.swedz.miextended.registry.items.MIEItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Mod(MIExtended.ID)
public final class MIExtended
{
	public static final String ID = "miextended";
	
	public static ResourceLocation id(String name)
	{
		return new ResourceLocation(ID, name);
	}
	
	public static final Logger LOGGER = LoggerFactory.getLogger("MI Extended");
	
	// TODO use this for translation generating
	public static Set<MCIdentifiable> getAllIdentifiables()
	{
		Set<MCIdentifiable> identifiables = Sets.newHashSet();
		identifiables.addAll(MIEItems.values());
		identifiables.addAll(MIEBlocks.values());
		identifiables.addAll(MIEFluids.values());
		return identifiables;
	}
	
	public MIExtended(IEventBus bus)
	{
		MIEItems.init(bus);
		MIEBlocks.init(bus);
		MIEFluids.init(bus);
		MIEOtherRegistries.init(bus);
		
		IsolatedListeners.init();
		
		bus.register(new DatagenDelegator());
		
		bus.addListener(FMLCommonSetupEvent.class, (event) ->
		{
			MIEItems.values().forEach(ItemHolder::triggerRegistrationListener);
			MIEBlocks.values().forEach(BlockHolder::triggerRegistrationListener);
			MIEFluids.values().forEach(FluidHolder::triggerRegistrationListener);
		});
		bus.addListener(RegisterCapabilitiesEvent.class, CapabilitiesListeners::triggerAll);
		
		bus.addListener(RegisterDataMapTypesEvent.class, (event) ->
				event.register(MIEDataMaps.FERTILIZER_POTENCY));
	}
}
