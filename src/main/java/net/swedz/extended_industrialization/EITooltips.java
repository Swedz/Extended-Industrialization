package net.swedz.extended_industrialization;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.technici4n.grandpower.api.ILongEnergyStorage;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.swedz.extended_industrialization.datamap.EnchantmentModule;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.extended_industrialization.item.PhotovoltaicCellItem;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.LargeElectricFurnaceBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower.TeslaTowerBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower.TeslaTowerTier;
import net.swedz.tesseract.neoforge.api.Assert;
import net.swedz.tesseract.neoforge.api.WorldPos;
import net.swedz.tesseract.neoforge.tooltip.BiParser;
import net.swedz.tesseract.neoforge.tooltip.Parser;
import net.swedz.tesseract.neoforge.tooltip.TooltipAttachment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static aztech.modern_industrialization.MITooltips.*;

public final class EITooltips
{
	private static final BiParser<Boolean, Float> MAYBE_SPACED_PERCENTAGE_PARSER = (space, ratio) ->
			Component.literal("%s%s%%".formatted((int) (ratio * 100), space ? " " : ""));
	
	public static final Parser<Float> PERCENTAGE_PARSER = (ratio) -> MAYBE_SPACED_PERCENTAGE_PARSER.parse(false, ratio);
	
	public static final Parser<Float> SPACED_PERCENTAGE_PARSER = (ratio) -> MAYBE_SPACED_PERCENTAGE_PARSER.parse(true, ratio);
	
	public static final Parser<Long> TICKS_TO_MINUTES_PARSER = (ticks) ->
	{
		float minutes = (float) ticks / (60 * 20);
		return Component.literal("%.2f".formatted(minutes));
	};
	
	public static final Parser<Boolean> ACTIVATED_BOOLEAN_PARSER = (value) -> value ? EI.text().activated() : EI.text().deactivated();
	
	public static final Parser<WorldPos> TESLA_NETWORK_KEY_PARSER = (key) -> Parser.GLOBAL_POS.withStyle(DEFAULT_STYLE).parse(GlobalPos.of(key.dimension(), key.pos()));
	
	public static final Parser<String> KEYBIND_PARSER = (key) ->
	{
		if(key.equals("alt"))
		{
			return EI.text().keyAlt();
		}
		else if(key.equals("mouse_scroll"))
		{
			return EI.text().keyMouseScroll();
		}
		return Parser.KEYBIND.parse(key);
	};
	
	public static final Parser<Float> DAMAGE_PARSER = (damage) ->
	{
		MutableComponent line;
		if(damage == Integer.MAX_VALUE)
		{
			line = EI.text().damage(EI.text().infinity());
		}
		else
		{
			damage /= 2;
			line = damage % 1 == 0 ?
					EI.text().damage(damage.intValue()) :
					EI.text().damage(damage);
		}
		return line;
	};
	
	public static final TooltipAttachment ENERGY_STORED_ITEM = TooltipAttachment.singleLineOptional(
			(stack, item) -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(EI.ID),
			(flags, context, stack, item) ->
			{
				ILongEnergyStorage energyStorage = stack.getCapability(EnergyApi.ITEM);
				if(energyStorage != null)
				{
					long capacity = energyStorage.getCapacity();
					if(capacity > 0)
					{
						return Optional.of(MIText.EnergyStored.text(EU_MAXED_PARSER.parse(new NumberWithMax(energyStorage.getAmount(), capacity))).withStyle(DEFAULT_STYLE));
					}
				}
				return Optional.empty();
			}
	).noShiftRequired();
	
	public static final TooltipAttachment MULCH_GANG_FOR_LIFE = TooltipAttachment.multilines(
			EIItems.MULCH,
			List.of(
					EI.text().mulchGangForLife0(),
					EI.text().mulchGangForLife1()
			)
	).noShiftRequired();
	
	public static final TooltipAttachment COILS_LEF = TooltipAttachment.singleLine(
			(stack, item) ->
					item instanceof BlockItem blockItem &&
					LargeElectricFurnaceBlockEntity.getTiersByCoil().containsKey(BuiltInRegistries.BLOCK.getKey(blockItem.getBlock())),
			(flags, context, stack, item) ->
			{
				LargeElectricFurnaceBlockEntity.Tier tier = LargeElectricFurnaceBlockEntity.getTiersByCoil()
						.get(BuiltInRegistries.BLOCK.getKey(((BlockItem) stack.getItem()).getBlock()));
				int batchSize = tier.batchSize();
				float euCostMultiplier = tier.euCostMultiplier();
				return EI.text().coilsLEFTier(batchSize, euCostMultiplier);
			}
	);
	
