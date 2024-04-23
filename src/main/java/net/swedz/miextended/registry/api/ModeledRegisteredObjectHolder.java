package net.swedz.miextended.registry.api;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ModeledRegisteredObjectHolder<Thing, ActualThing extends Thing, Properties, ModelBuilderType extends ModelBuilder, Self extends ModeledRegisteredObjectHolder<Thing, ActualThing, Properties, ModelBuilderType, Self>> extends RegisteredObjectHolder<Thing, ActualThing, Properties, Self>
{
	protected Consumer<ModelBuilderType> modelBuilder;
	
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
		this.modelBuilder = modelBuilderCreator.apply(this.self());
		return this.self();
	}
}
