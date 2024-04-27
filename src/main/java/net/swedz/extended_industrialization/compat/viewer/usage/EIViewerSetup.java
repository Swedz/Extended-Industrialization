package net.swedz.extended_industrialization.compat.viewer.usage;

import aztech.modern_industrialization.compat.viewer.abstraction.ViewerCategory;

import java.util.List;

public final class EIViewerSetup
{
	public static void setup(List<ViewerCategory<?>> registry)
	{
		registry.add(new FluidFertilizerCategory());
	}
}
