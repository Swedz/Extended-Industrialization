package net.swedz.extended_industrialization.api;

import java.util.Objects;

public final class Assert
{
	public static void that(boolean bool, String message, ThrowableSupplier throwable)
	{
		if(!bool)
		{
			throw throwable.apply(message);
		}
	}
	
	public static void that(boolean bool, String message)
	{
		that(bool, message, IllegalArgumentException::new);
	}
	
	public static void that(boolean bool, ThrowableSupplier throwable)
	{
		that(bool, null, throwable);
	}
	
	public static void that(boolean bool)
	{
		that(bool, (String) null);
	}
	
	public static void notNull(Object object, String message, ThrowableSupplier throwable)
	{
		that(object != null, message, throwable);
	}
	
	public static void notNull(Object object, String message)
	{
		notNull(object != null, message, NullPointerException::new);
	}
	
	public static void notNull(Object object, ThrowableSupplier throwable)
	{
		notNull(object, null, throwable);
	}
	
	public static void notNull(Object object)
	{
		notNull(object, (String) null);
	}
	
	public static void noneNull(Object... objects)
	{
		for(Object object : objects)
		{
			that(object != null, null, NullPointerException::new);
		}
	}
	
	public static void equals(Object a, Object b, String message, ThrowableSupplier throwable)
	{
		that(Objects.equals(a, b), message, throwable);
	}
	
	public static void equals(Object a, Object b, String message)
	{
		equals(a, b, message, IllegalArgumentException::new);
	}
	
	public static void equals(Object a, Object b, ThrowableSupplier throwable)
	{
		equals(a, b, null, throwable);
	}
	
	public static void equals(Object a, Object b)
	{
		equals(a, b, (String) null);
	}
	
	public interface ThrowableSupplier
	{
		RuntimeException apply(String message);
	}
}
