package net.swedz.miextended.registry.api.registerable;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SimpleRegisterableWrapper<Type, ActualType extends Type> extends RegisterableWrapper<ActualType, DeferredHolder<Type, ActualType>, DeferredRegister<Type>, Void>
{
	public SimpleRegisterableWrapper(DeferredRegister<Type> register, Supplier<ActualType> creator)
	{
		super(register, null, (__) -> creator.get());
	}
}