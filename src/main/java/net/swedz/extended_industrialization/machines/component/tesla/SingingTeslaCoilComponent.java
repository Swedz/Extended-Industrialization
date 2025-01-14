package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.machines.IComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.extended_industrialization.machines.blockentity.tesla.TeslaCoilMachineBlockEntity;
import net.swedz.extended_industrialization.proxy.EIProxy;
import net.swedz.tesseract.neoforge.api.Assert;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.function.Supplier;

public final class SingingTeslaCoilComponent implements IComponent
{
	private final TeslaCoilMachineBlockEntity machine;
	private final Supplier<Boolean>           active;
	
	private int note = -1;
	
	public SingingTeslaCoilComponent(TeslaCoilMachineBlockEntity machine, Supplier<Boolean> active)
	{
		this.machine = machine;
		this.active = active;
	}
	
	public boolean shouldPlay()
	{
		return this.hasNote() && active.get();
	}
	
	public boolean hasNote()
	{
		return note > -1;
	}
	
	public int getNote()
	{
		return note;
	}
	
	public float getPitch()
	{
		return this.hasNote() ? NoteBlock.getPitchFromNote(note) : 1;
	}
	
	private int getWorldNote()
	{
		if(!machine.hasLevel())
		{
			return -1;
		}
		BlockPos below = machine.getBlockPos().below();
		BlockState state = machine.getLevel().getBlockState(below);
		return state.is(Blocks.NOTE_BLOCK) ? state.getValue(NoteBlock.NOTE) : -1;
	}
	
	private boolean running;
	
	public void tickClient()
	{
		Assert.that(machine.hasLevel() && machine.getLevel().isClientSide());
		boolean originalRunning = running;
		running = this.shouldPlay();
		if(running && !originalRunning)
		{
			Proxies.get(EIProxy.class).startTeslaCoilSingingSound(machine);
		}
	}
	
	public boolean updateNote()
	{
		Assert.that(machine.hasLevel() && !machine.getLevel().isClientSide());
		int originalNote = note;
		note = this.getWorldNote();
		return originalNote != note;
	}
	
	@Override
	public void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.putInt("singing_note", note);
	}
	
	@Override
	public void readClientNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		note = tag.getInt("singing_note");
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
	}
}
