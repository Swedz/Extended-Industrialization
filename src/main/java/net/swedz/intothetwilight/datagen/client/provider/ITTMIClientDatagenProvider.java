package net.swedz.intothetwilight.datagen.client.provider;

import aztech.modern_industrialization.MI;
import net.minecraft.data.DataGenerator;
import net.swedz.intothetwilight.datagen.api.DatagenProvider;
import net.swedz.intothetwilight.datagen.api.object.DatagenLanguageWrapper;
import net.swedz.intothetwilight.datagen.api.object.DatagenModelWrapper;
import net.swedz.intothetwilight.mi.machines.MIMachineHookTracker;

import java.util.Map;
import java.util.function.Consumer;

public final class ITTMIClientDatagenProvider extends DatagenProvider
{
	public ITTMIClientDatagenProvider(DataGenerator generator)
	{
		super(generator, "Into the Twilight Datagen/Client/MI", MI.ID);
	}
	
	private void language()
	{
		log.info("Start of LANGUAGE");
		final DatagenLanguageWrapper lang = new DatagenLanguageWrapper(this);
		for(Consumer<DatagenLanguageWrapper> action : MIMachineHookTracker.LANGUAGE)
		{
			action.accept(lang);
		}
		lang.write();
		log.info("End of LANGUAGE");
	}
	
	private void models(Map<String, Consumer<DatagenModelWrapper>> map, String path)
	{
		for(String id : map.keySet())
		{
			final DatagenModelWrapper modelWrapper = new DatagenModelWrapper(this, path, id);
			map.get(id).accept(modelWrapper);
			modelWrapper.write();
		}
	}
	
	private void blockStates()
	{
		log.info("Start of BLOCK_STATES");
		this.models(MIMachineHookTracker.BLOCK_STATES, "blockstates");
		log.info("End of BLOCK_STATES");
	}
	
	private void blockModels()
	{
		log.info("Start of BLOCK_MODELS");
		this.models(MIMachineHookTracker.BLOCK_MODELS, "models/block");
		log.info("End of BLOCK_MODELS");
	}
	
	private void itemModels()
	{
		log.info("Start of ITEM_MODELS");
		this.models(MIMachineHookTracker.ITEM_MODELS, "models/item");
		log.info("End of ITEM_MODELS");
	}
	
	@Override
	public void run()
	{
		this.language();
		this.blockStates();
		this.blockModels();
		this.itemModels();
	}
}
