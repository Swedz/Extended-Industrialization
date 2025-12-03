package net.swedz.extended_industrialization.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.extended_industrialization.network.EICustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record ModifyElectricToolSpeedPacket(boolean increase) implements EICustomPacket
{
	public static final StreamCodec<ByteBuf, ModifyElectricToolSpeedPacket> STREAM_CODEC = ByteBufCodecs.BOOL
			.map(ModifyElectricToolSpeedPacket::new, ModifyElectricToolSpeedPacket::increase);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		var player = context.getPlayer();
		var stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		
		if(stack.getItem() instanceof ElectricToolItem item && item.getToolType().hasAdjustableSpeed())
		{
			int originalSpeed = ElectricToolItem.getToolSpeed(stack);
			
			int speed = originalSpeed;
			speed += (increase ? 1 : -1);
			speed = Mth.clamp(speed, ElectricToolItem.SPEED_MIN, ElectricToolItem.SPEED_MAX);
			
			if(speed != originalSpeed)
			{
				ElectricToolItem.setToolSpeed(stack, speed);
				
				player.displayClientMessage(EI.text().toolChangedMiningSpeed((float) speed / ElectricToolItem.SPEED_MAX), true);
			}
		}
	}
}
