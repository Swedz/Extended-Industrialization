package net.swedz.extended_industrialization.machines.guicomponent.teslanetwork;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.gui.GuiComponentServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.tesla.network.receiver.TeslaReceiverState;
import net.swedz.tesseract.neoforge.api.WorldPos;
import net.swedz.tesseract.neoforge.compat.mi.serialization.MIStreamCodecs;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class TeslaNetworkBar implements GuiComponentServer<TeslaNetworkBar.Params, Optional<TeslaNetworkBar.Data>>
{
	private static final Map<String, StreamCodec<ByteBuf, ? extends Data<?>>> DATA_TYPES = Map.of(
			TransmitterData.ID, TransmitterData.STREAM_CODEC,
			ReceiverData.ID, ReceiverData.STREAM_CODEC,
			SingingData.ID, SingingData.STREAM_CODEC
	);
	
	public static final Type<Params, Optional<Data>> TYPE = new Type<>(EI.id("tesla_network_bar"), Params.STREAM_CODEC, ByteBufCodecs.optional(Data.STREAM_CODEC));
	
	private final Params params;
	
	private final Supplier<Optional<Data>> data;
	
	public TeslaNetworkBar(Params params, Supplier<Optional<Data>> data)
	{
		this.params = params;
		this.data = data;
	}
	
	@Override
	public Params getParams()
	{
		return params;
	}
	
	@Override
	public Optional<Data> extractData()
	{
		return data.get();
	}
	
	@Override
	public Type<Params, Optional<Data>> getType()
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
	
	public interface Data<T extends Data<T>>
	{
		StreamCodec<ByteBuf, Data> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.dispatch(Data::id, DATA_TYPES::get);
		
		String id();
		
		int iconIndex();
	}
	
	public record TransmitterData(
			int receivers, long energyTransmitting, CableTier cableTier, long energyDrain, long energyConsuming
	) implements Data<TransmitterData>
	{
		private static final String ID = "transmitter";
		
		public static final StreamCodec<ByteBuf, TransmitterData> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, TransmitterData::receivers,
				ByteBufCodecs.VAR_LONG, TransmitterData::energyTransmitting,
				MIStreamCodecs.CABLE_TIER, TransmitterData::cableTier,
				ByteBufCodecs.VAR_LONG, TransmitterData::energyDrain,
				ByteBufCodecs.VAR_LONG, TransmitterData::energyConsuming,
				TransmitterData::new
		);
		
		@Override
		public String id()
		{
			return ID;
		}
		
		@Override
		public int iconIndex()
		{
			return 1;
		}
	}
	
	public record ReceiverData(
			TeslaReceiverState state, Optional<WorldPos> linked, Optional<CableTier> networkCableTier
	) implements Data<ReceiverData>
	{
		private static final String ID = "receiver";
		
		public static final StreamCodec<ByteBuf, ReceiverData> STREAM_CODEC = StreamCodec.composite(
				CodecHelper.forEnumStream(TeslaReceiverState.class), ReceiverData::state,
				ByteBufCodecs.optional(WorldPos.STREAM_CODEC), ReceiverData::linked,
				ByteBufCodecs.optional(MIStreamCodecs.CABLE_TIER), ReceiverData::networkCableTier,
				ReceiverData::new
		);
		
		@Override
		public String id()
		{
			return ID;
		}
		
		@Override
		public int iconIndex()
		{
			if(state.isFailure())
			{
				return switch (state)
				{
					case NO_LINK -> 0;
					case UNLOADED_TRANSMITTER -> 3;
					case MISMATCHING_VOLTAGE -> 4;
					case TOO_FAR -> 5;
					case UNDEFINED -> 2;
					default -> throw new IllegalStateException("Unexpected value: " + state);
				};
			}
			return 1;
		}
	}
	
	public record SingingData(
			int note, long energyConsuming
	) implements Data<SingingData>
	{
		private static final String ID = "singing";
		
		public static final StreamCodec<ByteBuf, SingingData> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, SingingData::note,
				ByteBufCodecs.VAR_LONG, SingingData::energyConsuming,
				SingingData::new
		);
		
		private static final char ZERO = '\u2080';
		private static final char ONE  = '\u2081';
		private static final char TWO  = '\u2082';
		
		private static final String[] READABLE_NOTES = {
				"F#" + ZERO, "G" + ZERO, "G#" + ZERO, "A" + ZERO, "A#" + ZERO, "B" + ZERO, "C" + ZERO, "C#" + ZERO, "D" + ZERO, "D#" + ZERO, "E" + ZERO, "F" + ZERO,
				"F#" + ONE, "G" + ONE, "G#" + ONE, "A" + ONE, "A#" + ONE, "B" + ONE, "C" + ONE, "C#" + ONE, "D" + ONE, "D#" + ONE, "E" + ONE, "F" + ONE,
				"F#" + TWO
		};
		
		public String getReadableNote()
		{
			return READABLE_NOTES[note];
		}
		
		@Override
		public String id()
		{
			return ID;
		}
		
		@Override
		public int iconIndex()
		{
			return 6;
		}
	}
}
