package net.swedz.extended_industrialization.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.item.teslalinkable.TeslaHandheldReceiverItem;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaReceiverHolder;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.PlayerTeslaReceiver;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiver;
import net.swedz.extended_industrialization.proxy.modslot.EIModSlotProxy;
import net.swedz.tesseract.neoforge.api.WorldPos;
import net.swedz.tesseract.neoforge.proxy.Proxies;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(Player.class)
@Implements(@Interface(iface = TeslaReceiverHolder.class, prefix = "teslaNetwork$"))
public abstract class TeslaNetworkReceiversPlayerMixin extends LivingEntity
{
	protected TeslaNetworkReceiversPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level)
	{
		super(entityType, level);
	}
	
	@Unique
	private final Map<WorldPos, PlayerTeslaReceiver> playerReceivers = Maps.newHashMap();
	
	public Collection<TeslaReceiver> teslaNetwork$getTeslaReceivers()
	{
		return Collections.unmodifiableCollection(playerReceivers.values());
	}
	
	@Unique
	private List<ItemStack> getAllItems()
	{
		Player player = (Player) (Object) this;
		
		Inventory inventory = player.getInventory();
		
		List<ItemStack> items = Lists.newArrayList();
		items.addAll(inventory.armor);
		items.addAll(inventory.items);
		items.addAll(inventory.offhand);
		items.addAll(Proxies.get(EIModSlotProxy.class).getContents(player, (stack) -> true));
		
		return items;
	}
	
	@Inject(
			method = "tick",
			at = @At("TAIL")
	)
	private void tick(CallbackInfo callback)
	{
		Player player = (Player) (Object) this;
		
		if(this.level().isClientSide())
		{
			return;
		}
		
		Set<WorldPos> found = Sets.newHashSet();
		for(ItemStack stack : this.getAllItems())
		{
			if(stack.getItem() instanceof TeslaHandheldReceiverItem && stack.has(EIComponents.SELECTED_TESLA_NETWORK))
			{
				found.add(stack.get(EIComponents.SELECTED_TESLA_NETWORK).key());
			}
		}
		
		Set<WorldPos> toRemove = Sets.difference(playerReceivers.keySet(), found);
		for(WorldPos key : toRemove)
		{
			PlayerTeslaReceiver receiver = playerReceivers.remove(key);
			receiver.getNetwork().remove(receiver);
		}
		
		Set<WorldPos> toAdd = Sets.difference(found, playerReceivers.keySet());
		for(WorldPos key : toAdd)
		{
			PlayerTeslaReceiver receiver = new PlayerTeslaReceiver(player, key);
			receiver.getNetwork().add(receiver);
			playerReceivers.put(key, receiver);
		}
	}
	
	@Inject(
			method = "remove",
			at = @At("TAIL")
	)
	private void remove(Entity.RemovalReason reason,
						CallbackInfo callback)
	{
		if(this.level().isClientSide())
		{
			return;
		}
		
		playerReceivers.forEach((key, receiver) -> receiver.getNetwork().remove(receiver));
		playerReceivers.clear();
	}
}
