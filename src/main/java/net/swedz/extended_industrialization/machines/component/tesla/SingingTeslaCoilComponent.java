package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.tesseract.neoforge.api.Assert;

public final class SingingTeslaCoilComponent implements IComponent
{
	private final MachineBlockEntity machine;
	
	private int note = -1;
	
	public SingingTeslaCoilComponent(MachineBlockEntity machine)
	{
		this.machine = machine;
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
