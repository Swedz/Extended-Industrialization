package net.swedz.miextended.tooltips;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.energy.EnergyApi;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.items.MIEItems;
import net.swedz.miextended.text.MIEText;

import java.util.List;
import java.util.Optional;

import static aztech.modern_industrialization.MITooltips.*;

public final class MIETooltips
{
	public static final Parser<MutableComponent> ADDED_BY_MIE_PARSER = (component) ->
			component.withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.BLUE);
	
	public static final Parser<MutableComponent> MULCH_GANG_FOR_LIFE_PARSER = (component) ->
			component.withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
	
	public static final TooltipAttachment ENERGY_STORED_ITEM = TooltipAttachment.of(
			(itemStack, item) ->
			{
				if(BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(MIExtended.ID))
				{
					var energyStorage = itemStack.getCapability(EnergyApi.ITEM);
					if(energyStorage != null)
					{
						long capacity = energyStorage.getCapacity();
						if(capacity > 0)
						{
							return Optional.of(new Line(MIText.EnergyStored)
									.arg(new NumberWithMax(energyStorage.getAmount(), capacity), EU_MAXED_PARSER).build());
						}
					}
				}
				return Optional.empty();
			}).noShiftRequired();
	
	public static final TooltipAttachment ADDED_BY_MIE = TooltipAttachment.of(
			(itemStack, item) ->
			{
				ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
				if(key.getNamespace().equals(MI.ID) && MIExtended.isItemRegisteredByMIButActuallyFromMIE(key))
				{
					return Optional.of(ADDED_BY_MIE_PARSER.parse(MIEText.ADDED_BY_MIE.text()));
				}
				return Optional.empty();
			}).noShiftRequired();
	
	public static final TooltipAttachment MULCH_GANG_FOR_LIFE = TooltipAttachment.ofMultilines(
			MIEItems.MULCH,
			List.of(
					MULCH_GANG_FOR_LIFE_PARSER.parse(MIEText.MULCH_GANG_FOR_LIFE_0.text()),
					MULCH_GANG_FOR_LIFE_PARSER.parse(MIEText.MULCH_GANG_FOR_LIFE_1.text())
			)
	).noShiftRequired();
	
	public static void init()
	{
	}
}
