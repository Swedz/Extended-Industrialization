package net.swedz.extended_industrialization.machines.guicomponent.teslanetwork;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.gui.GuiComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.api.WorldPos;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiverState;

import java.util.Optional;
import java.util.function.Supplier;

public final class TeslaNetworkBar
{
	public static final ResourceLocation ID = EI.id("tesla_network_bar");
	
	public static final class Server implements GuiComponent.Server<Optional<Data>>
	{
		private final Parameters params;
		
		private final Supplier<Optional<Data>> data;
		
		public Server(Parameters params, Supplier<Optional<Data>> data)
		{
			this.params = params;
			this.data = data;
		}
		
		@Override
		public Optional<Data> copyData()
		{
			return data.get();
		}
		
		@Override
		public boolean needsSync(Optional<Data> cachedData)
		{
			return !cachedData.equals(this.copyData());
		}
		
		@Override
		public void writeInitialData(RegistryFriendlyByteBuf buf)
		{
			buf.writeVarInt(params.renderX);
			buf.writeVarInt(params.renderY);
			this.writeCurrentData(buf);
		}
		
		@Override
		public void writeCurrentData(RegistryFriendlyByteBuf buf)
		{
			Optional<Data> data = this.data.get();
			buf.writeBoolean(data.isPresent());
			data.ifPresent((d) -> d.write(buf));
		}
		
		@Override
		public ResourceLocation getId()
		{
			return ID;
		}
	}
	
	public record Parameters(int renderX, int renderY)
	{
	}
	
	public interface Data
	{
		void write(RegistryFriendlyByteBuf buf);
		
		int iconIndex();
	}
	
	public record TransmitterData(
			int receivers, long energyTransmitting, CableTier cableTier, long energyDrain, long energyConsuming
	) implements Data
	{
		@Override
		public void write(RegistryFriendlyByteBuf buf)
		{
			buf.writeBoolean(true);
			
			buf.writeVarInt(receivers);
			buf.writeVarLong(energyTransmitting);
			buf.writeUtf(cableTier.name);
			buf.writeVarLong(energyDrain);
			buf.writeVarLong(energyConsuming);
		}
		
		@Override
		public int iconIndex()
		{
			return 1;
		}
	}
	
	public record ReceiverData(
			TeslaReceiverState state, Optional<WorldPos> linked, Optional<CableTier> networkCableTier
	) implements Data
	{
		@Override
		public void write(RegistryFriendlyByteBuf buf)
		{
			buf.writeBoolean(false);
			
			buf.writeEnum(state);
			buf.writeOptional(linked, WorldPos.STREAM_CODEC);
			if(linked.isPresent())
			{
				buf.writeBoolean(networkCableTier.isPresent());
				networkCableTier.ifPresent((cableTier) -> buf.writeUtf(cableTier.name));
			}
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
}
