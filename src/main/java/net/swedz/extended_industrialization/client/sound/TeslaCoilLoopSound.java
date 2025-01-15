package net.swedz.extended_industrialization.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.function.Supplier;

public final class TeslaCoilLoopSound extends AbstractTickableSoundInstance
{
	private final Supplier<Boolean> shouldStop;
	private final Supplier<Float>   getPitch;
	private final Runnable          onStop;
	
	public TeslaCoilLoopSound(
			BlockPos origin, SoundEvent sound, SoundSource source,
			Supplier<Boolean> shouldStop, Supplier<Float> getPitch,
			Runnable onStop
	)
	{
		super(sound, source, SoundInstance.createUnseededRandom());
		
		var pos = origin.getCenter();
		
		this.x = pos.x();
		this.y = pos.y();
		this.z = pos.z();
		this.looping = true;
		this.volume = 0;
		
		this.shouldStop = shouldStop;
		this.getPitch = getPitch;
		this.onStop = onStop;
	}
	
	@Override
	public void tick()
	{
		if(this.isStopped())
		{
			return;
		}
		
		if(shouldStop.get())
		{
			this.stop();
			onStop.run();
			return;
		}
		
		volume = 1;
		pitch = getPitch.get();
	}
	
	@Override
	public boolean canStartSilent()
	{
		return true;
	}
}
