package net.swedz.extended_industrialization.mixin.mi.accessor;

import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractCraftingMultiblockBlockEntity;
import aztech.modern_industrialization.machines.components.ActiveShapeComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractCraftingMultiblockBlockEntity.class)
public interface AbstractCraftingMultiblockBlockEntityAccessor
{
	@Accessor
	ActiveShapeComponent getActiveShape();
}
