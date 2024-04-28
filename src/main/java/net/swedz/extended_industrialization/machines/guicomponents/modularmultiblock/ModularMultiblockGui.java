package net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock;

import aztech.modern_industrialization.machines.gui.GuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.Supplier;

public final class ModularMultiblockGui
{
	public static final ResourceLocation ID = EI.id("modular_multiblock_gui");
	
	public static final class Server implements GuiComponent.Server<Data>
	{
		private final Supplier<List<ModularMultiblockGuiLine>> textSupplier;
		
		public Server(Supplier<List<ModularMultiblockGuiLine>> textSupplier)
		{
			this.textSupplier = textSupplier;
		}
		
		@Override
		public Data copyData()
		{
			return new Data(textSupplier.get());
		}
		
		@Override
		public boolean needsSync(Data cachedData)
		{
			return !textSupplier.get().equals(cachedData.text);
		}
		
		@Override
		public void writeInitialData(FriendlyByteBuf buf)
		{
			this.writeCurrentData(buf);
		}
		
		@Override
		public void writeCurrentData(FriendlyByteBuf buf)
		{
			buf.writeCollection(textSupplier.get(), ModularMultiblockGuiLine::write);
		}
		
		@Override
		public ResourceLocation getId()
		{
			return ID;
		}
	}
	
	private static final class Data
	{
		final List<ModularMultiblockGuiLine> text;
		
		private Data()
		{
			this(Lists.newArrayList());
		}
		
		private Data(List<ModularMultiblockGuiLine> text)
		{
			this.text = text;
		}
	}
	
	public static final int X = 4;
	public static final int Y = 16;
	public static final int W = 166;
	public static final int H = 80;
}
