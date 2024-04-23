package net.swedz.miextended.registry.fluids;

import aztech.modern_industrialization.definition.FluidTexture;
import com.google.common.collect.Sets;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.registry.blocks.MIEBlocks;
import net.swedz.miextended.registry.items.MIEItems;

import java.util.Set;

import static aztech.modern_industrialization.definition.FluidDefinition.*;

public final class MIEFluids
{
	public static final class Registry
	{
		public static final  DeferredRegister<Fluid>     FLUIDS      = DeferredRegister.create(Registries.FLUID, MIExtended.ID);
		public static final  DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MIExtended.ID);
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
		MIFluidHolder holder = new MIFluidHolder(MIExtended.id(id), englishName, Registry.FLUIDS, Registry.FLUID_TYPES, MIEBlocks.Registry.BLOCKS, MIEItems.Registry.ITEMS, properties);
		Registry.include(holder);
		MIEBlocks.Registry.include(holder.block());
		MIEItems.Registry.include(holder.bucketItem());
		return holder;
	}
	
	public static MIFluidHolder create(String id, String englishName, int color, int opacity, FluidTexture texture, boolean isGas)
	{
		return create(id, englishName, new FluidProperties(color, opacity, texture, isGas));
	}
}
