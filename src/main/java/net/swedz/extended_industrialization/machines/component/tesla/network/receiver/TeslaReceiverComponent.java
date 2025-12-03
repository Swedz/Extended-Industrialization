package net.swedz.extended_industrialization.machines.component.tesla.network.receiver;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineComponent;
import com.google.common.collect.Lists;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaNetwork;
import net.swedz.tesseract.neoforge.api.WorldPos;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.configurationpanel.ConfigurationPanelBuilder;
import net.swedz.tesseract.neoforge.proxy.Proxies;
import net.swedz.tesseract.neoforge.proxy.builtin.TesseractProxy;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class TeslaReceiverComponent implements MachineComponent, TeslaReceiver
{
	private static final int PRIORITY_RANGE = 16;
	
	private final MachineBlockEntity machine;
	private final MIEnergyStorage insertable;
	private final Supplier<Boolean> canOperate;
	private final Supplier<CableTier> cableTier;
	
	private Optional<WorldPos> networkKey = Optional.empty();
	
	private int priority = 0;
	
	public TeslaReceiverComponent(MachineBlockEntity machine, MIEnergyStorage energyInsertable,
								  Supplier<Boolean> canOperate, Supplier<CableTier> cableTier)
	{
		this.machine = machine;
		this.insertable = energyInsertable;
		this.canOperate = canOperate;
		this.cableTier = cableTier;
	}
	
	@Override
	public int getPriority()
	{
		return priority;
	}
	
	@Override
	public TeslaReceiverState checkReceiveFrom(TeslaNetwork network)
	{
		if(this.getCableTier() != network.getCableTier())
		{
			return TeslaReceiverState.MISMATCHING_VOLTAGE;
		}
		else if(machine.hasLevel())
		{
			return TeslaReceiver.super.checkReceiveFrom(network);
		}
		else
		{
			return TeslaReceiverState.UNDEFINED;
		}
	}
	
	@Override
	public boolean hasNetwork()
	{
		return networkKey.isPresent();
	}
	
	@Override
	public WorldPos getNetworkKey()
	{
		return networkKey.orElseThrow();
	}
	
	@Override
	public void setNetwork(WorldPos key)
	{
		if(!Proxies.get(TesseractProxy.class).hasServer())
		{
			throw new IllegalStateException("Cannot set network of a receiver from the client");
		}
		
		this.removeFromNetwork();
		
		networkKey = Optional.ofNullable(key);
		
		this.addToNetwork();
	}
	
	@Override
	public WorldPos getPosition()
	{
		return new WorldPos(machine.getLevel(), machine.getBlockPos());
	}
	
	@Override
	public WorldPos getSourcePosition()
	{
		return this.getPosition();
	}
	
	@Override
	public CableTier getCableTier()
	{
		return cableTier.get();
	}
	
	@Override
	public long receiveEnergy(long maxReceive, boolean simulate)
	{
		if(!canOperate.get())
		{
			return 0;
		}
		return insertable.receive(maxReceive, simulate);
	}
	
	@Override
	public long getStoredEnergy()
	{
		return insertable.getAmount();
	}
	
	@Override
	public long getEnergyCapacity()
	{
		return insertable.getCapacity();
	}
	
	public void removeFromNetwork()
	{
		if(this.hasNetwork())
		{
			this.getNetwork().remove(this);
		}
	}
	
	public void addToNetwork()
	{
		if(this.hasNetwork())
		{
			this.getNetwork().add(this);
		}
	}
	
	private List<Component> createPriorityTranslations()
	{
		List<Component> lines = Lists.newArrayList();
		for(int priority = -PRIORITY_RANGE; priority <= PRIORITY_RANGE; priority++)
		{
			lines.add(EI.text().priority(priority));
		}
		return lines;
	}
	
	public void appendSelectionPanel(MachineBlockEntity machine, ConfigurationPanelBuilder builder)
	{
		builder.add(
				this.createPriorityTranslations(), false,
				(delta) ->
				{
					int newPriority = priority + delta;
					if(newPriority >= -PRIORITY_RANGE && newPriority <= PRIORITY_RANGE)
					{
						priority = newPriority;
					}
				},
				() -> priority + PRIORITY_RANGE
		);
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		if(this.hasNetwork())
		{
			var key = this.getNetworkKey();
			WorldPos.CODEC.encodeStart(NbtOps.INSTANCE, key).result().ifPresent((t) -> tag.put("network_key", t));
		}
		
		tag.putInt("priority", priority);
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
		if(tag.contains("network_key", Tag.TAG_COMPOUND))
		{
			var keyTag = tag.getCompound("network_key");
			this.setNetwork(WorldPos.CODEC.parse(NbtOps.INSTANCE, keyTag).result().orElse(null));
		}
		else
		{
			this.setNetwork(null);
		}
		
		priority = Mth.clamp(tag.getInt("priority"), -PRIORITY_RANGE, PRIORITY_RANGE);
	}
	
	@Override
	public void readClientNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		if(tag.contains("network_key", Tag.TAG_COMPOUND))
		{
			var keyTag = tag.getCompound("network_key");
			networkKey = WorldPos.CODEC.parse(NbtOps.INSTANCE, keyTag).result();
		}
		else
		{
			networkKey = Optional.empty();
		}
		
		priority = Mth.clamp(tag.getInt("priority"), -PRIORITY_RANGE, PRIORITY_RANGE);
	}
}
