package net.swedz.extended_industrialization;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class EIClientConfig
{
	private static final ModConfigSpec.Builder BUILDER;
	
	private static final ModConfigSpec.BooleanValue RENDER_TESLA_ANIMATIONS;
	
	public static final ModConfigSpec SPEC;
	
	static
	{
		BUILDER = new ModConfigSpec.Builder();
		
		RENDER_TESLA_ANIMATIONS = BUILDER
				.comment("Whether tesla animations should be rendered or not")
				.define("render_tesla_animations", true);
		
		SPEC = BUILDER.build();
	}
	
	public static boolean renderTeslaAnimations;
	
	public static void loadConfig()
	{
		renderTeslaAnimations = RENDER_TESLA_ANIMATIONS.get();
	}
}
