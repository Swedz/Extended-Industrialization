package net.swedz.extended_industrialization.compat.mi;

import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIMachines;
import net.swedz.extended_industrialization.EITooltips;
import net.swedz.extended_industrialization.compat.viewer.common.FluidFertilizerCategory;
import net.swedz.extended_industrialization.machines.guicomponent.beacon.BeaconEffectsView;
import net.swedz.extended_industrialization.machines.guicomponent.beacon.BeaconEffectsViewClient;
import net.swedz.extended_industrialization.machines.guicomponent.processingarraymachineslot.ProcessingArrayMachineSlot;
import net.swedz.extended_industrialization.machines.guicomponent.processingarraymachineslot.ProcessingArrayMachineSlotClient;
import net.swedz.extended_industrialization.machines.guicomponent.solarefficiency.SolarEfficiencyBar;
import net.swedz.extended_industrialization.machines.guicomponent.solarefficiency.SolarEfficiencyBarClient;
import net.swedz.extended_industrialization.machines.guicomponent.teslanetwork.TeslaNetworkBar;
import net.swedz.extended_industrialization.machines.guicomponent.teslanetwork.TeslaNetworkBarClient;
import net.swedz.extended_industrialization.machines.guicomponent.universaltransformer.UniversalTransformerSlots;
import net.swedz.extended_industrialization.machines.guicomponent.universaltransformer.UniversalTransformerSlotsClient;
import net.swedz.extended_industrialization.machines.recipe.condition.RuntimeGeneratedFlagProcessCondition;
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHookEntrypoint;
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

@MIHookEntrypoint
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
		hook.register(BeaconEffectsView.TYPE, BeaconEffectsViewClient::new);
		hook.register(SolarEfficiencyBar.TYPE, SolarEfficiencyBarClient::new);
		hook.register(ProcessingArrayMachineSlot.TYPE, ProcessingArrayMachineSlotClient::new);
		hook.register(UniversalTransformerSlots.TYPE, UniversalTransformerSlotsClient::new);
		hook.register(TeslaNetworkBar.TYPE, TeslaNetworkBarClient::new);
	}
	
	@Override
	public void machineCasings(MachineCasingsMIHookContext hook)
	{
		EIMachines.casings(hook);
	}
	
	@Override
	public void machineProcessConditions(MachineProcessConditionsMIHookContext hook)
	{
		hook.register(EI.id("runtime_generated_flag"), RuntimeGeneratedFlagProcessCondition.CODEC, RuntimeGeneratedFlagProcessCondition.STREAM_CODEC);
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
		
		ReiMachineRecipes.categories.forEach((__, params) ->
		{
			boolean isValidForProcessingArray = false;
			for(var workstationId : params.workstations)
			{
				var workstationItem = BuiltInRegistries.ITEM.get(workstationId);
				if(ProcessingArrayMachineSlot.isMachine(workstationItem))
				{
					isValidForProcessingArray = true;
					break;
				}
			}
			if(isValidForProcessingArray)
			{
				params.workstations.add(EI.id("processing_array"));
			}
		});
	}
}
