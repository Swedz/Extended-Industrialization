package net.swedz.miextended.datagen.client.provider;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.definition.FluidDefinition;
import aztech.modern_industrialization.resource.FastPathPackResources;
import aztech.modern_industrialization.textures.TextureHelper;
import aztech.modern_industrialization.textures.coloramp.Coloramp;
import aztech.modern_industrialization.textures.coloramp.IColoramp;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.datagen.api.DatagenProvider;
import net.swedz.miextended.datagen.api.object.DatagenImageWrapper;
import net.swedz.miextended.datagen.api.object.DatagenLanguageWrapper;
import net.swedz.miextended.datagen.api.object.DatagenModelWrapper;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class MIHookClientDatagenProvider extends DatagenProvider
{
	public MIHookClientDatagenProvider(GatherDataEvent event)
	{
		super(event, "MI Extended Datagen/Client/MI", MI.ID);
	}
	
	private void language()
	{
		log.info("Start of LANGUAGE");
		final DatagenLanguageWrapper lang = new DatagenLanguageWrapper(this);
		for(Consumer<DatagenLanguageWrapper> action : MIHookTracker.LANGUAGE)
		{
			action.accept(lang);
		}
		lang.write();
		log.info("End of LANGUAGE");
	}
	
	private void models(Map<String, Consumer<DatagenModelWrapper>> map, String path)
	{
		for(String id : map.keySet())
		{
			final DatagenModelWrapper modelWrapper = new DatagenModelWrapper(this, path, id);
			map.get(id).accept(modelWrapper);
			modelWrapper.write();
		}
	}
	
	private void blockStates()
	{
		log.info("Start of BLOCK_STATES");
		this.models(MIHookTracker.BLOCK_STATES, "blockstates");
		log.info("End of BLOCK_STATES");
	}
	
	private void blockModels()
	{
		log.info("Start of BLOCK_MODELS");
		this.models(MIHookTracker.BLOCK_MODELS, "models/block");
		log.info("End of BLOCK_MODELS");
	}
	
	private void machineCasingModels()
	{
		log.info("Start of MACHINE_CASING_MODELS");
		this.models(MIHookTracker.MACHINE_CASING_MODELS, "models/machine_casing");
		log.info("End of MACHINE_CASING_MODELS");
	}
	
	private void itemModels()
	{
		log.info("Start of ITEM_MODELS");
		this.models(MIHookTracker.ITEM_MODELS, "models/item");
		log.info("End of ITEM_MODELS");
	}
	
	private void fluidBucketTexture(ResourceProvider resources, FluidDefinition fluid, IColoramp coloramp) throws IOException
	{
		NativeImage bucketImage = this.getTexture(resources, "modern_industrialization:textures/fluid/bucket.png");
		NativeImage bucketContentImage = this.getTexture(resources, "modern_industrialization:textures/fluid/bucket_content.png");
		TextureHelper.colorize(bucketContentImage, coloramp);
		NativeImage oldBucketImage = bucketImage;
		bucketImage = TextureHelper.blend(oldBucketImage, bucketContentImage);
		oldBucketImage.close();
		if(fluid.isGas)
		{
			TextureHelper.flip(bucketImage);
		}
		DatagenImageWrapper wrapper = new DatagenImageWrapper(
				this,
				"textures/item",
				"%s_bucket".formatted(fluid.path()),
				bucketImage
		);
		wrapper.write();
	}
	
	private void fluidStillTexture(ResourceProvider resources, FluidDefinition fluid, IColoramp coloramp) throws IOException
	{
		NativeImage fluidAnimation = this.getTexture(resources, "modern_industrialization:textures/fluid/template/%s.png".formatted(fluid.fluidTexture.path));
		TextureHelper.colorize(fluidAnimation, coloramp);
		TextureHelper.setAlpha(fluidAnimation, fluid.opacity);
		DatagenImageWrapper wrapper = new DatagenImageWrapper(
				this,
				"textures/fluid",
				"%s_still".formatted(fluid.path()),
				fluidAnimation
		);
		wrapper.withMCMetaInfo(fluid.fluidTexture.mcMetaInfo);
		wrapper.write();
	}
	
	private void fluidTextures(ResourceProvider resources) throws IOException
	{
		log.info("Start of FLUID_TEXTURES");
		for(FluidDefinition fluid : MIHookTracker.FLUID_DEFINITIONS)
		{
			IColoramp coloramp = new Coloramp(fluid.color);
			this.fluidBucketTexture(resources, fluid, coloramp);
			this.fluidStillTexture(resources, fluid, coloramp);
		}
		log.info("End of FLUID_TEXTURES");
	}
	
	private void textures()
	{
		log.info("Start of TEXTURES");
		try (MultiPackResourceManager resources = new MultiPackResourceManager(PackType.CLIENT_RESOURCES, this.getPacks()))
		{
			this.fluidTextures(resources);
		}
		catch (IOException ex)
		{
			throw new RuntimeException("Failed to generate textures", ex);
		}
		log.info("End of TEXTURES");
	}
	
	@Override
	public void run()
	{
		this.language();
		this.blockStates();
		this.blockModels();
		this.machineCasingModels();
		this.itemModels();
		this.textures();
	}
	
	private List<PackResources> getPacks()
	{
		List<PackResources> packs = Lists.newArrayList();
		
		// Modern Industrialization Assets
		packs.add(new FilePackResources(
				"mi",
				new FilePackResources.SharedZipFileAccess(ModList.get().getModFileById(MI.ID).getFile().getFilePath().toFile()),
				true, ""
		));
		
		// Our Generated Assets
		packs.add(new FastPathPackResources(
				"gen",
				output.getOutputFolder(),
				true
		));
		
		return packs;
	}
	
	private NativeImage getTexture(ResourceProvider resources, String textureId) throws IOException
	{
		Resource resource = resources.getResource(new ResourceLocation(textureId))
				.orElseThrow(() -> new IOException("Couldn't find texture " + textureId));
		try (InputStream in = resource.open())
		{
			return NativeImage.read(in);
		}
	}
}
