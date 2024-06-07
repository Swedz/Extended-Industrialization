package net.swedz.extended_industrialization.machines.recipe.condition;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.api.CableTierHolder;

import java.util.List;

public record VoltageProcessCondition(CableTier tier) implements MachineProcessCondition
{
	public static final Codec<VoltageProcessCondition> CODEC = RecordCodecBuilder.create(
			(g) -> g.group(
					StringRepresentable.fromValues(() -> CableTier.allTiers().stream().map(WrappedCableTier::new).toList().toArray(new WrappedCableTier[0]))
							.fieldOf("voltage")
							.forGetter((c) -> new WrappedCableTier(c.tier()))
			).apply(g, (wrappedTier) -> new VoltageProcessCondition(wrappedTier.tier()))
	);
	
	@Override
	public boolean canProcessRecipe(Context context, MachineRecipe recipe)
	{
		if(context.getBlockEntity() instanceof CableTierHolder machine)
		{
			return machine.getCableTier().eu >= tier.eu;
		}
		return false;
	}
	
	@Override
	public void appendDescription(List<Component> list)
	{
		list.add(EIText.RECIPE_REQUIRES_VOLTAGE.text(Component.translatable(tier.shortEnglishKey())));
	}
	
	@Override
	public Codec<? extends MachineProcessCondition> codec(boolean syncToClient)
	{
		return CODEC;
	}
	
	private record WrappedCableTier(CableTier tier) implements StringRepresentable
	{
		@Override
		public String getSerializedName()
		{
			return tier.name;
		}
	}
}