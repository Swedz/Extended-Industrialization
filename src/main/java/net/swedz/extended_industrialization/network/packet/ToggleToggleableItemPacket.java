package net.swedz.extended_industrialization.network.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.item.ToggleableItem;
import net.swedz.extended_industrialization.network.EIBasePacket;

public record ToggleToggleableItemPacket(EquipmentSlot slot, boolean activated) implements EIBasePacket
{
	public static final StreamCodec<ByteBuf, ToggleToggleableItemPacket> STREAM_CODEC = ByteBufCodecs.fromCodec(RecordCodecBuilder.create((instance) -> instance
			.group(
					EquipmentSlot.CODEC.fieldOf("slot").forGetter(ToggleToggleableItemPacket::slot),
					Codec.BOOL.fieldOf("activated").forGetter(ToggleToggleableItemPacket::activated)
			)
			.apply(instance, ToggleToggleableItemPacket::new)
	));
	
	@Override
	public void handle(Context context)
	{
		if(context.isOnClient())
		{
			EI.LOGGER.warn("Received ToggleToggleableItemPacket on the client??");
			return;
		}
		
		Player player = context.getPlayer();
		
		ItemStack stack = player.getItemBySlot(slot);
		if(stack.getItem() instanceof ToggleableItem item)
		{
			item.setActivated(player, stack, !item.isActivated(stack));
		}
	}
}
