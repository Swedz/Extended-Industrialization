package net.swedz.extended_industrialization.machines.blockentities.fluidharvesting.honeyextractor;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.api.EuConsumerBehavior;
import net.swedz.extended_industrialization.machines.blockentities.fluidharvesting.FluidHarvestingBehavior;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;

import java.util.Optional;

public final class HoneyExtractorBehavior extends FluidHarvestingBehavior
{
	public static HoneyExtractorBehavior steam(MachineBlockEntity machine)
	{
		return new HoneyExtractorBehavior(machine, EuConsumerBehavior.steam(machine), 1);
	}
	
	public static HoneyExtractorBehavior electric(MachineBlockEntity machine, EnergyComponent energy, RedstoneControlComponent redstone)
	{
		return new HoneyExtractorBehavior(machine, EuConsumerBehavior.electric(machine, energy, redstone), 2);
	}
	
	private BeehiveBlockEntity hive;
	
	private HoneyExtractorBehavior(MachineBlockEntity machine, EuConsumerBehavior euBehavior, float outputMultiplier)
	{
		super(machine, euBehavior, 100, outputMultiplier);
	}
	
	@Override
	public boolean canOperate()
	{
		Optional<BeehiveBlockEntity> optionalHive = this.getHive();
		if(optionalHive.isEmpty())
		{
			return false;
		}
		hive = optionalHive.get();
		return true;
	}
	
	@Override
	public void operate()
	{
		if(hive == null)
		{
			throw new IllegalStateException("Called operate with no hive found");
		}
		
		MachineBlockEntity machine = this.getMachineBlockEntity();
		BlockState hiveBlockState = hive.getBlockState();
		
		int honeyLevel = hiveBlockState.getValue(BeehiveBlock.HONEY_LEVEL);
		
		if(honeyLevel > 0)
		{
			ConfigurableFluidStack fluidStack = this.getMachineBlockFluidStack();
			
			machine.getLevel().setBlock(hive.getBlockPos(), hiveBlockState.setValue(BeehiveBlock.HONEY_LEVEL, honeyLevel - 1), 1 | 2);
			
			long honeyToCollect = Math.min((long) this.getOutputMultiplier() * FluidType.BUCKET_VOLUME / 5, fluidStack.getRemainingSpace());
			
			fluidStack.setKey(FluidVariant.of(EIFluids.HONEY.asFluid()));
			fluidStack.increment(honeyToCollect);
		}
	}
	
	private Optional<BeehiveBlockEntity> getHive()
	{
		MachineBlockEntity machine = this.getMachineBlockEntity();
		BlockPos touchingBlockPos = machine.getBlockPos().relative(machine.orientation.facingDirection);
		BlockEntity touchingBlockEntity = machine.getLevel().getBlockEntity(touchingBlockPos);
		return touchingBlockEntity instanceof BeehiveBlockEntity beehive ? Optional.of(beehive) : Optional.empty();
	}
}
