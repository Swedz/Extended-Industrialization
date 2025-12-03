package net.swedz.extended_industrialization.commands;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaNetworkPart;
import net.swedz.tesseract.neoforge.api.WorldPos;

import static net.minecraft.commands.Commands.*;
import static net.minecraft.commands.arguments.DimensionArgument.*;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.*;

@EventBusSubscriber(modid = EI.ID)
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
		source.sendSuccess(() -> EI.text().commandTeslaNetworkDumpResult1(pos), true);
		
		source.sendSuccess(() -> EI.text().commandTeslaNetworkDumpResult2(network.key()), true);
		
		Component transmitterResult;
		if(network.hasTransmitter())
		{
			var transmitterPosition = network.getTransmitter().getPosition();
			boolean ticking = network.isTransmitterLoaded();
			transmitterResult = EI.text().commandTeslaNetworkDumpResultYesTransmitter(transmitterPosition, ticking, ticking ? network.getCableTier().shortEnglishName() : Component.literal("N/A"));
		}
		else
		{
			transmitterResult = EI.text().commandTeslaNetworkDumpResultNoTransmitter();
		}
		source.sendSuccess(() -> EI.text().commandTeslaNetworkDumpResult3(transmitterResult), true);
		
		source.sendSuccess(() -> EI.text().commandTeslaNetworkDumpResult4(network.receiverCount(), network.loadedReceiverCount()), true);
	}
	
	private static int teslaNetworkDump(CommandSourceStack source, WorldPos pos)
	{
		var level = pos.level();
		var cache = level.getServer().getTeslaNetworks();
		
		if(cache.exists(pos))
		{
			TeslaNetwork network = cache.get(pos);
			teslaNetworkDump(source, network, pos.pos());
		}
		else
		{
			source.sendFailure(EI.text().commandTeslaNetworkDumpNoNetwork(pos));
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int teslaNetworkDumpAt(CommandSourceStack source, BlockPos pos)
	{
		var level = source.getLevel();
		var blockEntity = level.getBlockEntity(pos);
		
		if(blockEntity instanceof TeslaNetworkPart networkPart)
		{
			if(networkPart.hasNetwork())
			{
				var network = networkPart.getNetwork();
				teslaNetworkDump(source, network, pos);
			}
			else
			{
				source.sendFailure(EI.text().commandTeslaNetworkDumpNoNetwork(new WorldPos(source.getLevel(), pos)));
			}
		}
		else
		{
			source.sendFailure(EI.text().commandTeslaNetworkDumpCantHaveNetwork(pos));
		}
		
		return Command.SINGLE_SUCCESS;
	}
}
