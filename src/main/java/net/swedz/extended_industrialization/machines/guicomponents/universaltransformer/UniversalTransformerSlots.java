package net.swedz.extended_industrialization.machines.guicomponents.universaltransformer;

import aztech.modern_industrialization.inventory.HackySlot;
import aztech.modern_industrialization.inventory.SlotGroup;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.gui.GuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.components.TransformerTierComponent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class UniversalTransformerSlots
{
	public static final ResourceLocation ID = EI.id("universal_transformer");
	
	public static int getSlotX()
	{
		return -22;
	}
	
	public static int getSlotY(int index)
	{
		return 19 + index * 36;
	}
	
	public static final class Server implements GuiComponent.ServerNoData
	{
		private final MachineBlockEntity machine;
		
		private final TransformerTierComponent transformerFrom;
		private final TransformerTierComponent transformerTo;
		
		public Server(MachineBlockEntity machine, TransformerTierComponent transformerFrom, TransformerTierComponent transformerTo)
		{
			this.machine = machine;
			this.transformerFrom = transformerFrom;
			this.transformerTo = transformerTo;
		}
		
		@Override
		public void writeInitialData(FriendlyByteBuf buf)
		{
		}
		
		@Override
		public ResourceLocation getId()
		{
			return ID;
		}
		
		private void addSlot(GuiComponent.MenuFacade menu, int index, Supplier<ItemStack> getStack, Consumer<ItemStack> setStack)
		{
			menu.addSlotToMenu(
					new HackySlot(getSlotX(), getSlotY(index))
					{
						@Override
						protected ItemStack getRealStack()
						{
							return getStack.get();
						}
						
						@Override
						protected void setRealStack(ItemStack itemStack)
						{
							setStack.accept(itemStack);
						}
						
						@Override
						public boolean mayPlace(ItemStack itemStack)
						{
							return TransformerTierComponent.getTierFromCasing(itemStack) != null;
						}
						
						@Override
						public int getMaxStackSize()
						{
							return 1;
						}
					},
					SlotGroup.CONFIGURABLE_STACKS
			);
		}
		
		@Override
		public void setupMenu(GuiComponent.MenuFacade menu)
		{
			this.addSlot(menu, 0, transformerFrom::getStack, (stack) -> transformerFrom.setCasing(machine, stack));
			this.addSlot(menu, 1, transformerTo::getStack, (stack) -> transformerTo.setCasing(machine, stack));
		}
	}
}
