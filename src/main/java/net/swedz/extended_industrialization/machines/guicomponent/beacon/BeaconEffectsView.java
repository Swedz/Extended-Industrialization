package net.swedz.extended_industrialization.machines.guicomponent.beacon;

import aztech.modern_industrialization.machines.gui.GuiComponentServer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.beacon.BeaconEffectComponent;

import java.util.List;
import java.util.function.Supplier;

public final class BeaconEffectsView implements GuiComponentServer<Unit, BeaconEffectsView.Data>
{
	public static final Type<Unit, BeaconEffectsView.Data> TYPE = new Type<>(EI.id("beacon_effects_view"), StreamCodec.unit(Unit.INSTANCE), BeaconEffectsView.Data.STREAM_CODEC);
	
	private final Supplier<Boolean>                            shapeValidSupplier;
	private final Supplier<Boolean>                            beamActiveSupplier;
	private final Supplier<List<BeaconEffectComponent.Effect>> effectSupplier;
	private final Supplier<Integer>                            totalTicksSupplier;
	private final Supplier<Integer>                            remainingTicksSupplier;
	private final Supplier<Long>                               euCostSupplier;
	
	public BeaconEffectsView(
			Supplier<Boolean> shapeValidSupplier,
			Supplier<Boolean> beamActiveSupplier,
			Supplier<List<BeaconEffectComponent.Effect>> effectSupplier,
			Supplier<Integer> totalTicksSupplier,
			Supplier<Integer> remainingTicksSupplier,
			Supplier<Long> euCostSupplier
	)
	{
		this.shapeValidSupplier = shapeValidSupplier;
		this.beamActiveSupplier = beamActiveSupplier;
		this.effectSupplier = effectSupplier;
		this.totalTicksSupplier = totalTicksSupplier;
		this.remainingTicksSupplier = remainingTicksSupplier;
		this.euCostSupplier = euCostSupplier;
	}
	
	@Override
	public Unit getParams()
	{
		return Unit.INSTANCE;
	}
	
	@Override
	public Data extractData()
	{
		return new Data(
				shapeValidSupplier.get(),
				beamActiveSupplier.get(),
				effectSupplier.get(),
				totalTicksSupplier.get(),
				remainingTicksSupplier.get(),
				euCostSupplier.get()
		);
	}
	
	@Override
	public Type<Unit, Data> getType()
	{
		return TYPE;
	}
	
	public record Data(
			boolean shapeValid,
			boolean beamActive,
			List<BeaconEffectComponent.Effect> effects,
			int totalTicks,
			int remainingTicks,
			long euCost
	)
	{
		public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.BOOL,
				Data::shapeValid,
				ByteBufCodecs.BOOL,
				Data::beamActive,
				BeaconEffectComponent.Effect.STREAM_CODEC.apply(ByteBufCodecs.list()),
				Data::effects,
				ByteBufCodecs.INT,
				Data::totalTicks,
				ByteBufCodecs.INT,
				Data::remainingTicks,
				ByteBufCodecs.VAR_LONG,
				Data::euCost,
				Data::new
		);
	}
}
