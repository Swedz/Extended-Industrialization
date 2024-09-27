package net.swedz.extended_industrialization.commands;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.api.WorldPos;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkPart;

import static net.minecraft.commands.Commands.*;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.*;

@EventBusSubscriber(modid = EI.ID, bus = EventBusSubscriber.Bus.GAME)
public final class EICommands
{
	@SubscribeEvent
	private static void registerCommands(RegisterCommandsEvent event)
	{
		event.getDispatcher().register(literal("ei")
				.requires((s) -> s.hasPermission(4))
				.then(literal("tesla_network")
						.then(argument("pos", blockPos())
								.then(literal("dump")
										.executes((context) -> teslaNetworkDump(context.getSource(), getLoadedBlockPos(context, "pos")))))));
	}
	
	private static int teslaNetworkDump(CommandSourceStack source, BlockPos pos)
	{
		Level level = source.getLevel();
		
		if(level.isLoaded(pos))
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			
			if(blockEntity instanceof TeslaNetworkPart networkPart)
			{
				if(networkPart.hasNetwork())
				{
					TeslaNetwork network = networkPart.getNetwork();
					
					source.sendSuccess(() -> EIText.COMMAND_TESLA_NETWORK_DUMP_RESULT_1.text(pos.toShortString()), true);
					
					source.sendSuccess(() -> EIText.COMMAND_TESLA_NETWORK_DUMP_RESULT_2.text("%s (%s)".formatted(network.key().pos().toShortString(), network.key().dimension().location().toString())), true);
					
					Component transmitterResult;
					if(network.hasTransmitter())
					{
						WorldPos transmitterPosition = network.getTransmitter().getPosition();
						boolean ticking = network.isTransmitterLoaded();
						transmitterResult = EIText.COMMAND_TESLA_NETWORK_DUMP_RESULT_YES_TRANSMITTER.text(
								"%s (%s)".formatted(transmitterPosition.pos().toShortString(), transmitterPosition.dimension().location().toString()),
								Boolean.toString(ticking),
								ticking ? network.getCableTier().shortEnglishName() : Component.literal("N/A")
						);
					}
					else
					{
						transmitterResult = EIText.COMMAND_TESLA_NETWORK_DUMP_RESULT_NO_TRANSMITTER.text();
					}
					source.sendSuccess(() -> EIText.COMMAND_TESLA_NETWORK_DUMP_RESULT_3.text(transmitterResult), true);
					
					source.sendSuccess(() -> EIText.COMMAND_TESLA_NETWORK_DUMP_RESULT_4.text(network.receiverCount(), network.loadedReceiverCount()), true);
				}
				else
				{
					source.sendFailure(EIText.COMMAND_TESLA_NETWORK_DUMP_NO_NETWORK.text(pos.toShortString()));
				}
			}
			else
			{
				source.sendFailure(EIText.COMMAND_TESLA_NETWORK_DUMP_CANT_HAVE_NETWORK.text(pos.toShortString()));
			}
		}
		else
		{
			source.sendFailure(EIText.COMMAND_TESLA_NETWORK_DUMP_CHUNK_NOT_LOADED.text(pos.toShortString()));
		}
		
		return Command.SINGLE_SUCCESS;
	}
}
