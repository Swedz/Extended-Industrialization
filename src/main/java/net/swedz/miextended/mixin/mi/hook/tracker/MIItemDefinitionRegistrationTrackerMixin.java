package net.swedz.miextended.mixin.mi.hook.tracker;

import aztech.modern_industrialization.definition.ItemDefinition;
import aztech.modern_industrialization.items.SortOrder;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.DeferredItem;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;
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
					  BiConsumer<Item, ItemModelProvider> modelGenerator, SortOrder sortOrder,
					  CallbackInfo callback)
	{
		if(MIHookTracker.isOpen())
		{
			MIHookTracker.addItemLanguageEntry(item, englishName);
			MIHookTracker.addItemModel(item, modelGenerator);
			MIExtended.includeItemRegisteredByMI(item.getId());
		}
	}
}
