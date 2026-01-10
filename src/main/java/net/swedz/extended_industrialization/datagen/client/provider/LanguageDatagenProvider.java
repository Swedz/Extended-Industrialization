package net.swedz.extended_industrialization.datagen.client.provider;

import aztech.modern_industrialization.MI;
import com.google.common.collect.Sets;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIEntities;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EIKeybinds;
import net.swedz.extended_industrialization.EITags;
import net.swedz.tesseract.neoforge.datagen.mi.MIDatagenHooks;
import net.swedz.tesseract.neoforge.lang.LangInstance;
import net.swedz.tesseract.neoforge.registry.holder.FluidHolder;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Set;

public final class LanguageDatagenProvider extends LanguageProvider
{
	private static final Set<LangInstance<?>> INSTANCES = Sets.newHashSet();
	
	public static void include(LangInstance<?> instance)
	{
		INSTANCES.add(instance);
	}
	
	public LanguageDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), EI.ID, "en_us");
	}
	
	@Override
	protected void addTranslations()
	{
		for(var instance : INSTANCES)
		{
			instance.datagen(this);
		}
		
		for(ItemHolder item : EIItems.values())
		{
			this.add(item.asItem(), item.identifier().englishName());
		}
		this.add("item.extended_industrialization.nyano_helmet", "Nyano Helmet");
		this.add("item.extended_industrialization.nyano_quantum_helmet", "Quantum Nyano Helmet");
		
		for(FluidHolder fluid : EIFluids.values())
		{
			this.add(fluid.block().get(), fluid.identifier().englishName());
		}
		
		for(var keybind : EIKeybinds.Registry.getMappings())
		{
			this.add(keybind.descriptionId(), keybind.englishName());
		}
		
		EITags.translations().forEach(this::add);
		
		MIDatagenHooks.Client.withLanguageHook(this, EI.ID);
		
		this.add("itemGroup.%s.%s".formatted(EI.ID, EI.ID), EI.NAME);
		this.add(EIKeybinds.CATEGORY, EI.NAME);
		
		this.add("lef_tier.%s.%s.%s".formatted(EI.ID, MI.ID, "cupronickel_coil"), "Cupronickel");
		this.add("lef_tier.%s.%s.%s".formatted(EI.ID, MI.ID, "kanthal_coil"), "Kanthal");
		
		this.add("teslatower_tier.%s.%s.%s".formatted(EI.ID, EI.ID, "copper_tesla_winding"), "Copper");
		this.add("teslatower_tier.%s.%s.%s".formatted(EI.ID, EI.ID, "electrum_tesla_winding"), "Electrum");
		this.add("teslatower_tier.%s.%s.%s".formatted(EI.ID, EI.ID, "aluminum_tesla_winding"), "Aluminum");
		this.add("teslatower_tier.%s.%s.%s".formatted(EI.ID, EI.ID, "annealed_copper_tesla_winding"), "Annealed Copper");
		this.add("teslatower_tier.%s.%s.%s".formatted(EI.ID, EI.ID, "superconductor_tesla_winding"), "Superconductor");
		
		this.add(EIEntities.NANO_SABER_SWEEP.get(), "Nano Saber Sweep");
		
		this.add(EI.id("tesla").toLanguageKey("death.attack"), "%1$s was electrocuted to death");
		this.add(EI.id("nano_saber_sweep").toLanguageKey("death.attack"), "%1$s was vaporized");
		this.add(EI.id("nano_saber_sweep.player").toLanguageKey("death.attack"), "%1$s was vaporized by %2$s");
		this.add(EI.id("nano_saber_sweep.item").toLanguageKey("death.attack"), "%1$s was vaporized by %2$s using %3$s");
		
		this.add("curios.identifier.shoulders", "Shoulders");
	}
}
