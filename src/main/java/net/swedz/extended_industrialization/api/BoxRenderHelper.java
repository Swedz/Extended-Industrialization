package net.swedz.extended_industrialization.api;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

import java.util.function.Predicate;

public final class BoxRenderHelper
{
	public static void renderFace(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								  float x1, float y1, float z1,
								  float x2, float y2, float z2,
								  Direction direction,
								  float textureWidth, float textureHeight, float textureScale,
								  float red, float green, float blue, float alpha)
	{
		float u1, v1, u2, v2;
		switch (direction)
		{
			case DOWN, UP ->
			{
				u1 = (x1 / textureWidth) / textureScale;
				v1 = (z1 / textureHeight) / textureScale;
				u2 = (x2 / textureWidth) / textureScale;
				v2 = (z2 / textureHeight) / textureScale;
			}
			case NORTH, SOUTH ->
			{
				u1 = (x1 / textureWidth) / textureScale;
				v1 = (y1 / textureHeight) / textureScale;
				u2 = (x2 / textureWidth) / textureScale;
				v2 = (y2 / textureHeight) / textureScale;
			}
			case WEST, EAST ->
			{
				u1 = (z1 / textureWidth) / textureScale;
				v1 = (y1 / textureHeight) / textureScale;
				u2 = (z2 / textureWidth) / textureScale;
				v2 = (y2 / textureHeight) / textureScale;
			}
			default -> throw new IllegalStateException("Unexpected value: " + direction);
		}
		switch (direction)
		{
			case DOWN ->
			{
				addVertex(matrices, light, overlay, vc, x1, y1, z1, red, green, blue, alpha, u1, v1);
				addVertex(matrices, light, overlay, vc, x2, y1, z1, red, green, blue, alpha, u2, v1);
				addVertex(matrices, light, overlay, vc, x2, y1, z2, red, green, blue, alpha, u2, v2);
				addVertex(matrices, light, overlay, vc, x1, y1, z2, red, green, blue, alpha, u1, v2);
			}
			case UP ->
			{
				addVertex(matrices, light, overlay, vc, x1, y2, z1, red, green, blue, alpha, u1, v1);
				addVertex(matrices, light, overlay, vc, x2, y2, z1, red, green, blue, alpha, u2, v1);
				addVertex(matrices, light, overlay, vc, x2, y2, z2, red, green, blue, alpha, u2, v2);
				addVertex(matrices, light, overlay, vc, x1, y2, z2, red, green, blue, alpha, u1, v2);
			}
			case NORTH ->
			{
				addVertex(matrices, light, overlay, vc, x1, y1, z1, red, green, blue, alpha, u1, v1);
				addVertex(matrices, light, overlay, vc, x2, y1, z1, red, green, blue, alpha, u2, v1);
				addVertex(matrices, light, overlay, vc, x2, y2, z1, red, green, blue, alpha, u2, v2);
				addVertex(matrices, light, overlay, vc, x1, y2, z1, red, green, blue, alpha, u1, v2);
			}
			case SOUTH ->
			{
				addVertex(matrices, light, overlay, vc, x1, y1, z2, red, green, blue, alpha, u1, v1);
				addVertex(matrices, light, overlay, vc, x2, y1, z2, red, green, blue, alpha, u2, v1);
				addVertex(matrices, light, overlay, vc, x2, y2, z2, red, green, blue, alpha, u2, v2);
				addVertex(matrices, light, overlay, vc, x1, y2, z2, red, green, blue, alpha, u1, v2);
			}
			case WEST ->
			{
				addVertex(matrices, light, overlay, vc, x1, y1, z1, red, green, blue, alpha, u1, v1);
				addVertex(matrices, light, overlay, vc, x1, y1, z2, red, green, blue, alpha, u2, v1);
				addVertex(matrices, light, overlay, vc, x1, y2, z2, red, green, blue, alpha, u2, v2);
				addVertex(matrices, light, overlay, vc, x1, y2, z1, red, green, blue, alpha, u1, v2);
			}
			case EAST ->
			{
				addVertex(matrices, light, overlay, vc, x2, y1, z1, red, green, blue, alpha, u1, v1);
				addVertex(matrices, light, overlay, vc, x2, y1, z2, red, green, blue, alpha, u2, v1);
				addVertex(matrices, light, overlay, vc, x2, y2, z2, red, green, blue, alpha, u2, v2);
				addVertex(matrices, light, overlay, vc, x2, y2, z1, red, green, blue, alpha, u1, v2);
			}
		}
	}
	
