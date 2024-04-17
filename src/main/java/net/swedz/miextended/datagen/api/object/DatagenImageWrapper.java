package net.swedz.miextended.datagen.api.object;

import aztech.modern_industrialization.textures.MCMetaInfo;
import com.mojang.blaze3d.platform.NativeImage;
import net.swedz.miextended.datagen.api.DatagenOutputTarget;
import net.swedz.miextended.datagen.api.DatagenProvider;

import java.util.Optional;

public class DatagenImageWrapper extends DatagenObjectWrapper<NativeImage>
{
	private final String name;
	
	private Optional<MCMetaInfo> mcMetaInfo = Optional.empty();
	
	public DatagenImageWrapper(DatagenProvider provider, String path, String name, NativeImage object)
	{
		super(provider, DatagenOutputTarget.RESOURCE_PACK, (p) -> p.resolve(path), object);
		this.name = name;
	}
	
	public void withMCMetaInfo(MCMetaInfo mcMetaInfo)
	{
		this.mcMetaInfo = Optional.of(mcMetaInfo);
	}
	
	@Override
	public void write()
	{
		provider.writeImageIfNotExist(
				target,
				(p) -> pathFunction.apply(p).resolve(name + ".png"),
				object
		);
		mcMetaInfo.ifPresent((mcmi) -> provider.writeJsonIfNotExist(
				target,
				(p) -> pathFunction.apply(p).resolve(name + ".png.mcmeta"),
				DatagenProvider.GSON.toJsonTree(mcmi)
		));
	}
}
