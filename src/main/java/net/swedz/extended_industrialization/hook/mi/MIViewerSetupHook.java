package net.swedz.extended_industrialization.hook.mi;

import aztech.modern_industrialization.compat.viewer.abstraction.ViewerCategory;
import net.swedz.extended_industrialization.compat.viewer.usage.EIViewerSetup;

import java.util.List;

public final class MIViewerSetupHook
{
	public static void hook(List<ViewerCategory<?>> registry)
	{
		EIViewerSetup.setup(registry);
	}
}
