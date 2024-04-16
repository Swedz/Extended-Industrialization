package net.swedz.intothetwilight.mi.machines;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import aztech.modern_industrialization.machines.init.MachineRegistrationHelper;
import aztech.modern_industrialization.machines.models.MachineCasings;
import com.google.common.collect.Lists;
import net.swedz.intothetwilight.datagen.ITTModernIndustrializationDatagenTracker;
import net.swedz.intothetwilight.mi.machines.blockentities.SolarBoilerMachineBlockEntity;

import java.util.List;

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
	
	public static void singleBlockCraftingMachines()
	{
	}
	
	public static void singleBlockSpecialMachines()
	{
		MachineRegistrationHelper.registerMachine(
				"Bronze Solar Boiler", "bronze_solar_boiler",
				(bep) -> new SolarBoilerMachineBlockEntity(bep, true),
				MachineBlockEntity::registerFluidApi
		);
		MachineRegistrationHelper.registerMachine(
				"Steel Solar Boiler", "steel_solar_boiler",
				(bep) -> new SolarBoilerMachineBlockEntity(bep, false),
				MachineBlockEntity::registerFluidApi
		);
		
		MachineRegistrationHelper.addMachineModel("bronze_solar_boiler", "solar_boiler", MachineCasings.BRICKED_BRONZE, true, true, false);
		MachineRegistrationHelper.addMachineModel("steel_solar_boiler", "solar_boiler", MachineCasings.BRICKED_STEEL, true, true, false);
		
		ITTModernIndustrializationDatagenTracker.trackBlock("bronze_solar_boiler");
		ITTModernIndustrializationDatagenTracker.trackBlock("steel_solar_boiler");
	}
}
