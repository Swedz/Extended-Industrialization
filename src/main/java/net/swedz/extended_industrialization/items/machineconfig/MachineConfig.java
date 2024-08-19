package net.swedz.extended_industrialization.items.machineconfig;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.Simulation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public record MachineConfig(
		Block machineBlock,
		MachineConfigSlots slots,
		MachineConfigOrientation orientation,
		MachineConfigPanel panel
) implements MachineConfigApplicable<MachineBlockEntity>
{
	public static final Codec<MachineConfig> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					BuiltInRegistries.BLOCK.byNameCodec().fieldOf("machine_block").forGetter(MachineConfig::machineBlock),
					MachineConfigSlots.CODEC.fieldOf("slots").forGetter(MachineConfig::slots),
					MachineConfigOrientation.CODEC.fieldOf("orientation").forGetter(MachineConfig::orientation),
					MachineConfigPanel.CODEC.fieldOf("panel").forGetter(MachineConfig::panel)
			)
			.apply(instance, MachineConfig::new));
	
	public static final StreamCodec<RegistryFriendlyByteBuf, MachineConfig> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
	
	public static MachineConfig from(MachineBlockEntity machine)
	{
		return new MachineConfig(
				machine.getBlockState().getBlock(),
				MachineConfigSlots.from(machine),
				MachineConfigOrientation.from(machine.orientation),
				MachineConfigPanel.from(machine)
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
			panel.apply(player, target, simulation);
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
}
