package net.swedz.extended_industrialization.api;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record ItemStackTooltipComponent(ItemStack stack) implements TooltipComponent
{
}
