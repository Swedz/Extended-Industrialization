package net.swedz.extended_industrialization;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.MITooltips;
import aztech.modern_industrialization.api.energy.EnergyApi;
import com.google.common.collect.Lists;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.swedz.extended_industrialization.items.PhotovoltaicCellItem;
import net.swedz.extended_industrialization.machines.blockentities.multiblock.LargeElectricFurnaceBlockEntity;

import java.util.List;
import java.util.Optional;

import static aztech.modern_industrialization.MITooltips.*;

public final class EITooltips
{
	public static final Parser<Float> RATIO_PERCENTAGE_PARSER = (ratio) ->
			Component.literal("%d%%".formatted((int) (ratio * 100))).withStyle(NUMBER_TEXT);
	
	public static final Parser<Long> TICKS_TO_MINUTES_PARSER = (ticks) ->
	{
		float minutes = (float) ticks / (60 * 20);
		return Component.literal("%.2f".formatted(minutes)).withStyle(NUMBER_TEXT);
	};
	
	public static final Parser<Integer> NUMBERED_LIST_BULLET_PARSER = (number) ->
			Component.literal("%d)".formatted(number)).withStyle(NUMBER_TEXT);
	
	// TODO remove this in favor of the parser i will be adding in MITooltips
	public static final Parser<String> KEYBIND = (keybind) ->
			Component.keybind("key.%s".formatted(keybind)).withStyle(NUMBER_TEXT);
	
	public static final TooltipAttachment ENERGY_STORED_ITEM = TooltipAttachment.of(
			(itemStack, item) ->
			{
				if(BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(EI.ID))
				{
					var energyStorage = itemStack.getCapability(EnergyApi.ITEM);
					if(energyStorage != null)
					{
						long capacity = energyStorage.getCapacity();
						if(capacity > 0)
						{
							return Optional.of(
									MITooltips.line(MIText.EnergyStored)
											.arg(new NumberWithMax(energyStorage.getAmount(), capacity), EU_MAXED_PARSER)
											.build()
							);
						}
					}
				}
				return Optional.empty();
			}).noShiftRequired();
	
	public static final TooltipAttachment MULCH_GANG_FOR_LIFE = TooltipAttachment.ofMultilines(
			EIItems.MULCH,
			List.of(
					line(EIText.MULCH_GANG_FOR_LIFE_0, DEFAULT_STYLE.withItalic(true)).build(),
					line(EIText.MULCH_GANG_FOR_LIFE_1, DEFAULT_STYLE.withItalic(true)).build()
			)
	).noShiftRequired();
	
	public static final TooltipAttachment COILS = TooltipAttachment.of(
			(itemStack, item) ->
			{
				if(item instanceof BlockItem blockItem && LargeElectricFurnaceBlockEntity.getTiersByCoil().containsKey(BuiltInRegistries.BLOCK.getKey(blockItem.getBlock())))
				{
					LargeElectricFurnaceBlockEntity.Tier tier = LargeElectricFurnaceBlockEntity.getTiersByCoil()
							.get(BuiltInRegistries.BLOCK.getKey(((BlockItem) itemStack.getItem()).getBlock()));
					int batchSize = tier.batchSize();
					float euCostMultiplier = tier.euCostMultiplier();
					return Optional.of(line(EIText.COILS_LEF_TIER).arg(batchSize).arg(euCostMultiplier, RATIO_PERCENTAGE_PARSER).build());
				}
				else
				{
					return Optional.empty();
				}
			});
	
	public static final TooltipAttachment PHOTOVOLTAIC_CELLS = TooltipAttachment.ofMultilines(
			(itemStack, item) ->
			{
				if(item instanceof PhotovoltaicCellItem photovoltaicCell)
				{
					int euPerTick = photovoltaicCell.getEuPerTick();
					List<Component> lines = Lists.newArrayList();
					lines.add(line(EIText.PHOTOVOLTAIC_CELL_EU).arg(euPerTick, EU_PER_TICK_PARSER).build());
					if(!photovoltaicCell.lastsForever())
					{
						int solarTicksRemaining = photovoltaicCell.getSolarTicksRemaining(itemStack);
						lines.add(line(EIText.PHOTOVOLTAIC_CELL_REMAINING_OPERATION_TIME_MINUTES).arg((long) solarTicksRemaining, TICKS_TO_MINUTES_PARSER).build());
					}
					else
					{
						lines.add(line(EIText.PHOTOVOLTAIC_CELL_REMAINING_OPERATION_TIME).arg(Component.literal("\u221E").withStyle(NUMBER_TEXT)).build());
					}
					return Optional.of(lines);
				}
				return Optional.empty();
			});
	
	public static final TooltipAttachment STEAM_CHAINSAW = TooltipAttachment.ofMultilines(
			EIItems.STEAM_CHAINSAW,
			List.of(
					line(EIText.STEAM_CHAINSAW_1).arg("use", KEYBIND).build(),
					line(EIText.STEAM_CHAINSAW_2).arg("use", KEYBIND).build(),
					line(EIText.STEAM_CHAINSAW_3).build(),
					line(EIText.STEAM_CHAINSAW_4).arg("sneak", KEYBIND).arg("use", KEYBIND).build()
			)
	);
	
	public static final TooltipAttachment MACHINE_CONFIG_CARD = TooltipAttachment.ofMultilines(
			EIItems.MACHINE_CONFIG_CARD,
			List.of(
					line(EIText.MACHINE_CONFIG_CARD_HELP_1).arg("sneak", KEYBIND).arg("use", KEYBIND).build(),
					line(EIText.MACHINE_CONFIG_CARD_HELP_2).arg("use", KEYBIND).build(),
					line(EIText.MACHINE_CONFIG_CARD_HELP_3).build(),
					line(EIText.MACHINE_CONFIG_CARD_HELP_4).arg("sneak", KEYBIND).arg("use", KEYBIND).build()
			)
	);
	
	public static Line line(EIText text, Style style)
	{
		return new Line(text, style);
	}
	
	public static Line line(EIText text)
	{
		return line(text, DEFAULT_STYLE);
	}
	
	public static final class Line
	{
		private final EIText text;
		private final Style  style;
		
		private final List<Component> arguments = Lists.newArrayList();
		
		private Line(EIText text, Style style)
		{
			this.text = text;
			this.style = style;
		}
		
		public <T> Line arg(T arg, Parser<T> parser)
		{
			arguments.add(parser.parse(arg));
			return this;
		}
		
		public Line arg(Object arg)
		{
			return this.arg(arg, DEFAULT_PARSER);
		}
		
		public Component build()
		{
			return text.text(arguments.toArray()).withStyle(style);
		}
	}
	
	public static void init()
	{
	}
}
