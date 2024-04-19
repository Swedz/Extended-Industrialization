package net.swedz.miextended.api;

public interface Creator<O, I>
{
	O create(I input);
}
