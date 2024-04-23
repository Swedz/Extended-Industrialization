package net.swedz.miextended.registry.fluids;

import aztech.modern_industrialization.definition.FluidTexture;

public record FluidProperties(int color, int opacity, FluidTexture texture, boolean isGas)
{
}
