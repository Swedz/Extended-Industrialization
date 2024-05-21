package net.swedz.extended_industrialization.network.packets;

import aztech.modern_industrialization.machines.gui.MachineMenuServer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.swedz.extended_industrialization.machines.guicomponents.modularselection.ConfigurationPanel;
import net.swedz.extended_industrialization.network.EIBasePacket;

/**
 * This was stolen from {@link aztech.modern_industrialization.network.machines.ChangeShapePacket} to make my own generic "configuration panel" component to be used for non-shape related configuring of machines.
 */
public record ConfigureMachinePacket(int syncId, int line, boolean clickedLeftButton) implements EIBasePacket
{
	public ConfigureMachinePacket(FriendlyByteBuf buf)
	{
		this(buf.readUnsignedByte(), buf.readVarInt(), buf.readBoolean());
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeByte(syncId);
		buf.writeVarInt(line);
		buf.writeBoolean(clickedLeftButton);
	}
	
	@Override
	public void handle(Context ctx)
	{
		ctx.assertOnServer();
		
		AbstractContainerMenu menu = ctx.getPlayer().containerMenu;
		if(menu.containerId == syncId && menu instanceof MachineMenuServer machineMenu)
		{
			ConfigurationPanel.Server configurationPanel = machineMenu.blockEntity.getComponent(ConfigurationPanel.ID);
			configurationPanel.behavior.handleClick(line, clickedLeftButton ? -1 : +1);
		}
	}
}
