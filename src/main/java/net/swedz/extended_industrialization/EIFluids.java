package net.swedz.extended_industrialization;

import aztech.modern_industrialization.definition.FluidTexture;
import com.google.common.collect.Sets;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.swedz.tesseract.neoforge.registry.MIFluidProperties;
import net.swedz.tesseract.neoforge.registry.holder.FluidHolder;
import net.swedz.tesseract.neoforge.registry.holder.MIFluidHolder;

import java.util.Set;

import static aztech.modern_industrialization.definition.FluidDefinition.*;

public final class EIFluids
{
	public static final class Registry
	{
		public static final  DeferredRegister<Fluid>     FLUIDS      = DeferredRegister.create(Registries.FLUID, EI.ID);
		public static final  DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, EI.ID);
		private static final Set<FluidHolder>            HOLDERS     = Sets.newHashSet();
		
		private static void init(IEventBus bus)
		{
			FLUIDS.register(bus);
			FLUID_TYPES.register(bus);
		}
		
		public static void include(FluidHolder holder)
		{
			HOLDERS.add(holder);
		}
	}
	
	public static void init(IEventBus bus)
	{
		Registry.init(bus);
	}
	
	public static final MIFluidHolder HONEY               = create("honey", "Honey", 0xF2AE21, NEAR_OPACITY, FluidTexture.WATER_LIKE, false).register();
	public static final MIFluidHolder MANURE              = create("manure", "Manure", 0x211404, FULL_OPACITY, FluidTexture.LAVA_LIKE, false).register();
	public static final MIFluidHolder COMPOSTED_MANURE    = create("composted_manure", "Composted Manure", 0x301b00, FULL_OPACITY, FluidTexture.LAVA_LIKE, false).register();
	public static final MIFluidHolder PHOSPHORIC_ACID     = create("phosphoric_acid", "Phosphoric Acid", 0x00A000, NEAR_OPACITY, FluidTexture.WATER_LIKE, false).register();
	public static final MIFluidHolder POTASSIUM_CHLORIDE  = create("potassium_chloride", "Potassium Chloride", 0xCECECE, NEAR_OPACITY, FluidTexture.WATER_LIKE, false).register();
	public static final MIFluidHolder POTASSIUM_HYDROXIDE = create("potassium_hydroxide", "Potassium Hydroxide", 0xD7AF03, LOW_OPACITY, FluidTexture.WATER_LIKE, false).register();
	public static final MIFluidHolder NPK_FERTILIZER      = create("npk_fertilizer", "NPK Fertilizer", 0x4ABD44, NEAR_OPACITY, FluidTexture.WATER_LIKE, false).register();
	public static final MIFluidHolder DISTILLED_WATER     = create("distilled_water", "Distilled Water", 0xADCDFF, LOW_OPACITY, FluidTexture.WATER_LIKE, false).register();
	
	public static Set<FluidHolder> values()
	{
		return Set.copyOf(Registry.HOLDERS);
	}
	
	public static MIFluidHolder create(String id, String englishName, MIFluidProperties properties)
	{
		MIFluidHolder holder = new MIFluidHolder(
				EI.id(id), englishName,
				Registry.FLUIDS, Registry.FLUID_TYPES,
				EIBlocks.Registry.BLOCKS,
				EIItems.Registry.ITEMS, EISortOrder.BUCKETS,
				properties
		);
		Registry.include(holder);
		EIBlocks.Registry.include(holder.block());
		EIItems.Registry.include(holder.bucketItem());
		return holder;
	}
	
	public static MIFluidHolder create(String id, String englishName, int color, int opacity, FluidTexture texture, boolean isGas)
	{
		return create(id, englishName, new MIFluidProperties(color, opacity, texture, isGas));
	}
}
