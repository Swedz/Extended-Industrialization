package net.swedz.extended_industrialization.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.swedz.extended_industrialization.EI;

@Mod.EventBusSubscriber(modid = EI.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EIConfig
{
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	
	private static final ModConfigSpec.BooleanValue REQUIRE_WATER_BIOME_FOR_PUMP = BUILDER
			.comment("Whether water pumps require a water biome (river or ocean) to operate")
			.define("require_water_biome_for_pump", true);
	
	public static final ModConfigSpec SPEC = BUILDER.build();
	
	public static boolean requireWaterBiomeForPump;
	
	@SubscribeEvent
	static void onConfigLoad(ModConfigEvent event)
	{
		requireWaterBiomeForPump = REQUIRE_WATER_BIOME_FOR_PUMP.get();
	}
}
