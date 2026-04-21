package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.util.Simulation;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.network.packet.EntitiesElectrocutedPacket;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.configurationpanel.ConfigurationPanelBuilder;

import java.util.List;
import java.util.function.Supplier;

public final class LethalTeslaCoilComponent implements MachineComponent
{
	private final MachineBlockEntity machine;
	private final Supplier<Float>    damageAmount;
	private final EnergyComponent    energy;
	private final Supplier<Long>     energyCost;
	
	private final Supplier<Integer>      range;
	private final Supplier<Long>         damageInterval;
	private final Supplier<DamageSource> damageSource;
	
	private boolean damagesPlayers;
	
	private int entityCount;
	
	public LethalTeslaCoilComponent(
			MachineBlockEntity machine,
			Supplier<Float> damageAmount,
			EnergyComponent energy,
			Supplier<Long> energyCost,
			Supplier<Integer> range,
			Supplier<Long> damageInterval,
			Supplier<DamageSource> damageSource
	)
	{
		this.machine = machine;
		this.damageAmount = damageAmount;
		this.energy = energy;
		this.energyCost = energyCost;
		this.range = range;
		this.damageInterval = damageInterval;
		this.damageSource = damageSource;
	}
	
	public boolean hasNearbyEntities()
	{
		return entityCount > 0;
	}
	
	private AABB getDamageArea()
	{
		int range = this.range.get();
		var center = machine.getBlockPos().getCenter();
		return new AABB(
				center.subtract(range, range, range).subtract(0.5, 0.5, 0.5),
				center.add(range, range, range).add(0.5, 0.5, 0.5)
		);
	}
	
	private boolean canDamageEntity(Entity entity)
	{
		return entity.isAlive() &&
			   entity instanceof LivingEntity &&
			   (!(entity instanceof Player player) ||
				(damagesPlayers && !player.getUUID().equals(machine.placedBy.placerId)));
	}
	
	private List<Entity> getEntitiesInDamageArea()
	{
		return machine.getLevel().getEntities((Entity) null, this.getDamageArea(), this::canDamageEntity);
	}
	
	public void appendSelectionPanel(MachineBlockEntity machine, ConfigurationPanelBuilder builder)
	{
		builder.add(
				List.of(
						EI.text().teslaLethalCoilDamagesPlayersNo(),
						EI.text().teslaLethalCoilDamagesPlayersYes()
				),
				true,
				(delta) -> damagesPlayers = !damagesPlayers,
				() -> damagesPlayers ? 1 : 0
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
				var source = damageSource.get();
				if(!entities.isEmpty())
				{
					var entityIds = new IntArrayList();
					for(var entity : entities)
					{
						entity.hurt(source, damage);
						entityIds.add(entity.getId());
					}
					new EntitiesElectrocutedPacket(entityIds).broadcastToClients((ServerLevel) level, worldPosition, 32);
				}
			}
		}
		else
		{
			entityCount = 0;
		}
		
		return active;
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.putBoolean("damages_players", damagesPlayers);
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
		damagesPlayers = tag.getBoolean("damages_players");
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
