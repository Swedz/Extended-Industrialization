package net.swedz.extended_industrialization.entity;

import com.google.common.collect.Lists;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.swedz.extended_industrialization.EIDamageTypes;
import net.swedz.extended_industrialization.EIEntities;
import net.swedz.tesseract.neoforge.api.tuple.Pair;

import java.util.List;
import java.util.function.Predicate;

public final class NanoSwipeEntity extends Projectile
{
	private static final EntityDataAccessor<Integer> DATA_COLOR   = SynchedEntityData.defineId(NanoSwipeEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> DATA_RAINBOW = SynchedEntityData.defineId(NanoSwipeEntity.class, EntityDataSerializers.BOOLEAN);
	
	private long ticks;
	
	private int     color;
	private boolean rainbow;
	private float   power;
	private boolean beheading;
	
	public NanoSwipeEntity(EntityType<? extends NanoSwipeEntity> type, Level level)
	{
		super(type, level);
	}
	
	public NanoSwipeEntity(Level level, LivingEntity owner, Vec3 movement,
						   int color, boolean rainbow,
						   float power, boolean beheading)
	{
		super(EIEntities.NANO_SWIPE.get(), level);
		
		this.setOwner(owner);
		this.setDeltaMovement(movement.normalize().multiply(3, 3, 3));
		
		this.setColor(color);
		this.setRainbow(rainbow);
		this.setPower(power);
		this.setBeheading(beheading);
	}
	
	public int getColor()
	{
		return color;
	}
	
	public void setColor(int color)
	{
		this.color = color;
		
		entityData.set(DATA_COLOR, color);
	}
	
	public boolean isRainbow()
	{
		return rainbow;
	}
	
	public void setRainbow(boolean rainbow)
	{
		this.rainbow = rainbow;
		
		entityData.set(DATA_RAINBOW, rainbow);
	}
	
	public float getPower()
	{
		return power;
	}
	
	public void setPower(float power)
	{
		this.power = Math.max(0, power);
	}
	
	public boolean isBeheading()
	{
		return beheading;
	}
	
	public void setBeheading(boolean beheading)
	{
		this.beheading = beheading;
	}
	
	@Override
	public boolean isOnFire()
	{
		return false;
	}
	
	private static Pair<BlockHitResult, List<Entity>> getHitResult(Projectile projectile, ClipContext.Block context, Predicate<Entity> filter, float margin)
	{
		Vec3 movement = projectile.getDeltaMovement();
		Vec3 start = projectile.position();
		Vec3 end = start.add(movement);
		return new Pair<>(
				projectile.level().clip(new ClipContext(start, end, context, ClipContext.Fluid.ANY, projectile)),
				getEntityHitResult(projectile, start, end, projectile.getBoundingBox().expandTowards(movement).inflate(1), filter, margin)
		);
	}
	
	private static List<Entity> getEntityHitResult(Entity projectile, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> filter, float inflationAmount)
	{
		List<Entity> entities = Lists.newArrayList();
		for(Entity entity : projectile.level().getEntities(projectile, boundingBox, filter))
		{
			AABB entityBounds = entity.getBoundingBox().inflate(inflationAmount);
			if(entityBounds.clip(startVec, endVec).isPresent())
			{
				entities.add(entity);
			}
		}
		return entities;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick()
	{
		super.tick();
		
		var level = this.level();
		
		if(ticks >= 10 * 20)
		{
			this.discard();
			level.playSound(null, this.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.25f, 2);
			return;
		}
		
		Entity owner = this.getOwner();
		if(level.isClientSide() || (owner == null || !owner.isRemoved()) && level.hasChunkAt(this.blockPosition()))
		{
			var hitResult = getHitResult(this, ClipContext.Block.COLLIDER, this::canHitEntity, 1);
			var blockHitResult = hitResult.a();
			if(blockHitResult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, blockHitResult))
			{
				this.hitTargetOrDeflectSelf(blockHitResult);
			}
			for(var hitEntity : hitResult.b())
			{
				this.hitTargetOrDeflectSelf(new EntityHitResult(hitEntity));
			}
			
			this.checkInsideBlocks();
			
			Vec3 movement = this.getDeltaMovement();
			double x = this.getX() + movement.x;
			double y = this.getY() + movement.y;
			double z = this.getZ() + movement.z;
			this.setPos(x, y, z);
		}
		else
		{
			this.discard();
			level.playSound(null, this.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.25f, 2);
		}
		
		ticks++;
	}
	
	@Override
	protected void onHitEntity(EntityHitResult result)
	{
		super.onHitEntity(result);
		
		if(this.level() instanceof ServerLevel level)
		{
			var hitEntity = result.getEntity();
			
			level.sendParticles(ParticleTypes.LARGE_SMOKE, hitEntity.getX(), hitEntity.getY(), hitEntity.getZ(), 10, 0.1, 0.1, 0.1, 0.1);
			
			if(this.getOwner() instanceof LivingEntity owner)
			{
				var source = EIDamageTypes.nanoSwipe(level, owner, beheading);
				hitEntity.hurt(source, power);
				if(hitEntity.isAlive())
				{
					EnchantmentHelper.doPostAttackEffects(level, hitEntity, source);
				}
			}
		}
	}
	
	@Override
	protected void onHitBlock(BlockHitResult result)
	{
		super.onHitBlock(result);
		
		if(this.level() instanceof ServerLevel level)
		{
			this.discard();
			
			var hitPos = result.getBlockPos().getCenter().relative(result.getDirection(), 0.6);
			level.sendParticles(ParticleTypes.LARGE_SMOKE, hitPos.x(), hitPos.y(), hitPos.z(), 10, 0.1, 0.1, 0.1, 0.1);
			
			level.playSound(null, this.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.25f, 2);
		}
	}
	
	@Override
	public void onRemovedFromLevel()
	{
		super.onRemovedFromLevel();
		
	}
	
	@Override
	public boolean hurt(DamageSource source, float amount)
	{
		return false;
	}
	
	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity)
	{
		int data = (color & 0xFFFFFF) |
				   ((rainbow ? 1 : 0) << 24);
		return new ClientboundAddEntityPacket(this, serverEntity, data);
	}
	
	@Override
	public void recreateFromPacket(ClientboundAddEntityPacket packet)
	{
		super.recreateFromPacket(packet);
		
		int data = packet.getData();
		
		this.setColor(data & 0xFFFFFF);
		this.setRainbow(((data >> 24) & 0x1) != 0);
	}
	
	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
		builder.define(DATA_COLOR, color);
		builder.define(DATA_RAINBOW, rainbow);
	}
	
	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key)
	{
		if(key.equals(DATA_COLOR))
		{
			color = entityData.get(DATA_COLOR);
		}
		else if(key.equals(DATA_RAINBOW))
		{
			rainbow = entityData.get(DATA_RAINBOW);
		}
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag tag)
	{
		super.addAdditionalSaveData(tag);
		
		tag.putLong("Ticks", ticks);
		tag.putInt("Color", color);
		tag.putBoolean("Rainbow", rainbow);
		tag.putFloat("Power", power);
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag tag)
	{
		super.readAdditionalSaveData(tag);
		
		ticks = tag.getLong("Ticks");
		this.setColor(tag.getInt("Color"));
		this.setRainbow(tag.getBoolean("Rainbow"));
		this.setPower(tag.getFloat("Power"));
	}
}
