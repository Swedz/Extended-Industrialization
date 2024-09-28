package net.swedz.extended_industrialization.machines.component.tesla.receiver;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.api.WorldPos;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;
import net.swedz.extended_industrialization.proxy.modslot.EIModSlotProxy;
import net.swedz.tesseract.neoforge.compat.mi.helper.ChargeInventoryHelper;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.List;

public final class PlayerTeslaReceiver implements TeslaReceiver
{
	private final Player   player;
	private final WorldPos networkKey;
	
	public PlayerTeslaReceiver(Player player, WorldPos networkKey)
	{
		this.player = player;
		this.networkKey = networkKey;
	}
	
	@Override
	public ReceiveCheckResult canReceiveFrom(TeslaNetwork network)
	{
		return ReceiveCheckResult.SUCCESS;
	}
	
	@Override
	public long receiveEnergy(long maxReceive, boolean simulate)
	{
		Inventory inventory = player.getInventory();
		
		List<ItemStack> items = Lists.newArrayList();
		items.addAll(inventory.armor);
		items.addAll(inventory.items);
		items.addAll(inventory.offhand);
		items.addAll(Proxies.get(EIModSlotProxy.class).getContents(player, (stack) -> stack.getCapability(EnergyApi.ITEM) != null));
		
		return ChargeInventoryHelper.charge(items, maxReceive, simulate);
	}
	
	@Override
	public long getStoredEnergy()
	{
		throw new UnsupportedOperationException("Cannot get stored energy for a player receiver");
	}
	
	@Override
	public long getEnergyCapacity()
	{
		throw new UnsupportedOperationException("Cannot get energy capacity for a player receiver");
	}
	
	@Override
	public boolean hasNetwork()
	{
		return true;
	}
	
	@Override
	public WorldPos getNetworkKey()
	{
		return networkKey;
	}
	
	@Override
	public void setNetwork(WorldPos key)
	{
		throw new UnsupportedOperationException("Cannot set network for a player receiver");
	}
	
	@Override
	public WorldPos getPosition()
	{
		return new WorldPos(player.level(), player.blockPosition());
	}
	
	@Override
	public CableTier getCableTier()
	{
		throw new UnsupportedOperationException("Cannot get cable tier for a player receiver");
	}
}
