package net.swedz.extended_industrialization.client.tesla.generator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.api.Assert;
import team.lodestar.lodestone.systems.rendering.trail.TrailPointBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class TeslaArcs
{
	private static final Set<Direction> ALL_DIRECTIONS = Arrays.stream(Direction.values()).collect(Collectors.toUnmodifiableSet());
	
	public static final Random RANDOM = new Random();
	
	private final float widthScale;
	private final int   arcDuration;
	private final int   arcs;
	private final int   minLength;
	private final int   maxLength;
	private final float maxSectionLength;
	private final int   sectionSplits;
	
	private final Supplier<Vec3> originSupplier;
	
	private final Map<Direction.Axis, Supplier<Float>> offsetGenerators;
	
	private final List<TrailPointBuilder> trails = Lists.newArrayList();
	
	public TeslaArcs(float widthScale,
					 int arcDuration, int arcs,
					 int minLength, int maxLength,
					 float maxSectionLength, int sectionSplits,
					 Supplier<Vec3> originSupplier,
					 Set<Direction> allowedDirections)
	{
		Assert.that(widthScale > 0);
		Assert.that(arcDuration > 0);
		Assert.that(arcs > 0);
		Assert.that(minLength > 0);
		Assert.that(maxLength > 0 && maxLength >= minLength);
		Assert.that(maxSectionLength > 0);
		Assert.that(sectionSplits > 0);
		Assert.notNull(originSupplier);
		Assert.notNull(allowedDirections);
		Assert.that(!allowedDirections.isEmpty());
		
		this.widthScale = widthScale;
		this.arcDuration = arcDuration;
		this.arcs = arcs;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.maxSectionLength = maxSectionLength;
		this.sectionSplits = sectionSplits;
		this.originSupplier = originSupplier;
		this.offsetGenerators = this.buildOffsetGenerators(allowedDirections);
	}
	
	public TeslaArcs(float widthScale,
					 int arcDuration, int arcs,
					 int minLength, int maxLength,
					 float maxSectionLength, int sectionSplits,
					 Supplier<Vec3> originSupplier)
	{
		this(widthScale, arcDuration, arcs, minLength, maxLength, maxSectionLength, sectionSplits, originSupplier, ALL_DIRECTIONS);
	}
	
	private Map<Direction.Axis, Supplier<Float>> buildOffsetGenerators(Set<Direction> allowedDirections)
	{
		Map<Direction.Axis, Supplier<Float>> generators = Maps.newHashMap();
		
		for(Direction.Axis axis : Direction.Axis.values())
		{
			Direction positive = Direction.get(Direction.AxisDirection.POSITIVE, axis);
			boolean allowPositive = allowedDirections.contains(positive);
			Direction negative = Direction.get(Direction.AxisDirection.NEGATIVE, axis);
			boolean allowNegative = allowedDirections.contains(negative);
			if(allowPositive && allowNegative)
			{
				generators.put(axis, () -> RANDOM.nextFloat(2) - 1);
			}
			else if(allowPositive)
			{
				generators.put(axis, RANDOM::nextFloat);
			}
			else if(allowNegative)
			{
				generators.put(axis, () -> RANDOM.nextFloat() - 1);
			}
		}
		
		return Collections.unmodifiableMap(generators);
	}
	
	public float widthScale()
	{
		return widthScale;
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
	
	public List<TrailPointBuilder> getTrails()
	{
		return Collections.unmodifiableList(trails);
	}
	
	private float randomOffset(Direction.Axis axis)
	{
		Supplier<Float> generator = offsetGenerators.get(axis);
		return generator != null ? generator.get() : 0;
	}
	
	private void createArc()
	{
		TrailPointBuilder trail = TrailPointBuilder.create(arcDuration);
		int length = RANDOM.nextInt(minLength, maxLength + 1);
		Vec3 origin = originSupplier.get();
		double x = origin.x();
		double y = origin.y();
		double z = origin.z();
		float dirX = this.randomOffset(Direction.Axis.X);
		float dirY = this.randomOffset(Direction.Axis.Y);
		float dirZ = this.randomOffset(Direction.Axis.Z);
		for(int i = 0; i < length; i++)
		{
			float sectionLength = maxSectionLength / (i + 1);
			float offsetX = this.randomOffset(Direction.Axis.X);
			float offsetY = this.randomOffset(Direction.Axis.Y);
			float offsetZ = this.randomOffset(Direction.Axis.Z);
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
			int maxCreate = arcs - trails.size();
			int create = Math.max(Math.min(arcs / 2, maxCreate), 1);
			for(int i = 0; i < create; i++)
			{
				this.createArc();
			}
		}
	}
}
