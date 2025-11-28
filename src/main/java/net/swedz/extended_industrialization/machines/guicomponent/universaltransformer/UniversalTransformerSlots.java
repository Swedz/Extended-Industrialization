package net.swedz.extended_industrialization.machines.guicomponent.universaltransformer;

import aztech.modern_industrialization.inventory.HackySlot;
import aztech.modern_industrialization.inventory.SlotGroup;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.gui.GuiComponent;
import aztech.modern_industrialization.machines.gui.GuiComponentServer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.TransformerTierComponent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class UniversalTransformerSlots implements GuiComponentServer<Unit, Unit>
{
	public static final Type<Unit, Unit> TYPE = new Type<>(EI.id("universal_transformer"), StreamCodec.unit(Unit.INSTANCE), StreamCodec.unit(Unit.INSTANCE));
	
	public static int getSlotX()
	{
		return -22;
	}
	
	public static int getSlotY(int index)
	{
		return 19 + index * 36;
	}
	
	private final MachineBlockEntity machine;
	
	private final TransformerTierComponent transformerFrom;
	private final TransformerTierComponent transformerTo;
	
	public UniversalTransformerSlots(MachineBlockEntity machine, TransformerTierComponent transformerFrom, TransformerTierComponent transformerTo)
	{
		this.machine = machine;
		this.transformerFrom = transformerFrom;
		this.transformerTo = transformerTo;
	}
	
	@Override
	public Unit getParams()
	{
		return Unit.INSTANCE;
	}
	
	@Override
	public Unit extractData()
	{
		return Unit.INSTANCE;
	}
	
	@Override
	public Type<Unit, Unit> getType()
	{
		return TYPE;
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
