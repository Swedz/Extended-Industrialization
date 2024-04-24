package net.swedz.extended_industrialization;

import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineBlockEntityRenderer;
import aztech.modern_industrialization.machines.blockentities.multiblocks.LargeTankMultiblockBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBER;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockTankBER;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.swedz.extended_industrialization.registry.blocks.EIBlocks;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = EI.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EIClient
{
	@SubscribeEvent
	private static void init(FMLConstructModEvent __)
	{
		// Do nothing for now, but this could be useful
	}
	
	/**
	 * Taken from {@link aztech.modern_industrialization.MIClient#registerBlockEntityRenderers(FMLClientSetupEvent)}. This is needed to make multiblocks render their layout when holding a wrench.
	 */
	@SubscribeEvent
	private static void registerBlockEntityRenderers(FMLClientSetupEvent event)
	{
		for(DeferredHolder<Block, ? extends Block> blockDef : EIBlocks.Registry.BLOCKS.getEntries())
		{
			if(blockDef.get() instanceof MachineBlock machine)
			{
				MachineBlockEntity blockEntity = machine.getBlockEntityInstance();
				BlockEntityType type = blockEntity.getType();
				
				if(blockEntity instanceof LargeTankMultiblockBlockEntity)
				{
					BlockEntityRenderers.register(type, MultiblockTankBER::new);
				}
				else if(blockEntity instanceof MultiblockMachineBlockEntity)
				{
					BlockEntityRenderers.register(type, MultiblockMachineBER::new);
				}
				else
				{
					BlockEntityRenderers.register(type, (c) -> new MachineBlockEntityRenderer(c));
				}
			}
		}
	}
}
