package net.swedz.extended_industrialization.compat.viewer.usage;

import aztech.modern_industrialization.compat.viewer.abstraction.ViewerCategory;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.material.Fluid;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.datamaps.FertilizerPotency;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;
import net.swedz.extended_industrialization.registry.items.EIItems;
import net.swedz.extended_industrialization.text.EIText;

import java.util.function.Consumer;

public final class FluidFertilizerCategory extends ViewerCategory<Fluid>
{
	public FluidFertilizerCategory()
	{
		super(Fluid.class, EI.id("fluid_fertilizers"), EIText.FLUID_FERTILIZERS.text(), EIFluids.NPK_FERTILIZER.asFluid().getBucket().getDefaultInstance(), 150, 35);
	}
	
	@Override
	public void buildWorkstations(WorkstationConsumer consumer)
	{
		consumer.accept(EIItems.valueOf("electric_farmer"));
	}
	
	@Override
	public void buildRecipes(RecipeManager recipeManager, RegistryAccess registryAccess, Consumer<Fluid> consumer)
	{
		for(Fluid fluid : registryAccess.registryOrThrow(Registries.FLUID))
		{
			if(FertilizerPotency.getFor(fluid) != null)
			{
				consumer.accept(fluid);
			}
		}
	}
	
	@Override
	public void buildLayout(Fluid recipe, LayoutBuilder builder)
	{
		builder.inputSlot(15, 10).variant(FluidVariant.of(recipe));
	}
	
	@Override
	public void buildWidgets(Fluid recipe, WidgetList widgets)
	{
		FertilizerPotency fertilizerPotency = FertilizerPotency.getFor(recipe);
		Component text = EIText.FLUID_FERTILIZERS_RATE.text(fertilizerPotency.mbToConsumePerFertilizerTick(), fertilizerPotency.tickRate() / 20f);
		widgets.secondaryText(text, 40, 14);
	}
}
