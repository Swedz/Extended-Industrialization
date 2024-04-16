package net.swedz.intothetwilight.mi.machines;

import aztech.modern_industrialization.machines.GuiComponentsClient;
import net.swedz.intothetwilight.mi.machines.guicomponents.solarefficiency.SolarEfficiencyBar;
import net.swedz.intothetwilight.mi.machines.guicomponents.solarefficiency.SolarEfficiencyBarClient;
import net.swedz.intothetwilight.mi.machines.guicomponents.waterpumpenvironment.WaterPumpEnvironmentGui;
import net.swedz.intothetwilight.mi.machines.guicomponents.waterpumpenvironment.WaterPumpEnvironmentGuiClient;

public final class MIGuiComponentsClientHook
{
	public static void hook()
	{
		GuiComponentsClient.register(SolarEfficiencyBar.ID, SolarEfficiencyBarClient::new);
		GuiComponentsClient.register(WaterPumpEnvironmentGui.ID, WaterPumpEnvironmentGuiClient::new);
	}
}
