package net.swedz.extended_industrialization.api.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ModeledRegisteredObjectHolder<Thing, ActualThing extends Thing, ModelBuilderType, Self extends ModeledRegisteredObjectHolder<Thing, ActualThing, ModelBuilderType, Self>> extends RegisteredObjectHolder<Thing, ActualThing, Self>
{
	protected Consumer<ModelBuilderType> modelBuilder = (__) ->
	{
	};
	
	public ModeledRegisteredObjectHolder(ResourceLocation location, String englishName)
	{
		super(location, englishName);
	}
	
	public Consumer<ModelBuilderType> modelBuilder()
	{
		return modelBuilder;
	}
	
	public Self withModel(Function<Self, Consumer<ModelBuilderType>> modelBuilderCreator)
	{
		this.guaranteeUnlocked();
		this.modelBuilder = modelBuilderCreator.apply(this.self());
		return this.self();
	}
}
