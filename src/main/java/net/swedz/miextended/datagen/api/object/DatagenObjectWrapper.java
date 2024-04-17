package net.swedz.miextended.datagen.api.object;

import net.swedz.miextended.datagen.api.DatagenOutputTarget;
import net.swedz.miextended.datagen.api.DatagenProvider;

import java.nio.file.Path;
import java.util.function.Function;

public abstract class DatagenObjectWrapper<T>
{
	protected final DatagenProvider provider;
	
	protected final DatagenOutputTarget  target;
	protected final Function<Path, Path> pathFunction;
	
	protected T object;
	
	public DatagenObjectWrapper(DatagenProvider provider, DatagenOutputTarget target, Function<Path, Path> pathFunction, T object)
	{
		this.provider = provider;
		this.target = target;
		this.pathFunction = pathFunction;
		this.object = object;
	}
	
	public T get()
	{
		return object;
	}
	
	public void set(T object)
	{
		this.object = object;
	}
	
	public abstract void write();
}
