package net.swedz.extended_industrialization.client.tesla.arcs;

import com.google.common.collect.Lists;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.api.Assert;
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
	private final int   sectionSplits;
	
	private final Supplier<Vec3> offset;
	
	private final List<TrailPointBuilder> trails = Lists.newArrayList();
	
	public TeslaArcs(int arcDuration, int arcs,
					 int minLength, int maxLength,
					 float maxSectionLength, int sectionSplits,
					 Supplier<Vec3> offset)
	{
		Assert.that(arcDuration > 0);
		Assert.that(arcs > 0);
		Assert.that(minLength > 0);
		Assert.that(maxLength > 0 && maxLength >= minLength);
		Assert.that(maxSectionLength > 0);
		Assert.that(sectionSplits > 0);
		Assert.notNull(offset);
		
		this.arcDuration = arcDuration;
		this.arcs = arcs;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.maxSectionLength = maxSectionLength;
		this.sectionSplits = sectionSplits;
		this.offset = offset;
	}
	
	public int duration()
	{
		return arcDuration;
	}
	
	public int count()
	{
		return arcs;
	}
	
	public int minLength()
	{
		return minLength;
	}
	
	public int maxLength()
	{
		return maxLength;
	}
	
	public float maxSectionLength()
	{
		return maxSectionLength;
	}
	
	public int sectionSplits()
	{
		return sectionSplits;
	}
	
	public Vec3 offset()
	{
		return offset.get();
	}
	
	public List<TrailPointBuilder> getTrails()
	{
		return Collections.unmodifiableList(trails);
	}
	
	private float randomOffset()
	{
		return RANDOM.nextFloat(3) - 1;
	}
	
	private void createArc(Vec3 offset)
	{
		TrailPointBuilder trail = TrailPointBuilder.create(arcDuration);
		int length = RANDOM.nextInt(minLength, maxLength + 1);
		double x = offset.x();
		double y = offset.y();
		double z = offset.z();
		float dirX = this.randomOffset();
		float dirY = this.randomOffset();
		float dirZ = this.randomOffset();
		for(int i = 0; i < length; i++)
		{
			float sectionLength = maxSectionLength / (i + 1);
			float offsetX = this.randomOffset();
			float offsetY = this.randomOffset();
			float offsetZ = this.randomOffset();
			for(int j = 0; j < sectionSplits; j++)
			{
				trail.addTrailPoint(new Vec3(x, y, z));
				x += (sectionLength * offsetX * dirX) / sectionSplits;
				y += (sectionLength * offsetY * dirY) / sectionSplits;
				z += (sectionLength * offsetZ * dirZ) / sectionSplits;
			}
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
			Vec3 offset = this.offset();
			int maxCreate = arcs - trails.size();
			int create = Math.max(Math.min(arcs / 2, maxCreate), 1);
			for(int i = 0; i < create; i++)
			{
				this.createArc(offset);
			}
		}
	}
}
