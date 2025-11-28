package net.swedz.extended_industrialization.machines.guicomponent.solarefficiency;

import aztech.modern_industrialization.machines.gui.GuiComponentServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.extended_industrialization.EI;

import java.util.function.Supplier;

public final class SolarEfficiencyBar implements GuiComponentServer<SolarEfficiencyBar.Params, SolarEfficiencyBar.Data>
{
	public static final Type<Params, Data> TYPE = new Type<>(EI.id("solar_efficiency_bar"), Params.STREAM_CODEC, Data.STREAM_CODEC);
	
	private final Params params;
	
	private final Supplier<Boolean> workingSupplier;
	private final Supplier<Integer> efficiencySupplier;
	private final Supplier<Integer> calcificationSupplier;
	private final Supplier<Long>    energyProducedSupplier;
	
	private SolarEfficiencyBar(Params params, Supplier<Boolean> workingSupplier, Supplier<Integer> efficiencySupplier, Supplier<Integer> calcificationSupplier, Supplier<Long> energyProducedSupplier)
	{
		this.params = params;
		this.workingSupplier = workingSupplier;
		this.efficiencySupplier = efficiencySupplier;
		this.calcificationSupplier = calcificationSupplier;
		this.energyProducedSupplier = energyProducedSupplier;
	}
	
	public static SolarEfficiencyBar calcification(Params params, Supplier<Boolean> workingSupplier, Supplier<Integer> efficiencySupplier, Supplier<Integer> calcificationSupplier)
	{
		return new SolarEfficiencyBar(params, workingSupplier, efficiencySupplier, calcificationSupplier, () -> -1L);
	}
	
	public static SolarEfficiencyBar energyProduced(Params params, Supplier<Boolean> workingSupplier, Supplier<Integer> efficiencySupplier, Supplier<Long> energyProducedSupplier)
	{
		return new SolarEfficiencyBar(params, workingSupplier, efficiencySupplier, () -> -1, energyProducedSupplier);
	}
	
	@Override
	public Params getParams()
	{
		return params;
	}
	
	@Override
	public Data extractData()
	{
		return new Data(workingSupplier.get(), efficiencySupplier.get(), calcificationSupplier.get(), energyProducedSupplier.get());
	}
	
	@Override
	public Type<Params, Data> getType()
	{
		return TYPE;
	}
	
	public record Params(int renderX, int renderY)
	{
		public static final StreamCodec<ByteBuf, Params> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, Params::renderX,
				ByteBufCodecs.VAR_INT, Params::renderY,
				Params::new
		);
	}
	
	public record Data(boolean working, int efficiency, int calcification, long energyProduced)
	{
		public static final StreamCodec<ByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.BOOL, Data::working,
				ByteBufCodecs.VAR_INT, Data::efficiency,
				ByteBufCodecs.VAR_INT, Data::calcification,
				ByteBufCodecs.VAR_LONG, Data::energyProduced,
				Data::new
		);
	}
}
