package net.swedz.extended_industrialization;

import com.google.common.collect.Sets;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import net.swedz.extended_industrialization.datagen.DatagenDelegator;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.LargeElectricFurnaceBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower.TeslaTowerBlockEntity;
import net.swedz.extended_industrialization.network.EIPackets;
import net.swedz.tesseract.neoforge.api.MCIdentifiable;
import net.swedz.tesseract.neoforge.capabilities.CapabilitiesListeners;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;
import net.swedz.tesseract.neoforge.registry.holder.FluidHolder;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Mod(EI.ID)
public final class EI
{
	public static final String ID   = "extended_industrialization";
	public static final String NAME = "Extended Industrialization";
	
	public static ResourceLocation id(String name)
	{
		return ResourceLocation.fromNamespaceAndPath(ID, name);
	}
	
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	
	// TODO use this for translation generating
	public static Set<MCIdentifiable> getAllIdentifiables()
	{
		Set<MCIdentifiable> identifiables = Sets.newHashSet();
		identifiables.addAll(EIItems.values());
		identifiables.addAll(EIBlocks.values());
		identifiables.addAll(EIFluids.values());
		return identifiables;
	}
	
	public EI(IEventBus bus, ModContainer container)
	{
		container.registerConfig(ModConfig.Type.STARTUP, EIConfig.SPEC);
		EIConfig.loadConfig();
		bus.addListener(FMLCommonSetupEvent.class, (event) -> EIConfig.loadConfig());
		
		EILocalizedListeners.INSTANCE.init();
		
		EIComponents.init(bus);
		EIArmorMaterials.init(bus);
		EIItems.init(bus);
		EIBlocks.init(bus);
		EIFluids.init(bus);
		EIOtherRegistries.init(bus);
		
		bus.register(new DatagenDelegator());
		
		bus.addListener(FMLCommonSetupEvent.class, (event) ->
		{
			EIItems.values().forEach(ItemHolder::triggerRegistrationListener);
			EIBlocks.values().forEach(BlockHolder::triggerRegistrationListener);
			EIFluids.values().forEach(FluidHolder::triggerRegistrationListener);
		});
		
		bus.addListener(RegisterCapabilitiesEvent.class, (event) -> CapabilitiesListeners.triggerAll(ID, event));
		bus.addListener(RegisterPayloadHandlersEvent.class, EIPackets::init);
		
		bus.addListener(RegisterDataMapTypesEvent.class, EIDataMaps::init);
		
		NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, DataMapsUpdatedEvent.class, (event) ->
				event.ifRegistry(Registries.BLOCK, (registry) -> LargeElectricFurnaceBlockEntity.initTiers()));
		TeslaTowerBlockEntity.registerTieredShapes();
	}
}
