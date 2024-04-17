package net.swedz.miextended.mi.machines;

import aztech.modern_industrialization.machines.GuiComponentsClient;
import net.swedz.miextended.mi.machines.guicomponents.solarefficiency.SolarEfficiencyBar;
import net.swedz.miextended.mi.machines.guicomponents.solarefficiency.SolarEfficiencyBarClient;
import net.swedz.miextended.mi.machines.guicomponents.waterpumpenvironment.WaterPumpEnvironmentGui;
import net.swedz.miextended.mi.machines.guicomponents.waterpumpenvironment.WaterPumpEnvironmentGuiClient;

public final class MIGuiComponentsClientHook
{
	public static void hook()
	{
		GuiComponentsClient.register(SolarEfficiencyBar.ID, SolarEfficiencyBarClient::new);
		GuiComponentsClient.register(WaterPumpEnvironmentGui.ID, WaterPumpEnvironmentGuiClient::new);
	}
}
