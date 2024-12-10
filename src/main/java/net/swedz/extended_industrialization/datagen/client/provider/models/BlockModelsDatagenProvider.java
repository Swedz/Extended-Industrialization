package net.swedz.extended_industrialization.datagen.client.provider.models;

import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIBlocks;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;

public final class BlockModelsDatagenProvider extends BlockStateProvider
{
	public BlockModelsDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), EI.ID, event.getExistingFileHelper());
	}
	
	@Override
	protected void registerStatesAndModels()
	{
		for(BlockHolder<?> block : EIBlocks.values())
		{
			if(block.hasModelProvider())
			{
				block.modelProvider().accept(this);
			}
		}
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
