package net.swedz.miextended.mi.hook;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.ProgressBar;
import aztech.modern_industrialization.machines.guicomponents.RecipeEfficiencyBar;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.init.SingleBlockCraftingMachines;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import com.google.common.collect.Lists;
import net.swedz.miextended.mi.machines.blockentities.SolarBoilerMachineBlockEntity;

import java.util.List;

import static aztech.modern_industrialization.machines.init.SingleBlockCraftingMachines.*;

public final class MIMachineHook
{
	public static List<ElectricBlastFurnaceBlockEntity.Tier> blastFurnaceTiers()
	{
		List<ElectricBlastFurnaceBlockEntity.Tier> list = Lists.newArrayList();
		
		return list;
	}
	
	public static void multiblockMachines()
	{
	}
	
	public static MachineRecipeType
			BENDING_MACHINE,
			ALLOY_SMELTER;
	
	public static void machineRecipeTypes()
	{
		BENDING_MACHINE = MIMachineRecipeTypes.create("bending_machine").withItemInputs().withItemOutputs();
		ALLOY_SMELTER = MIMachineRecipeTypes.create("alloy_smelter").withItemInputs().withItemOutputs();
	}
	
	public static void singleBlockCraftingMachines()
	{
		// @formatter:off
		
		SingleBlockCraftingMachines.registerMachineTiers(
				"Bending Machine", "bending_machine", BENDING_MACHINE,
				1, 1, 0, 0,
				(params) -> {},
				new ProgressBar.Parameters(77, 34, "compress"),
				new RecipeEfficiencyBar.Parameters(38, 62),
				new EnergyBar.Parameters(18, 30),
				(items) -> items.addSlot(56, 35).addSlot(102, 35),
				(fluids) -> {},
				true, false, false,
				TIER_BRONZE | TIER_STEEL | TIER_ELECTRIC,
				16
		);
		
		SingleBlockCraftingMachines.registerMachineTiers(
				"Alloy Smelter", "alloy_smelter", ALLOY_SMELTER,
				2, 1, 0, 0,
				(params) -> {},
				new ProgressBar.Parameters(88, 33, "arrow"),
				new RecipeEfficiencyBar.Parameters(38, 62),
				new EnergyBar.Parameters(14, 34),
				(items) -> items.addSlots(40, 35, 2, 1).addSlot(120, 35),
				(fluids) -> {},
				true, false, false,
				TIER_STEEL | TIER_ELECTRIC,
				16
		);
		
		// @formatter:on
	}
	
	public static void singleBlockSpecialMachines()
	{
		MIMachineHookHelper.registerSingleBlockSpecialMachine(
				"Bronze Solar Boiler", "bronze_solar_boiler", "solar_boiler",
				MachineCasings.BRICKED_BRONZE, true, true, false,
				(bep) -> new SolarBoilerMachineBlockEntity(bep, true),
				MachineBlockEntity::registerFluidApi
		);
		MIMachineHookHelper.registerSingleBlockSpecialMachine(
				"Steel Solar Boiler", "steel_solar_boiler", "solar_boiler",
				MachineCasings.BRICKED_STEEL, true, true, false,
				(bep) -> new SolarBoilerMachineBlockEntity(bep, false),
				MachineBlockEntity::registerFluidApi
		);
	}
}
