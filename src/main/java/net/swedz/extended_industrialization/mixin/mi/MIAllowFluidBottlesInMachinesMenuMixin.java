package net.swedz.extended_industrialization.mixin.mi;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import com.google.common.primitives.Ints;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Mixin(ConfigurableFluidStack.ConfigurableFluidSlot.class)
public class MIAllowFluidBottlesInMachinesMenuMixin
{
	@Unique
	private static final int CAPACITY = 250;
	
	@Unique
	private static final Map<Fluid, ItemStack> BOTTLE_ITEMS = Map.of(
			Fluids.WATER, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER),
			EIFluids.HONEY.asFluid(), new ItemStack(Items.HONEY_BOTTLE)
	);
	
	@Unique
	private FluidTank bottleTank(ItemStack item)
	{
		FluidTank tank = new FluidTank(CAPACITY, (v) -> BOTTLE_ITEMS.containsKey(v.getFluid()));
		if(!item.is(Items.GLASS_BOTTLE))
		{
			Optional<Fluid> optionalFluid = BOTTLE_ITEMS.entrySet().stream()
					.filter((entry) -> ItemStack.isSameItemSameTags(item, entry.getValue()))
					.map(Map.Entry::getKey)
					.findFirst();
			optionalFluid.ifPresent((fluid) -> tank.setFluid(new FluidStack(fluid, tank.getCapacity())));
		}
		return tank;
	}
	
	@Unique
	private FluidTank slotTank()
	{
		ConfigurableFluidStack.ConfigurableFluidSlot self = (ConfigurableFluidStack.ConfigurableFluidSlot) (Object) this;
		ConfigurableFluidStack configurableFluidStack = self.getConfStack();
		
		FluidTank tank = new FluidTank(Ints.saturatedCast(configurableFluidStack.getCapacity()), (v) -> self.canInsertFluid(FluidVariant.of(v)));
		tank.setFluid(configurableFluidStack.getVariant().toStack(Ints.saturatedCast(configurableFluidStack.getAmount())));
		
		return tank;
	}
	
	@Unique
	private boolean transfer(FluidTank destination, FluidTank source, FluidTank slotTank, ItemStack item, SlotAccess slot, Player player, IItemHandler inventory, ItemStack resultItem)
	{
		FluidStack transfer = FluidUtil.tryFluidTransfer(destination, source, Integer.MAX_VALUE, false);
		if(transfer.getAmount() == CAPACITY)
		{
			if(player != null && player.getAbilities().instabuild)
			{
				FluidUtil.tryFluidTransfer(destination, source, Integer.MAX_VALUE, true);
				
				this.completed(transfer, slotTank, player);
				
				return true;
			}
			else if(item.getCount() == 1)
			{
				FluidUtil.tryFluidTransfer(destination, source, Integer.MAX_VALUE, true);
				
				slot.set(resultItem.copy());
				
				this.completed(transfer, slotTank, player);
				
				return true;
			}
			else
			{
				ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, resultItem.copy(), true);
				if(remainder.isEmpty() || player != null)
				{
					FluidUtil.tryFluidTransfer(destination, source, Integer.MAX_VALUE, true);
					
					remainder = ItemHandlerHelper.insertItemStacked(inventory, resultItem.copy(), false);
					if(!remainder.isEmpty() && player != null)
					{
						ItemHandlerHelper.giveItemToPlayer(player, remainder);
					}
					
					ItemStack result = item.copy();
					result.shrink(1);
					slot.set(result);
					
					this.completed(transfer, slotTank, player);
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Unique
	private void completed(FluidStack transfer, FluidTank content, Player player)
	{
		ConfigurableFluidStack configurableFluidStack = ((ConfigurableFluidStack.ConfigurableFluidSlot) (Object) this).getConfStack();
		
		configurableFluidStack.setKey(FluidVariant.of(content.getFluid()));
		configurableFluidStack.setAmount(content.getFluidAmount());
		
		if(player != null)
		{
			SoundEvent soundevent = transfer.getFluidType().getSound(transfer, SoundActions.BUCKET_FILL);
			if(soundevent != null)
			{
				player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), soundevent, SoundSource.BLOCKS, 1f, 1f);
			}
		}
	}
	
	@Unique
	private boolean fillGlassBottle(ItemStack item, SlotAccess slot, Player player, IItemHandler inventory)
	{
		FluidTank destination = this.bottleTank(item);
		if(!destination.isEmpty())
		{
			return false;
		}
		FluidTank source = this.slotTank();
		Fluid fluid = source.getFluid().getFluid();
		
		return this.transfer(destination, source, source, item, slot, player, inventory, BOTTLE_ITEMS.get(fluid));
	}
	
	@Unique
	private boolean emptyFluidBottle(ItemStack item, SlotAccess slot, Player player, IItemHandler inventory)
	{
		FluidTank source = this.bottleTank(item);
		if(source.isEmpty())
		{
			return false;
		}
		FluidTank destination = this.slotTank();
		
		return this.transfer(destination, source, destination, item, slot, player, inventory, new ItemStack(Items.GLASS_BOTTLE));
	}
	
	@Inject(
			method = "playerInteract",
			at = @At("HEAD"),
			cancellable = true
	)
	private void playerInteract(SlotAccess slot, Player player, boolean allowSlotExtract,
								CallbackInfoReturnable<Boolean> callback)
	{
		ConfigurableFluidStack.ConfigurableFluidSlot self = (ConfigurableFluidStack.ConfigurableFluidSlot) (Object) this;
		ConfigurableFluidStack configurableFluidStack = self.getConfStack();
		
		PlayerMainInvWrapper inventory = new PlayerMainInvWrapper(player.getInventory());
		ItemStack item = slot.get();
		if(item.getCapability(Capabilities.FluidHandler.ITEM) == null)
		{
			if(item.is(Items.GLASS_BOTTLE) && allowSlotExtract && !configurableFluidStack.isEmpty() && self.canExtractFluid(configurableFluidStack.getVariant()))
			{
				if(this.fillGlassBottle(item, slot, player, inventory))
				{
					callback.setReturnValue(true);
					return;
				}
			}
			else
			{
				if(this.emptyFluidBottle(item, slot, player, inventory))
				{
					callback.setReturnValue(true);
					return;
				}
			}
			callback.setReturnValue(false);
		}
	}
}
