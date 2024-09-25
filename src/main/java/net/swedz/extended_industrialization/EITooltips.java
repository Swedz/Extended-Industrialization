package net.swedz.extended_industrialization;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.energy.EnergyApi;
import com.google.common.collect.Lists;
import dev.technici4n.grandpower.api.ILongEnergyStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.extended_industrialization.item.PhotovoltaicCellItem;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.LargeElectricFurnaceBlockEntity;
import net.swedz.tesseract.neoforge.tooltip.BiParser;
import net.swedz.tesseract.neoforge.tooltip.Parser;
import net.swedz.tesseract.neoforge.tooltip.TooltipAttachment;

import java.util.List;
import java.util.Optional;

import static aztech.modern_industrialization.MITooltips.*;
import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.line;

public final class EITooltips
{
	private static final BiParser<Boolean, Float> MAYBE_SPACED_PERCENTAGE_PARSER = (space, ratio) ->
			Component.literal("%d%s%%".formatted((int) (ratio * 100), space ? " " : "")).withStyle(NUMBER_TEXT);
	
	public static final Parser<Float> PERCENTAGE_PARSER = (ratio) -> MAYBE_SPACED_PERCENTAGE_PARSER.parse(false, ratio);
	
	public static final Parser<Float> SPACED_PERCENTAGE_PARSER = (ratio) -> MAYBE_SPACED_PERCENTAGE_PARSER.parse(true, ratio);
	
	public static final Parser<Long> TICKS_TO_MINUTES_PARSER = (ticks) ->
	{
		float minutes = (float) ticks / (60 * 20);
		return Component.literal("%.2f".formatted(minutes)).withStyle(NUMBER_TEXT);
	};
	
	public static final Parser<Integer> NUMBERED_LIST_BULLET_PARSER = (number) ->
			Component.literal("%d)".formatted(number)).withStyle(NUMBER_TEXT);
	
	public static final Parser<Boolean> ACTIVATED_BOOLEAN_PARSER = (value) ->
			value ? EIText.ACTIVATED.text().withStyle(ChatFormatting.GREEN) : EIText.DEACTIVATED.text().withStyle(ChatFormatting.RED);
	
	public static final Parser<GlobalPos> GLOBAL_POS_PARSER = Parser.GLOBAL_POS.withStyle(DEFAULT_STYLE);
	
	public static final Parser<String> KEYBIND_PARSER = Parser.KEYBIND.withStyle(NUMBER_TEXT);
	
	public static final TooltipAttachment ENERGY_STORED_ITEM = TooltipAttachment.singleLineOptional(
			(stack, item) -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(EI.ID),
			(stack, item) ->
			{
				ILongEnergyStorage energyStorage = stack.getCapability(EnergyApi.ITEM);
				if(energyStorage != null)
				{
					long capacity = energyStorage.getCapacity();
					if(capacity > 0)
					{
						return Optional.of(
								line(MIText.EnergyStored)
										.arg(new NumberWithMax(energyStorage.getAmount(), capacity), EU_MAXED_PARSER)
						);
					}
				}
				return Optional.empty();
			}
	).noShiftRequired();
	
	public static final TooltipAttachment MULCH_GANG_FOR_LIFE = TooltipAttachment.multilines(
			EIItems.MULCH,
			List.of(
					line(EIText.MULCH_GANG_FOR_LIFE_0, DEFAULT_STYLE.withItalic(true)),
					line(EIText.MULCH_GANG_FOR_LIFE_1, DEFAULT_STYLE.withItalic(true))
			)
	).noShiftRequired();
	
	public static final TooltipAttachment COILS = TooltipAttachment.singleLine(
			(stack, item) ->
					item instanceof BlockItem blockItem &&
					LargeElectricFurnaceBlockEntity.getTiersByCoil().containsKey(BuiltInRegistries.BLOCK.getKey(blockItem.getBlock())),
			(stack, item) ->
			{
				LargeElectricFurnaceBlockEntity.Tier tier = LargeElectricFurnaceBlockEntity.getTiersByCoil()
						.get(BuiltInRegistries.BLOCK.getKey(((BlockItem) stack.getItem()).getBlock()));
				int batchSize = tier.batchSize();
				float euCostMultiplier = tier.euCostMultiplier();
				return line(EIText.COILS_LEF_TIER).arg(batchSize).arg(euCostMultiplier, PERCENTAGE_PARSER);
			}
	);
	
	public static final TooltipAttachment PHOTOVOLTAIC_CELLS = TooltipAttachment.multilines(
			PhotovoltaicCellItem.class,
			(stack, item) ->
			{
				int euPerTick = item.getEuPerTick();
				List<Component> lines = Lists.newArrayList();
				lines.add(line(EIText.PHOTOVOLTAIC_CELL_EU).arg(euPerTick, EU_PER_TICK_PARSER));
				if(!item.lastsForever())
				{
					int solarTicksRemaining = item.getSolarTicksRemaining(stack);
					lines.add(line(EIText.PHOTOVOLTAIC_CELL_REMAINING_OPERATION_TIME_MINUTES).arg((long) solarTicksRemaining, TICKS_TO_MINUTES_PARSER));
				}
				else
				{
					lines.add(line(EIText.PHOTOVOLTAIC_CELL_REMAINING_OPERATION_TIME).arg(Component.literal("\u221E").withStyle(NUMBER_TEXT)));
				}
				return lines;
			}
	);
	
