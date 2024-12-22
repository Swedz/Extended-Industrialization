package net.swedz.extended_industrialization.item.nanosuit.decoration;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;

import java.util.List;
import java.util.Optional;

import static aztech.modern_industrialization.MITooltips.*;
import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.line;

public final class MeowNanoDecoration implements NanoSuitDecoration
{
	@Override
	public boolean isActiveFor(NanoSuitArmorItem item, ItemStack stack)
	{
		return stack.getOrDefault(EIComponents.MEOW, false);
	}
	
	@Override
	public ArmorItem.Type armorType()
	{
		return ArmorItem.Type.HELMET;
	}
	
	@Override
	public String getDescriptionId(NanoSuitArmorItem item, ItemStack stack)
	{
		return item.isQuantum() ?
				"item.extended_industrialization.quantum_nyano_helmet" :
				"item.extended_industrialization.nyano_helmet";
	}
	
	@Override
	public Optional<List<Component>> getTooltipLines(NanoSuitArmorItem item, ItemStack stack)
	{
		return Optional.of(List.of(line(EIText.MEOW).withStyle(DEFAULT_STYLE.withItalic(true))));
	}
	
	@Override
	public ItemProperty itemProperty(NanoSuitArmorItem item)
	{
		return new ItemProperty(
				EI.id("meow"),
				item.isQuantum() ? "quantum_nyano_helmet" : "nyano_helmet",
				EIComponents.MEOW
		);
	}
}
