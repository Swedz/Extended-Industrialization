package net.swedz.intothetwilight.datagen.lang;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.definition.BlockDefinition;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.swedz.intothetwilight.datagen.ITTModernIndustrializationDatagenTracker;

public final class ITTModernIndustrializationLanguageProvider extends LanguageProvider
{
	public ITTModernIndustrializationLanguageProvider(PackOutput output)
	{
		super(output, MI.ID, "en_us");
	}
	
	@Override
	protected void addTranslations()
	{
		for(String id : ITTModernIndustrializationDatagenTracker.trackedBlocks())
		{
			BlockDefinition<?> block = MIBlock.BLOCK_DEFINITIONS.get(MI.id(id));
			String englishName = block.getEnglishName();
			this.addBlock(block, englishName);
		}
	}
}