	public static void renderBox(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								 float x1, float y1, float z1,
								 float x2, float y2, float z2,
								 Iterable<Direction> directions,
								 float textureWidth, float textureHeight, float textureScale,
								 float red, float green, float blue, float alpha)
	{
		for(Direction direction : directions)
		{
			renderFace(
					matrices, light, overlay, vc,
					x1, y1, z1, x2, y2, z2,
					direction,
					textureWidth, textureHeight, textureScale,
					red, green, blue, alpha
			);
		}
	}
	
	public static void renderBox(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								 float x1, float y1, float z1,
								 float x2, float y2, float z2,
								 Predicate<Direction> directionFilter,
								 float textureWidth, float textureHeight, float textureScale,
								 float red, float green, float blue, float alpha)
	{
		for(Direction direction : Direction.values())
		{
			if(directionFilter.test(direction))
			{
				renderFace(
						matrices, light, overlay, vc,
						x1, y1, z1, x2, y2, z2,
						direction,
						textureWidth, textureHeight, textureScale,
						red, green, blue, alpha
				);
			}
		}
	}
	
	public static void renderBox(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								 float x1, float y1, float z1,
								 float x2, float y2, float z2,
								 float textureWidth, float textureHeight, float textureScale,
								 float red, float green, float blue, float alpha)
	{
		renderBox(
				matrices, light, overlay, vc,
				x1, y1, z1, x2, y2, z2,
				(__) -> true,
				textureWidth, textureHeight, textureScale,
				red, green, blue, alpha
		);
	}
	
	public static void renderFace(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								  AABB box, Direction direction,
								  float textureWidth, float textureHeight, float textureScale,
								  float red, float green, float blue, float alpha)
	{
		renderFace(
				matrices, light, overlay, vc,
				(float) box.minX, (float) box.minY, (float) box.minZ,
				(float) box.maxX, (float) box.maxY, (float) box.maxZ,
				direction,
				textureWidth, textureHeight, textureScale,
				red, green, blue, alpha
		);
	}
	
	public static void renderBox(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								 AABB box, Iterable<Direction> directions,
								 float textureWidth, float textureHeight, float textureScale,
								 float red, float green, float blue, float alpha)
	{
		for(Direction direction : directions)
		{
			renderFace(
					matrices, light, overlay, vc,
					box, direction,
					textureWidth, textureHeight, textureScale,
					red, green, blue, alpha
			);
		}
	}
	
	public static void renderBox(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								 AABB box, Predicate<Direction> directionFilter,
								 float textureWidth, float textureHeight, float textureScale,
								 float red, float green, float blue, float alpha)
	{
		for(Direction direction : Direction.values())
		{
			if(directionFilter.test(direction))
			{
				renderFace(
						matrices, light, overlay, vc,
						box, direction,
						textureWidth, textureHeight, textureScale,
						red, green, blue, alpha
				);
			}
		}
	}
	
	public static void renderBox(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								 AABB box,
								 float textureWidth, float textureHeight, float textureScale,
								 float red, float green, float blue, float alpha)
	{
		renderBox(
				matrices, light, overlay, vc,
				box, (__) -> true,
				textureWidth, textureHeight, textureScale,
				red, green, blue, alpha
		);
	}
	
	private static void addVertex(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								  float x, float y, float z,
								  float red, float green, float blue, float alpha,
								  float u, float v)
	{
		PoseStack.Pose pose = matrices.last();
		vc.addVertex(pose, x, y, z)
				.setColor(red, green, blue, alpha)
				.setLight(light)
				.setNormal(pose, 0, 0, 0)
				.setOverlay(overlay)
				.setUv(u, v);
	}
	
