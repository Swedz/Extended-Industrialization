package net.swedz.extended_industrialization.machines.guicomponents.exposecabletier;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.machines.gui.MachineScreen;
import net.minecraft.network.FriendlyByteBuf;

public class ExposeCableTierGuiClient implements GuiComponentClient
{
	private CableTier cableTier;
	
	public ExposeCableTierGuiClient(FriendlyByteBuf buf)
	{
		this.readCurrentData(buf);
	}
	
	public CableTier getCableTier()
	{
		return cableTier;
	}
	
	@Override
	public void readCurrentData(FriendlyByteBuf buf)
	{
		cableTier = CableTier.getTier(new String(buf.readByteArray()));
	}
	
	@Override
	public ClientComponentRenderer createRenderer(MachineScreen machineScreen)
	{
		return (guiGraphics, leftPos, topPos) ->
		{
		};
	}
}
