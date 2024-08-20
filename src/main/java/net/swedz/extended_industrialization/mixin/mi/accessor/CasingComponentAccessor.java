package net.swedz.extended_industrialization.mixin.mi.accessor;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.components.CasingComponent;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.api.ComponentStackHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(
		value = CasingComponent.class,
		remap = false
)
public interface CasingComponentAccessor extends ComponentStackHolder
{
	@Accessor("currentTier")
	CableTier getCurrentTier();
	
	@Accessor("casingStack")
	ItemStack getStack();
	
	@Invoker("setCasingStack")
	void setStack(ItemStack stack);
}
