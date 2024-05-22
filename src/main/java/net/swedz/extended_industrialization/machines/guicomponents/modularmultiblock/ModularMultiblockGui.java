package net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock;

import aztech.modern_industrialization.machines.gui.GuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;

import java.util.List;
import java.util.function.Supplier;

public final class ModularMultiblockGui
{
	public static final ResourceLocation ID = EI.id("modular_multiblock_gui");
	
	public static final class Server implements GuiComponent.Server<Data>
	{
		private final int height;
		
		private final Supplier<List<ModularMultiblockGuiLine>> textSupplier;
		
		public Server(int height, Supplier<List<ModularMultiblockGuiLine>> textSupplier)
		{
			if(height <= 4 || height > H)
			{
				throw new IllegalArgumentException("Provided height outside of acceptable bounds");
			}
			this.height = height;
			this.textSupplier = textSupplier;
		}
		
		@Override
		public Data copyData()
		{
			return new Data(height, textSupplier.get());
		}
		
		@Override
		public boolean needsSync(Data cachedData)
		{
			return !cachedData.equals(this.copyData());
		}
		
		@Override
		public void writeInitialData(FriendlyByteBuf buf)
		{
			this.writeCurrentData(buf);
		}
		
		@Override
		public void writeCurrentData(FriendlyByteBuf buf)
		{
			buf.writeInt(height);
			buf.writeCollection(textSupplier.get(), ModularMultiblockGuiLine::write);
		}
		
		@Override
		public ResourceLocation getId()
		{
			return ID;
		}
	}
	
	private record Data(int height, List<ModularMultiblockGuiLine> text)
	{
	}
	
	public static final int X = 4;
	public static final int Y = 16;
	public static final int W = 166;
	public static final int H = 80;
}
