package net.swedz.extended_industrialization;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.energy.EnergyApi;
import com.google.common.collect.Lists;
import dev.technici4n.grandpower.api.ILongEnergyStorage;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.extended_industrialization.item.PhotovoltaicCellItem;
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
	
	public static final Parser<String> KEYBIND_PARSER = Parser.KEYBIND.withStyle(NUMBER_TEXT);
	
	public static final TooltipAttachment ENERGY_STORED_ITEM = TooltipAttachment.singleLineOptional(
			(stack, item) -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(EI.ID),
			(itemStack, item) ->
			{
				ILongEnergyStorage energyStorage = itemStack.getCapability(EnergyApi.ITEM);
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
			(itemStack, item) ->
			{
				LargeElectricFurnaceBlockEntity.Tier tier = LargeElectricFurnaceBlockEntity.getTiersByCoil()
						.get(BuiltInRegistries.BLOCK.getKey(((BlockItem) itemStack.getItem()).getBlock()));
				int batchSize = tier.batchSize();
				float euCostMultiplier = tier.euCostMultiplier();
				return line(EIText.COILS_LEF_TIER).arg(batchSize).arg(euCostMultiplier, PERCENTAGE_PARSER);
			}
	);
	
	public static final TooltipAttachment PHOTOVOLTAIC_CELLS = TooltipAttachment.multilines(
			PhotovoltaicCellItem.class,
			(itemStack, item) ->
			{
				int euPerTick = item.getEuPerTick();
				List<Component> lines = Lists.newArrayList();
				lines.add(line(EIText.PHOTOVOLTAIC_CELL_EU).arg(euPerTick, EU_PER_TICK_PARSER));
				if(!item.lastsForever())
				{
					int solarTicksRemaining = item.getSolarTicksRemaining(itemStack);
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
					line(EIText.STEAM_CHAINSAW_3),
					line(EIText.STEAM_CHAINSAW_4).arg("sneak", KEYBIND_PARSER).arg("use", KEYBIND_PARSER)
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
	
	public static final TooltipAttachment ELECTRIC_TOOL_SPEED = TooltipAttachment.singleLine(
			ElectricToolItem.class,
			(itemStack, item) ->
			{
				int speed = ElectricToolItem.getToolSpeed(itemStack);
				return line(EIText.MINING_SPEED)
						.arg((float) speed / ElectricToolItem.SPEED_MAX, SPACED_PERCENTAGE_PARSER);
			}
	).noShiftRequired();
	
	public static final TooltipAttachment ELECTRIC_TOOL_CONTROLS = TooltipAttachment.multilines(
			ElectricToolItem.class,
			(stack, tool) -> List.of(
					line(EIText.ELECTRIC_TOOL_HELP_1),
					line(tool.getToolType().includeLooting() ? EIText.ELECTRIC_TOOL_HELP_2_LOOTING : EIText.ELECTRIC_TOOL_HELP_2)
							.arg("sneak", KEYBIND_PARSER).arg("use", KEYBIND_PARSER),
					line(EIText.ELECTRIC_TOOL_HELP_3)
							.arg(EIText.KEY_ALT.text().withStyle(NUMBER_TEXT))
							.arg(EIText.KEY_MOUSE_SCROLL.text().withStyle(NUMBER_TEXT))
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
	
	public static void init()
	{
	}
}
