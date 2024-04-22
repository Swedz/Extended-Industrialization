package net.swedz.miextended.items;

import aztech.modern_industrialization.api.energy.EnergyApi;
import com.google.common.collect.Sets;
import dev.technici4n.grandpower.api.ISimpleEnergyItem;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.api.capabilities.CapabilitiesListeners;
import net.swedz.miextended.api.item.ItemWrapper;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class MIEItemWrapper<I extends Item> extends ItemWrapper<I, MIEItemProperties, MIEItemWrapper<I>>
{
	public MIEItemWrapper()
	{
		super(MIExtended.ID);
	}
	
	private final Set<TagKey<Item>> tags = Sets.newHashSet();
	
	public Set<TagKey<Item>> getTags()
	{
		return Set.copyOf(tags);
	}
	
	@SafeVarargs
	public final MIEItemWrapper<I> tag(TagKey<Item>... tags)
	{
		Collections.addAll(this.tags, tags);
		return this;
	}
	
	private Consumer<? super I> itemRegistrationListener;
	
	public void runItemRegistrationListener()
	{
		if(itemRegistrationListener != null)
		{
			itemRegistrationListener.accept(this.asItem());
		}
	}
	
	public MIEItemWrapper<I> withItemRegistrationListener(Consumer<? super I> itemRegistrationListener)
	{
		this.itemRegistrationListener = itemRegistrationListener;
		return this;
	}
	
	public MIEItemWrapper<I> withItemCapabilities(BiConsumer<? super I, RegisterCapabilitiesEvent> listener)
	{
		return this.withItemRegistrationListener((item) -> CapabilitiesListeners.register((event) -> listener.accept(item, event)));
	}
	
	public MIEItemWrapper<I> withSimplyEnergyItemCapability()
	{
		return this.withItemCapabilities((item, event) ->
		{
			ISimpleEnergyItem simpleEnergyItem = (ISimpleEnergyItem) item;
			event.registerItem(
					EnergyApi.ITEM,
					(stack, ctx) -> ISimpleEnergyItem.createStorage(
							stack,
							simpleEnergyItem.getEnergyCapacity(stack),
							simpleEnergyItem.getEnergyMaxInput(stack),
							simpleEnergyItem.getEnergyMaxOutput(stack)
					),
					item
			);
		});
	}
	
	@Override
	protected MIEItemProperties defaultProperties()
	{
		return new MIEItemProperties();
	}
	
	@Override
	protected DeferredItem<I> commonRegister()
	{
		return MIEItems.include(this);
	}
}
