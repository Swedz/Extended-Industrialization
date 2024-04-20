package net.swedz.miextended.api.isolatedlistener;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class IsolatedListenerMultiMap
{
	private final Map<LevelAccessor, HashMap<ChunkPos, Map<Class<?>, Set<IsolatedListener<?>>>>> storage = new HashMap<>();
	
	public <E extends Event> void add(LevelAccessor level, ChunkPos chunk, Class<E> listenerClass, IsolatedListener<E> listener)
	{
		storage.computeIfAbsent(level, (__) -> Maps.newHashMap())
				.computeIfAbsent(chunk, (__) -> Maps.newHashMap())
				.computeIfAbsent(listenerClass, (__) -> Sets.newHashSet())
				.add(listener);
	}
	
	public <E extends Event> void remove(LevelAccessor level, ChunkPos chunk, Class<E> listenerClass, IsolatedListener<E> listener)
	{
		Map<ChunkPos, Map<Class<?>, Set<IsolatedListener<?>>>> chunkMap = storage.get(level);
		if(chunkMap == null)
		{
			return;
		}
		Map<Class<?>, Set<IsolatedListener<?>>> listenerMap = chunkMap.get(chunk);
		if(listenerMap == null)
		{
			return;
		}
		Set<IsolatedListener<?>> listeners = listenerMap.get(listenerClass);
		if(listeners == null)
		{
			return;
		}
		
		if(!listeners.remove(listener))
		{
			throw new RuntimeException("Could not remove element at position " + chunk + " as it does not exist.");
		}
		
		if(listeners.size() == 0)
		{
			listenerMap.remove(listenerClass);
			if(listenerMap.size() == 0)
			{
				chunkMap.remove(chunk);
				if(chunkMap.size() == 0)
				{
					storage.remove(level);
				}
			}
		}
	}
	
	@Nullable
	public <E extends Event> Set<IsolatedListener<E>> get(LevelAccessor level, ChunkPos chunk, Class<E> listenerClass)
	{
		Map<ChunkPos, Map<Class<?>, Set<IsolatedListener<?>>>> chunkMap = storage.get(level);
		if(chunkMap == null)
		{
			return null;
		}
		Map<Class<?>, Set<IsolatedListener<?>>> listenerMap = chunkMap.get(chunk);
		if(listenerMap == null)
		{
			return null;
		}
		Set<IsolatedListener<?>> listeners = listenerMap.get(listenerClass);
		if(listeners == null)
		{
			return null;
		}
		return listeners.stream()
				.map((l) -> (IsolatedListener<E>) l)
				.collect(Collectors.toSet());
	}
	
	public int size()
	{
		return storage.size();
	}
}
