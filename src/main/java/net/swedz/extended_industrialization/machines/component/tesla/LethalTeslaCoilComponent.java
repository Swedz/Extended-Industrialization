package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.util.Simulation;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIDamageTypes;
import net.swedz.extended_industrialization.EISounds;
import net.swedz.extended_industrialization.network.packet.EntitiesElectrocutedPacket;

import java.util.List;
import java.util.function.Supplier;

public final class LethalTeslaCoilComponent implements IComponent, TeslaBuzzing
{
	private final MachineBlockEntity machine;
	private final Supplier<Float>    damageAmount;
	private final EnergyComponent    energy;
	private final Supplier<Long>     energyCost;
	
	private final Supplier<Integer> range;
	private final Supplier<Long>    damageInterval;
	
	private int entityCount;
	
	public LethalTeslaCoilComponent(
			MachineBlockEntity machine, Supplier<Float> damageAmount, EnergyComponent energy, Supplier<Long> energyCost,
			Supplier<Integer> range, Supplier<Long> damageInterval
	)
	{
		this.machine = machine;
		this.damageAmount = damageAmount;
		this.energy = energy;
		this.energyCost = energyCost;
		this.range = range;
		this.damageInterval = damageInterval;
	}
	
	private AABB getDamageArea()
	{
		int range = EI.config().lethalTeslaCoil().range();
		var center = machine.getBlockPos().getCenter();
		return new AABB(
				center.subtract(range, range, range).subtract(0.5, 0.5, 0.5),
				center.add(range, range, range).add(0.5, 0.5, 0.5)
		);
	}
	
	private List<Entity> getEntitiesInDamageArea()
	{
		return machine.getLevel().getEntities(
				(Entity) null,
				this.getDamageArea(),
				(entity) -> entity.isAlive() && entity instanceof LivingEntity && !(entity instanceof Player)
		);
	}
	
	private long tick;
	
	public boolean tick()
	{
		var level = machine.getLevel();
		var worldPosition = machine.getBlockPos();
		
		boolean active = false;
		
		float damage = damageAmount.get();
		if(damage > 0)
		{
			long energyCost = this.energyCost.get();
			active = energy.consumeEu(energyCost, Simulation.SIMULATE) == energyCost;
			
			var entities = this.getEntitiesInDamageArea();
			int originalEntityCount = entityCount;
			entityCount = entities.size();
			if(entityCount != originalEntityCount)
			{
				machine.sync(false);
			}
			
			if(active && tick++ % damageInterval.get() == 0)
			{
				energy.consumeEu(energyCost, Simulation.ACT);
				var source = EIDamageTypes.tesla(level, worldPosition.getCenter());
				if(!entities.isEmpty())
				{
					var entityIds = new IntArrayList();
					for(var entity : entities)
					{
						entity.hurt(source, damage);
						entityIds.add(entity.getId());
					}
					new EntitiesElectrocutedPacket(worldPosition, entityIds).broadcastToClients((ServerLevel) level, worldPosition, 32);
				}
			}
		}
		
		return active;
	}
	
	public void reset()
	{
		entityCount = 0;
	}
	
	private boolean buzzing;
	
	@Override
	public MachineBlockEntity getBuzzingMachine()
	{
		return machine;
	}
	
	@Override
	public SoundEvent getBuzzingSound()
	{
		return EISounds.TESLA_COIL_LOOP.get();
	}
	
	@Override
	public SoundSource getBuzzingSoundSource()
	{
		return SoundSource.BLOCKS;
	}
	
	@Override
	public boolean isBuzzing()
	{
		return buzzing;
	}
	
	@Override
	public void setBuzzing(boolean buzzing)
	{
		this.buzzing = buzzing;
	}
	
	@Override
	public boolean shouldBuzz()
	{
		return entityCount > 0;
	}
	
	@Override
	public float getBuzzingPitch()
	{
		return 1;
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
	}
	
	@Override
	public void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.putInt("damaging_entities", entityCount);
	}
	
	@Override
	public void readClientNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		entityCount = tag.getInt("damaging_entities");
	}
}
