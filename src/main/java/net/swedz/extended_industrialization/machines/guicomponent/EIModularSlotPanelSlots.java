package net.swedz.extended_industrialization.machines.guicomponent;

import aztech.modern_industrialization.inventory.SlotGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.EIText;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.slotpanel.ModularSlotPanel;

import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.*;

public final class EIModularSlotPanelSlots
{
	public static final ResourceLocation TESLA_TOWER_UPGRADE = register(
			"tesla_tower_upgrade", SlotGroup.UPGRADES, 64,
			(stack) -> stack.is(EIItems.TESLA_INTERDIMENSIONAL_UPGRADE.asItem()),
			2, 0, () -> line(EIText.TESLA_TOWER_UPGRADE)
	);
	
	public static final ResourceLocation FARMER_ENCHANTMENT_MODULE = registerEnchantmentModule(
			"farmer", EITags.Items.EnchantmentModules.FARMER
	);
	
	public static final ResourceLocation LETHAL_TESLA_COIL_ENCHANTMENT_MODULE = registerEnchantmentModule(
			"lethal_tesla_coil", EITags.Items.EnchantmentModules.LETHAL_TESLA_COIL
	);
	
	public static void init()
	{
	}
	
	private static ResourceLocation register(String name, SlotGroup group,
											 int stackLimit, Predicate<ItemStack> insertionChecker,
											 int x, int y,
											 Supplier<Component> tooltip)
	{
		return ModularSlotPanel.registerSlot(
				EI.id(name), group,
				stackLimit, insertionChecker,
				EI.id("textures/gui/container/slot_atlas.png"), x * 18, y * 18,
				tooltip
		);
	}
	
	private static ResourceLocation registerEnchantmentModule(String name, TagKey<Item> tag)
	{
		return register(
				"%s_enchantment_module".formatted(name), SlotGroup.CONFIGURABLE_STACKS, 1,
				(stack) -> stack.is(tag),
				3, 0, () -> line(EIText.ENCHANTMENT_MODULE_INPUT)
		);
	}
	
	private static ResourceLocation register(String name, SlotGroup group,
											 int stackLimit, Predicate<ItemStack> insertionChecker,
											 Supplier<Component> tooltip)
	{
		return register(name, group, stackLimit, insertionChecker, 0, 0, tooltip);
	}
}
