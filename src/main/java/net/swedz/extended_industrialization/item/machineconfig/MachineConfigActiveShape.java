package net.swedz.extended_industrialization.item.machineconfig;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.ActiveShapeComponent;
import aztech.modern_industrialization.util.Simulation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.swedz.extended_industrialization.mixin.mi.accessor.ActiveShapeComponentAccessor;

import java.util.Optional;

record MachineConfigActiveShape(
		int activeShape
) implements MachineConfigApplicable<MachineBlockEntity>
{
	public static final Codec<MachineConfigActiveShape> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.INT.fieldOf("active_shape").forGetter(MachineConfigActiveShape::activeShape)
			)
			.apply(instance, MachineConfigActiveShape::new));
	
	public static Optional<MachineConfigActiveShape> from(MachineBlockEntity machine)
	{
		return machine.components.mapOrDefault(
				ActiveShapeComponent.class,
				(component) -> Optional.of(new MachineConfigActiveShape(component.getActiveShapeIndex())),
				Optional.empty()
		);
	}
	
	@Override
	public boolean matches(MachineBlockEntity target)
	{
		return target.components.mapOrDefault(
				ActiveShapeComponent.class,
				(component) -> activeShape < component.shapeTemplates.length,
				false
		);
	}
	
	@Override
	public boolean apply(Player player, MachineBlockEntity target, Simulation simulation)
	{
		if(!this.matches(target))
		{
			return false;
		}
		
		if(simulation.isActing())
		{
			((ActiveShapeComponentAccessor) target.components.get(ActiveShapeComponent.class)).setActiveShape(activeShape);
		}
		
		return true;
	}
}
