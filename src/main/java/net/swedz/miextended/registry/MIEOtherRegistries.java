package net.swedz.miextended.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.registry.items.MIEItems;

import java.util.Comparator;
import java.util.function.Supplier;

public final class MIEOtherRegistries
{
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MIExtended.ID);
	
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MIExtended.ID);
	
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MIExtended.ID);
	
	public static final Supplier<CreativeModeTab> CREATIVE_TAB = MIEOtherRegistries.CREATIVE_MODE_TABS.register(MIExtended.ID, () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.%s.%s".formatted(MIExtended.ID, MIExtended.ID)))
			.icon(() ->
			{
				ItemStack stack = MIEItems.ELETRIC_MINING_DRILL.asItem().getDefaultInstance();
				stack.getOrCreateTag().putBoolean("hide_bar", true);
				return stack;
			})
			.displayItems((params, output) ->
			{
				// TODO custom sorting
				MIEItems.values().stream()
						.sorted(Comparator.comparing(a -> a.identifier().id()))
						.forEach(output::accept);
			})
			.build());
	
	public static void init(IEventBus bus)
	{
		RECIPE_SERIALIZERS.register(bus);
		RECIPE_TYPES.register(bus);
		CREATIVE_MODE_TABS.register(bus);
	}
}
