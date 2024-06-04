package net.swedz.extended_industrialization.api.registry;

import aztech.modern_industrialization.definition.FluidTexture;

public record FluidProperties(int color, int opacity, FluidTexture texture, boolean isGas)
{
}
