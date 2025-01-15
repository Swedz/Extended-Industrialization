package net.swedz.extended_industrialization.client.ber.tesla.arc;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

/**
 * Taken from Lodestone's <code>TrailPoint</code>
 * <br><br>
 * <b>Source:</b> <a href="https://github.com/LodestarMC/Lodestone/">https://github.com/LodestarMC/Lodestone/</a>
 */
public final class TeslaArcPoint
{
	private final Vec3 position;
	
	private int timeActive;
	
	public TeslaArcPoint(Vec3 position, int timeActive)
	{
		this.position = position;
		this.timeActive = timeActive;
	}
	
	public TeslaArcPoint(Vec3 position)
	{
		this(position, 0);
	}
	
	public Vector4f matrixPosition()
	{
		Vec3 position = this.position();
		return new Vector4f((float) position.x, (float) position.y, (float) position.z, 1.0f);
	}
	
	public Vec3 position()
	{
		return position;
	}
	
	public int timeActive()
	{
		return timeActive;
	}
	
	public TeslaArcPoint lerp(TeslaArcPoint trailPoint, float delta)
	{
		Vec3 position = this.position();
		return new TeslaArcPoint(position.lerp(trailPoint.position, delta), timeActive);
	}
	
	public void tick()
	{
		timeActive++;
	}
}
