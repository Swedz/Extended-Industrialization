package net.swedz.extended_industrialization.machines.blockentities;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.MITooltips;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyComponentHolder;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.machines.components.solar.SolarSunlightComponent;
import net.swedz.extended_industrialization.machines.components.solar.electric.SolarGeneratorComponent;
import net.swedz.extended_industrialization.machines.guicomponents.solarefficiency.SolarEfficiencyBar;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;
import net.swedz.extended_industrialization.text.EIText;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public final class SolarPanelMachineBlockEntity extends MachineBlockEntity implements Tickable, EnergyComponentHolder
{
	private static final int CELL_X = 58;
	private static final int CELL_Y = 40;
	
	private static final int WATER_X = 38;
	private static final int WATER_Y = 40;
	
	private static final int ENERGY_X = 126;
	private static final int ENERGY_Y = 39;
	
	private static final int SOLAR_EFFICIENCY_X = 38;
	private static final int SOLAR_EFFICIENCY_Y = 75;
	
	private final CableTier tier;
	
	private final RedstoneControlComponent redstoneControl;
	
	private final MIInventory inventory;
	
	private final EnergyComponent energy;
	private final MIEnergyStorage extractable;
	
	private final SolarSunlightComponent  sunlight;
	private final SolarGeneratorComponent generator;
	
	public SolarPanelMachineBlockEntity(BEP bep, String blockName, CableTier tier)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(blockName, true).backgroundHeight(180).build(),
				new OrientationComponent.Params(false, false, false)
		);
		
		this.tier = tier;
		
		long capacity = 32 * FluidType.BUCKET_VOLUME;
		
		redstoneControl = new RedstoneControlComponent();
		
		List<ConfigurableItemStack> itemStacks = List.of(
				ConfigurableItemStack.standardInputSlot()
		);
		SlotPositions itemPositions = new SlotPositions.Builder().addSlot(CELL_X, CELL_Y).build();
		List<ConfigurableFluidStack> fluidStacks = List.of(
				ConfigurableFluidStack.lockedInputSlot(capacity, EIFluids.DISTILLED_WATER.asFluid())
		);
		SlotPositions fluidPositions = new SlotPositions.Builder().addSlot(WATER_X, WATER_Y).build();
		inventory = new MIInventory(itemStacks, fluidStacks, itemPositions, fluidPositions);
		
		energy = new EnergyComponent(this, () -> tier.getEu() * 100);
		extractable = energy.buildExtractable((otherTier) -> otherTier == tier);
		
		sunlight = new SolarSunlightComponent(this);
		generator = new SolarGeneratorComponent(inventory, energy, this::getEfficiency, (cell) -> cell.getTier() == tier);
		
		this.registerGuiComponent(new EnergyBar.Server(new EnergyBar.Parameters(ENERGY_X, ENERGY_Y), energy::getEu, energy::getCapacity));
		
		this.registerGuiComponent(new SlotPanel.Server(this)
				.withRedstoneControl(redstoneControl));
		
		this.registerGuiComponent(SolarEfficiencyBar.Server.energyProduced(
				new SolarEfficiencyBar.Parameters(SOLAR_EFFICIENCY_X, SOLAR_EFFICIENCY_Y),
				sunlight::canOperate,
				() -> (int) (this.getEfficiency() * 100),
				generator::getEnergyPerTick
		));
		
		this.registerComponents(inventory, energy, redstoneControl, sunlight, generator);
	}
	
	public float getEfficiency()
	{
		return sunlight.getSolarEfficiency();
	}
	
	@Override
	public MIInventory getInventory()
	{
		return inventory;
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData(tier.casing);
		orientation.writeModelData(data);
		return data;
	}
	
	@Override
	public EnergyAccess getEnergyComponent()
	{
		return energy;
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			return;
		}
		
		if(sunlight.canOperate() && redstoneControl.doAllowNormalOperation(this))
		{
			generator.tick();
		}
	}
	
	@Override
	protected InteractionResult onUse(Player player, InteractionHand hand, Direction face)
	{
		InteractionResult result = super.onUse(player, hand, face);
		if(!result.consumesAction())
		{
			result = redstoneControl.onUse(this, player, hand);
		}
		return result;
	}
	
	@Override
	public List<Component> getTooltips()
	{
		List<Component> tooltips = Lists.newArrayList();
		tooltips.add(MITooltips.DEFAULT_PARSER.parse(EIText.SOLAR_PANEL_PHOTOVOLTAIC_CELL.text()));
		tooltips.add(MITooltips.DEFAULT_PARSER.parse(EIText.SOLAR_PANEL_SUNLIGHT.text()));
		tooltips.add(
				MITooltips.DEFAULT_PARSER.parse(EIText.SOLAR_PANEL_DISTILLED_WATER.text(
						MITooltips.FLUID_PARSER.parse(EIFluids.DISTILLED_WATER.asFluid())
				))
		);
		return tooltips;
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) ->
						direction != Direction.UP ? ((SolarPanelMachineBlockEntity) be).extractable : null));
	}
}
