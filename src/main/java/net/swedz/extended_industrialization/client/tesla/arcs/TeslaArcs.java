package net.swedz.extended_industrialization.client.tesla.arcs;

import com.google.common.collect.Lists;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.systems.rendering.trail.TrailPointBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public final class TeslaArcs
{
	private static final Random RANDOM = new Random();
	
	private final int   arcDuration;
	private final int   arcs;
	private final int   minLength;
	private final int   maxLength;
	private final float maxSectionLength;
	
	private final Supplier<Vec3> offset;
	
	private final List<TrailPointBuilder> trails = Lists.newArrayList();
	
	public TeslaArcs(int arcDuration, int arcs, int minLength, int maxLength, float maxSectionLength,
					 Supplier<Vec3> offset)
	{
		this.arcDuration = arcDuration;
		this.arcs = arcs;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.maxSectionLength = maxSectionLength;
		this.offset = offset;
	}
	
	public List<TrailPointBuilder> getTrails()
	{
		return Collections.unmodifiableList(trails);
	}
	
	private void createArc(Vec3 offset)
	{
		TrailPointBuilder trail = TrailPointBuilder.create(arcDuration);
		int length = RANDOM.nextInt(minLength, maxLength + 1);
		double x = offset.x();
		double y = offset.y();
		double z = offset.z();
		float dirX = RANDOM.nextFloat(3) - 1;
		float dirY = RANDOM.nextFloat(3) - 1;
		float dirZ = RANDOM.nextFloat(3) - 1;
		for(int point = 0; point < length; point++)
		{
			trail.addTrailPoint(new Vec3(x, y, z));
			float sectionLength = maxSectionLength / (point + 1);
			float offsetX = RANDOM.nextFloat(3) - 1;
			float offsetY = RANDOM.nextFloat(3) - 1;
			float offsetZ = RANDOM.nextFloat(3) - 1;
			x += sectionLength * offsetX * dirX;
			y += sectionLength * offsetY * dirY;
			z += sectionLength * offsetZ * dirZ;
		}
		trails.add(trail);
	}
	
	public void tick()
	{
		trails.removeIf((trail) ->
		{
			trail.tickTrailPoints();
			return trail.getTrailPoints().isEmpty();
		});
		
		if(trails.size() < arcs)
		{
			Vec3 offset = this.offset.get();
			int arcsToCreate = arcs - trails.size();
			for(int arc = 0; arc < arcsToCreate; arc++)
			{
				this.createArc(offset);
			}
		}
	}
}
