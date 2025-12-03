package net.swedz.extended_industrialization;

import dev.technici4n.grandpower.api.ISimpleEnergyItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Comparator;
import java.util.function.Supplier;

public final class EICreativeTabs
{
	private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EI.ID);
	
	public static final Supplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register(EI.ID, () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.%s.%s".formatted(EI.ID, EI.ID)))
			.icon(() ->
			{
				var stack = EIItems.ELECTRIC_MINING_DRILL.asItem().getDefaultInstance();
				stack.set(EIComponents.HIDE_BAR, true);
				return stack;
			})
			.displayItems((params, output) ->
			{
				Comparator<ItemHolder> compareBySortOrder = Comparator.comparing(ItemHolder::sortOrder);
				Comparator<ItemHolder> compareByName = Comparator.comparing((i) -> i.identifier().id());
				EIItems.values().stream()
						.sorted(compareBySortOrder.thenComparing(compareByName))
						.forEach((item) ->
						{
							output.accept(item);
							
							// Include full energy copies of items too
							if(item.get() instanceof ISimpleEnergyItem energyItem)
							{
								var chargedStack = item.get().getDefaultInstance();
								if(energyItem.getEnergyCapacity(chargedStack) > 0)
								{
									energyItem.setStoredEnergy(chargedStack, energyItem.getEnergyCapacity(chargedStack));
									output.accept(chargedStack);
								}
							}
						});
			})
			.build());
	
	public static void init(IEventBus bus)
	{
		CREATIVE_MODE_TABS.register(bus);
	}
}
