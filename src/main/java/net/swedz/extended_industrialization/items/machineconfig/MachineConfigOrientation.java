package net.swedz.extended_industrialization.items.machineconfig;

import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

record MachineConfigOrientation(
		Direction facingDirection, boolean hasOutput, Direction outputDirection, boolean extractItems,
		boolean extractFluids
) implements MachineConfigSerializable, MachineConfigApplicable<OrientationComponent>
{
	public static MachineConfigOrientation from(OrientationComponent component)
	{
		return new MachineConfigOrientation(
				component.facingDirection,
				component.params.hasOutput,
				component.outputDirection,
				component.extractItems,
				component.extractFluids
		);
	}
	
	public static MachineConfigOrientation deserialize(CompoundTag tag)
	{
		Direction facingDirection = Direction.from3DDataValue(tag.getInt("facingDirection"));
		boolean hasOutput = tag.getBoolean("hasOutput");
		Direction outputDirection = null;
		if(hasOutput)
		{
			outputDirection = Direction.from3DDataValue(tag.getInt("outputDirection"));
		}
		boolean extractItems = tag.getBoolean("extractItems");
		boolean extractFluids = tag.getBoolean("extractFluids");
		return new MachineConfigOrientation(
				facingDirection,
				hasOutput,
				outputDirection,
				extractItems,
				extractFluids
		);
	}
	
	@Override
	public boolean matches(OrientationComponent target)
	{
		return hasOutput == target.params.hasOutput;
	}
	
	@Override
	public boolean apply(Player player, OrientationComponent target, Simulation simulation)
	{
		if(!this.matches(target))
		{
			return false;
		}
		
		if(simulation.isActing())
		{
			target.facingDirection = facingDirection;
			if(target.params.hasOutput)
			{
				target.outputDirection = outputDirection;
				target.extractItems = extractItems;
				target.extractFluids = extractFluids;
			}
		}
		
		return true;
	}
	
	@Override
	public CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		tag.putInt("facingDirection", facingDirection.get3DDataValue());
		tag.putBoolean("hasOutput", hasOutput);
		if(hasOutput)
		{
			tag.putInt("outputDirection", outputDirection.get3DDataValue());
			tag.putBoolean("extractItems", extractItems);
			tag.putBoolean("extractFluids", extractFluids);
		}
		return tag;
	}
}
