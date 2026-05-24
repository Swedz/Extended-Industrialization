package net.swedz.extended_industrialization.client.ber.tesla.behavior;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.client.ber.tesla.arc.TeslaArcBuilder;
import net.swedz.extended_industrialization.client.model.tesla.TeslaBakedModel;
import net.swedz.tesseract.api.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public final class TeslaArcInstance
{
	public static final Random RANDOM = new Random();
	
	private final Vec3                      worldPosition;
	private final Supplier<Direction>       facingDirection;
	private final Supplier<TeslaBakedModel> tesla;
	
	private final List<TeslaArcBuilder> trails = Lists.newArrayList();
	
	public TeslaArcInstance(BlockPos worldPosition, Supplier<Direction> facingDirection, Supplier<TeslaBakedModel> tesla)
	{
		Assert.notNull(worldPosition);
		Assert.notNull(facingDirection);
		Assert.notNull(tesla);
		
		this.worldPosition = worldPosition.getCenter();
		this.facingDirection = facingDirection;
		this.tesla = tesla;
	}
	
	private TeslaBakedModel tesla()
	{
		return tesla.get();
	}
	
	public Vec3 closestOrigin(Vec3 target)
	{
		var tesla = this.tesla();
		if(tesla == null)
		{
			return null;
		}
		Vec3 closest = null;
		double closestDistance = 0;
		for(var origin : tesla.arcs().worldOrigins(worldPosition, facingDirection.get()))
		{
			double distance = origin.distanceTo(target);
			if(closest == null || distance < closestDistance)
			{
				closest = origin;
				closestDistance = distance;
			}
		}
		return closest;
	}
	
	public List<TeslaArcBuilder> getTrails()
	{
		return Collections.unmodifiableList(trails);
	}
	
	private void createArc(Vec3 worldOrigin, Vec3 target, int duration, int segments, int segmentSplits)
	{
		if(worldOrigin == null)
		{
			return;
		}
		var tesla = this.tesla();
		if(tesla == null)
		{
			return;
		}
		var builder = TeslaArcBuilder.create(duration);
		var origin = worldOrigin.subtract(worldPosition).add(0.5, 0.5, 0.5);
		var direction = target.subtract(worldOrigin).normalize();
		double distance = worldOrigin.distanceTo(target);
		double segmentLength = distance / segments;
		var offset = direction.scale(segmentLength);
		for(int i = 0; i < segments; i++)
		{
			var direct = direction.scale(segmentLength * i).add(origin);
			var tangent = i == segments - 1 ? Vec3.ZERO : randomTangent(RANDOM, direction).scale(tesla.arcs().randomVariance(RANDOM));
			tangent = tangent.add(offset);
			double x = direct.x();
			double y = direct.y();
			double z = direct.z();
			for(int j = 0; j < segmentSplits; j++)
			{
				builder.add(new Vec3(x, y, z));
				x += tangent.x() / segmentSplits;
				y += tangent.y() / segmentSplits;
				z += tangent.z() / segmentSplits;
			}
		}
		trails.add(builder);
	}
	
	public void createArc(Vec3 origin, Vec3 target)
	{
		var tesla = this.tesla();
		if(tesla == null)
		{
			return;
		}
		var arcs = tesla.arcs();
		this.createArc(origin, target, arcs.duration(), RANDOM.nextInt(arcs.minSegments(), arcs.maxSegments() + 1), arcs.segmentSplits());
	}
	
	private static Vec3 randomTangent(Random random, Vec3 vector)
	{
		var normal = vector.normalize();
		var tangent = normal.cross(new Vec3(-normal.z(), normal.x(), normal.y()));
		var bitangent = normal.cross(tangent);
		var angle = random.nextDouble(-Math.PI, Math.PI);
		return tangent.scale(Math.sin(angle)).add(bitangent.scale(Math.cos(angle)));
	}
	
	public void tick()
	{
		trails.removeIf((trail) ->
		{
			trail.tick();
			return trail.points().isEmpty();
		});
		
		var tesla = this.tesla();
		if(tesla != null)
		{
			var arcs = tesla.arcs();
			if(arcs != null)
			{
				if(arcs.attachesToNearbyEntities())
				{
					var level = Minecraft.getInstance().level;
					var box = arcs.worldNearbyEntitiesBounds(worldPosition, facingDirection.get());
					var entities = level.getEntities(
							(Entity) null,
							box,
							(entity) -> entity.isAlive() && entity instanceof LivingEntity && !(entity instanceof Player)
					);
					for(var entity : entities)
					{
						Vec3 target = entity.getBoundingBox().getCenter();
						this.createArc(this.closestOrigin(target), target);
					}
				}
				
				if(arcs.hasRandomBounds() && trails.size() < arcs.count())
				{
					int maxCreate = arcs.count() - trails.size();
					int create = Math.max(Math.min(arcs.count() / 2, maxCreate), 1);
					for(int i = 0; i < create; i++)
					{
						Vec3 target = tesla.arcs().worldRandomPointInBounds(RANDOM, worldPosition, facingDirection.get());
						this.createArc(this.closestOrigin(target), target);
					}
				}
			}
		}
	}
}
