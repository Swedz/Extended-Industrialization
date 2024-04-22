package net.swedz.miextended.machines.components.farmer.task.tasks;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.swedz.miextended.api.MachineInventoryHelper;
import net.swedz.miextended.datamaps.FertilizerPotency;
import net.swedz.miextended.machines.components.farmer.FarmerComponentPlantableStacks;
import net.swedz.miextended.machines.components.farmer.block.FarmerBlock;
import net.swedz.miextended.machines.components.farmer.block.FarmerBlockMap;
import net.swedz.miextended.machines.components.farmer.block.FarmerTile;
import net.swedz.miextended.machines.components.farmer.task.FarmerTask;
import net.swedz.miextended.machines.components.farmer.task.FarmerTaskType;

import java.util.List;

public final class FertilizingFarmerTask extends FarmerTask
{
	private int fertilizerTickRate;
	private int fertilizerTicks;
	
	public FertilizingFarmerTask(MultiblockInventoryComponent inventory, FarmerBlockMap blockMap, FarmerComponentPlantableStacks plantableStacks, int maxOperations, int processInterval)
	{
		super(FarmerTaskType.FERTLIZING, inventory, blockMap, plantableStacks, maxOperations, processInterval);
	}
	
	private static boolean tryConsume(MultiblockInventoryComponent inventory, Fluid fluid, long amount, Simulation simulation)
	{
		return MachineInventoryHelper.consumeFluid(inventory.getFluidInputs(), fluid, amount, simulation) == amount;
	}
	
	private static FertilizerPotency tryConsumeFertilizer(MultiblockInventoryComponent inventory, Simulation simulation)
	{
		for(ConfigurableFluidStack fluidInput : inventory.getFluidInputs())
		{
			Fluid fluid = fluidInput.getResource().getFluid();
			FertilizerPotency potency = FertilizerPotency.getFor(fluid);
			if(potency != null && tryConsume(inventory, fluid, potency.mbToConsumePerFertilizerTick(), simulation))
			{
				return potency;
			}
		}
		return null;
	}
	
	@Override
	protected boolean run()
	{
		if(fertilizerTicks <= 0 || fertilizerTickRate <= 0)
		{
			FertilizerPotency potency = tryConsumeFertilizer(inventory, Simulation.ACT);
			if(potency != null)
			{
				fertilizerTickRate = potency.tickRate();
				fertilizerTicks = 1;
			}
		}
		
		if(fertilizerTicks > 0 && fertilizerTickRate > 0 && processTick % fertilizerTickRate == 0)
		{
			List<FarmerBlock> crops = blockMap.tiles().stream()
					.map(FarmerTile::crop)
					.filter((cropBlock) -> cropBlock.state(level).getBlock() instanceof BonemealableBlock)
					.toList();
			
			if(crops.size() == 0)
			{
				return false;
			}
			
			FarmerBlock crop = crops.get(level.getRandom().nextInt(crops.size() - 1));
			BlockPos pos = crop.pos();
			BlockState state = crop.state(level);
			BonemealableBlock bonemealable = (BonemealableBlock) state.getBlock();
			
			if(bonemealable.isValidBonemealTarget(level, pos, state) && bonemealable.isBonemealSuccess(level, level.getRandom(), pos, state))
			{
				bonemealable.performBonemeal((ServerLevel) level, level.getRandom(), pos, state);
				level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, 0);
				
				fertilizerTicks--;
				
				if(operations.operate())
				{
					return true;
				}
			}
		}
		
		return operations.didOperate();
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
		tag.putInt("fertilizer_tick_rate", fertilizerTickRate);
		tag.putInt("fertilizer_ticks", fertilizerTicks);
	}
	
	@Override
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
		fertilizerTickRate = tag.getInt("fertilizer_tick_rate");
		fertilizerTicks = tag.getInt("fertilizer_ticks");
	}
}
