package net.swedz.extended_industrialization.proxy;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.swedz.tesseract.neoforge.proxy.Proxy;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;

import java.util.function.Supplier;

@ProxyEntrypoint
public class EIProxy implements Proxy
{
	public void tickTesla(BlockPos blockPos)
	{
	}
	
	public void createTeslaArc(BlockPos blockPos, Vec3 target)
	{
	}
	
	public void startTeslaCoilLoopSound(BlockPos origin, SoundEvent sound, SoundSource source, Supplier<Boolean> shouldStop, Supplier<Float> getPitch, Runnable onStop)
	{
	}
	
	public final void startTeslaCoilLoopSound(BlockPos origin, SoundEvent sound, SoundSource source, Supplier<Boolean> shouldStop, Supplier<Float> getPitch)
	{
		this.startTeslaCoilLoopSound(origin, sound, source, shouldStop, getPitch, () ->
		{
		});
	}
	
	public void removeTesla(BlockPos blockPos)
	{
	}
	
	public boolean shouldCauseElectricToolBreakReset()
	{
		return false;
	}
}
