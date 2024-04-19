package net.swedz.miextended.datagen.server.provider.recipes;

import com.google.common.collect.Sets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.fluids.MIEFluids;
import net.swedz.miextended.mi.hook.MIMachineHook;

import java.util.Set;

public final class CanningMachineRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public CanningMachineRecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event, "MI Extended Datagen/Server/Recipes/Canning Machine", MIExtended.ID);
	}
	
	private void addFillingAndEmptyingRecipes(FluidStack fluidStack, Item emptyItem, Item fullItem)
	{
		ResourceLocation id = BuiltInRegistries.FLUID.getKey(fluidStack.getFluid());
		this.addMachineRecipe("canning_machine/filling/%s".formatted(id.getNamespace()), id.getPath(), MIMachineHook.CANNING_MACHINE, 2, 5 * 20, (r) -> r
				.addFluidInput(fluidStack.getFluid(), fluidStack.getAmount())
				.addItemInput(emptyItem, 1)
				.addItemOutput(fullItem, 1));
		this.addMachineRecipe("canning_machine/emptying/%s".formatted(id.getNamespace()), id.getPath(), MIMachineHook.CANNING_MACHINE, 2, 5 * 20, (r) -> r
				.addItemInput(fullItem, 1)
				.addItemOutput(emptyItem, 1)
				.addFluidOutput(fluidStack.getFluid(), fluidStack.getAmount()));
	}
	
	private void bucketRecipes()
	{
		Set<Fluid> uniqueFluids = Sets.newHashSet();
		for(Fluid fluid : BuiltInRegistries.FLUID)
		{
			Fluid processedFluid = fluid instanceof FlowingFluid flowingFluid ? flowingFluid.getSource() : fluid;
			if(uniqueFluids.add(processedFluid))
			{
				Item fullItem = processedFluid.getBucket();
				if(fullItem != Items.AIR)
				{
					this.addFillingAndEmptyingRecipes(new FluidStack(processedFluid, 1000), Items.BUCKET, fullItem);
				}
			}
		}
	}
	
	@Override
	public void run()
	{
		this.bucketRecipes();
		
		this.addFillingAndEmptyingRecipes(new FluidStack(MIEFluids.HONEY.asFluid(), 250), Items.GLASS_BOTTLE, Items.HONEY_BOTTLE);
	}
}
