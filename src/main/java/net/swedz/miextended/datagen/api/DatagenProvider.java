package net.swedz.miextended.datagen.api;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.MIExtended;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class DatagenProvider implements DataProvider
{
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	protected final DataGenerator      generator;
	protected final ExistingFileHelper existingFileHelper;
	protected final PackOutput         output;
	
	protected final List<CompletableFuture<?>> saveFutures = Lists.newArrayList();
	
	protected CachedOutput cachedOutput;
	
	private final String name, modId;
	
	protected final Logger log;
	
	protected DatagenProvider(GatherDataEvent event, String name, String modId)
	{
		this.generator = event.getGenerator();
		this.existingFileHelper = event.getExistingFileHelper();
		this.output = generator.getPackOutput();
		this.name = name;
		this.modId = modId;
		this.log = LoggerFactory.getLogger(name);
	}
	
	public DataGenerator generator()
	{
		return generator;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	public String modId()
	{
		return modId;
	}
	
	@Override
	public CompletableFuture<?> run(CachedOutput cachedOutput)
	{
		this.cachedOutput = cachedOutput;
		this.run();
		return CompletableFuture.allOf(saveFutures.toArray(new CompletableFuture[0]));
	}
	
	public abstract void run();
	
	public Path generatedPath()
	{
		return output.getOutputFolder();
	}
	
	public Path generatedPath(DatagenOutputTarget target)
	{
		return this.generatedPath().resolve(target.directory()).resolve(modId);
	}
	
	public Path nonGeneratedPath()
	{
		return output.getOutputFolder().getParent().getParent().resolve("main").resolve("resources");
	}
	
	public Path nonGeneratedPath(DatagenOutputTarget target)
	{
		return this.nonGeneratedPath().resolve(target.directory()).resolve(modId);
	}
	
	public void writeJsonForce(DatagenOutputTarget target, Path path, JsonElement json)
	{
		Path generatedPath = this.generatedPath().resolve(target.directory()).resolve(path);
		saveFutures.add(DataProvider.saveStable(cachedOutput, GSON.toJsonTree(json), generatedPath));
	}
	
	public void writeJsonForce(DatagenOutputTarget target, Function<Path, Path> pathFunction, JsonElement json)
	{
		Path generatedPath = pathFunction.apply(this.generatedPath(target));
		saveFutures.add(DataProvider.saveStable(cachedOutput, GSON.toJsonTree(json), generatedPath));
	}
	
	public void writeJsonIfNotExist(DatagenOutputTarget target, Function<Path, Path> pathFunction, JsonElement json)
	{
		Path nonGeneratedPath = pathFunction.apply(this.nonGeneratedPath(target));
		if(!Files.exists(nonGeneratedPath))
		{
			this.writeJsonForce(target, pathFunction, json);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void writeImageIfNotExist(DatagenOutputTarget target, Function<Path, Path> pathFunction, NativeImage image)
	{
		Path nonGeneratedPath = pathFunction.apply(this.nonGeneratedPath(target));
		if(!Files.exists(nonGeneratedPath))
		{
			Path generatedPath = pathFunction.apply(this.generatedPath(target));
			saveFutures.add(CompletableFuture.runAsync(() ->
			{
				try
				{
					byte[] textureBytes = image.asByteArray();
					cachedOutput.writeIfNeeded(
							generatedPath,
							textureBytes,
							Hashing.sha1().hashBytes(textureBytes)
					);
					image.close();
				}
				catch (IOException ex)
				{
					MIExtended.LOGGER.error("Failed to save file to {}", generatedPath, ex);
				}
			}));
		}
	}
}