	public static void renderFace(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								  float x1, float y1, float z1,
								  float x2, float y2, float z2,
								  Direction direction,
								  float red, float green, float blue, float alpha)
	{
		switch (direction)
		{
			case DOWN ->
			{
				addVertex(matrices, light, overlay, vc, x1, y1, z1, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x2, y1, z1, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x2, y1, z2, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x1, y1, z2, red, green, blue, alpha);
			}
			case UP ->
			{
				addVertex(matrices, light, overlay, vc, x1, y2, z1, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x2, y2, z1, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x2, y2, z2, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x1, y2, z2, red, green, blue, alpha);
			}
			case NORTH ->
			{
				addVertex(matrices, light, overlay, vc, x1, y1, z1, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x2, y1, z1, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x2, y2, z1, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x1, y2, z1, red, green, blue, alpha);
			}
			case SOUTH ->
			{
				addVertex(matrices, light, overlay, vc, x1, y1, z2, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x2, y1, z2, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x2, y2, z2, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x1, y2, z2, red, green, blue, alpha);
			}
			case WEST ->
			{
				addVertex(matrices, light, overlay, vc, x1, y1, z1, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x1, y1, z2, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x1, y2, z2, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x1, y2, z1, red, green, blue, alpha);
			}
			case EAST ->
			{
				addVertex(matrices, light, overlay, vc, x2, y1, z1, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x2, y1, z2, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x2, y2, z2, red, green, blue, alpha);
				addVertex(matrices, light, overlay, vc, x2, y2, z1, red, green, blue, alpha);
			}
		}
	}
	
	public static void renderBox(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								 float x1, float y1, float z1,
								 float x2, float y2, float z2,
								 Iterable<Direction> directions,
								 float red, float green, float blue, float alpha)
	{
		for(Direction direction : directions)
		{
			renderFace(
					matrices, light, overlay, vc,
					x1, y1, z1, x2, y2, z2,
					direction,
					red, green, blue, alpha
			);
		}
	}
	
	public static void renderBox(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								 float x1, float y1, float z1,
								 float x2, float y2, float z2,
								 Predicate<Direction> directionFilter,
								 float red, float green, float blue, float alpha)
	{
		for(Direction direction : Direction.values())
		{
			if(directionFilter.test(direction))
			{
				renderFace(
						matrices, light, overlay, vc,
						x1, y1, z1, x2, y2, z2,
						direction,
						red, green, blue, alpha
				);
			}
		}
	}
	
	public static void renderBox(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								 float x1, float y1, float z1,
								 float x2, float y2, float z2,
								 float red, float green, float blue, float alpha)
	{
		renderBox(
				matrices, light, overlay, vc,
				x1, y1, z1, x2, y2, z2,
				(__) -> true,
				red, green, blue, alpha
		);
	}
	
	public static void renderFace(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								  AABB box, Direction direction,
								  float red, float green, float blue, float alpha)
	{
		renderFace(
				matrices, light, overlay, vc,
				(float) box.minX, (float) box.minY, (float) box.minZ,
				(float) box.maxX, (float) box.maxY, (float) box.maxZ,
				direction,
				red, green, blue, alpha
		);
	}
	
	public static void renderBox(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								 AABB box, Iterable<Direction> directions,
								 float red, float green, float blue, float alpha)
	{
		for(Direction direction : directions)
		{
			renderFace(
					matrices, light, overlay, vc,
					box, direction,
					red, green, blue, alpha
			);
		}
	}
	
	public static void renderBox(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								 AABB box, Predicate<Direction> directionFilter,
								 float red, float green, float blue, float alpha)
	{
		for(Direction direction : Direction.values())
		{
			if(directionFilter.test(direction))
			{
				renderFace(
						matrices, light, overlay, vc,
						box, direction,
						red, green, blue, alpha
				);
			}
		}
	}
	
	public static void renderBox(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								 AABB box,
								 float red, float green, float blue, float alpha)
	{
		renderBox(
				matrices, light, overlay, vc,
				box, (__) -> true,
				red, green, blue, alpha
		);
	}
	
	private static void addVertex(PoseStack matrices, int light, int overlay, VertexConsumer vc,
								  float x, float y, float z,
								  float red, float green, float blue, float alpha)
	{
		PoseStack.Pose pose = matrices.last();
		vc.addVertex(pose, x, y, z)
				.setColor(red, green, blue, alpha)
				.setLight(light)
				.setNormal(pose, 0, 0, 0)
				.setOverlay(overlay);
	}
}
