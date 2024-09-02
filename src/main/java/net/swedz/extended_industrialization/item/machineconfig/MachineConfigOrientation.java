package net.swedz.extended_industrialization.item.machineconfig;

import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.util.Simulation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

record MachineConfigOrientation(
		Direction facingDirection,
		boolean hasOutput, Direction outputDirection,
		boolean extractItems, boolean extractFluids
) implements MachineConfigApplicable<OrientationComponent>
{
	public static final Codec<MachineConfigOrientation> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Direction.CODEC.fieldOf("facing_direction").forGetter(MachineConfigOrientation::facingDirection),
					Codec.BOOL.fieldOf("has_output").forGetter(MachineConfigOrientation::hasOutput),
					Direction.CODEC.optionalFieldOf("output_direction").forGetter((o) -> Optional.ofNullable(o.outputDirection())),
					Codec.BOOL.fieldOf("extract_items").forGetter(MachineConfigOrientation::extractItems),
					Codec.BOOL.fieldOf("extract_fluids").forGetter(MachineConfigOrientation::extractFluids)
			)
			.apply(instance, (facingDirection, hasOutput, outputDirection, extractItems, extractFluids) ->
					new MachineConfigOrientation(facingDirection, hasOutput, outputDirection.orElse(null), extractItems, extractFluids)));
	
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
}
