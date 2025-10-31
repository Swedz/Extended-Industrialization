package net.swedz.extended_industrialization;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.util.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Style;
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
import net.swedz.extended_industrialization.component.RainbowDataComponent;
import net.swedz.extended_industrialization.datagen.DatagenDelegator;
import net.swedz.extended_industrialization.datagen.client.provider.LanguageDatagenProvider;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.LargeElectricFurnaceBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower.TeslaTowerBlockEntity;
import net.swedz.extended_industrialization.machines.guicomponent.EIModularSlotPanelSlots;
import net.swedz.extended_industrialization.material.EIMaterialRegistry;
import net.swedz.extended_industrialization.network.EIPackets;
import net.swedz.tesseract.neoforge.api.Assert;
import net.swedz.tesseract.neoforge.api.tuple.Pair;
import net.swedz.tesseract.neoforge.capabilities.CapabilitiesListeners;
import net.swedz.tesseract.neoforge.compat.mi.TesseractMI;
import net.swedz.tesseract.neoforge.compat.mi.component.craft.multiplied.EuCostTransformer;
import net.swedz.tesseract.neoforge.compat.mi.tooltip.MIParser;
import net.swedz.tesseract.neoforge.config.ConfigManager;
import net.swedz.tesseract.neoforge.lang.LangManager;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;
import net.swedz.tesseract.neoforge.registry.holder.FluidHolder;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;
import net.swedz.tesseract.neoforge.tooltip.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static aztech.modern_industrialization.MITooltips.*;

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
	
	public EI(IEventBus bus, ModContainer container)
	{
		setupText();
		setupConfig(bus, container);
		
		EILocalizedListeners.INSTANCE.init();
		
		TesseractMI.init(ID);
		EIComponents.init(bus);
		EIArmorMaterials.init(bus);
		EIItems.init(bus);
		EIBlocks.init(bus);
		EIFluids.init(bus);
		EIEntities.init(bus);
		EILootModifiers.init(bus);
		EIMaterialRegistry.init();
		EICreativeTabs.init(bus);
		EIRecipeTypes.init(bus);
		EISounds.init(bus);
		EIModularSlotPanelSlots.init();
		
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
	
	private static EIConfig CONFIG;
	
	public static EIConfig config()
	{
		Assert.notNull(CONFIG, "Config not yet loaded");
		return CONFIG;
	}
	
	private static void setupConfig(IEventBus bus, ModContainer container)
	{
		var manager = new ConfigManager()
				.includeDefaultValueComments();
		manager.codecs()
				.register(EIConfig.CableTierDamages.class, EIConfig.CableTierDamages.CODEC);
		CONFIG = manager
				.build(EIConfig.class)
				.register(container, ModConfig.Type.STARTUP)
				.load()
				.listenToLoad(bus)
				.config();
	}
	
	private static EIText TEXT;
	
	public static EIText text()
	{
		Assert.notNull(TEXT, "Text not yet loaded");
		return TEXT;
	}
	
	private static void setupText()
	{
		var instance = new LangManager(ID)
				.style("clear", () -> Style.EMPTY)
				.style("tooltip", () -> DEFAULT_STYLE)
				.style("tooltip_subtext", () -> DEFAULT_STYLE.withItalic(true))
				.style("highlighted", () -> HIGHLIGHT_STYLE)
				.style("green", () -> Style.EMPTY.withColor(ChatFormatting.GREEN))
				.style("red", () -> Style.EMPTY.withColor(ChatFormatting.RED))
				.style("rainbow", () -> Style.EMPTY.withColor(RainbowDataComponent.getCurrentRainbowColor()))
				
				.builtinParsers()
				.parser("keybind", String.class, () -> EITooltips.KEYBIND_PARSER)
				.parser("item", ResourceLocation.class, () -> Parser.ITEM_ID)
				.parser("block", ResourceLocation.class, () -> Parser.BLOCK_ID)
				
				.parser("percentage", float.class, () -> EITooltips.PERCENTAGE_PARSER)
				
				.parser("eu_per_tick", long.class, () -> (value) ->
				{
					var amount = TextHelper.getAmountGeneric(value);
					return MIText.EuT.text(amount.digit(), amount.unit());
				})
				.parser("eu", long.class, () -> (value) ->
				{
					var amount = TextHelper.getAmountGeneric(value);
					return MIText.Eu.text(amount.digit(), amount.unit());
				})
				
				.parser("damage", float.class, () -> EITooltips.DAMAGE_PARSER)
				
				.parser("ticks_to_minutes", long.class, () -> EITooltips.TICKS_TO_MINUTES_PARSER)
				
				.parser("activated", boolean.class, () -> EITooltips.ACTIVATED_BOOLEAN_PARSER)
				.parser("short", CableTier.class, () -> MIParser.CABLE_TIER_SHORT)
				.parser(EuCostTransformer.class, () -> MIParser.EU_COST_TRANSFORMER_PARSER)
				.parser(ElectricToolItem.Mode.class, () -> (mode) -> mode.text().name())
				
				.parser(EIText.EnchantmentWithLevelField.class, () -> (value) -> Parser.ENCHANTMENT_AND_LEVEL.parse(value.registry(), new Pair<>(value.enchantment(), value.level())))
				.parser(EIText.EnchantmentField.class, () -> (value) -> Parser.ENCHANTMENT.parse(value.registry(), value.enchantment()))
				.parser("enchantment_level", int.class, () -> Parser.ENCHANTMENT_LEVEL)
				
				.build(EIText.class)
				.load();
		LanguageDatagenProvider.include(instance);
		TEXT = instance.lang();
	}
}
