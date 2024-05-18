package net.swedz.extended_industrialization.hook.mi;

import aztech.modern_industrialization.compat.viewer.abstraction.ViewerCategory;
import aztech.modern_industrialization.machines.GuiComponentsClient;
import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import com.google.common.collect.Lists;
import net.swedz.extended_industrialization.compat.viewer.usage.EIViewerSetup;
import net.swedz.extended_industrialization.machines.EIMachines;
import net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock.ModularMultiblockGui;
import net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock.ModularMultiblockGuiClient;
import net.swedz.extended_industrialization.machines.guicomponents.processingarraymachineslot.ProcessingArrayMachineSlot;
import net.swedz.extended_industrialization.machines.guicomponents.processingarraymachineslot.ProcessingArrayMachineSlotClient;
import net.swedz.extended_industrialization.machines.guicomponents.solarefficiency.SolarEfficiencyBar;
import net.swedz.extended_industrialization.machines.guicomponents.solarefficiency.SolarEfficiencyBarClient;
import net.swedz.extended_industrialization.machines.guicomponents.waterpumpenvironment.WaterPumpEnvironmentGui;
import net.swedz.extended_industrialization.machines.guicomponents.waterpumpenvironment.WaterPumpEnvironmentGuiClient;
import net.swedz.extended_industrialization.tooltips.EITooltips;

import java.util.List;

public final class MIHookDelegator
{
	public static void clientGuiComponents()
	{
		GuiComponentsClient.register(SolarEfficiencyBar.ID, SolarEfficiencyBarClient::new);
		GuiComponentsClient.register(WaterPumpEnvironmentGui.ID, WaterPumpEnvironmentGuiClient::new);
		GuiComponentsClient.register(ModularMultiblockGui.ID, ModularMultiblockGuiClient::new);
		GuiComponentsClient.register(ProcessingArrayMachineSlot.ID, ProcessingArrayMachineSlotClient::new);
	}
	
	public static void machineCasings()
	{
		EIMachines.casings();
	}
	
	public static List<ElectricBlastFurnaceBlockEntity.Tier> machinesBlastFurnaceTier()
	{
		List<ElectricBlastFurnaceBlockEntity.Tier> list = Lists.newArrayList();
		EIMachines.blastFurnaceTiers(list);
		return list;
	}
	
	public static void machinesMultiblock()
	{
		EIMachines.multiblocks();
	}
	
	public static void machinesRecipeType()
	{
		EIMachines.recipeTypes();
	}
	
	public static void machinesSingleBlockCrafting()
	{
		EIMachines.singleBlockCrafting();
	}
	
	public static void machinesSingleBlockSpecial()
	{
		EIMachines.singleBlockSpecial();
	}
	
	public static void tooltips()
	{
		EITooltips.init();
	}
	
	public static void viewerCategories(List<ViewerCategory<?>> registry)
	{
		EIViewerSetup.setup(registry);
	}
}
