package net.swedz.extended_industrialization.datagen.server.provider.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.tesseract.neoforge.registry.holder.FluidHolder;

import java.util.Comparator;

public final class FluidTagDatagenProvider extends FluidTagsProvider
{
	public FluidTagDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider(), EI.ID, event.getExistingFileHelper());
	}
	
	@Override
	protected void addTags(HolderLookup.Provider provider)
	{
		for(FluidHolder<?, ?, ?, ?> fluid : EIFluids.values().stream().sorted(Comparator.comparing((fluid) -> fluid.identifier().id())).toList())
		{
			for(TagKey<Fluid> tag : fluid.tags())
			{
				this.tag(tag).add(fluid.get());
			}
		}
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
