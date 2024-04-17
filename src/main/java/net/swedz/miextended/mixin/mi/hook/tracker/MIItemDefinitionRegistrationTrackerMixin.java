package net.swedz.miextended.mixin.mi.hook.tracker;

import aztech.modern_industrialization.definition.ItemDefinition;
import aztech.modern_industrialization.items.SortOrder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;
import net.swedz.miextended.tooltips.MIETooltips;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(ItemDefinition.class)
public class MIItemDefinitionRegistrationTrackerMixin
{
	@Inject(
			method = "<init>",
			at = @At("RETURN")
	)
	private void item(String englishName, DeferredItem item,
					  BiConsumer modelGenerator, SortOrder sortOrder,
					  CallbackInfo callback)
	{
		if(MIHookTracker.isOpen())
		{
			String id = item.getId().getPath();
			MIHookTracker.addItemLanguageEntry(id, englishName);
			// TODO use proper item model generation
			//  can leave this as-is until we need an item with a non-standard item model
			MIHookTracker.addStandardItemModelEntry(id);
			MIETooltips.addExtendedItem(id);
		}
	}
}
