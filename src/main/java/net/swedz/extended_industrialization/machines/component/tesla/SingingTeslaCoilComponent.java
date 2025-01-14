package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.extended_industrialization.EISounds;
import net.swedz.tesseract.neoforge.api.Assert;

import java.util.function.Supplier;

public final class SingingTeslaCoilComponent implements IComponent, TeslaBuzzing
{
	private final MachineBlockEntity machine;
	private final Supplier<Boolean>  active;
	
	private int note = -1;
	
	public SingingTeslaCoilComponent(MachineBlockEntity machine, Supplier<Boolean> active)
	{
		this.machine = machine;
		this.active = active;
	}
	
	public boolean hasNote()
	{
		return note > -1;
	}
	
	public int getNote()
	{
		return note;
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
	
	public boolean updateNote()
	{
		Assert.that(machine.hasLevel() && !machine.getLevel().isClientSide());
		int originalNote = note;
		note = this.getWorldNote();
		return originalNote != note;
	}
	
	private boolean buzzing;
	
	@Override
	public MachineBlockEntity getBuzzingMachine()
	{
		return machine;
	}
	
	@Override
	public SoundEvent getBuzzingSound()
	{
		return EISounds.TESLA_COIL_SINGING.get();
	}
	
	@Override
	public SoundSource getBuzzingSoundSource()
	{
		return SoundSource.RECORDS;
	}
	
	@Override
	public boolean isBuzzing()
	{
		return buzzing;
	}
	
	@Override
	public void setBuzzing(boolean buzzing)
	{
		this.buzzing = buzzing;
	}
	
	@Override
	public boolean shouldBuzz()
	{
		return this.hasNote() && active.get();
	}
	
	@Override
	public float getBuzzingPitch()
	{
		return this.hasNote() ? NoteBlock.getPitchFromNote(note) : 1;
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
