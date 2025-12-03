package net.swedz.extended_industrialization.client.ber.tesla.arc;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector4f;

import java.util.List;
import java.util.function.Function;

/**
 * Taken from Lodestone's <code>VFXBuilders#WorldVFXBuilder</code>
 * <br><br>
 * <b>Source:</b> <a href="https://github.com/LodestarMC/Lodestone/">https://github.com/LodestarMC/Lodestone/</a>
 */
public final class TeslaArcRenderer
{
	public static void renderArc(PoseStack matrices, VertexConsumer consumer,
								 List<TeslaArcPoint> points, Function<Float, Float> widthFunction,
								 float r, float g, float b, float a)
	{
		var pose = matrices.last().pose();
		
		if(points.size() < 2)
		{
			return;
		}
		
		List<Vector4f> positions = Lists.newArrayList();
		for(TeslaArcPoint point : points)
		{
			var matrixPosition = point.matrixPosition();
			matrixPosition.mul(pose);
			positions.add(matrixPosition);
		}
		int count = points.size() - 1;
		float increment = 1f / count;
		
		var renderPoints = new TeslaArcRenderPoint[points.size()];
		for(int i = 1; i < count; i++)
		{
			float width = widthFunction.apply(increment * i);
			var previous = positions.get(i - 1);
			var current = positions.get(i);
			var next = positions.get(i + 1);
			renderPoints[i] = new TeslaArcRenderPoint(current, perpendicularPoints(previous, next, width));
		}
		renderPoints[0] = new TeslaArcRenderPoint(
				positions.get(0),
				perpendicularPoints(positions.get(0), positions.get(1), widthFunction.apply(0f))
		);
		renderPoints[count] = new TeslaArcRenderPoint(
				positions.get(count),
				perpendicularPoints(positions.get(count - 1), positions.get(count), widthFunction.apply(1f))
		);
		
		renderPoints[0].renderStart(consumer, 0, 0, 1, Mth.lerp(increment, 0, 1), r, g, b, a);
		for(int i = 1; i < count; i++)
		{
			float current = Mth.lerp(i * increment, 0, 1);
			renderPoints[i].renderMid(consumer, 0, current, 1, current, r, g, b, a);
		}
		renderPoints[count].renderEnd(consumer, 0, Mth.lerp((count) * increment, 0, 1), 1, 1, r, g, b, a);
	}
	
	private static Vec2 perpendicularPoints(Vector4f start, Vector4f end, float width)
	{
		float x = -start.x();
		float y = -start.y();
		if(Math.abs(start.z()) > 0)
		{
			float ratio = end.z() / start.z();
			x = end.x() + x * ratio;
			y = end.y() + y * ratio;
		}
		else if(Math.abs(end.z()) <= 0)
		{
			x += end.x();
			y += end.y();
		}
		if(start.z() > 0)
		{
			x = -x;
			y = -y;
		}
		if(x * x + y * y > 0F)
		{
			float normalize = width * 0.5F / distance(x, y);
			x *= normalize;
			y *= normalize;
		}
		return new Vec2(-y, x);
	}
	
	private static float distSqr(float... a)
	{
		float d = 0.0F;
		for(float f : a)
		{
			d += f * f;
		}
		return d;
	}
	
	private static float distance(float... a)
	{
		return Mth.sqrt(distSqr(a));
	}
}
