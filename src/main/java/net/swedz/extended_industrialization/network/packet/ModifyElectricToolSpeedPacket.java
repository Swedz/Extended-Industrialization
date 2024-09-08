package net.swedz.extended_industrialization.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.EITooltips;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.extended_industrialization.network.EICustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.*;

public record ModifyElectricToolSpeedPacket(boolean increase) implements EICustomPacket
{
	public static final StreamCodec<ByteBuf, ModifyElectricToolSpeedPacket> STREAM_CODEC = ByteBufCodecs.BOOL
			.map(ModifyElectricToolSpeedPacket::new, ModifyElectricToolSpeedPacket::increase);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		Player player = context.getPlayer();
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		
		if(stack.getItem() instanceof ElectricToolItem item)
		{
			int originalSpeed = ElectricToolItem.getToolSpeed(stack);
			
			int speed = originalSpeed;
			speed += (increase ? 1 : -1);
			speed = Mth.clamp(speed, ElectricToolItem.SPEED_MIN, ElectricToolItem.SPEED_MAX);
			
			if(speed != originalSpeed)
			{
				ElectricToolItem.setToolSpeed(stack, speed);
				
				player.displayClientMessage(
						line(EIText.MINING_SPEED, Style.EMPTY)
								.arg((float) speed / ElectricToolItem.SPEED_MAX, EITooltips.SPACED_PERCENTAGE_PARSER.withStyle(Style.EMPTY)),
						true
				);
			}
		}
	}
}
