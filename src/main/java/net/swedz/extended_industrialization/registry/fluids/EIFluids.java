package net.swedz.extended_industrialization.registry.fluids;

import aztech.modern_industrialization.definition.FluidTexture;
import com.google.common.collect.Sets;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.registry.blocks.EIBlocks;
import net.swedz.extended_industrialization.registry.items.EIItems;

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
	
	public static final FluidHolder HONEY            = create("honey", "Honey", 0xF2AE21, NEAR_OPACITY, FluidTexture.WATER_LIKE, false).register();
	public static final FluidHolder MANURE           = create("manure", "Manure", 0x211404, FULL_OPACITY, FluidTexture.LAVA_LIKE, false).register();
	public static final FluidHolder COMPOSTED_MANURE = create("composted_manure", "Composted Manure", 0x301b00, FULL_OPACITY, FluidTexture.LAVA_LIKE, false).register();
	public static final FluidHolder NPK_FERTILIZER   = create("npk_fertilizer", "NPK Fertilizer", 0x4ABD44, NEAR_OPACITY, FluidTexture.WATER_LIKE, false).register();
	
	public static Set<FluidHolder> values()
	{
		return Set.copyOf(Registry.HOLDERS);
	}
	
	public static MIFluidHolder create(String id, String englishName, FluidProperties properties)
	{
		MIFluidHolder holder = new MIFluidHolder(EI.id(id), englishName, Registry.FLUIDS, Registry.FLUID_TYPES, EIBlocks.Registry.BLOCKS, EIItems.Registry.ITEMS, properties);
		Registry.include(holder);
		EIBlocks.Registry.include(holder.block());
		EIItems.Registry.include(holder.bucketItem());
		return holder;
	}
	
	public static MIFluidHolder create(String id, String englishName, int color, int opacity, FluidTexture texture, boolean isGas)
	{
		return create(id, englishName, new FluidProperties(color, opacity, texture, isGas));
	}
}