	public static final TooltipAttachment WINDINGS_TESLA = TooltipAttachment.singleLine(
			(stack, item) ->
					item instanceof BlockItem blockItem &&
					TeslaTowerBlockEntity.getTiersByWinding().containsKey(BuiltInRegistries.BLOCK.getKey(blockItem.getBlock())),
			(flags, context, stack, item) ->
			{
				TeslaTowerTier tier = TeslaTowerBlockEntity.getTiersByWinding()
						.get(BuiltInRegistries.BLOCK.getKey(((BlockItem) stack.getItem()).getBlock()));
				return EI.text().windingsTeslaTowerTier(tier.maxTransfer(), tier.maxDistance(), tier.drain());
			}
	);
	
	public static final TooltipAttachment PHOTOVOLTAIC_CELLS = TooltipAttachment.multilines(
			PhotovoltaicCellItem.class,
			(flags, context, stack, item) ->
			{
				int euPerTick = item.getEuPerTick();
				List<Component> lines = Lists.newArrayList();
				lines.add(EI.text().photovoltaicCellEU(euPerTick));
				if(!item.lastsForever())
				{
					int solarTicksRemaining = item.getSolarTicksRemaining(stack);
					lines.add(EI.text().photovoltaicCellRemainingOperationTimeMinutes(solarTicksRemaining));
				}
				else
				{
					lines.add(EI.text().photovoltaicCellRemainingOperationTime(EI.text().infinity()));
				}
				return lines;
			}
	);
	
	public static final TooltipAttachment STEAM_CHAINSAW = TooltipAttachment.multilines(
			EIItems.STEAM_CHAINSAW,
			List.of(
					EI.text().steamChainsaw1("use"),
					EI.text().steamChainsaw2("use"),
					EI.text().steamChainsaw3("sneak", "use")
			)
	);
	
	public static final TooltipAttachment ROBOT_AUTO_FEEDER = TooltipAttachment.multilines(
			EIItems.ROBOT_AUTO_FEEDER,
			List.of(
					EI.text().robotAutoFeederHelp1(),
					EI.text().robotAutoFeederHelp2()
			)
	);
	
	public static final TooltipAttachment MACHINE_CONFIG_CARD = TooltipAttachment.multilines(
			EIItems.MACHINE_CONFIG_CARD,
			List.of(
					EI.text().machineConfigCardHelp1("sneak", "use"),
					EI.text().machineConfigCardHelp2("use"),
					EI.text().machineConfigCardHelp3(),
					EI.text().machineConfigCardHelp4("sneak", "use")
			)
	);
	
	public static final TooltipAttachment TESLA_CALIBRATOR = TooltipAttachment.multilines(
			EIItems.TESLA_CALIBRATOR,
			List.of(
					EI.text().teslaCalibratorHelp1("sneak", "use"),
					EI.text().teslaCalibratorHelp2("use"),
					EI.text().teslaCalibratorHelp3(),
					EI.text().teslaCalibratorHelp4("sneak", "use")
			)
	);
	
	public static final TooltipAttachment ELECTRIC_TOOL_HELP = TooltipAttachment.multilines(
			ElectricToolItem.class,
			(flags, context, stack, item) ->
			{
				List<Component> lines = Lists.newArrayList();
				lines.add(EI.text().electricToolHelp1());
				if(stack.is(ItemTags.DYEABLE))
				{
					lines.add(EI.text().dyeableHelp());
				}
				lines.add(item.getToolType().helpText());
				if(item.getToolType().hasAdjustableSpeed())
				{
					lines.add(EI.text().electricToolHelp3("alt", "mouse_scroll"));
				}
				if(item.getToolType().canDo3by3())
				{
					lines.add(EI.text().electricToolHelp4("%s.toggle_main_hand_ability".formatted(EI.ID), "mouse.right"));
				}
				if(item.getToolType() == ElectricToolItem.Type.SABER)
				{
					lines.add(EI.text().nanoSaberHelp("use"));
				}
				return lines;
			}
	);
	
