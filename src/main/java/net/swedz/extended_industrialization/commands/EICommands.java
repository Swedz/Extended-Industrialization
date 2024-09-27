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
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkCache;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkPart;

import static net.minecraft.commands.Commands.*;
import static net.minecraft.commands.arguments.DimensionArgument.*;
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
						.then(literal("dump")
								.then(argument("pos", blockPos())
										.then(argument("dimension", dimension())
												.executes((context) -> teslaNetworkDump(context.getSource(), new WorldPos(getDimension(context, "dimension"), getBlockPos(context, "pos")))))))
						.then(literal("dump_at")
								.then(argument("pos", blockPos())
										.executes((context) -> teslaNetworkDumpAt(context.getSource(), getLoadedBlockPos(context, "pos")))))));
	}
	
	private static void teslaNetworkDump(CommandSourceStack source, TeslaNetwork network, BlockPos pos)
	{
		source.sendSuccess(() -> EIText.COMMAND_TESLA_NETWORK_DUMP_RESULT_1.text(pos.toShortString()), true);
		
		// TODO use parser
		source.sendSuccess(() -> EIText.COMMAND_TESLA_NETWORK_DUMP_RESULT_2.text("%s (%s)".formatted(network.key().pos().toShortString(), network.key().dimension().location().toString())), true);
		
		Component transmitterResult;
		if(network.hasTransmitter())
		{
			WorldPos transmitterPosition = network.getTransmitter().getPosition();
			boolean ticking = network.isTransmitterLoaded();
			transmitterResult = EIText.COMMAND_TESLA_NETWORK_DUMP_RESULT_YES_TRANSMITTER.text(
					// TODO use parser
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
	
	private static int teslaNetworkDump(CommandSourceStack source, WorldPos pos)
	{
		Level level = pos.level();
		TeslaNetworkCache cache = level.getServer().getTeslaNetworks();
		
		if(cache.exists(pos))
		{
			TeslaNetwork network = cache.get(pos);
			teslaNetworkDump(source, network, pos.pos());
		}
		else
		{
			// TODO use parser
			source.sendFailure(EIText.COMMAND_TESLA_NETWORK_DUMP_NO_NETWORK.text("%s (%s)".formatted(pos.pos().toShortString(), pos.dimension().location().toString())));
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int teslaNetworkDumpAt(CommandSourceStack source, BlockPos pos)
	{
		Level level = source.getLevel();
		BlockEntity blockEntity = level.getBlockEntity(pos);
		
		if(blockEntity instanceof TeslaNetworkPart networkPart)
		{
			if(networkPart.hasNetwork())
			{
				TeslaNetwork network = networkPart.getNetwork();
				teslaNetworkDump(source, network, pos);
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
		
		return Command.SINGLE_SUCCESS;
	}
}
