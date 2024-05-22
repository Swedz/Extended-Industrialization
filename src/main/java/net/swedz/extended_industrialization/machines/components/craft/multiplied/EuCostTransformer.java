package net.swedz.extended_industrialization.machines.components.craft.multiplied;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.swedz.extended_industrialization.EI;

import java.util.function.Supplier;

public abstract class EuCostTransformer
{
	private final String id;
	
	public EuCostTransformer(String id)
	{
		this.id = id;
	}
	
	public String getTranslationKey()
	{
		return "eu_cost_transformer.%s.%s".formatted(EI.ID, id);
	}
	
	public MutableComponent text()
	{
		return Component.translatable(this.getTranslationKey());
	}
	
	public abstract long transform(MultipliedCrafterComponent crafter, long eu);
	
	public static final class PercentageEuCostTransformer extends EuCostTransformer
	{
		private final Supplier<Float> percentage;
		
		public PercentageEuCostTransformer(Supplier<Float> percentage)
		{
			super("percentage");
			this.percentage = percentage;
		}
		
		@Override
		public long transform(MultipliedCrafterComponent crafter, long eu)
		{
			return (long) (eu * crafter.getRecipeMultiplier() * percentage.get());
		}
		
		@Override
		public MutableComponent text()
		{
			return Component.translatable(this.getTranslationKey(), (int) (percentage.get() * 100));
		}
	}
}
