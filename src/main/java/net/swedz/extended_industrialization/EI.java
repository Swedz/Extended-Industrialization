package net.swedz.extended_industrialization;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import net.swedz.extended_industrialization.compat.mi.EIMIHookListener;
import net.swedz.extended_industrialization.compat.mi.EIMIHookRegistry;
import net.swedz.extended_industrialization.datagen.DatagenDelegator;
import net.swedz.extended_industrialization.machines.blockentities.multiblock.LargeElectricFurnaceBlockEntity;
import net.swedz.extended_industrialization.machines.components.craft.potion.PotionRecipe;
import net.swedz.tesseract.neoforge.api.MCIdentifiable;
import net.swedz.tesseract.neoforge.capabilities.CapabilitiesListeners;
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHooks;
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
		this.loadConfig();
		
		MIHooks.registerListener(ID, new EIMIHookRegistry(), new EIMIHookListener());
		
		EIItems.init(bus);
		EIBlocks.init(bus);
		EIFluids.init(bus);
		EIAttachments.init(bus);
		EIOtherRegistries.init(bus);
		
		bus.register(new DatagenDelegator());
		
		bus.addListener(FMLCommonSetupEvent.class, (event) ->
		{
			EIItems.values().forEach(ItemHolder::triggerRegistrationListener);
			EIBlocks.values().forEach(BlockHolder::triggerRegistrationListener);
			EIFluids.values().forEach(FluidHolder::triggerRegistrationListener);
			PotionRecipe.init();
		});
		
		bus.addListener(RegisterCapabilitiesEvent.class, (event) -> CapabilitiesListeners.triggerAll(ID, event));
		
		bus.addListener(RegisterDataMapTypesEvent.class, EIDataMaps::init);
		
		NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, TagsUpdatedEvent.class, (event) ->
		{
			if(event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD)
			{
				LargeElectricFurnaceBlockEntity.initTiers();
			}
		});
	}
	
	private void loadConfig()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EIConfig.SPEC);
		
		CommentedFileConfig configData = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve("extended_industrialization-common.toml"))
				.preserveInsertionOrder()
				.autoreload()
				.writingMode(WritingMode.REPLACE)
				.sync()
				.build();
		configData.load();
		EIConfig.SPEC.setConfig(configData);
		EIConfig.loadConfig();
		
		EI.LOGGER.info("Forcefully early-loaded config");
	}
}
