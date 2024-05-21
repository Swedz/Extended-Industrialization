package net.swedz.extended_industrialization.machines.guicomponents.modularselection;

import aztech.modern_industrialization.machines.gui.GuiComponent;
import com.google.common.base.Preconditions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;

import java.util.List;
import java.util.stream.IntStream;

/**
 * This was stolen from {@link aztech.modern_industrialization.machines.guicomponents.ShapeSelection} to make my own generic "configuration panel" component to be used for non-shape related configuring of machines.
 */
public final class ConfigurationPanel
{
	public static final ResourceLocation ID = EI.id("configuration_panel");
	
	public interface Behavior
	{
		void handleClick(int clickedLine, int delta);
		
		int getCurrentIndex(int line);
	}
	
	public record LineInfo(int numValues, List<? extends Component> translations, boolean useArrows)
	{
		public LineInfo
		{
			Preconditions.checkArgument(numValues == translations.size());
		}
	}
	
	public static class Server implements GuiComponent.Server<int[]>
	{
		public final  Behavior       behavior;
		private final List<LineInfo> lines;
		
		public Server(Behavior behavior, LineInfo... lines)
		{
			Preconditions.checkArgument(lines.length > 0);
			
			this.behavior = behavior;
			this.lines = List.of(lines);
		}
		
		@Override
		public int[] copyData()
		{
			return IntStream.range(0, lines.size()).map(behavior::getCurrentIndex).toArray();
		}
		
		@Override
		public boolean needsSync(int[] cachedData)
		{
			for(int i = 0; i < lines.size(); ++i)
			{
				if(cachedData[i] != behavior.getCurrentIndex(i))
				{
					return true;
				}
			}
			return false;
		}
		
		@Override
		public void writeInitialData(FriendlyByteBuf buf)
		{
			buf.writeVarInt(lines.size());
			for(LineInfo line : lines)
			{
				buf.writeVarInt(line.numValues);
				for(Component component : line.translations)
				{
					buf.writeComponent(component);
				}
				buf.writeBoolean(line.useArrows);
			}
			writeCurrentData(buf);
		}
		
		@Override
		public void writeCurrentData(FriendlyByteBuf buf)
		{
			for(int i = 0; i < lines.size(); ++i)
			{
				buf.writeVarInt(behavior.getCurrentIndex(i));
			}
		}
		
		@Override
		public ResourceLocation getId()
		{
			return ID;
		}
	}
}
