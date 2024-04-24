package net.swedz.extended_industrialization.datagen.api.recipe;

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.swedz.extended_industrialization.datagen.api.MachineRecipeBuilderWrapper;

public abstract class RecipeBuilder<T extends RecipeBuilder<T>>
{
	protected ItemStack result;
	
	public T setOutput(ItemLike result, int count)
	{
		this.result = new ItemStack(result, count);
		return (T) this;
	}
	
	public T setOutput(String result, int count)
	{
		return this.setOutput(BuiltInRegistries.ITEM.get(new ResourceLocation(result)), count);
	}
	
	public abstract MachineRecipeBuilderWrapper exportToMachine(MachineRecipeType machine, int eu, int duration, int division);
	
	public MachineRecipeBuilderWrapper exportToAssembler()
	{
		return this.exportToMachine(MIMachineRecipeTypes.ASSEMBLER, 8, 200, 1);
	}
	
	public abstract void validate();
	
	public abstract Recipe<?> convert();
	
	public void offerTo(RecipeOutput output, ResourceLocation location)
	{
		this.validate();
		output.accept(location, this.convert(), null);
	}
}
