package net.swedz.extended_industrialization.entity.goal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.network.packet.CatHammerPacket;

import java.util.List;

public final class MeowTradeGoal extends Goal
{
	private final Cat cat;
	
	private int ticks;
	
	private boolean hammered;
	private boolean poof2, poof3;
	private boolean turn;
	
	public MeowTradeGoal(Cat cat)
	{
		this.cat = cat;
	}
	
	private boolean isNanoHelmet(ItemStack stack)
	{
		return !stack.isEmpty() &&
			   (stack.is(EIItems.NANO_HELMET.asItem()) || stack.is(EIItems.NANO_QUANTUM_HELMET.asItem())) &&
			   !stack.getOrDefault(EIComponents.MEOW, false);
	}
	
	private List<ItemEntity> findItems()
	{
		return cat.level().getEntitiesOfClass(
				ItemEntity.class,
				cat.getBoundingBox().inflate(1, 1, 1),
				(item) ->
						item.getOwner() != null && item.getOwner().getUUID().equals(cat.getOwnerUUID()) &&
						this.isNanoHelmet(item.getItem())
		);
	}
	
	private Player findOwnerNearby()
	{
		var owner = cat.level().getPlayerByUUID(cat.getOwnerUUID());
		return owner != null && owner.isAlive() && owner.distanceTo(cat) <= 10 ? owner : null;
	}
	
	@Override
	public boolean canUse()
	{
		return !cat.isLying() &&
			   cat.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() &&
			   !this.findItems().isEmpty();
	}
	
	@Override
	public boolean canContinueToUse()
	{
		return this.isNanoHelmet(cat.getItemInHand(InteractionHand.MAIN_HAND)) &&
			   ticks < 5 * 20;
	}
	
	private void enforceSitting()
	{
		if(!cat.isOrderedToSit())
		{
			cat.setOrderedToSit(true);
		}
	}
	
	@Override
	public void start()
	{
		ticks = 0;
		hammered = false;
		poof2 = false;
		poof3 = false;
		turn = false;
		this.enforceSitting();
	}
	
	@Override
	public void tick()
	{
		this.enforceSitting();
		
		if(!hammered && ticks > 1.5 * 20)
		{
			cat.playSound(SoundEvents.ANVIL_USE, 1, 1);
			new CatHammerPacket(cat.getId()).broadcastToClients((ServerLevel) cat.level(), cat.position(), 32);
			hammered = true;
		}
		if(!poof2 && ticks > (1.5 + 0.4) * 20)
		{
			new CatHammerPacket(cat.getId()).broadcastToClients((ServerLevel) cat.level(), cat.position(), 32);
			poof2 = true;
		}
		if(!poof3 && ticks > (1.5 + (0.4 * 2)) * 20)
		{
			new CatHammerPacket(cat.getId()).broadcastToClients((ServerLevel) cat.level(), cat.position(), 32);
			poof3 = true;
		}
		
		var heldItem = cat.getItemInHand(InteractionHand.MAIN_HAND);
		if(heldItem.isEmpty())
		{
			var items = this.findItems();
			if(!items.isEmpty())
			{
				var item = items.getFirst();
				var itemStack = item.getItem().copy();
				cat.take(item, itemStack.getCount());
				cat.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
				item.discard();
				cat.onItemPickup(item);
				cat.playSound(SoundEvents.ITEM_PICKUP, 1, 1);
				cat.playSound(SoundEvents.CAT_PURR, 1, 1);
			}
		}
		else
		{
			var owner = this.findOwnerNearby();
			if(owner != null)
			{
				if(ticks < 3 * 20)
				{
					double dx = owner.getX() - cat.getX();
					double dz = owner.getZ() - cat.getZ();
					double x = cat.getX() - dx;
					double y = cat.getY() - 1;
					double z = cat.getZ() - dz;
					cat.getLookControl().setLookAt(x, y, z);
				}
				else
				{
					if(!turn)
					{
						cat.playSound(SoundEvents.CAT_PURR, 1, 1);
						turn = true;
					}
					cat.getLookControl().setLookAt(owner);
				}
			}
		}
		
		ticks += 2;
	}
	
	@Override
	public void stop()
	{
		this.drop();
		ticks = 0;
	}
	
	private void drop()
	{
		var stack = cat.getItemInHand(InteractionHand.MAIN_HAND);
		if(!stack.isEmpty() && this.isNanoHelmet(stack))
		{
			cat.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
			
			var owner = this.findOwnerNearby();
			Vec3 pos;
			if(owner == null)
			{
				var randomPos = LandRandomPos.getPos(cat, 4, 2);
				pos = randomPos == null ? cat.position() : randomPos;
			}
			else
			{
				pos = owner.position();
				cat.getLookControl().setLookAt(owner);
			}
			stack.set(EIComponents.MEOW, true);
			BehaviorUtils.throwItem(cat, stack, pos.add(0, 2, 0));
			
			cat.playSound(SoundEvents.CAT_PURREOW, 1, 1);
		}
	}
}
