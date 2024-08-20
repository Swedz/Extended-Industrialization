package net.swedz.extended_industrialization.mixin.mi.accessor;

import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.api.ComponentStackHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
		value = RedstoneControlComponent.class,
		remap = false
)
public interface RedstoneControlComponentAccessor extends ComponentStackHolder
{
	@Accessor("controlModule")
	ItemStack getStack();
	
	@Accessor("controlModule")
	void setStack(ItemStack stack);
}
