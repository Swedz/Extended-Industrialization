package net.swedz.extended_industrialization.machines.component.farmer.task.task;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.swedz.extended_industrialization.EIConfig;
import net.swedz.extended_industrialization.datamap.FertilizerPotency;
import net.swedz.extended_industrialization.machines.component.farmer.FarmerComponent;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerBlock;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerTile;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerTask;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerTaskType;
import net.swedz.extended_industrialization.network.packet.FarmerFertilizeBlockPacket;
import net.swedz.tesseract.neoforge.compat.mi.helper.MachineInventoryHelper;

import java.util.List;

public final class FertilizingFarmerTask extends FarmerTask
{
	private int fertilizerTickRate;
	private int fertilizerTicks;
	
	public FertilizingFarmerTask(FarmerComponent component)
	{
		super(FarmerTaskType.FERTLIZING, component);
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
					.filter((cropBlock) -> cropBlock.state(level).isRandomlyTicking())
					.toList();
			
			if(crops.isEmpty())
			{
				return false;
			}
			
			FarmerBlock crop = crops.get(level.getRandom().nextInt(crops.size()));
			BlockPos pos = crop.pos();
			BlockState state = crop.state(level);
			int randomTicks = 0;
			BlockState modifiedState = state;
			do
			{
				modifiedState.randomTick((ServerLevel) level, pos, level.getRandom());
				modifiedState = level.getBlockState(pos);
				randomTicks++;
			}
			while(randomTicks < EIConfig.farmerFertilizerMaxRandomTicks && modifiedState.isRandomlyTicking());
			
			new FarmerFertilizeBlockPacket(pos).broadcastToClients((ServerLevel) level, pos, 32);
			
			fertilizerTicks--;
			
			if(operations.operate())
			{
				return true;
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
