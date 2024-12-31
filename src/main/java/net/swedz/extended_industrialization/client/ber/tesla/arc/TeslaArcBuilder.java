package net.swedz.extended_industrialization.client.ber.tesla.arc;

import com.google.common.collect.Lists;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Taken from Lodestone's <code>TrailPointBuilder</code>
 * <br><br>
 * <b>Source:</b> <a href="https://github.com/LodestarMC/Lodestone/">https://github.com/LodestarMC/Lodestone/</a>
 */
public final class TeslaArcBuilder
{
	private final List<TeslaArcPoint> points = Lists.newArrayList();
	
	public final Supplier<Integer> length;
	
	private TeslaArcBuilder(Supplier<Integer> length)
	{
		this.length = length;
	}
	
	public static TeslaArcBuilder create(int length)
	{
		return create(() -> length);
	}
	
	public static TeslaArcBuilder create(Supplier<Integer> length)
	{
		return new TeslaArcBuilder(length);
	}
	
	public List<TeslaArcPoint> points()
	{
		return points;
	}
	
	public List<TeslaArcPoint> points(float lerp)
	{
		List<TeslaArcPoint> lerpedTeslaArcPoints = Lists.newArrayList();
		final int size = points.size();
		if(size > 1)
		{
			for(int i = 0; i < size - 2; i++)
			{
				lerpedTeslaArcPoints.add(points.get(i).lerp(points.get(i + 1), lerp));
			}
		}
		return lerpedTeslaArcPoints;
	}
	
	public TeslaArcBuilder add(Vec3 point)
	{
		return this.add(new TeslaArcPoint(point, 0));
	}
	
	public TeslaArcBuilder add(TeslaArcPoint point)
	{
		points.add(point);
		return this;
	}
	
	public TeslaArcBuilder tick()
	{
		int trailLength = this.length.get();
		points.forEach(TeslaArcPoint::tick);
		points.removeIf((point) -> point.timeActive() > trailLength);
		return this;
	}
	
	public List<Vector4f> build()
	{
		return points.stream().map(TeslaArcPoint::matrixPosition).collect(Collectors.toList());
	}
}
