package net.swedz.extended_industrialization.machines.blockentity.brewery;

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
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.EIFluids;

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
	protected MachineInventoryComponent buildInventory()
	{
		List<ConfigurableItemStack> itemInputs = Lists.newArrayList();
		List<ConfigurableItemStack> itemOutputs = Lists.newArrayList();
		for(int i = 0; i < 9; i++)
		{
			itemInputs.add(ConfigurableItemStack.standardInputSlot());
		}
		for(int i = 0; i < 9; i++)
		{
			itemOutputs.add(ConfigurableItemStack.standardOutputSlot());
		}
		SlotPositions itemPositions = new SlotPositions.Builder()
				.addSlots(INPUT_SLOTS_X, INPUT_SLOTS_Y, 3, 3)
				.addSlots(OUTPUT_SLOTS_X, OUTPUT_SLOTS_Y, 3, 3)
				.build();
		
		List<ConfigurableFluidStack> fluidInputs = Arrays.asList(
				ConfigurableFluidStack.lockedInputSlot(capacity, MIFluids.STEAM.asFluid()),
				ConfigurableFluidStack.lockedInputSlot(capacity, EIFluids.BLAZING_ESSENCE.asFluid())
		);
		SlotPositions fluidPositions = new SlotPositions.Builder()
				.addSlot(STEAM_SLOT_X, STEAM_SLOT_Y)
				.addSlot(BLAZING_ESSENCE_SLOT_X, BLAZING_ESSENCE_SLOT_Y)
				.build();
		
		return new MachineInventoryComponent(itemInputs, itemOutputs, fluidInputs, List.of(), itemPositions, fluidPositions);
	}
	
	@Override
	public long consumeEu(long max, Simulation simulation)
	{
		return SteamHelper.consumeSteamEu(inventory.getFluidInputs(), max, simulation);
	}
	
	@Override
	public long getMaxRecipeEu()
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
		List<Component> tooltips = Lists.newArrayList();
		tooltips.addAll(overclockComponent.getTooltips());
		tooltips.addAll(super.getTooltips());
		return tooltips;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		overclockComponent.tick(this);
	}
	
	@Override
	protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face)
	{
		ItemInteractionResult result = super.useItemOn(player, hand, face);
		if(!result.consumesAction())
		{
			return overclockComponent.onUse(this, player, hand);
		}
		return result;
	}
}
