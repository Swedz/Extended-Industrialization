package net.swedz.extended_industrialization.compat.mi;

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
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.BlastFurnaceTiersMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.ClientGuiComponentsMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.MachineCasingsMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.MachineProcessConditionsMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.MachineRecipeTypesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.MultiblockMachinesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.SingleBlockCraftingMachinesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.SingleBlockSpecialMachinesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.ViewerSetupMIHookContext;

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
		hook.register(SolarEfficiencyBar.ID, SolarEfficiencyBarClient::new);
		hook.register(ProcessingArrayMachineSlot.ID, ProcessingArrayMachineSlotClient::new);
		hook.register(UniversalTransformerSlots.ID, UniversalTransformerSlotsClient::new);
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
