package net.swedz.miextended.datagen.client.provider.models;

import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.registry.blocks.BlockHolder;
import net.swedz.miextended.registry.blocks.MIEBlocks;

public final class BlockModelsDatagenProvider extends BlockStateProvider
{
	public BlockModelsDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), MIExtended.ID, event.getExistingFileHelper());
	}
	
	@Override
	protected void registerStatesAndModels()
	{
		for(BlockHolder<?> block : MIEBlocks.values())
		{
			block.modelBuilder().accept(this);
		}
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
