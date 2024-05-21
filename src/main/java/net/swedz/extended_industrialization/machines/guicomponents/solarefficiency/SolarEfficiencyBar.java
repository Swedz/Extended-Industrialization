package net.swedz.extended_industrialization.machines.guicomponents.solarefficiency;

import aztech.modern_industrialization.machines.gui.GuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;

import java.util.function.Supplier;

public final class SolarEfficiencyBar
{
	public static final ResourceLocation ID = EI.id("solar_efficiency_bar");
	
	public static final class Server implements GuiComponent.Server<Data>
	{
		private final Parameters params;
		
		private final Supplier<Boolean> workingSupplier;
		private final Supplier<Integer> efficiencySupplier;
		private final Supplier<Integer> calcificationSupplier;
		
		public Server(Parameters params, Supplier<Boolean> workingSupplier, Supplier<Integer> efficiencySupplier, Supplier<Integer> calcificationSupplier)
		{
			this.params = params;
			this.workingSupplier = workingSupplier;
			this.efficiencySupplier = efficiencySupplier;
			this.calcificationSupplier = calcificationSupplier;
		}
		
		@Override
		public Data copyData()
		{
			return new Data(workingSupplier.get(), efficiencySupplier.get(), calcificationSupplier.get());
		}
		
		@Override
		public boolean needsSync(Data cachedData)
		{
			return !cachedData.equals(this.copyData());
		}
		
		@Override
		public void writeInitialData(FriendlyByteBuf buf)
		{
			buf.writeInt(params.renderX);
			buf.writeInt(params.renderY);
			this.writeCurrentData(buf);
		}
		
		@Override
		public void writeCurrentData(FriendlyByteBuf buf)
		{
			buf.writeBoolean(workingSupplier.get());
			buf.writeInt(efficiencySupplier.get());
			buf.writeInt(calcificationSupplier.get());
		}
		
		@Override
		public ResourceLocation getId()
		{
			return ID;
		}
	}
	
	private static final class Data
	{
		final boolean working;
		final int     efficiency;
		final int     calcification;
		
		private Data(boolean working, int efficiency, int calcification)
		{
			this.working = working;
			this.efficiency = efficiency;
			this.calcification = calcification;
		}
	}
	
	public static class Parameters
	{
		public final int renderX, renderY;
		
		public Parameters(int renderX, int renderY)
		{
			this.renderX = renderX;
			this.renderY = renderY;
		}
	}
}
