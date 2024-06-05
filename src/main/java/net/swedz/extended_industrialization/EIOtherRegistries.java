package net.swedz.extended_industrialization;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.api.registry.holder.ItemHolder;

import java.util.Comparator;
import java.util.function.Supplier;

public final class EIOtherRegistries
{
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, EI.ID);
	
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, EI.ID);
	
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EI.ID);
	
	public static final Supplier<CreativeModeTab> CREATIVE_TAB = EIOtherRegistries.CREATIVE_MODE_TABS.register(EI.ID, () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.%s.%s".formatted(EI.ID, EI.ID)))
			.icon(() ->
			{
				ItemStack stack = EIItems.ELECTRIC_MINING_DRILL.asItem().getDefaultInstance();
				stack.getOrCreateTag().putBoolean("hide_bar", true);
				return stack;
			})
			.displayItems((params, output) ->
			{
				Comparator<ItemHolder> compareBySortOrder = Comparator.comparing(ItemHolder::sortOrder);
				Comparator<ItemHolder> compareByName = Comparator.comparing((i) -> i.identifier().id());
				EIItems.values().stream()
						.sorted(compareBySortOrder.thenComparing(compareByName))
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
