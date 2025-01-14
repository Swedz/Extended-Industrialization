package net.swedz.extended_industrialization.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.swedz.extended_industrialization.EISounds;
import net.swedz.extended_industrialization.machines.blockentity.tesla.TeslaCoilMachineBlockEntity;

public final class TeslaCoilSingingSound extends AbstractTickableSoundInstance
{
	private final TeslaCoilMachineBlockEntity machine;
	
	public TeslaCoilSingingSound(TeslaCoilMachineBlockEntity machine)
	{
		super(EISounds.TESLA_COIL_LOOP.get(), SoundSource.RECORDS, SoundInstance.createUnseededRandom());
		
		var pos = machine.getBlockPos().getCenter();
		
		this.x = pos.x();
		this.y = pos.y();
		this.z = pos.z();
		this.looping = true;
		this.volume = 0;
		
		this.machine = machine;
	}
	
	@Override
	public void tick()
	{
		if(this.isStopped())
		{
			return;
		}
		
		var singing = machine.getSingingComponent();
		if(machine.isRemoved() || !singing.shouldPlay())
		{
			this.stop();
			return;
		}
		
		volume = 1;
		pitch = singing.getPitch();
	}
	
	@Override
	public boolean canStartSilent()
	{
		return true;
	}
}
