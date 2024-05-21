package net.swedz.extended_industrialization.machines.blockentities;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.MITooltips;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.ProgressBar;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.machines.components.ExtendedSteamHeaterComponent;
import net.swedz.extended_industrialization.machines.components.solar.boiler.SolarBoilerCalcificationComponent;
import net.swedz.extended_industrialization.machines.components.solar.boiler.SolarBurningComponent;
import net.swedz.extended_industrialization.machines.guicomponents.solarefficiency.SolarEfficiencyBar;
import net.swedz.extended_industrialization.text.EIText;
import net.swedz.extended_industrialization.tooltips.EITooltips;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SolarBoilerMachineBlockEntity extends MachineBlockEntity implements Tickable
{
	private static final int WATER_SLOT_X = 38;
	private static final int WATER_SLOT_Y = 32;
	
	private static final int OUTPUT_SLOT_X = 122;
	private static final int OUTPUT_SLOT_Y = 32;
	
	private static final int BURNING_PROGRESS_X = 120;
	private static final int BURNING_PROGRESS_Y = 50;
	
	private static final int SOLAR_EFFICIENCY_X = 38;
	private static final int SOLAR_EFFICIENCY_Y = 75;
	
	private final MIInventory inventory;
	
	private final boolean bronze;
	private final long    maxEuProduction;
	
	private final SolarBoilerCalcificationComponent calcification;
	private final ExtendedSteamHeaterComponent      steamHeater;
	private final SolarBurningComponent             solarBurning;
	
	private IsActiveComponent isActiveComponent;
	
	public SolarBoilerMachineBlockEntity(BEP bep, boolean bronze)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(bronze ? "bronze_solar_boiler" : "steel_solar_boiler", true).backgroundHeight(180).build(),
				new OrientationComponent.Params(false, false, false)
		);
		
		int capacity = FluidType.BUCKET_VOLUME * (bronze ? 8 : 16);
		
		List<ConfigurableItemStack> itemStacks = Collections.emptyList();
		SlotPositions itemPositions = new SlotPositions.Builder().build();
		
		List<ConfigurableFluidStack> fluidStacks = Arrays.asList(
				ConfigurableFluidStack.lockedInputSlot(capacity, Fluids.WATER),
				ConfigurableFluidStack.lockedOutputSlot(capacity, MIFluids.STEAM.asFluid())
		);
		SlotPositions fluidPositions = new SlotPositions.Builder().addSlot(WATER_SLOT_X, WATER_SLOT_Y).addSlot(OUTPUT_SLOT_X, OUTPUT_SLOT_Y).build();
		inventory = new MIInventory(itemStacks, fluidStacks, itemPositions, fluidPositions);
		
		this.bronze = bronze;
		maxEuProduction = bronze ? 4 : 8;
		calcification = new SolarBoilerCalcificationComponent();
		steamHeater = new ExtendedSteamHeaterComponent(2400, 480, () -> (long) Math.ceil(maxEuProduction * calcification.getEfficiency()));
		solarBurning = new SolarBurningComponent(steamHeater, bronze ? 1 : 2, 4);
		this.isActiveComponent = new IsActiveComponent();
		
		this.registerGuiComponent(new ProgressBar.Server(
				new ProgressBar.Parameters(BURNING_PROGRESS_X, BURNING_PROGRESS_Y, "furnace", true),
				steamHeater::getWorkingTemperatureFullness
		));
		this.registerGuiComponent(new SolarEfficiencyBar.Server(
				new SolarEfficiencyBar.Parameters(SOLAR_EFFICIENCY_X, SOLAR_EFFICIENCY_Y),
				solarBurning::isWorking,
				() -> (int) ((steamHeater.getTemperatureFullness() * calcification.getEfficiency()) * 100),
				() -> (int) (calcification.getCalcification() * 100)
		));
		
		this.registerComponents(inventory, isActiveComponent, calcification, steamHeater, solarBurning);
	}
	
	@Override
	public MIInventory getInventory()
	{
		return inventory;
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData(bronze ? MachineCasings.BRICKED_BRONZE : MachineCasings.BRICKED_STEEL);
		data.isActive = isActiveComponent.isActive;
		orientation.writeModelData(data);
		return data;
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide)
		{
			return;
		}
		
		steamHeater.tick(
				Collections.singletonList(inventory.getFluidStacks().get(0)),
				Collections.singletonList(inventory.getFluidStacks().get(1))
		);
		solarBurning.tick(level, this.getBlockPos());
		
		for(Direction direction : Direction.values())
		{
			this.getInventory().autoExtractFluids(level, worldPosition, direction);
		}
		
		if(steamHeater.isWorking())
		{
			calcification.tick();
		}
		
		isActiveComponent.updateActive(steamHeater.isWorking(), this);
		
		this.setChanged();
	}
	
	@Override
	public List<Component> getTooltips()
	{
		List<Component> tooltips = new ArrayList<>();
		tooltips.add(
				new MITooltips.Line(MIText.MaxEuProductionSteam)
						.arg(maxEuProduction, MITooltips.EU_PER_TICK_PARSER)
						.arg(MIFluids.STEAM)
						.build()
		);
		tooltips.add(
				MITooltips.DEFAULT_PARSER.parse(EIText.SOLAR_BOILER_CALCIFICATION.text(EITooltips.RATIO_PERCENTAGE_PARSER.parse(SolarBoilerCalcificationComponent.MINIMUM_EFFICIENCY)))
		);
		return tooltips;
	}
}