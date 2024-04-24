package net.swedz.extended_industrialization.tooltips;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.energy.EnergyApi;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.registry.items.EIItems;
import net.swedz.extended_industrialization.text.EIText;

import java.util.List;
import java.util.Optional;

import static aztech.modern_industrialization.MITooltips.*;

public final class EITooltips
{
	public static final Parser<MutableComponent> MULCH_GANG_FOR_LIFE_PARSER = (component) ->
			component.withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
	
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
							return Optional.of(new Line(MIText.EnergyStored)
									.arg(new NumberWithMax(energyStorage.getAmount(), capacity), EU_MAXED_PARSER).build());
						}
					}
				}
				return Optional.empty();
			}).noShiftRequired();
	
	public static final TooltipAttachment MULCH_GANG_FOR_LIFE = TooltipAttachment.ofMultilines(
			EIItems.MULCH,
			List.of(
					MULCH_GANG_FOR_LIFE_PARSER.parse(EIText.MULCH_GANG_FOR_LIFE_0.text()),
					MULCH_GANG_FOR_LIFE_PARSER.parse(EIText.MULCH_GANG_FOR_LIFE_1.text())
			)
	).noShiftRequired();
	
	public static void init()
	{
	}
}
