package net.swedz.miextended.fluids;

import aztech.modern_industrialization.definition.FluidDefinition;
import aztech.modern_industrialization.definition.FluidTexture;

import static aztech.modern_industrialization.MIFluids.*;
import static aztech.modern_industrialization.definition.FluidDefinition.*;

public final class MIEFluids
{
	public static final FluidDefinition HONEY            = fluid("Honey", "honey", 0xF2AE21, NEAR_OPACITY);
	public static final FluidDefinition MANURE           = fluid("Manure", "manure", 0x211404, FULL_OPACITY, FluidTexture.LAVA_LIKE, false);
	public static final FluidDefinition COMPOSTED_MANURE = fluid("Composted Manure", "composted_manure", 0x301b00, FULL_OPACITY, FluidTexture.LAVA_LIKE, false);
	public static final FluidDefinition NPK_FERTILIZER   = fluid("NPK Fertilizer", "npk_fertilizer", 0x4ABD44, NEAR_OPACITY);
	
	public static void init()
	{
	}
}
