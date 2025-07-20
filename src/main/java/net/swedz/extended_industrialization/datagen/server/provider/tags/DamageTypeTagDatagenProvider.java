package net.swedz.extended_industrialization.datagen.server.provider.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIDamageTypes;
import net.swedz.extended_industrialization.EITags;

public final class DamageTypeTagDatagenProvider extends DamageTypeTagsProvider
{
	public DamageTypeTagDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider(), EI.ID, event.getExistingFileHelper());
	}
	
	@Override
	protected void addTags(HolderLookup.Provider provider)
	{
		this.tag(EITags.DamageTypes.AUTO_SMELT)
				.add(EIDamageTypes.NANO_SWIPE_SMELT);
		
		this.tag(EITags.DamageTypes.BEHEADING)
				.add(EIDamageTypes.NANO_SWIPE_BEHEAD);
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
