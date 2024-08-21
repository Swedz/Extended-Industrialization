package net.swedz.extended_industrialization.machines.guicomponent.solarefficiency;

import aztech.modern_industrialization.machines.gui.GuiComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
		private final Supplier<Long>    energyProducedSupplier;
		
		private Server(Parameters params, Supplier<Boolean> workingSupplier, Supplier<Integer> efficiencySupplier, Supplier<Integer> calcificationSupplier, Supplier<Long> energyProducedSupplier)
		{
			this.params = params;
			this.workingSupplier = workingSupplier;
			this.efficiencySupplier = efficiencySupplier;
			this.calcificationSupplier = calcificationSupplier;
			this.energyProducedSupplier = energyProducedSupplier;
		}
		
		public static Server calcification(Parameters params, Supplier<Boolean> workingSupplier, Supplier<Integer> efficiencySupplier, Supplier<Integer> calcificationSupplier)
		{
			return new Server(params, workingSupplier, efficiencySupplier, calcificationSupplier, () -> -1L);
		}
		
		public static Server energyProduced(Parameters params, Supplier<Boolean> workingSupplier, Supplier<Integer> efficiencySupplier, Supplier<Long> energyProducedSupplier)
		{
			return new Server(params, workingSupplier, efficiencySupplier, () -> -1, energyProducedSupplier);
		}
		
		@Override
		public Data copyData()
		{
			return new Data(workingSupplier.get(), efficiencySupplier.get(), calcificationSupplier.get(), energyProducedSupplier.get());
		}
		
		@Override
		public boolean needsSync(Data cachedData)
		{
			return !cachedData.equals(this.copyData());
		}
		
		@Override
		public void writeInitialData(RegistryFriendlyByteBuf buf)
		{
			buf.writeInt(params.renderX);
			buf.writeInt(params.renderY);
			this.writeCurrentData(buf);
		}
		
		@Override
		public void writeCurrentData(RegistryFriendlyByteBuf buf)
		{
			buf.writeBoolean(workingSupplier.get());
			buf.writeInt(efficiencySupplier.get());
			
			int calcification = calcificationSupplier.get();
			boolean hasCalcification = calcification >= 0;
			buf.writeBoolean(hasCalcification);
			if(hasCalcification)
			{
				buf.writeInt(calcification);
			}
			
			long energyProduced = energyProducedSupplier.get();
			boolean hasEnergyProduced = energyProduced >= 0;
			buf.writeBoolean(hasEnergyProduced);
			if(hasEnergyProduced)
			{
				buf.writeLong(energyProduced);
			}
		}
		
		@Override
		public ResourceLocation getId()
		{
			return ID;
		}
	}
	
	private record Data(boolean working, int efficiency, int calcification, long energyProduced)
	{
	}
	
	public record Parameters(int renderX, int renderY)
	{
	}
}
