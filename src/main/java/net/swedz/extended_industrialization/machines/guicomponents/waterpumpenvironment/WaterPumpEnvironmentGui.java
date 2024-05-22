package net.swedz.extended_industrialization.machines.guicomponents.waterpumpenvironment;

import aztech.modern_industrialization.machines.gui.GuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;

import java.util.function.Supplier;

public final class WaterPumpEnvironmentGui
{
	public static ResourceLocation ID = EI.id("water_pump_environment");
	
	public static final class Server implements GuiComponent.Server<Boolean>
	{
		public final WaterPumpEnvironmentGui.Parameters params;
		public final Supplier<Boolean>                  validEnvironmentSupplier;
		
		public Server(WaterPumpEnvironmentGui.Parameters params, Supplier<Boolean> validEnvironmentSupplier)
		{
			this.params = params;
			this.validEnvironmentSupplier = validEnvironmentSupplier;
		}
		
		@Override
		public Boolean copyData()
		{
			return validEnvironmentSupplier.get();
		}
		
		@Override
		public boolean needsSync(Boolean cachedData)
		{
			return !cachedData.equals(validEnvironmentSupplier.get());
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
			buf.writeBoolean(validEnvironmentSupplier.get());
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
}
