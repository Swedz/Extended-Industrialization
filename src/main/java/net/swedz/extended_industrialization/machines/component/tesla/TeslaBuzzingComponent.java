package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.swedz.extended_industrialization.proxy.EIProxy;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.function.Supplier;

public final class TeslaBuzzingComponent implements IComponent.ClientOnly
{
	private final MachineBlockEntity machine;
	
	private final SoundEvent sound;
	private final SoundSource source;
	
	private final Supplier<Boolean> shouldBuzz;
	private final Supplier<Float>   buzzingPitch;
	
	private boolean buzzing;
	
	public TeslaBuzzingComponent(
			MachineBlockEntity machine,
			SoundEvent sound, SoundSource source,
			Supplier<Boolean> shouldBuzz, Supplier<Float> buzzingPitch
	)
	{
		this.machine = machine;
		this.sound = sound;
		this.source = source;
		this.shouldBuzz = shouldBuzz;
		this.buzzingPitch = buzzingPitch;
	}
	
	public void tick()
	{
		if(!buzzing && shouldBuzz.get())
		{
			buzzing = true;
			Proxies.get(EIProxy.class).startTeslaCoilLoopSound(
					machine.getBlockPos(), sound, source,
					() -> machine.isRemoved() || !shouldBuzz.get(),
					buzzingPitch,
					() -> buzzing = false
			);
		}
	}
	
	@Override
	public void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
	}
	
	@Override
	public void readClientNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
	}
}
