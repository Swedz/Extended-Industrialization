package net.swedz.extended_industrialization.client.ber.tesla.arc;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector4f;

/**
 * Taken from Lodestone's <code>TrailRenderPoint</code>
 * <br><br>
 * <b>Source:</b> <a href="https://github.com/LodestarMC/Lodestone/">https://github.com/LodestarMC/Lodestone/</a>
 */
public final class TeslaArcRenderPoint
{
	public final float xp, xn, yp, yn, z;
	
	public TeslaArcRenderPoint(float xp, float xn, float yp, float yn, float z)
	{
		this.xp = xp;
		this.xn = xn;
		this.yp = yp;
		this.yn = yn;
		this.z = z;
	}
	
	public TeslaArcRenderPoint(Vector4f pos, Vec2 perp)
	{
		this(pos.x() + perp.x, pos.x() - perp.x, pos.y() + perp.y, pos.y() - perp.y, pos.z());
	}
	
	public void renderStart(VertexConsumer consumer,
							float u0, float v0, float u1, float v1,
							float r, float g, float b, float a)
	{
		consumer.addVertex(xp, yp, z)
				.setUv(u0, v0)
				.setColor(r, g, b, a)
				.setLight(0xF000F0);
		
		consumer.addVertex(xn, yn, z)
				.setUv(u1, v0)
				.setColor(r, g, b, a)
				.setLight(0xF000F0);
	}
	
	public void renderEnd(VertexConsumer consumer,
						  float u0, float v0, float u1, float v1,
						  float r, float g, float b, float a)
	{
		consumer.addVertex(xn, yn, z)
				.setUv(u1, v1)
				.setColor(r, g, b, a)
				.setLight(0xF000F0);
		
		consumer.addVertex(xp, yp, z)
				.setUv(u0, v1)
				.setColor(r, g, b, a)
				.setLight(0xF000F0);
	}
	
	public void renderMid(VertexConsumer consumer,
						  float u0, float v0, float u1, float v1,
						  float r, float g, float b, float a)
	{
		this.renderEnd(consumer, u0, v0, u1, v1, r, g, b, a);
		this.renderStart(consumer, u0, v0, u1, v1, r, g, b, a);
	}
}
