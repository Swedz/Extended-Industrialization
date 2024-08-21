package net.swedz.extended_industrialization.machines.component.fluidharvesting.honeyextractor;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.machines.component.fluidharvesting.FluidHarvestingBehavior;
import net.swedz.extended_industrialization.machines.component.fluidharvesting.FluidHarvestingBehaviorCreator;
import net.swedz.tesseract.neoforge.compat.mi.helper.EuConsumerBehavior;

import java.util.Optional;

public final class HoneyExtractorBehavior extends FluidHarvestingBehavior
{
	public static final FluidHarvestingBehaviorCreator STEEL    = (m, e) -> new HoneyExtractorBehavior(m, e, 100, 1);
	public static final FluidHarvestingBehaviorCreator ELECTRIC = (m, e) -> new HoneyExtractorBehavior(m, e, 100, 2);
	
	private BeehiveBlockEntity hive;
	
	private HoneyExtractorBehavior(MachineBlockEntity machine, EuConsumerBehavior euBehavior, int totalPumpingTicks, float outputMultiplier)
	{
		super(machine, euBehavior, totalPumpingTicks, outputMultiplier);
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
