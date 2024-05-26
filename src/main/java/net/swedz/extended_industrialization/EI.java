package net.swedz.extended_industrialization;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import net.swedz.extended_industrialization.api.MCIdentifiable;
import net.swedz.extended_industrialization.api.capabilities.CapabilitiesListeners;
import net.swedz.extended_industrialization.api.isolatedlistener.IsolatedListeners;
import net.swedz.extended_industrialization.attachments.EIAttachments;
import net.swedz.extended_industrialization.config.EIConfig;
import net.swedz.extended_industrialization.datagen.DatagenDelegator;
import net.swedz.extended_industrialization.datamaps.EIDataMaps;
import net.swedz.extended_industrialization.machines.blockentities.multiblock.LargeElectricFurnaceBlockEntity;
import net.swedz.extended_industrialization.machines.components.craft.potion.PotionRecipe;
import net.swedz.extended_industrialization.network.EIPackets;
import net.swedz.extended_industrialization.registry.EIOtherRegistries;
import net.swedz.extended_industrialization.registry.blocks.BlockHolder;
import net.swedz.extended_industrialization.registry.blocks.EIBlocks;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;
import net.swedz.extended_industrialization.registry.fluids.FluidHolder;
import net.swedz.extended_industrialization.registry.items.EIItems;
import net.swedz.extended_industrialization.registry.items.ItemHolder;
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
		return new ResourceLocation(ID, name);
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
	
	public EI(IEventBus bus)
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EIConfig.SPEC);
		
		EIItems.init(bus);
		EIBlocks.init(bus);
		EIFluids.init(bus);
		EIAttachments.init(bus);
		EIOtherRegistries.init(bus);
		
		IsolatedListeners.init();
		
		bus.register(new DatagenDelegator());
		
		bus.addListener(FMLCommonSetupEvent.class, (event) ->
		{
			EIItems.values().forEach(ItemHolder::triggerRegistrationListener);
			EIBlocks.values().forEach(BlockHolder::triggerRegistrationListener);
			EIFluids.values().forEach(FluidHolder::triggerRegistrationListener);
			PotionRecipe.init();
		});
		
		bus.addListener(RegisterCapabilitiesEvent.class, CapabilitiesListeners::triggerAll);
		
		bus.addListener(RegisterPayloadHandlerEvent.class, EIPackets::init);
		
		bus.addListener(RegisterDataMapTypesEvent.class, EIDataMaps::init);
		
		NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, TagsUpdatedEvent.class, (event) ->
		{
			if(event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD)
			{
				LargeElectricFurnaceBlockEntity.initTiers();
			}
		});
	}
}
