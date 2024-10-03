package net.swedz.extended_industrialization.machines.guicomponent;

import aztech.modern_industrialization.inventory.SlotGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.machines.guicomponent.modularslots.ModularSlotPanel;

import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.*;

public final class EIModularSlotPanelSlots
{
	public static final ResourceLocation TESLA_TOWER_UPGRADE = register(
			"tesla_tower_upgrade", SlotGroup.UPGRADES, 64,
			(stack) -> stack.is(EIItems.TESLA_INTERDIMENSIONAL_UPGRADE.asItem()),
			0, 80, () -> line(EIText.TESLA_TOWER_UPGRADE)
	);
	
	public static void init()
	{
	}
	
	private static ResourceLocation register(String name, SlotGroup group,
											 int stackLimit, Predicate<ItemStack> insertionChecker,
											 int u, int v,
											 Supplier<Component> tooltip)
	{
		return ModularSlotPanel.registerSlot(EI.id(name), group, stackLimit, insertionChecker, u, v, tooltip);
	}
}
