package net.swedz.extended_industrialization.compat.mi;

import aztech.modern_industrialization.machines.GuiComponentsClient;
import net.swedz.extended_industrialization.EIMachines;
import net.swedz.extended_industrialization.EITooltips;
import net.swedz.extended_industrialization.compat.viewer.usage.FluidFertilizerCategory;
import net.swedz.extended_industrialization.machines.guicomponents.processingarraymachineslot.ProcessingArrayMachineSlot;
import net.swedz.extended_industrialization.machines.guicomponents.processingarraymachineslot.ProcessingArrayMachineSlotClient;
import net.swedz.extended_industrialization.machines.guicomponents.solarefficiency.SolarEfficiencyBar;
import net.swedz.extended_industrialization.machines.guicomponents.solarefficiency.SolarEfficiencyBarClient;
import net.swedz.extended_industrialization.machines.guicomponents.universaltransformer.UniversalTransformerSlots;
import net.swedz.extended_industrialization.machines.guicomponents.universaltransformer.UniversalTransformerSlotsClient;
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
		GuiComponentsClient.register(ProcessingArrayMachineSlot.ID, ProcessingArrayMachineSlotClient::new);
		GuiComponentsClient.register(UniversalTransformerSlots.ID, UniversalTransformerSlotsClient::new);
	}
	
	@Override
	public void machineCasings(MachineCasingsMIHookContext hook)
	{
		EIMachines.casings(hook);
	}
	
	@Override
	public void machineProcessConditions(MachineProcessConditionsMIHookContext hook)
	{
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
