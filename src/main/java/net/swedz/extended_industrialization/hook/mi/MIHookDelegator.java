package net.swedz.extended_industrialization.hook.mi;

import aztech.modern_industrialization.compat.viewer.abstraction.ViewerCategory;
import aztech.modern_industrialization.machines.GuiComponentsClient;
import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessConditions;
import com.google.common.collect.Lists;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.compat.viewer.usage.EIViewerSetup;
import net.swedz.extended_industrialization.machines.EIMachines;
import net.swedz.extended_industrialization.machines.guicomponents.exposecabletier.ExposeCableTierGui;
import net.swedz.extended_industrialization.machines.guicomponents.exposecabletier.ExposeCableTierGuiClient;
import net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock.ModularMultiblockGui;
import net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock.ModularMultiblockGuiClient;
import net.swedz.extended_industrialization.machines.guicomponents.modularselection.ConfigurationPanel;
import net.swedz.extended_industrialization.machines.guicomponents.modularselection.ConfigurationPanelClient;
import net.swedz.extended_industrialization.machines.guicomponents.processingarraymachineslot.ProcessingArrayMachineSlot;
import net.swedz.extended_industrialization.machines.guicomponents.processingarraymachineslot.ProcessingArrayMachineSlotClient;
import net.swedz.extended_industrialization.machines.guicomponents.solarefficiency.SolarEfficiencyBar;
import net.swedz.extended_industrialization.machines.guicomponents.solarefficiency.SolarEfficiencyBarClient;
import net.swedz.extended_industrialization.machines.guicomponents.universaltransformer.UniversalTransformerSlots;
import net.swedz.extended_industrialization.machines.guicomponents.universaltransformer.UniversalTransformerSlotsClient;
import net.swedz.extended_industrialization.machines.guicomponents.waterpumpenvironment.WaterPumpEnvironmentGui;
import net.swedz.extended_industrialization.machines.guicomponents.waterpumpenvironment.WaterPumpEnvironmentGuiClient;
import net.swedz.extended_industrialization.machines.recipe.condition.VoltageProcessCondition;
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
		GuiComponentsClient.register(ConfigurationPanel.ID, ConfigurationPanelClient::new);
		GuiComponentsClient.register(UniversalTransformerSlots.ID, UniversalTransformerSlotsClient::new);
		GuiComponentsClient.register(ExposeCableTierGui.ID, ExposeCableTierGuiClient::new);
	}
	
	public static List<ElectricBlastFurnaceBlockEntity.Tier> machinesBlastFurnaceTier()
	{
		List<ElectricBlastFurnaceBlockEntity.Tier> list = Lists.newArrayList();
		EIMachines.blastFurnaceTiers(list);
		return list;
	}
	
	public static void machineCasings()
	{
		EIMachines.casings();
	}
	
	public static void machineProcessConditions()
	{
		MachineProcessConditions.register(EI.id("voltage"), VoltageProcessCondition.CODEC);
	}
	
	public static void machinesRecipeType()
	{
		EIMachines.recipeTypes();
	}
	
	public static void machinesMultiblock()
	{
		EIMachines.multiblocks();
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
