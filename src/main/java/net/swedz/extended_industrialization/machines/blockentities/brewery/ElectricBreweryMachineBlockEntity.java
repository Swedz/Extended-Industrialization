package net.swedz.extended_industrialization.machines.blockentities.brewery;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyComponentHolder;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.MachineInventoryComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.machines.components.craft.potion.PotionCrafterComponent;
import net.swedz.extended_industrialization.machines.components.craft.potion.PotionCrafterComponent.SlotRange;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.recipeefficiency.ModularRecipeEfficiencyBar;
import net.swedz.tesseract.neoforge.compat.mi.helper.ModularLubricantHelper;

import java.util.List;

public final class ElectricBreweryMachineBlockEntity extends BreweryMachineBlockEntity implements EnergyComponentHolder
{
	private static final int EFFICIENCY_BAR_X = 38;
	private static final int EFFICIENCY_BAR_Y = 106;
	
	private final EnergyComponent energy;
	private final MIEnergyStorage insertable;
	private final RedstoneControlComponent redstoneControl;
	private final CasingComponent casing;
	private final UpgradeComponent upgrades;
	
	public ElectricBreweryMachineBlockEntity(BEP bep)
	{
		super(bep, "electric_brewery", MachineTier.LV, 32 * FluidType.BUCKET_VOLUME);
		
		this.redstoneControl = new RedstoneControlComponent();
		this.casing = new CasingComponent();
		this.upgrades = new UpgradeComponent();
		
		this.energy = new EnergyComponent(this, 3200);
		this.insertable = energy.buildInsertable(casing::canInsertEu);
		
		this.registerGuiComponent(new EnergyBar.Server(new EnergyBar.Parameters(STEAM_SLOT_X + 1, STEAM_SLOT_Y - 1), energy::getEu, energy::getCapacity));
		this.registerGuiComponent(new ModularRecipeEfficiencyBar.Server(new ModularRecipeEfficiencyBar.Parameters(EFFICIENCY_BAR_X, EFFICIENCY_BAR_Y), crafter));
		this.registerGuiComponent(new SlotPanel.Server(this)
				.withRedstoneControl(redstoneControl)
				.withUpgrades(upgrades)
				.withCasing(casing));
		
		this.registerComponents(energy, redstoneControl, casing, upgrades);
	}
	
	@Override
	protected Pair<MachineInventoryComponent, PotionCrafterComponent.Params> buildInventory()
	{
		List<ConfigurableItemStack> itemInputs = Lists.newArrayList();
		List<ConfigurableItemStack> itemOutputs = Lists.newArrayList();
		itemInputs.add(ConfigurableItemStack.lockedInputSlot(Items.BLAZE_POWDER));
		for(int i = 0; i < 9; i++)
		{
			itemInputs.add(ConfigurableItemStack.standardInputSlot());
		}
		for(int i = 0; i < 4; i++)
		{
			itemInputs.add(ConfigurableItemStack.standardInputSlot());
		}
		for(int i = 0; i < 9; i++)
		{
			itemOutputs.add(new PotionConfigurableItemStack());
		}
		SlotPositions itemPositions = new SlotPositions.Builder()
				.addSlot(BLAZING_ESSENCE_SLOT_X, BLAZING_ESSENCE_SLOT_Y)
				.addSlots(INPUT_BOTTLE_SLOTS_X, INPUT_BOTTLE_SLOTS_Y, 3, 3)
				.addSlots(INPUT_REAGENT_SLOTS_X, INPUT_REAGENT_SLOTS_Y, 4, 1)
				.addSlots(OUTPUT_SLOTS_X, OUTPUT_SLOTS_Y, 3, 3)
				.build();
		SlotRange<ConfigurableItemStack> slotsBlazePowder = SlotRange.item(0);
		SlotRange<ConfigurableItemStack> slotsBottle = SlotRange.item(1, 9);
		SlotRange<ConfigurableItemStack> slotsReagent = SlotRange.item(10, 13);
		SlotRange<ConfigurableItemStack> slotsOutput = SlotRange.item(0, 8);
		
		List<ConfigurableFluidStack> fluidInputs = List.of(
				ConfigurableFluidStack.lockedInputSlot(capacity, Fluids.WATER)
		);
		SlotPositions fluidPositions = new SlotPositions.Builder()
				.addSlot(WATER_SLOT_X, WATER_SLOT_Y)
				.build();
		SlotRange<ConfigurableFluidStack> slotsWater = SlotRange.fluid(0);
		
		return Pair.of(
				new MachineInventoryComponent(itemInputs, itemOutputs, fluidInputs, List.of(), itemPositions, fluidPositions),
				new PotionCrafterComponent.Params(
						slotsBlazePowder, slotsBottle, slotsReagent, slotsOutput,
						slotsWater
				)
		);
	}
	
	@Override
	public boolean isEnabled()
	{
		return redstoneControl.doAllowNormalOperation(this);
	}
	
	@Override
	public long consumeEu(long max, Simulation simulation)
	{
		return energy.consumeEu(max, simulation);
	}
	
	@Override
	public long getBaseMaxRecipeEu()
	{
		return tier.getMaxEu() + upgrades.getAddMaxEUPerTick();
	}
	
	@Override
	public EnergyAccess getEnergyComponent()
	{
		return energy;
	}
	
	@Override
	protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face)
	{
		ItemInteractionResult result = super.useItemOn(player, hand, face);
		if(!result.consumesAction())
		{
			result = redstoneControl.onUse(this, player, hand);
		}
		if(!result.consumesAction())
		{
			result = casing.onUse(this, player, hand);
		}
		if(!result.consumesAction())
		{
			result = upgrades.onUse(this, player, hand);
		}
		if(!result.consumesAction())
		{
			result = ModularLubricantHelper.onUse(crafter, player, hand);
		}
		return result;
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) -> ((ElectricBreweryMachineBlockEntity) be).insertable));
	}
}
