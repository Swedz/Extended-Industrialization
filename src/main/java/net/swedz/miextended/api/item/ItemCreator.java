package net.swedz.miextended.api.item;

import net.minecraft.world.item.Item;
import net.swedz.miextended.api.Creator;

public interface ItemCreator<P extends Item.Properties> extends Creator<Item, P>
{
}
