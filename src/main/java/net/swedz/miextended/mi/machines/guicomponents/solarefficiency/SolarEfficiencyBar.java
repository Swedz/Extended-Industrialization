package net.swedz.miextended.mi.machines.guicomponents.solarefficiency;

import aztech.modern_industrialization.machines.gui.GuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.swedz.miextended.MIExtended;

import java.util.function.Supplier;

public final class SolarEfficiencyBar
{
	public static final ResourceLocation ID = MIExtended.id("solar_efficiency_bar");
	
	public static final class Server implements GuiComponent.Server<Data>
	{
		private final Parameters params;
		
		private final Supplier<Boolean> workingSupplier;
		private final Supplier<Integer> efficiencySupplier;
		
		public Server(Parameters params, Supplier<Boolean> workingSupplier, Supplier<Integer> efficiencySupplier)
		{
			this.params = params;
			this.workingSupplier = workingSupplier;
			this.efficiencySupplier = efficiencySupplier;
		}
		
		@Override
		public Data copyData()
		{
			return new Data(workingSupplier.get(), efficiencySupplier.get());
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
		
		private Data(boolean working, int efficiency)
		{
			this.working = working;
			this.efficiency = efficiency;
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
