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

public final class LinkedManyChainersTrigger extends SimpleCriterionTrigger<LinkedManyChainersTrigger.TriggerInstance>
{
	public static Criterion<TriggerInstance> linked(int count)
	{
		return EICriterionTriggers.LINKED_MANY_CHAINERS.get().createCriterion(
				new TriggerInstance(
						Optional.empty(),
						count
				)
		);
	}
	
	@Override
	public Codec<TriggerInstance> codec()
	{
		return TriggerInstance.CODEC;
	}
	
	public void trigger(ServerPlayer player, int count)
	{
		this.trigger(player, (instance) -> instance.matches(player, count));
	}
	
	public record TriggerInstance(
			Optional<ContextAwarePredicate> player,
			int count
	) implements SimpleInstance
	{
		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance
				.group(
						EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
						Codec.INT.fieldOf("count").forGetter(TriggerInstance::count)
				)
				.apply(instance, TriggerInstance::new));
		
		public boolean matches(ServerPlayer player, int count)
		{
			return count >= this.count;
		}
	}
}
