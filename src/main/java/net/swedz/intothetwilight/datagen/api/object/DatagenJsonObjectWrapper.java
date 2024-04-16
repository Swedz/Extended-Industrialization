package net.swedz.intothetwilight.datagen.api.object;

import com.google.gson.JsonObject;
import net.swedz.intothetwilight.datagen.api.DatagenOutputTarget;
import net.swedz.intothetwilight.datagen.api.DatagenProvider;

import java.nio.file.Path;
import java.util.function.Function;

public class DatagenJsonObjectWrapper extends DatagenObjectWrapper<JsonObject>
{
	public DatagenJsonObjectWrapper(DatagenProvider provider, DatagenOutputTarget target, Function<Path, Path> pathFunction, JsonObject object)
	{
		super(provider, target, pathFunction, object);
	}
	
	public DatagenJsonObjectWrapper(DatagenProvider provider, DatagenOutputTarget target, Function<Path, Path> pathFunction)
	{
		this(provider, target, pathFunction, new JsonObject());
	}
	
	@Override
	public void write()
	{
		provider.writeJsonIfNotExist(target, pathFunction, object);
	}
}
