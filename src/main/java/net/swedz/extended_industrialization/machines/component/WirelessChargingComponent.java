package net.swedz.extended_industrialization.machines.component;

import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.util.Simulation;
import dev.technici4n.grandpower.api.ILongEnergyStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public final class WirelessChargingComponent implements IComponent.ServerOnly
{
	private final MachineBlockEntity machine;
	
	private final EnergyComponent energy;
	
	private final BiPredicate<MachineBlockEntity, Player> filter;
	private final Supplier<Long>                          maxEuTransfer;
	
	public WirelessChargingComponent(MachineBlockEntity machine, EnergyComponent energy, BiPredicate<MachineBlockEntity, Player> filter, Supplier<Long> maxEuTransfer)
	{
		this.machine = machine;
		this.energy = energy;
		this.filter = filter;
		this.maxEuTransfer = maxEuTransfer;
	}
	
	private UUID getPlacerId()
	{
		return machine.placedBy.placerId;
	}
	
	public Optional<Player> getPlayer()
	{
		ServerPlayer player = machine.getLevel().getServer().getPlayerList().getPlayer(this.getPlacerId());
		return player == null || !filter.test(machine, player) ? Optional.empty() : Optional.of(player);
	}
	
	private long charge(Player player, long maxEu)
	{
		Inventory inventory = player.getInventory();
		long eu = 0;
		
		for(ItemStack armor : inventory.armor)
		{
			ILongEnergyStorage energy = armor.getCapability(EnergyApi.ITEM);
			if(energy != null)
			{
				long received = energy.receive(Math.max(0, maxEu - eu), false);
				if(received > 0)
				{
					eu += received;
					if(eu == maxEu)
					{
						return eu;
					}
				}
			}
		}
		
		for(ItemStack item : inventory.items)
		{
			ILongEnergyStorage energy = item.getCapability(EnergyApi.ITEM);
			if(energy != null)
			{
				long received = energy.receive(Math.max(0, maxEu - eu), false);
				if(received > 0)
				{
					eu += received;
					if(eu == maxEu)
					{
						return eu;
					}
				}
			}
		}
		
		return eu;
	}
	
	public void tick()
	{
		Optional<Player> playerOptional = this.getPlayer();
		if(playerOptional.isPresent())
		{
			Player player = playerOptional.get();
			
			long eu = maxEuTransfer.get();
			eu = energy.consumeEu(eu, Simulation.SIMULATE);
			eu = this.charge(player, eu);
			energy.consumeEu(eu, Simulation.ACT);
		}
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
	}
}
