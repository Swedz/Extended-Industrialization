package net.swedz.extended_industrialization.item.machineconfig;

import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;
import java.util.function.Function;

public interface MachineConfigSlot<T, S extends AbstractConfigurableStack>
{
	Codec<MachineConfigSlot> CODEC = Codec.either(ItemSlot.CODEC, FluidSlot.CODEC)
			.xmap(
					(either) -> either.map(Function.identity(), Function.identity()),
					(slot) ->
					{
						if(slot instanceof ItemSlot itemSlot)
						{
							return Either.left(itemSlot);
						}
						else if(slot instanceof FluidSlot fluidSlot)
						{
							return Either.right(fluidSlot);
						}
						else
						{
							throw new IllegalArgumentException("Invalid machine config slot type provided");
						}
					}
			);
	
	int index();
	
	T lock();
	
	S stack(MIInventory inventory);
	
	record ItemSlot(int index, int capacity, Item lock) implements MachineConfigSlot<Item, ConfigurableItemStack>
	{
		private static final Codec<ItemSlot> CODEC = RecordCodecBuilder.create((instance) -> instance
				.group(
						Codec.INT.fieldOf("index").forGetter(ItemSlot::index),
						Codec.INT.fieldOf("capacity").forGetter(ItemSlot::capacity),
						BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("item_lock").forGetter((s) -> Optional.ofNullable(s.lock()))
				)
				.apply(instance, (index, capacity, lock) -> new ItemSlot(index, capacity, lock.orElse(null))));
		
		@Override
		public ConfigurableItemStack stack(MIInventory inventory)
		{
			return inventory.getItemStacks().get(index);
		}
	}
	
	record FluidSlot(int index, Fluid lock) implements MachineConfigSlot<Fluid, ConfigurableFluidStack>
	{
		private static final Codec<FluidSlot> CODEC = RecordCodecBuilder.create((instance) -> instance
				.group(
						Codec.INT.fieldOf("index").forGetter(FluidSlot::index),
						BuiltInRegistries.FLUID.byNameCodec().optionalFieldOf("fluid_lock").forGetter((s) -> Optional.ofNullable(s.lock()))
				)
				.apply(instance, (index, lock) -> new FluidSlot(index, lock.orElse(null))));
		
		@Override
		public ConfigurableFluidStack stack(MIInventory inventory)
		{
			return inventory.getFluidStacks().get(index);
		}
	}
}
