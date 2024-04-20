package net.swedz.miextended.api.isolatedlistener;

import net.neoforged.bus.api.Event;

public interface IsolatedListener<E extends Event>
{
	void on(E event);
}
