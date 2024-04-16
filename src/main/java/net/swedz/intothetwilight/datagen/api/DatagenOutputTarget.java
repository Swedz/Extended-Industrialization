package net.swedz.intothetwilight.datagen.api;

import net.minecraft.data.PackOutput;

public enum DatagenOutputTarget
{
	DATA_PACK("data", PackOutput.Target.DATA_PACK),
	RESOURCE_PACK("assets", PackOutput.Target.RESOURCE_PACK),
	REPORTS("reports", PackOutput.Target.REPORTS);
	
	private final String directory;
	private final PackOutput.Target legacy;
	
	DatagenOutputTarget(String directory, PackOutput.Target legacy)
	{
		this.directory = directory;
		this.legacy = legacy;
	}
	
	public String directory()
	{
		return directory;
	}
	
	public PackOutput.Target legacy()
	{
		return legacy;
	}
	
	public static DatagenOutputTarget from(PackOutput.Target other)
	{
		return switch (other)
				{
					case DATA_PACK -> DATA_PACK;
					case RESOURCE_PACK -> RESOURCE_PACK;
					case REPORTS -> REPORTS;
				};
	}
}
