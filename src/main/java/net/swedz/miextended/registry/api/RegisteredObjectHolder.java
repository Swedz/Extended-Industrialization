package net.swedz.miextended.registry.api;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.swedz.miextended.api.MCIdentifiable;
import net.swedz.miextended.api.MCIdentifier;
import net.swedz.miextended.api.capabilities.CapabilitiesListeners;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RegisteredObjectHolder<Thing, ActualThing extends Thing, Self extends RegisteredObjectHolder<Thing, ActualThing, Self>> implements MCIdentifiable, Supplier<ActualThing>
{
	protected final MCIdentifier identifier;
	
	protected final Set<TagKey<Thing>> tags = Sets.newHashSet();
	
	protected Consumer<? super ActualThing> registrationListener;
	
	private boolean locked;
	
	public RegisteredObjectHolder(ResourceLocation location, String englishName)
	{
		this.identifier = new MCIdentifier(location, englishName);
	}
	
	protected final Self self()
	{
		return (Self) this;
	}
	
	@Override
	public final MCIdentifier identifier()
	{
		return identifier;
	}
	
	public Set<TagKey<Thing>> tags()
	{
		return Set.copyOf(tags);
	}
	
	@SafeVarargs
	public final Self tag(TagKey<Thing>... tags)
	{
		Collections.addAll(this.tags, tags);
		return this.self();
	}
	
	public void triggerRegistrationListener()
	{
		if(registrationListener != null)
		{
			registrationListener.accept(this.get());
		}
	}
	
	public Self withRegistrationListener(Consumer<? super ActualThing> listener)
	{
		this.registrationListener = listener;
		return this.self();
	}
	
	public Self withCapabilities(BiConsumer<? super ActualThing, RegisterCapabilitiesEvent> listener)
	{
		return this.withRegistrationListener((thing) -> CapabilitiesListeners.register((event) -> listener.accept(thing, event)));
	}
	
	public final boolean isLocked()
	{
		return locked;
	}
	
	public final void guaranteeUnlocked()
	{
		if(locked)
		{
			throw new IllegalStateException("The holder is already locked");
		}
	}
	
	public final void lock()
	{
		locked = true;
	}
	
	public abstract Self register();
}
