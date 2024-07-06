package net.swedz.extended_industrialization.machines.blockentities.brewery;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.MachineInventoryComponent;
import aztech.modern_industrialization.machines.components.OverclockComponent;
import aztech.modern_industrialization.machines.guicomponents.GunpowderOverclockGui;
import aztech.modern_industrialization.machines.helper.SteamHelper;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.machines.components.craft.potion.PotionCrafterComponent;
import net.swedz.extended_industrialization.machines.components.craft.potion.PotionCrafterComponent.SlotRange;

import java.util.Arrays;
import java.util.List;

public final class SteamBreweryMachineBlockEntity extends BreweryMachineBlockEntity
{
	private final OverclockComponent overclockComponent;
	
	public SteamBreweryMachineBlockEntity(BEP bep, boolean bronze)
	{
		super(bep, (bronze ? "bronze" : "steel") + "_brewery", bronze ? MachineTier.BRONZE : MachineTier.STEEL, (bronze ? 8 : 16) * FluidType.BUCKET_VOLUME);
		
		this.overclockComponent = new OverclockComponent(OverclockComponent.getDefaultCatalysts()); // TODO allow kjs to hook into this
		GunpowderOverclockGui.Parameters gunpowderOverclockGuiParams = new GunpowderOverclockGui.Parameters(PROGRESS_BAR_X, PROGRESS_BAR_Y + 20);
		this.registerGuiComponent(new GunpowderOverclockGui.Server(gunpowderOverclockGuiParams, overclockComponent::getTicks));
		this.registerComponents(overclockComponent);
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
		itemInputs.add(ConfigurableItemStack.standardInputSlot());
		for(int i = 0; i < 9; i++)
		{
			itemOutputs.add(new PotionConfigurableItemStack());
		}
		SlotPositions itemPositions = new SlotPositions.Builder()
				.addSlot(BLAZING_ESSENCE_SLOT_X, BLAZING_ESSENCE_SLOT_Y)
				.addSlots(INPUT_BOTTLE_SLOTS_X, INPUT_BOTTLE_SLOTS_Y, 3, 3)
				.addSlot(INPUT_REAGENT_SLOTS_X + 27, INPUT_REAGENT_SLOTS_Y)
				.addSlots(OUTPUT_SLOTS_X, OUTPUT_SLOTS_Y, 3, 3)
				.build();
		SlotRange<ConfigurableItemStack> slotsBlazePowder = SlotRange.item(0);
		SlotRange<ConfigurableItemStack> slotsBottle = SlotRange.item(1, 9);
		SlotRange<ConfigurableItemStack> slotsReagent = SlotRange.item(10);
		SlotRange<ConfigurableItemStack> slotsOutput = SlotRange.item(0, 8);
		
		List<ConfigurableFluidStack> fluidInputs = Arrays.asList(
				ConfigurableFluidStack.lockedInputSlot(capacity, MIFluids.STEAM.asFluid()),
				ConfigurableFluidStack.lockedInputSlot(capacity, Fluids.WATER)
		);
		SlotPositions fluidPositions = new SlotPositions.Builder()
				.addSlot(STEAM_SLOT_X, STEAM_SLOT_Y)
				.addSlot(WATER_SLOT_X, WATER_SLOT_Y)
				.build();
		SlotRange<ConfigurableFluidStack> slotsWater = SlotRange.fluid(1);
		
		return Pair.of(
				new MachineInventoryComponent(itemInputs, itemOutputs, fluidInputs, List.of(), itemPositions, fluidPositions),
				new PotionCrafterComponent.Params(
						slotsBlazePowder, slotsBottle, slotsReagent, slotsOutput,
						slotsWater
				)
		);
	}
	
	@Override
	public long consumeEu(long max, Simulation simulation)
	{
		return SteamHelper.consumeSteamEu(inventory.getFluidInputs(), max, simulation);
	}
	
	@Override
	public long getBaseMaxRecipeEu()
	{
		return overclockComponent.getRecipeEu(tier.getMaxEu());
	}
	
	@Override
	public long getBaseRecipeEu()
	{
		return overclockComponent.getRecipeEu(tier.getBaseEu());
	}
	
	@Override
	public List<Component> getTooltips()
	{
		return overclockComponent.getTooltips();
	}
	
	@Override
	public void tick()
	{
		super.tick();
		overclockComponent.tick(this);
	}
	
	@Override
	protected InteractionResult onUse(Player player, InteractionHand hand, Direction face)
	{
		InteractionResult result = super.onUse(player, hand, face);
		if(!result.consumesAction())
		{
			return overclockComponent.onUse(this, player, hand);
		}
		return result;
	}
}
