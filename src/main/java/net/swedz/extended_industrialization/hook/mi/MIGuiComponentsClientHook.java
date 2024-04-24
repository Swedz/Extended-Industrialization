package net.swedz.extended_industrialization.hook.mi;

import aztech.modern_industrialization.machines.GuiComponentsClient;
import net.swedz.extended_industrialization.machines.guicomponents.solarefficiency.SolarEfficiencyBar;
import net.swedz.extended_industrialization.machines.guicomponents.solarefficiency.SolarEfficiencyBarClient;
import net.swedz.extended_industrialization.machines.guicomponents.waterpumpenvironment.WaterPumpEnvironmentGui;
import net.swedz.extended_industrialization.machines.guicomponents.waterpumpenvironment.WaterPumpEnvironmentGuiClient;

public final class MIGuiComponentsClientHook
{
	public static void hook()
	{
		GuiComponentsClient.register(SolarEfficiencyBar.ID, SolarEfficiencyBarClient::new);
		GuiComponentsClient.register(WaterPumpEnvironmentGui.ID, WaterPumpEnvironmentGuiClient::new);
	}
}