	public static final TooltipAttachment STEAM_CHAINSAW = TooltipAttachment.multilines(
			EIItems.STEAM_CHAINSAW,
			List.of(
					line(EIText.STEAM_CHAINSAW_1).arg("use", KEYBIND_PARSER),
					line(EIText.STEAM_CHAINSAW_2).arg("use", KEYBIND_PARSER),
					line(EIText.STEAM_CHAINSAW_3).arg("sneak", KEYBIND_PARSER).arg("use", KEYBIND_PARSER)
			)
	);
	
	public static final TooltipAttachment MACHINE_CONFIG_CARD = TooltipAttachment.multilines(
			EIItems.MACHINE_CONFIG_CARD,
			List.of(
					line(EIText.MACHINE_CONFIG_CARD_HELP_1).arg("sneak", KEYBIND_PARSER).arg("use", KEYBIND_PARSER),
					line(EIText.MACHINE_CONFIG_CARD_HELP_2).arg("use", KEYBIND_PARSER),
					line(EIText.MACHINE_CONFIG_CARD_HELP_3),
					line(EIText.MACHINE_CONFIG_CARD_HELP_4).arg("sneak", KEYBIND_PARSER).arg("use", KEYBIND_PARSER)
			)
	);
	
	public static final TooltipAttachment ELECTRIC_TOOL_HELP = TooltipAttachment.multilines(
			ElectricToolItem.class,
			(stack, item) ->
			{
				List<Component> lines = Lists.newArrayList();
				lines.add(line(EIText.ELECTRIC_TOOL_HELP_1));
				if(stack.is(ItemTags.DYEABLE))
				{
					lines.add(line(EIText.DYEABLE_HELP));
				}
				lines.add(line(item.getToolType().includeLooting() ? EIText.ELECTRIC_TOOL_HELP_2_LOOTING : EIText.ELECTRIC_TOOL_HELP_2)
						.arg("sneak", KEYBIND_PARSER).arg("use", KEYBIND_PARSER));
				lines.add(line(EIText.ELECTRIC_TOOL_HELP_3)
						.arg(EIText.KEY_ALT.text().withStyle(NUMBER_TEXT))
						.arg(EIText.KEY_MOUSE_SCROLL.text().withStyle(NUMBER_TEXT)));
				if(item.getToolType().canDo3by3())
				{
					lines.add(line(EIText.ELECTRIC_TOOL_HELP_4).arg("%s.toggle_main_hand_ability".formatted(EI.ID), KEYBIND_PARSER));
				}
				return lines;
			}
	);
	
	public static final TooltipAttachment NANO_SUIT_HELP = TooltipAttachment.multilines(
			NanoSuitArmorItem.class,
			(stack, item) ->
			{
				List<Component> lines = Lists.newArrayList();
				lines.add(line(EIText.NANO_SUIT_HELP_1));
				lines.add(line(EIText.DYEABLE_AND_TRIMMABLE_HELP));
				item.ability().ifPresent((ability) -> lines.addAll(ability.getHelpTooltipLines(item, stack)));
				return lines;
			}
	);
	
	public static final TooltipAttachment MACHINE_CHAINER = TooltipAttachment.multilines(
			List.of(EI.id("machine_chainer")),
			List.of(
					line(EIText.MACHINE_CHAINER_HELP_1).arg(EIConfig.machineChainerMaxConnections),
					line(EIText.MACHINE_CHAINER_HELP_2),
					line(EIText.MACHINE_CHAINER_HELP_3)
			)
	);
	
	public static final TooltipAttachment HONEY_EXTRACTOR = TooltipAttachment.singleLine(
			List.of(
					EI.id("steel_honey_extractor"),
					EI.id("electric_honey_extractor")
			),
			line(EIText.HONEY_EXTRACTOR_HELP)
	);
	
	public static final TooltipAttachment WASTE_COLLECTOR = TooltipAttachment.singleLine(
			List.of(
					EI.id("bronze_waste_collector"),
					EI.id("steel_waste_collector"),
					EI.id("electric_waste_collector")
			),
			line(EIText.WASTE_COLLECTOR_HELP)
	);
	
	public static final TooltipAttachment CHARGING_STATION_RANGED = TooltipAttachment.singleLine(
			List.of(EI.id("local_wireless_charging_station")),
			line(EIText.CHARGING_STATION_RANGED).arg(EIConfig.localWirelessChargingStationRange)
	);
	
	public static final TooltipAttachment CHARGING_STATION_GLOBAL = TooltipAttachment.singleLine(
			List.of(EI.id("global_wireless_charging_station")),
			line(EIText.CHARGING_STATION_GLOBAL)
	);
	
	public static final TooltipAttachment CHARGING_STATION_INTERDIMENSIONAL = TooltipAttachment.singleLine(
			List.of(EI.id("interdimensional_wireless_charging_station")),
			line(EIText.CHARGING_STATION_INTERDIMENSIONAL)
	);
	
	public static void init()
	{
	}
}
