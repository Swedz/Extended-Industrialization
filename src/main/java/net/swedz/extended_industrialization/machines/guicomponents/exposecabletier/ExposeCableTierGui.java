package net.swedz.extended_industrialization.machines.guicomponents.exposecabletier;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.gui.GuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.api.CableTierHolder;

public class ExposeCableTierGui
{
	public static final ResourceLocation ID = EI.id("expose_cable_tier");
	
	public static final class Server implements GuiComponent.Server<CableTier>
	{
		private final CableTierHolder cableTierHolder;
		
		public Server(CableTierHolder cableTierHolder)
		{
			this.cableTierHolder = cableTierHolder;
		}
		
		@Override
		public CableTier copyData()
		{
			return cableTierHolder.getCableTier();
		}
		
		@Override
		public boolean needsSync(CableTier cachedData)
		{
			return cachedData != cableTierHolder.getCableTier();
		}
		
		@Override
		public void writeInitialData(FriendlyByteBuf buf)
		{
			this.writeCurrentData(buf);
		}
		
		@Override
		public void writeCurrentData(FriendlyByteBuf buf)
		{
			buf.writeByteArray(cableTierHolder.getCableTier().name.getBytes());
		}
		
		@Override
		public ResourceLocation getId()
		{
			return ID;
		}
	}
}
