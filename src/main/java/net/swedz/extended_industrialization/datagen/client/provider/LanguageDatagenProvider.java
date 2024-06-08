package net.swedz.extended_industrialization.datagen.client.provider;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EIMachines;
import net.swedz.extended_industrialization.EIText;
import net.swedz.tesseract.neoforge.registry.holder.FluidHolder;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Map;

public final class LanguageDatagenProvider extends LanguageProvider
{
	public LanguageDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), EI.ID, "en_us");
	}
	
	@Override
	protected void addTranslations()
	{
		for(EIText text : EIText.values())
		{
			this.add(text.getTranslationKey(), text.englishText());
		}
		
		for(ItemHolder item : EIItems.values())
		{
			this.add(item.asItem(), item.identifier().englishName());
		}
		
		for(FluidHolder fluid : EIFluids.values())
		{
			this.add(fluid.block().get(), fluid.identifier().englishName());
		}
		
		for(Map.Entry<MachineRecipeType, String> entry : EIMachines.RecipeTypes.getNames().entrySet())
		{
			this.add("recipe_type.%s.%s".formatted(entry.getKey().getId().getNamespace(), entry.getKey().getPath()), entry.getValue());
		}
		
		this.add("itemGroup.%s.%s".formatted(EI.ID, EI.ID), EI.NAME);
		
		this.add("lef_tier.%s.%s.%s".formatted(EI.ID, MI.ID, "cupronickel_coil"), "Cupronickel");
		this.add("lef_tier.%s.%s.%s".formatted(EI.ID, MI.ID, "kanthal_coil"), "Kanthal");
	}
}
