package net.swedz.extended_industrialization.datagen.server.provider.advancements;

import aztech.modern_industrialization.MI;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
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
import net.swedz.extended_industrialization.advancement.KilledByLethalTeslaCoilTrigger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
					// TODO
					"",
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
			
			// TODO build multiblock
			hasItem(
					"large_steam_furnace",
					MI.id("bronze_furnace"),
					"Patience? A Virtue? You can't be serious",
					"Craft a Large Steam Furnace.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			// TODO build multiblock
			hasItem(
					"large_steam_macerator",
					MI.id("bronze_macerator"),
					// TODO
					"",
					"Craft a Large Steam Macerator.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			hasItem(
					"steel_brewery",
					MI.id("steel_machine_casing"),
					// TODO
					"",
					"Craft a Steel Brewery.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			hasItem(
					"steel_alloy_smelter",
					MI.id("steel_machine_casing"),
					// TODO
					"",
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
					// TODO
					"",
					"Craft a Steel Canning Machine.",
					AdvancementType.TASK,
					output,
					existingFileHelper
			);
			
			// TODO build multiblock
			hasItem(
					"steam_farmer",
					MI.id("analog_circuit"),
					"Legally Distinct Farming Multiblock",
					"Craft a Steam Farmer.",
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
			
			// TODO build multiblock
			hasItem(
					"electric_beacon",
					MI.id("electric_blast_furnace"),
					// TODO
					"",
					"Craft an Electric Beacon.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			// TODO max tier beacon out of diamonds/netherite - China has successfully killed the Wither, and now has the resources necessary to activate a beacon!
			
			hasItem(
					"machine_chainer",
					MI.id("electronic_circuit"),
					"Chainer? I hardly know her!",
					"Craft a Machine Chainer.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			hasItem(
					"tesla_coil",
					MI.id("electronic_circuit"),
					// TODO
					"",
					"Craft a Tesla Coil.",
					AdvancementType.TASK,
					output,
					existingFileHelper
			);
			
			hasItem(
					"lethal_tesla_coil",
					EI.id("tesla_coil"),
					// TODO
					"",
					"Craft a Lethal Tesla Coil.",
					AdvancementType.GOAL,
					output,
					existingFileHelper
			);
			
			add(
					"lethal_tesla_coil_kill_wither",
					EI.id("lethal_tesla_coil"),
					// TODO
					"",
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
					// TODO
					"",
					"Craft a Nano Saber.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			hasItems(
					"nanosuit",
					MI.id("chemical_reactor"),
					// TODO
					"",
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
			
			var nyanoHelmet = new ItemStack(EIItems.NANO_HELMET);
			nyanoHelmet.set(EIComponents.MEOW, true);
			hasItemPredicates(
					"nyano",
					EI.id("nanosuit"),
					"Meow",
					"Give your Nano Helmet to your pet cat!",
					nyanoHelmet,
					List.of(
							ItemPredicate.Builder.item()
									.of(EIItems.NANO_HELMET)
									.hasComponents(DataComponentPredicate.builder()
											.expect(EIComponents.MEOW.get(), true)
											.build())
					),
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
					// TODO
					"",
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
			
			// TODO build multiblock
			hasItem(
					"tesla_tower",
					MI.id("digital_circuit"),
					// TODO
					"",
					"Craft a Tesla Tower.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			// TODO build multiblock
			hasItem(
					"processing_array",
					MI.id("digital_circuit"),
					// TODO
					"",
					"Craft a Processing Array.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			hasItem(
					"ultimate_laser_drill",
					MI.id("superconductor_cable"),
					// TODO
					"",
					"Craft an Ultimate Laser Drill.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			hasItem(
					"nano_gravichestplate",
					MI.id("gravichestplate"),
					// TODO
					"",
					"Craft a Nano Gravichestplate.",
					AdvancementType.CHALLENGE,
					output,
					existingFileHelper
			);
			
			hasItems(
					"quantum_nanosuit",
					MI.id("quantum_chestplate"),
					// TODO
					"",
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
					// TODO
					"",
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
					(builder) -> builder
							.addCriterion(
									"inventory_changed",
									InventoryChangeTrigger.TriggerInstance.hasItems(itemPredicates.toArray(ItemPredicate.Builder[]::new))
							),
					output,
					existingFileHelper
			);
		}
	}
}
