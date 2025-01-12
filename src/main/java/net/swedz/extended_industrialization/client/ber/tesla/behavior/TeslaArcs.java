package net.swedz.extended_industrialization.client.ber.tesla.behavior;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.client.ber.tesla.arc.TeslaArcBuilder;
import net.swedz.tesseract.neoforge.api.Assert;

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
	
	private final BlockPos worldPosition;
	
	private final boolean constantArcs;
	private final float   widthScale;
	private final int     arcDuration;
	private final int     arcs;
	private final int     minLength;
	private final int     maxLength;
	private final float   maxSectionLength;
	private final int     sectionSplits;
	
	private final Supplier<Vec3> originSupplier;
	
	private final Map<Direction.Axis, Supplier<Float>> offsetGenerators;
	
	private final List<TeslaArcBuilder> trails = Lists.newArrayList();
	
	public TeslaArcs(BlockPos worldPosition, boolean constantArcs,
					 float widthScale,
					 int arcDuration, int arcs,
					 int minLength, int maxLength,
					 float maxSectionLength, int sectionSplits,
					 Supplier<Vec3> originSupplier,
					 Set<Direction> allowedDirections)
	{
		Assert.notNull(worldPosition);
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
		
		this.worldPosition = worldPosition;
		this.constantArcs = constantArcs;
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
	
	public TeslaArcs(BlockPos worldPosition, boolean constantArcs,
					 float widthScale,
					 int arcDuration, int arcs,
					 int minLength, int maxLength,
					 float maxSectionLength, int sectionSplits,
					 Supplier<Vec3> originSupplier)
	{
		this(worldPosition, constantArcs, widthScale, arcDuration, arcs, minLength, maxLength, maxSectionLength, sectionSplits, originSupplier, ALL_DIRECTIONS);
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
	
	public List<TeslaArcBuilder> getTrails()
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
		TeslaArcBuilder builder = TeslaArcBuilder.create(arcDuration);
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
			float offsetX = Math.abs(dirX) * RANDOM.nextFloat();
			float offsetY = Math.abs(dirY) * RANDOM.nextFloat();
			float offsetZ = Math.abs(dirZ) * RANDOM.nextFloat();
			for(int j = 0; j < sectionSplits; j++)
			{
				builder.add(new Vec3(x, y, z));
				x += (sectionLength * offsetX * dirX) / sectionSplits;
				y += (sectionLength * offsetY * dirY) / sectionSplits;
				z += (sectionLength * offsetZ * dirZ) / sectionSplits;
			}
		}
		trails.add(builder);
	}
	
	public void createArc(Vec3 target)
	{
		TeslaArcBuilder builder = TeslaArcBuilder.create(arcDuration);
		int segments = RANDOM.nextInt(minLength, maxLength + 1);
		Vec3 origin = originSupplier.get();
		Vec3 worldOrigin = origin.add(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
		Vec3 direction = target.subtract(worldOrigin).normalize();
		double distance = worldOrigin.distanceTo(target);
		double segmentLength = distance / segments;
		for(int i = 0; i < segments; i++)
		{
			Vec3 directPos = direction.scale(segmentLength * i).add(origin);
			Vec3 offset = direction.scale(segmentLength);
			Vec3 tangent = i == segments - 1 ? Vec3.ZERO : randomTangent(direction).scale(0.25);
			double offsetX = offset.x() + tangent.x();
			double offsetY = offset.y() + tangent.y();
			double offsetZ = offset.z() + tangent.z();
			double x = directPos.x();
			double y = directPos.y();
			double z = directPos.z();
			for(int j = 0; j < sectionSplits; j++)
			{
				builder.add(new Vec3(x, y, z));
				x += offsetX / sectionSplits;
				y += offsetY / sectionSplits;
				z += offsetZ / sectionSplits;
			}
		}
		trails.add(builder);
	}
	
	private static Vec3 randomTangent(Vec3 vector)
	{
		var normal = vector.normalize();
		var tangent = normal.cross(new Vec3(-normal.z(), normal.x(), normal.y()));
		var bitangent = normal.cross(tangent);
		var angle = RANDOM.nextDouble(-Math.PI, Math.PI);
		return tangent.scale(Math.sin(angle)).add(bitangent.scale(Math.cos(angle)));
	}
	
	public void tick()
	{
		trails.removeIf((trail) ->
		{
			trail.tick();
			return trail.points().isEmpty();
		});
		
		if(constantArcs && trails.size() < arcs)
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
