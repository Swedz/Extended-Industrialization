package net.swedz.extended_industrialization.items.machineconfig;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.Simulation;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public record MachineConfig(
		Block machineBlock,
		MachineConfigSlots slots,
		MachineConfigOrientation orientation,
		MachineConfigPanel upgrades
) implements MachineConfigSerializable, MachineConfigApplicable<MachineBlockEntity>
{
	public static final Codec<MachineConfig>                CODEC        = Codec
			.withAlternative(CompoundTag.CODEC, TagParser.AS_CODEC)
			.xmap(MachineConfig::deserialize, MachineConfig::serialize);
	public static final StreamCodec<ByteBuf, MachineConfig> STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG
			.map(MachineConfig::deserialize, MachineConfig::serialize);
	
	public static MachineConfig from(MachineBlockEntity machine)
	{
		return new MachineConfig(
				machine.getBlockState().getBlock(),
				MachineConfigSlots.from(machine),
				MachineConfigOrientation.from(machine.orientation),
				MachineConfigPanel.from(machine)
		);
	}
	
	private static MachineConfig deserialize(CompoundTag tag)
	{
		return new MachineConfig(
				BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString("machine_block"))),
				MachineConfigSlots.deserialize(tag.getCompound("slots")),
				MachineConfigOrientation.deserialize(tag.getCompound("orientation")),
				MachineConfigPanel.deserialize(tag.getCompound("panel"))
		);
	}
	
	@Override
	public boolean matches(MachineBlockEntity target)
	{
		return target.getBlockState().getBlock() == machineBlock;
	}
	
	@Override
	public boolean apply(Player player, MachineBlockEntity target, Simulation simulation)
	{
		if(!this.matches(target))
		{
			return false;
		}
		
		if(slots.apply(player, target, simulation) &&
		   orientation.apply(player, target.orientation, simulation))
		{
			upgrades.apply(player, target, simulation);
			if(simulation.isActing())
			{
				target.invalidateCapabilities();
				target.getLevel().blockUpdated(target.getBlockPos(), Blocks.AIR);
				target.setChanged();
				if(!target.getLevel().isClientSide())
				{
					target.sync();
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putString("machine_block", BuiltInRegistries.BLOCK.getKey(machineBlock).toString());
		tag.put("slots", slots.serialize());
		tag.put("orientation", orientation.serialize());
		
		return tag;
	}
}
