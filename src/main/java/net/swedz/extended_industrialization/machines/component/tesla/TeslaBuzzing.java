package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.swedz.extended_industrialization.proxy.EIProxy;
import net.swedz.tesseract.neoforge.proxy.Proxies;

public interface TeslaBuzzing
{
	MachineBlockEntity getBuzzingMachine();
	
	SoundEvent getBuzzingSound();
	
	SoundSource getBuzzingSoundSource();
	
	boolean isBuzzing();
	
	void setBuzzing(boolean buzzing);
	
	boolean shouldBuzz();
	
	float getBuzzingPitch();
	
	default void tickBuzzing()
	{
		if(!this.isBuzzing() && this.shouldBuzz())
		{
			this.setBuzzing(true);
			var machine = this.getBuzzingMachine();
			Proxies.get(EIProxy.class).startTeslaCoilLoopSound(
					machine.getBlockPos(), this.getBuzzingSound(), this.getBuzzingSoundSource(),
					() -> machine.isRemoved() || !this.shouldBuzz(),
					this::getBuzzingPitch,
					() -> this.setBuzzing(false)
			);
		}
	}
}
