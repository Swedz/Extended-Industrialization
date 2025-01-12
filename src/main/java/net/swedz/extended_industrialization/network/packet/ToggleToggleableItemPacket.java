package net.swedz.extended_industrialization.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.item.ToggleableItem;
import net.swedz.extended_industrialization.network.EICustomPacket;
import net.swedz.tesseract.neoforge.helper.CodecHelper;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record ToggleToggleableItemPacket(EquipmentSlot slot, boolean activated) implements EICustomPacket
{
	public static final StreamCodec<ByteBuf, ToggleToggleableItemPacket> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forEnumStream(EquipmentSlot.class),
			ToggleToggleableItemPacket::slot,
			ByteBufCodecs.BOOL,
			ToggleToggleableItemPacket::activated,
			ToggleToggleableItemPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertServerbound();
		
		Player player = context.getPlayer();
		
		ItemStack stack = player.getItemBySlot(slot);
		if(stack.getItem() instanceof ToggleableItem item)
		{
			item.setActivated(player, stack, !item.isActivated(stack));
		}
	}
}