	public static final TooltipAttachment NANO_SUIT_HELP = TooltipAttachment.multilines(
			NanoSuitArmorItem.class,
			(flags, context, stack, item) ->
			{
				List<Component> lines = Lists.newArrayList();
				lines.add(EI.text().nanoSuitHelp1());
				lines.add(EI.text().dyeableAndTrimmableHelp());
				item.ability().ifPresent((ability) -> lines.addAll(ability.getHelpTooltipLines(item, stack)));
				return lines;
			}
	);
	
	public static final TooltipAttachment MACHINE_CHAINER = TooltipAttachment.multilines(
			List.of(EI.id("machine_chainer")),
			List.of(
					EI.text().machineChainerHelp1(EI.config().machineChainerMaxConnections()),
					EI.text().machineChainerHelp2(),
					EI.text().machineChainerHelp3()
			)
	);
	
	public static final TooltipAttachment HONEY_EXTRACTOR = TooltipAttachment.singleLine(
			List.of(
					EI.id("steel_honey_extractor"),
					EI.id("electric_honey_extractor")
			),
			EI.text().honeyExtractorHelp()
	);
	
	public static final TooltipAttachment WASTE_COLLECTOR = TooltipAttachment.singleLine(
			List.of(
					EI.id("bronze_waste_collector"),
					EI.id("steel_waste_collector"),
					EI.id("electric_waste_collector")
			),
			EI.text().wasteCollectorHelp()
	);
	
	public static final TooltipAttachment TESLA_INTERDIMENSIONAL_UPGRADE = TooltipAttachment.singleLine(
			List.of(EI.id("tesla_interdimensional_upgrade")),
			EI.text().teslaInterdimensionalUpgradeHelp()
	);
	
	public static final TooltipAttachment TESLA_HANDHELD_RECEIVER = TooltipAttachment.multilines(
			List.of(EI.id("tesla_handheld_receiver")),
			List.of(
					EI.text().teslaHandheldHelp1(),
					EI.text().teslaHandheldHelp2(),
					EI.text().teslaHandheldHelp3("use"),
					EI.text().teslaHandheldHelp4("sneak", "use")
			)
	);
	
	private static final LinkedHashMap<TagKey<Item>, ResourceLocation> ENCHANTMENT_MODULE_MACHINES = Maps.newLinkedHashMap();
	
	public static void registerEnchantmentModuleMachine(TagKey<Item> tag, ResourceLocation machineId)
	{
		Assert.noneNull(tag, machineId);
		if(ENCHANTMENT_MODULE_MACHINES.put(tag, machineId) != null)
		{
			throw new IllegalArgumentException("Enchantment module machine already registered for tag " + tag.location());
		}
	}
	
	static
	{
		registerEnchantmentModuleMachine(EITags.Items.EnchantmentModules.FARMER, EI.id("electric_farmer"));
		registerEnchantmentModuleMachine(EITags.Items.EnchantmentModules.LETHAL_TESLA_COIL, EI.id("lethal_tesla_coil"));
	}
	
	public static final TooltipAttachment ENCHANTMENT_MODULE = TooltipAttachment.multilinesOptional(
			(flags, context, stack, item) ->
			{
				var module = EnchantmentModule.getFor(item);
				if(module != null)
				{
					List<Component> lines = Lists.newArrayList();
					
					for(var entry : ENCHANTMENT_MODULE_MACHINES.sequencedEntrySet())
					{
						if(stack.is(entry.getKey()))
						{
							lines.add(EI.text().enchantmentModuleMachine(entry.getValue()));
						}
					}
					
					if(module.values().isEmpty())
					{
						lines.add(EI.text().enchantmentModuleSingleValue(context.registries(), module.enchantment(), module.fallback().level(), module.fallback().euCost()));
					}
					else
					{
						lines.add(EI.text().enchantmentModuleValues(context.registries(), module.enchantment()));
						for(var tier : CableTier.allTiers())
						{
							var value = module.get(tier);
							lines.add(EI.text().voltageValueForCost(tier, value.level(), value.euCost()));
						}
					}
					return Optional.of(lines);
				}
				return Optional.empty();
			}
	);
	
	public static void init()
	{
	}
}
