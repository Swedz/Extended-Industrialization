package net.swedz.extended_industrialization;

import aztech.modern_industrialization.api.energy.CableTier;
import com.google.common.collect.Sets;
import net.minecraft.core.Holder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.component.RainbowDataComponent;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.extended_industrialization.item.PhotovoltaicCellItem;
import net.swedz.extended_industrialization.item.SteamChainsawItem;
import net.swedz.extended_industrialization.item.machineconfig.MachineConfigCardItem;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitAbility;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;
import net.swedz.tesseract.neoforge.registry.SortOrder;
import net.swedz.tesseract.neoforge.registry.common.CommonModelBuilders;
import net.swedz.tesseract.neoforge.registry.common.CommonRegistrations;
import net.swedz.tesseract.neoforge.registry.common.MICommonCapabitilies;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public final class EIItems
{
	public static final class Registry
	{
		public static final  DeferredRegister.Items ITEMS   = DeferredRegister.createItems(EI.ID);
		private static final Set<ItemHolder>        HOLDERS = Sets.newHashSet();
		
		private static void init(IEventBus bus)
		{
			ITEMS.register(bus);
		}
		
		public static void include(ItemHolder holder)
		{
			HOLDERS.add(holder);
		}
	}
	
	public static void init(IEventBus bus)
	{
		Registry.init(bus);
	}
	
	public static final ItemHolder<SteamChainsawItem> STEAM_CHAINSAW        = create("steam_chainsaw", "Steam Chainsaw", SteamChainsawItem::new, EISortOrder.GEAR).tag(ItemTags.AXES, ItemTags.HOES, ItemTags.SWORDS, Tags.Items.TOOLS_SHEAR).withModelBuilder(CommonModelBuilders::handheld).register();
	public static final ItemHolder<ElectricToolItem>  ELECTRIC_CHAINSAW     = create("electric_chainsaw", "Electric Chainsaw", (p) -> new ElectricToolItem(p, ElectricToolItem.Type.CHAINSAW), EISortOrder.GEAR).tag(ItemTags.AXES, ItemTags.HOES, ItemTags.SWORDS, Tags.Items.TOOLS_SHEAR).withCapabilities(MICommonCapabitilies::simpleEnergyItem).withModelBuilder(CommonModelBuilders::handheld).register();
	public static final ItemHolder<ElectricToolItem>  ELECTRIC_MINING_DRILL = create("electric_mining_drill", "Electric Mining Drill", (p) -> new ElectricToolItem(p, ElectricToolItem.Type.DRILL), EISortOrder.GEAR).tag(ItemTags.PICKAXES, ItemTags.SHOVELS).withCapabilities(MICommonCapabitilies::simpleEnergyItem).withModelBuilder(CommonModelBuilders::handheld).register();
	public static final ItemHolder<ElectricToolItem>  ULTIMATE_LASER_DRILL  = create("ultimate_laser_drill", "Ultimate Laser Drill", (p) -> new ElectricToolItem(p, ElectricToolItem.Type.ULTIMATE), EISortOrder.GEAR).tag(ItemTags.DYEABLE, EITags.Items.RAINBOW_DYEABLE, ItemTags.PICKAXES, ItemTags.SHOVELS, ItemTags.AXES, ItemTags.HOES, ItemTags.SWORDS, Tags.Items.TOOLS_SHEAR).withRegistrationListener(CommonRegistrations::cauldronClearDye).withRegistrationListener(RainbowDataComponent::cauldronClearDyeAndRainbow).withCapabilities(MICommonCapabitilies::simpleEnergyItem).withModelBuilder(CommonModelBuilders::handheldOverlayed).register();
	public static final ItemHolder<NanoSuitArmorItem> NANO_HELMET           = createNanosuitArmor("nano_helmet", "Nano Helmet", ArmorItem.Type.HELMET, NanoSuitAbility.NIGHT_VISION);
	public static final ItemHolder<NanoSuitArmorItem> NANO_CHESTPLATE       = createNanosuitArmor("nano_chestplate", "Nano Chestplate", ArmorItem.Type.CHESTPLATE);
	public static final ItemHolder<NanoSuitArmorItem> NANO_GRAVICHESTPLATE  = createNanosuitArmor("nano_gravichestplate", "Nano Gravichestplate", ArmorItem.Type.CHESTPLATE, EIArmorMaterials.NANO_GRAVICHESTPLATE, NanoSuitAbility.GRAVICHESTPLATE);
	public static final ItemHolder<NanoSuitArmorItem> NANO_LEGGINGS         = createNanosuitArmor("nano_leggings", "Nano Leggings", ArmorItem.Type.LEGGINGS, NanoSuitAbility.SPEED);
	public static final ItemHolder<NanoSuitArmorItem> NANO_BOOTS            = createNanosuitArmor("nano_boots", "Nano Boots", ArmorItem.Type.BOOTS);
	
	public static final ItemHolder<Item> TIN_CAN     = create("tin_can", "Tin Can", Item::new, EISortOrder.OTHER_GEAR).withModelBuilder(CommonModelBuilders::generated).register();
	public static final ItemHolder<Item> CANNED_FOOD = create("canned_food", "Canned Food", Item::new, EISortOrder.OTHER_GEAR).withProperties((p) -> p.food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3f).fast().usingConvertsTo(TIN_CAN).build())).tag(ItemTags.WOLF_FOOD, ItemTags.CAT_FOOD).withModelBuilder(CommonModelBuilders::generated).register();
	
	public static final ItemHolder<MachineConfigCardItem> MACHINE_CONFIG_CARD = create("machine_config_card", "Machine Config Card", MachineConfigCardItem::new, EISortOrder.OTHER_GEAR).withModelBuilder(CommonModelBuilders::generated).register();
	
	public static final ItemHolder<Item> NETHERITE_ROTARY_BLADE = create("netherite_rotary_blade", "Netherite Rotary Blade", Item::new, EISortOrder.PARTS).withModelBuilder(CommonModelBuilders::generated).register();
	public static final ItemHolder<Item> STEEL_COMBINE          = create("steel_combine", "Steel Combine", Item::new, EISortOrder.PARTS).withModelBuilder(CommonModelBuilders::generated).register();
	
	public static final ItemHolder<Item> MULCH              = create("mulch", "Mulch", Item::new, EISortOrder.RESOURCES).withProperties((p) -> p.food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3f).fast().build())).withModelBuilder(CommonModelBuilders::generated).register();
	public static final ItemHolder<Item> NETHERITE_DUST     = create("netherite_dust", "Netherite Dust", Item::new, EISortOrder.RESOURCES).tag(EITags.itemCommon("dusts"), EITags.itemCommon("dusts/netherite")).withModelBuilder(CommonModelBuilders::generated).register();
	public static final ItemHolder<Item> CRYSTALLIZED_HONEY = create("crystallized_honey", "Crystallized Honey", Item::new, EISortOrder.RESOURCES).withProperties((p) -> p.food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3f).fast().build())).withModelBuilder(CommonModelBuilders::generated).register();
	public static final ItemHolder<Item> GRANITE_DUST       = create("granite_dust", "Granite Dust", Item::new, EISortOrder.RESOURCES).tag(EITags.itemCommon("dusts"), EITags.itemCommon("dusts/granite")).withModelBuilder(CommonModelBuilders::generated).register();
	
	public static final ItemHolder<PhotovoltaicCellItem> LV_PHOTOVOLTAIC_CELL = createPhotovoltaicCell("lv", "LV", CableTier.LV, 16, 10 * 60 * 20);
	public static final ItemHolder<PhotovoltaicCellItem> MV_PHOTOVOLTAIC_CELL = createPhotovoltaicCell("mv", "MV", CableTier.MV, 64, 10 * 60 * 20);
	public static final ItemHolder<PhotovoltaicCellItem> HV_PHOTOVOLTAIC_CELL = createPhotovoltaicCell("hv", "HV", CableTier.HV, 256, 10 * 60 * 20);
	
	public static Set<ItemHolder> values()
	{
		return Set.copyOf(Registry.HOLDERS);
	}
	
	public static ItemHolder valueOf(String id)
	{
		return Registry.HOLDERS.stream()
				.filter((holder) -> holder.identifier().id().equals(id))
				.findFirst()
				.orElseThrow();
	}
	
	public static <Type extends Item> ItemHolder<Type> create(String id, String englishName, Function<Item.Properties, Type> creator, SortOrder sortOrder)
	{
		ItemHolder<Type> holder = new ItemHolder<>(EI.id(id), englishName, Registry.ITEMS, creator).sorted(sortOrder);
		Registry.include(holder);
		return holder;
	}
	
	public static ItemHolder<NanoSuitArmorItem> createNanosuitArmor(String id, String englishName, ArmorItem.Type armorType, Holder<ArmorMaterial> material, Optional<NanoSuitAbility> ability)
	{
		TagKey<Item> armorTag = switch (armorType)
		{
			case HELMET -> ItemTags.HEAD_ARMOR;
			case CHESTPLATE -> ItemTags.CHEST_ARMOR;
			case LEGGINGS -> ItemTags.LEG_ARMOR;
			case BOOTS -> ItemTags.FOOT_ARMOR;
			default ->
					throw new IllegalArgumentException("Cannot get tag for armor type %s".formatted(armorType.name()));
		};
		return create(id, englishName, (p) -> new NanoSuitArmorItem(material, armorType, p.rarity(ability.map(NanoSuitAbility::rarity).orElse(Rarity.UNCOMMON)), ability), EISortOrder.GEAR)
				.tag(armorTag, Tags.Items.ARMORS, ItemTags.TRIMMABLE_ARMOR, ItemTags.DYEABLE, EITags.Items.RAINBOW_DYEABLE)
				.withRegistrationListener(CommonRegistrations::cauldronClearDye)
				.withRegistrationListener(RainbowDataComponent::cauldronClearDyeAndRainbow)
				.withCapabilities(MICommonCapabitilies::simpleEnergyItem)
				.withModelBuilder(CommonModelBuilders::generatedOverlayed)
				.register();
	}
	
	public static ItemHolder<NanoSuitArmorItem> createNanosuitArmor(String id, String englishName, ArmorItem.Type armorType, Holder<ArmorMaterial> material, NanoSuitAbility ability)
	{
		return createNanosuitArmor(id, englishName, armorType, material, Optional.of(ability));
	}
	
	public static ItemHolder<NanoSuitArmorItem> createNanosuitArmor(String id, String englishName, ArmorItem.Type armorType, NanoSuitAbility ability)
	{
		return createNanosuitArmor(id, englishName, armorType, EIArmorMaterials.NANO, Optional.of(ability));
	}
	
	public static ItemHolder<NanoSuitArmorItem> createNanosuitArmor(String id, String englishName, ArmorItem.Type armorType, Holder<ArmorMaterial> material)
	{
		return createNanosuitArmor(id, englishName, armorType, material, Optional.empty());
	}
	
	public static ItemHolder<NanoSuitArmorItem> createNanosuitArmor(String id, String englishName, ArmorItem.Type armorType)
	{
		return createNanosuitArmor(id, englishName, armorType, EIArmorMaterials.NANO, Optional.empty());
	}
	
	public static ItemHolder<PhotovoltaicCellItem> createPhotovoltaicCell(String id, String name, CableTier tier, int euPerTick, int durationTicks)
	{
		return create("%s_photovoltaic_cell".formatted(id), "%s Photovoltaic Cell".formatted(name), (p) -> new PhotovoltaicCellItem(p, tier, euPerTick, durationTicks), EISortOrder.PARTS)
				.tag(EITags.Items.PHOTOVOLTAIC_CELL)
				.withModelBuilder(CommonModelBuilders::generated)
				.register();
	}
}
