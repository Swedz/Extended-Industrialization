package net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public record ModularMultiblockGuiLine(Component text, int color)
{
	public ModularMultiblockGuiLine(Component text)
	{
		this(text, 0xFFFFFF);
	}
	
	public static ModularMultiblockGuiLine read(FriendlyByteBuf buf)
	{
		Component text = buf.readComponent();
		int color = buf.readInt();
		return new ModularMultiblockGuiLine(text, color);
	}
	
	public static void write(FriendlyByteBuf buf, ModularMultiblockGuiLine line)
	{
		buf.writeComponent(line.text());
		buf.writeInt(line.color());
	}
}
