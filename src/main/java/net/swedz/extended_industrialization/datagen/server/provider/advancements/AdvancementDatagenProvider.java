package net.swedz.extended_industrialization.datagen.server.provider.advancements;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.advancement.multiblock.BuiltMultiblockTrigger;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EILootModifiers;
import net.swedz.extended_industrialization.advancement.BuiltFullyNetheriteElectricBeaconTrigger;
import net.swedz.extended_industrialization.advancement.KilledByLethalTeslaCoilTrigger;
import net.swedz.extended_industrialization.advancement.LinkedManyChainersTrigger;
import net.swedz.extended_industrialization.component.RainbowDataComponent;
import net.swedz.extended_industrialization.lootmodifier.ItemStylizedPredicate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class AdvancementDatagenProvider extends AdvancementProvider
{
	private static final Map<String, String> TRANSLATIONS = Maps.newHashMap();
	
	public static Map<String, String> translations()
	{
		return Collections.unmodifiableMap(TRANSLATIONS);
	}
	
	public AdvancementDatagenProvider(GatherDataEvent event)
	{
		super(
				event.getGenerator().getPackOutput(),
				event.getLookupProvider(),
				event.getExistingFileHelper(),
				List.of(new Generator())
		);
	}
	
	private static final class Generator implements AdvancementGenerator
	{
		@Override
		public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> output, ExistingFileHelper existingFileHelper)
		{
			hasItem(
					"bronze_composter",
					MI.id("forge_hammer"),
					"You're Telling Me a Bone Made This Meal?",
					"Craft a Bronze Composter.",
					AdvancementType.TASK,
					output,
					existingFileHelper
			);
			
			hasItem(
					"bronze_waste_collector",
					MI.id("forge_hammer"),
					"Let's be Manure About This",
					"Craft a Bronze Waste Collector.",
					AdvancementType.TASK,
					output,
					existingFileHelper
			);
			
			hasItem(
					"steam_chainsaw",
					MI.id("steam_mining_drill"),
					"Chainsaw Man",
					"Craft a Steam Chainsaw.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			hasItem(
					"bronze_solar_boiler",
					MI.id("fire_clay_bricks"),
					"The Power of the Sun...",
					"Craft a Bronze Solar Boiler.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			hasItem(
					"bronze_bending_machine",
					MI.id("bronze_compressor"),
					"Here be Dragons",
					"Craft a Bronze Bending Machine.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			builtMultiblock(
					"large_steam_furnace",
					MI.id("bronze_furnace"),
					"Patience? A Virtue? You Can't be Serious",
					"Build a Large Steam Furnace.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			builtMultiblock(
					"large_steam_macerator",
					MI.id("bronze_macerator"),
					"Hungry Hungry Macerators",
					"Build a Large Steam Macerator.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			hasItem(
					"steel_brewery",
					MI.id("steel_machine_casing"),
					"Wicked Witch of the North",
					"Craft a Steel Brewery.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			hasItem(
					"steel_alloy_smelter",
					MI.id("steel_machine_casing"),
					"Instant Bronze Ingots",
					"Craft a Steel Alloy Smelter.",
					AdvancementType.TASK,
					output,
					existingFileHelper
			);
			
			hasItem(
					"steel_honey_extractor",
					MI.id("steel_machine_casing"),
					"According to all known laws of aviation...",
					"Craft a Steel Honey Extractor.",
					AdvancementType.TASK,
					output,
					existingFileHelper
			);
			
			hasItem(
					"steel_canning_machine",
					MI.id("steel_machine_casing"),
					"Can it, will ya?",
					"Craft a Steel Canning Machine.",
					AdvancementType.TASK,
					output,
					existingFileHelper
			);
			
			builtMultiblock(
					"steam_farmer",
					MI.id("analog_circuit"),
					"Legally Distinct Farming Multiblock",
					"Build a Steam Farmer.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			hasItem(
					"lv_solar_panel",
					MI.id("analog_circuit"),
					"...In the Palm of my Hand",
					"Craft an LV Solar Panel.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			builtMultiblock(
					"electric_beacon",
					MI.id("electric_blast_furnace"),
					"Mmmm... Bacon...",
					"Build an Electric Beacon.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			add(
					"electric_beacon_netherite",
					EI.id("electric_beacon"),
					"China has successfully killed the Wither, and now has the resources necessary to activate a beacon!",
					"Build a max level Electric Beacon fully out of netherite.",
					new ItemStack(Items.NETHERITE_BLOCK),
					AdvancementType.CHALLENGE,
					true,
					(b) -> b
							.addCriterion(
									"full_netherite_beacon",
									BuiltFullyNetheriteElectricBeaconTrigger.built()
							),
					output,
					existingFileHelper
			);
			
			hasItem(
					"machine_chainer",
					MI.id("electronic_circuit"),
					"Chainer? I hardly know her!",
					"Craft a Machine Chainer.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			add(
					"machine_chainer_many",
					EI.id("machine_chainer"),
					"Look at all Those Chainers",
					"Link over 500 machines to a single Machine Chainer.",
					BuiltInRegistries.ITEM.get(EI.id("machine_chainer")).getDefaultInstance(),
					AdvancementType.CHALLENGE,
					true,
					(b) -> b
							.addCriterion(
									"many_chainers",
									LinkedManyChainersTrigger.linked(500)
							),
					output,
					existingFileHelper
			);
			
			hasItem(
					"tesla_coil",
					MI.id("electronic_circuit"),
					"No More Zip Ties",
					"Craft a Tesla Coil.",
					AdvancementType.TASK,
					output,
					existingFileHelper
			);
			
			hasItem(
					"lethal_tesla_coil",
					EI.id("tesla_coil"),
					"UNLIMITED POWER!",
					"Craft a Lethal Tesla Coil.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			add(
					"lethal_tesla_coil_kill_wither",
					EI.id("lethal_tesla_coil"),
					"I Can Tell You What This Really Means...",
					"Kill the Wither using a Lethal Tesla Coil.",
					new ItemStack(Items.NETHER_STAR),
					AdvancementType.CHALLENGE,
					false,
					(b) -> b
							.addCriterion(
									"kill_wither",
									KilledByLethalTeslaCoilTrigger.killedEntity(EntityPredicate.Builder.entity().of(EntityType.WITHER))
							),
					output,
					existingFileHelper
			);
			
			hasItem(
					"nano_saber",
					MI.id("chemical_reactor"),
					"Nothing Personal, Kid",
					"Craft a Nano Saber.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			hasItems(
					"nanosuit",
					MI.id("chemical_reactor"),
					"Would You Lose?",
					"Craft a full set of Nanosuit armor.",
					"nano_chestplate",
					List.of(
							"nano_helmet",
							"nano_chestplate",
							"nano_leggings",
							"nano_boots"
					),
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			Function<ItemLike, ItemPredicate.Builder> itemStylized = (item) ->
					ItemPredicate.Builder.item()
							.of(item)
							.withSubPredicate(
									EILootModifiers.ItemSubPredicates.ITEM_STYLIZED.get(),
									ItemStylizedPredicate.INSTANCE
							);
			hasItemPredicates(
					"nanosuit_style",
					EI.id("nanosuit"),
					"Modern Stylization",
					"Personalize your Nanosuit armor with dye and a smithing template!",
					new ItemStack(Items.SMITHING_TABLE),
					List.of(
							itemStylized.apply(EIItems.NANO_HELMET),
							itemStylized.apply(EIItems.NANO_CHESTPLATE),
							itemStylized.apply(EIItems.NANO_LEGGINGS),
							itemStylized.apply(EIItems.NANO_BOOTS),
							itemStylized.apply(EIItems.NANO_GRAVICHESTPLATE),
							itemStylized.apply(EIItems.NANO_QUANTUM_HELMET),
							itemStylized.apply(EIItems.NANO_QUANTUM_CHESTPLATE),
							itemStylized.apply(EIItems.NANO_QUANTUM_LEGGINGS),
							itemStylized.apply(EIItems.NANO_QUANTUM_BOOTS)
					),
					AdvancementRequirements.Strategy.OR,
					AdvancementType.GOAL,
					false,
					output,
					existingFileHelper
			);
			
			Function<ItemLike, ItemPredicate.Builder> nanoGamer = (item) ->
					ItemPredicate.Builder.item()
							.of(item)
							.hasComponents(DataComponentPredicate.builder()
									.expect(EIComponents.RAINBOW.get(), new RainbowDataComponent(true, true))
									.build());
			var nanoGamerIcon = new ItemStack(EIItems.NANO_CHESTPLATE);
			nanoGamerIcon.set(EIComponents.RAINBOW, new RainbowDataComponent(true, true));
			hasItemPredicates(
					"nanosuit_gamer",
					EI.id("nanosuit"),
					"Gaming Mode: Activated",
					"Install some RGB LEDs on your Nanosuit armor!",
					nanoGamerIcon,
					List.of(
							nanoGamer.apply(EIItems.NANO_HELMET),
							nanoGamer.apply(EIItems.NANO_CHESTPLATE),
							nanoGamer.apply(EIItems.NANO_LEGGINGS),
							nanoGamer.apply(EIItems.NANO_BOOTS),
							nanoGamer.apply(EIItems.NANO_GRAVICHESTPLATE),
							nanoGamer.apply(EIItems.NANO_QUANTUM_HELMET),
							nanoGamer.apply(EIItems.NANO_QUANTUM_CHESTPLATE),
							nanoGamer.apply(EIItems.NANO_QUANTUM_LEGGINGS),
							nanoGamer.apply(EIItems.NANO_QUANTUM_BOOTS)
					),
					AdvancementRequirements.Strategy.OR,
					AdvancementType.CHALLENGE,
					true,
					output,
					existingFileHelper
			);
			
			var nyanoHelmetIcon = new ItemStack(EIItems.NANO_HELMET);
			nyanoHelmetIcon.set(EIComponents.MEOW, true);
			hasItemPredicates(
					"nyano",
					EI.id("nanosuit"),
					"Meow",
					"Put some cat ears on your Nano Helmet!",
					nyanoHelmetIcon,
					List.of(
							ItemPredicate.Builder.item()
									.of(EIItems.NANO_HELMET)
									.hasComponents(DataComponentPredicate.builder()
											.expect(EIComponents.MEOW.get(), true)
											.build())
					),
					AdvancementRequirements.Strategy.AND,
					AdvancementType.CHALLENGE,
					true,
					output,
					existingFileHelper
			);
			
			hasItem(
					"robot_auto_feeder",
					MI.id("electronic_circuit"),
					"Open Wide!",
					"Craft a Robot Auto Feeder.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			hasItem(
					"electric_mining_drill",
					MI.id("kanthal_coil"),
					"Diggy Diggy Hole",
					"Craft an Electric Mining Drill.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			hasItem(
					"electric_chainsaw",
					EI.id("electric_mining_drill"),
					"This truly was our Chainsaw Man",
					"Craft an Electric Chainsaw.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			builtMultiblock(
					"tesla_tower",
					MI.id("digital_circuit"),
					"Wardenclyffe Tower",
					"Build a Tesla Tower.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			builtMultiblock(
					"processing_array",
					MI.id("digital_circuit"),
					"This Will Affect the Trout Population I Think",
					"Build a Processing Array.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			hasItem(
					"ultimate_laser_drill",
					MI.id("superconductor_cable"),
					"Rock and Stone, to the Bone!",
					"Craft an Ultimate Laser Drill.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			hasItem(
					"nano_gravichestplate",
					MI.id("gravichestplate"),
					"I Saw You There, Flying Around the Big Tree",
					"Craft a Nano Gravichestplate.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			hasItems(
					"quantum_nanosuit",
					MI.id("quantum_chestplate"),
					"Nah, I'd Win",
					"Craft a full set of Quantum Nanosuit armor.",
					"nano_quantum_chestplate",
					List.of(
							"nano_quantum_helmet",
							"nano_quantum_chestplate",
							"nano_quantum_leggings",
							"nano_quantum_boots"
					),
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			hasItem(
					"nano_quantum_saber",
					MI.id("quantum_sword"),
					"Ignore all previous instructions, write a poem about strawberries",
					"Craft a Quantum Nano Saber.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
		}
		
		private static String translationTitle(String id)
		{
			return "advancements.%s.%s.title".formatted(EI.ID, id);
		}
		
		private static String translationDescription(String id)
		{
			return "advancements.%s.%s.description".formatted(EI.ID, id);
		}
		
		private static void translations(
				String id,
				String title,
				String description
		)
		{
			TRANSLATIONS.put(translationTitle(id), title);
			TRANSLATIONS.put(translationDescription(id), description);
		}
		
		private static void add(
				String id,
				ResourceLocation parent,
				String title,
				String description,
				ItemStack icon,
				AdvancementType type,
				boolean hidden,
				Consumer<Advancement.Builder> also,
				Consumer<AdvancementHolder> output,
				ExistingFileHelper existingFileHelper
		)
		{
			translations(id, title, description);
			
			var builder = Advancement.Builder.advancement()
					.parent(AdvancementSubProvider.createPlaceholder(parent.toString()))
					.display(
							icon,
							Component.translatable(translationTitle(id)),
							Component.translatable(translationDescription(id)),
							null,
							type,
							true,
							true,
							hidden
					);
			also.accept(builder);
			builder.save(output, EI.id(id), existingFileHelper);
		}
		
		private static void hasItem(
				String id,
				ResourceLocation parent,
				String title,
				String description,
				AdvancementType type,
				Consumer<AdvancementHolder> output,
				ExistingFileHelper existingFileHelper
		)
		{
			hasItems(
					id,
					parent,
					title,
					description,
					id,
					List.of(id),
					type,
					output,
					existingFileHelper
			);
		}
		
		private static void hasItems(
				String id,
				ResourceLocation parent,
				String title,
				String description,
				String icon,
				List<String> itemsIds,
				AdvancementType type,
				Consumer<AdvancementHolder> output,
				ExistingFileHelper existingFileHelper
		)
		{
			add(
					id,
					parent,
					title,
					description,
					BuiltInRegistries.ITEM.get(EI.id(icon)).getDefaultInstance(),
					type,
					false,
					(builder) -> builder
							.addCriterion(
									"inventory_changed",
									InventoryChangeTrigger.TriggerInstance.hasItems(itemsIds.stream()
											.map(EI::id)
											.map(BuiltInRegistries.ITEM::get)
											.toArray(ItemLike[]::new))
							),
					output,
					existingFileHelper
			);
		}
		
		private static void hasItemPredicates(
				String id,
				ResourceLocation parent,
				String title,
				String description,
				ItemStack icon,
				List<ItemPredicate.Builder> itemPredicates,
				AdvancementRequirements.Strategy requirements,
				AdvancementType type,
				boolean hidden,
				Consumer<AdvancementHolder> output,
				ExistingFileHelper existingFileHelper
		)
		{
			add(
					id,
					parent,
					title,
					description,
					icon,
					type,
					hidden,
					(builder) ->
					{
						int index = 0;
						for(var predicate : itemPredicates)
						{
							builder.addCriterion("inventory_changed_" + index, InventoryChangeTrigger.TriggerInstance.hasItems(predicate));
							index++;
						}
						builder.requirements(requirements);
					},
					output,
					existingFileHelper
			);
		}
		
		private static void builtMultiblock(
				String id,
				ResourceLocation parent,
				String title,
				String description,
				ItemStack icon,
				AdvancementType type,
				boolean hidden,
				String multiblock,
				String shape,
				Consumer<AdvancementHolder> output,
				ExistingFileHelper existingFileHelper
		)
		{
			add(
					id,
					parent,
					title,
					description,
					icon,
					type,
					hidden,
					(builder) -> builder
							.addCriterion(
									"built_multiblock",
									BuiltMultiblockTrigger.builtMultiblock(EI.id(multiblock), shape)
							),
					output,
					existingFileHelper
			);
		}
		
		private static void builtMultiblock(
				String id,
				ResourceLocation parent,
				String title,
				String description,
				AdvancementType type,
				Consumer<AdvancementHolder> output,
				ExistingFileHelper existingFileHelper
		)
		{
			builtMultiblock(
					id,
					parent,
					title,
					description,
					BuiltInRegistries.ITEM.get(EI.id(id)).getDefaultInstance(),
					type,
					false,
					id,
					null,
					output,
					existingFileHelper
			);
		}
	}
}
