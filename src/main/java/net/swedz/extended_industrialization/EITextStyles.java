package net.swedz.extended_industrialization;

import aztech.modern_industrialization.MITooltips;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

/**
 * Same styles as those in {@link MITooltips}. Prefer referencing these over the MI ones, since the MI class runs code
 * on initialization.
 */
public final class EITextStyles
{
	public static final Style DEFAULT_STYLE   = Style.EMPTY.withColor(TextColor.fromRgb(0xA9A9A9)).withItalic(false);
	public static final Style HIGHLIGHT_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(0xFFDE7D)).withItalic(false);
}
