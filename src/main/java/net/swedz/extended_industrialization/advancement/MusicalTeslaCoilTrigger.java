package net.swedz.extended_industrialization.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.swedz.extended_industrialization.EICriterionTriggers;

import java.util.Optional;

public final class MusicalTeslaCoilTrigger extends SimpleCriterionTrigger<MusicalTeslaCoilTrigger.TriggerInstance>
{
	public static Criterion<TriggerInstance> sing()
	{
		return EICriterionTriggers.MUSICAL_TESLA_COIL.get().createCriterion(new TriggerInstance(Optional.empty()));
	}
	
	@Override
	public Codec<TriggerInstance> codec()
	{
		return TriggerInstance.CODEC;
	}
	
	public void trigger(ServerPlayer player)
	{
		this.trigger(player, (instance) -> true);
	}
	
	public record TriggerInstance(
			Optional<ContextAwarePredicate> player
	) implements SimpleInstance
	{
		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance
				.group(
						EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)
				)
				.apply(instance, TriggerInstance::new));
	}
}
