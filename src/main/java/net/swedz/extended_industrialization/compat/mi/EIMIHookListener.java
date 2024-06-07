package net.swedz.extended_industrialization.compat.mi;

import aztech.modern_industrialization.machines.GuiComponentsClient;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessConditions;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIMachines;
import net.swedz.extended_industrialization.EITooltips;
import net.swedz.extended_industrialization.compat.viewer.usage.FluidFertilizerCategory;
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
import net.swedz.extended_industrialization.machines.recipe.condition.EBFCoilProcessCondition;
import net.swedz.extended_industrialization.machines.recipe.condition.VoltageProcessCondition;
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHookListener;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.BlastFurnaceTiersMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.ClientGuiComponentsMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.MachineCasingsMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.MachineProcessConditionsMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.MachineRecipeTypesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.MultiblockMachinesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.SingleBlockCraftingMachinesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.SingleBlockSpecialMachinesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.ViewerSetupMIHookContext;

public final class EIMIHookListener implements MIHookListener
{
	@Override
	public void blastFurnaceTiers(BlastFurnaceTiersMIHookContext hook)
	{
		EIMachines.blastFurnaceTiers(hook);
	}
	
	@Override
	public void clientGuiComponents(ClientGuiComponentsMIHookContext hook)
	{
		GuiComponentsClient.register(SolarEfficiencyBar.ID, SolarEfficiencyBarClient::new);
		GuiComponentsClient.register(WaterPumpEnvironmentGui.ID, WaterPumpEnvironmentGuiClient::new);
		GuiComponentsClient.register(ModularMultiblockGui.ID, ModularMultiblockGuiClient::new);
		GuiComponentsClient.register(ProcessingArrayMachineSlot.ID, ProcessingArrayMachineSlotClient::new);
		GuiComponentsClient.register(ConfigurationPanel.ID, ConfigurationPanelClient::new);
		GuiComponentsClient.register(UniversalTransformerSlots.ID, UniversalTransformerSlotsClient::new);
		GuiComponentsClient.register(ExposeCableTierGui.ID, ExposeCableTierGuiClient::new);
	}
	
	@Override
	public void machineCasings(MachineCasingsMIHookContext hook)
	{
		EIMachines.casings(hook);
	}
	
	@Override
	public void machineProcessConditions(MachineProcessConditionsMIHookContext hook)
	{
		MachineProcessConditions.register(EI.id("voltage"), VoltageProcessCondition.CODEC);
		MachineProcessConditions.register(EI.id("ebf_coil"), EBFCoilProcessCondition.CODEC);
	}
	
	@Override
	public void machineRecipeTypes(MachineRecipeTypesMIHookContext hook)
	{
		EIMachines.recipeTypes(hook);
	}
	
	@Override
	public void multiblockMachines(MultiblockMachinesMIHookContext hook)
	{
		EIMachines.multiblocks(hook);
	}
	
	@Override
	public void singleBlockCraftingMachines(SingleBlockCraftingMachinesMIHookContext hook)
	{
		EIMachines.singleBlockCrafting(hook);
	}
	
	@Override
	public void singleBlockSpecialMachines(SingleBlockSpecialMachinesMIHookContext hook)
	{
		EIMachines.singleBlockSpecial(hook);
	}
	
	@Override
	public void tooltips()
	{
		EITooltips.init();
	}
	
	@Override
	public void viewerSetup(ViewerSetupMIHookContext hook)
	{
		hook.register(new FluidFertilizerCategory());
	}
}
