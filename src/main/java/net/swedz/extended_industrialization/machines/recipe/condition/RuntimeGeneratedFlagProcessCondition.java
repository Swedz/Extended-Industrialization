package net.swedz.extended_industrialization.machines.recipe.condition;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

/**
 * This is a process condition that does not affect the recipe's ability to process, but simply acts as a way to
 * identify recipes that were generated at runtime. This is used for the Brewery's recipe category in recipe viewers to
 * not show normal brewing recipes, since those are already shown in the brewing stand category.
 */
public record RuntimeGeneratedFlagProcessCondition() implements MachineProcessCondition
{
	public static boolean is(MachineRecipe recipe)
	{
		for(var condition : recipe.conditions)
		{
			if(condition instanceof RuntimeGeneratedFlagProcessCondition)
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNot(MachineRecipe recipe)
	{
		return !is(recipe);
	}
	
	public static RuntimeGeneratedFlagProcessCondition INSTANCE = new RuntimeGeneratedFlagProcessCondition();
	
	public static final MapCodec<RuntimeGeneratedFlagProcessCondition> CODEC = MapCodec.unit(INSTANCE);
	
	public static final StreamCodec<RegistryFriendlyByteBuf, RuntimeGeneratedFlagProcessCondition> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	
	@Override
	public boolean canProcessRecipe(Context context, MachineRecipe recipe)
	{
		return true;
	}
	
	@Override
	public void appendDescription(List<Component> list)
	{
	}
	
	@Override
	public MapCodec<? extends MachineProcessCondition> codec()
	{
		return CODEC;
	}
	
	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, ? extends MachineProcessCondition> streamCodec()
	{
		return STREAM_CODEC;
	}
}
