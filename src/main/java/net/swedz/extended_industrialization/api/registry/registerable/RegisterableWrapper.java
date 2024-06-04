package net.swedz.extended_industrialization.api.registry.registerable;

import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.api.MCIdentifier;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegisterableWrapper<Type, DeferredType extends DeferredHolder<? super Type, Type>, Register extends DeferredRegister<? super Type>, Properties>
{
	private final Register register;
	
	private final Properties properties;
	
	private final Function<Properties, Type> creator;
	
	private Optional<DeferredType> deferred = Optional.empty();
	
	public RegisterableWrapper(Register register, Properties properties, Function<Properties, Type> creator)
	{
		this.register = register;
		this.properties = properties;
		this.creator = creator;
	}
	
	public Properties properties()
	{
		return properties;
	}
	
	public void withProperties(Consumer<Properties> action)
	{
		action.accept(properties);
	}
	
	public Function<Properties, Type> creator()
	{
		return creator;
	}
	
	public void register(MCIdentifier identifier, PropertyDispatch.QuadFunction<Register, String, Function<Properties, Type>, Properties, DeferredType> builder)
	{
		this.deferred = Optional.of(builder.apply(register, identifier.id(), creator, properties));
	}
	
	public void registerSimple(MCIdentifier identifier, PropertyDispatch.TriFunction<Register, String, Supplier<Type>, DeferredType> builder)
	{
		this.register(identifier, (r, id, f, p) -> builder.apply(r, id, () -> creator().apply(null)));
	}
	
	public DeferredType get()
	{
		return deferred.orElseThrow(() -> new IllegalStateException("Cannot get object that hasn't been registered yet"));
	}
	
	public Type getOrThrow()
	{
		return this.get().get();
	}
}
