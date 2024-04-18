package net.swedz.miextended.mi.machines.blockentities.honeyextractor;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.ProgressBar;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.miextended.fluids.MIEFluids;

import java.util.List;
import java.util.Optional;

public abstract class HoneyExtractorMachineBlockEntity extends MachineBlockEntity implements Tickable
{
	protected static final int OUTPUT_SLOT_X = 110;
	protected static final int OUTPUT_SLOT_Y = 30;
	
	private static final int OPERATION_TICKS = 100;
	
	protected int pumpingTicks;
	
	protected final IsActiveComponent isActiveComponent;
	
	public HoneyExtractorMachineBlockEntity(BEP bep, String blockName)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(blockName, false).build(),
				new OrientationComponent.Params(true, false, false)
		);
		
		this.isActiveComponent = new IsActiveComponent();
		this.registerGuiComponent(new ProgressBar.Server(
				new ProgressBar.Parameters(79, 29, "extract"),
				() -> (float) pumpingTicks / OPERATION_TICKS
		));
		
		this.registerComponents(isActiveComponent, new IComponent()
		{
			@Override
			public void writeNbt(CompoundTag tag)
			{
				tag.putInt("pumpingTicks", pumpingTicks);
			}
			
			@Override
			public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
			{
				pumpingTicks = tag.getInt("pumpingTicks");
			}
		});
	}
	
	protected abstract long consumeEu(long max);
	
	protected abstract int getHoneyMultiplier();
	
	@Override
	public void tick()
	{
		if(level.isClientSide)
		{
			return;
		}
		
		List<ConfigurableFluidStack> fluidStacks = this.getInventory().getFluidStacks();
		ConfigurableFluidStack honeyStack = fluidStacks.get(fluidStacks.size() - 1);
		
		if(honeyStack.getRemainingSpace() < FluidType.BUCKET_VOLUME / 5)
		{
			this.updateActive(false);
			return;
		}
		
		Optional<BeehiveBlockEntity> optionalHive = this.getHive();
		if(optionalHive.isEmpty())
		{
			pumpingTicks = 0;
			this.updateActive(false);
			return;
		}
		BeehiveBlockEntity hive = optionalHive.get();
		BlockState hiveBlockState = hive.getBlockState();
		
		long eu = this.consumeEu(1);
		pumpingTicks += eu;
		this.updateActive(eu > 0);
		
		if(pumpingTicks == OPERATION_TICKS)
		{
			int honeyLevel = hiveBlockState.getValue(BeehiveBlock.HONEY_LEVEL);
			
			if(honeyLevel > 0)
			{
				level.setBlock(hive.getBlockPos(), hiveBlockState.setValue(BeehiveBlock.HONEY_LEVEL, honeyLevel - 1), 1 | 2);
				
				long honeyToCollect = Math.min((long) this.getHoneyMultiplier() * FluidType.BUCKET_VOLUME / 5, honeyStack.getRemainingSpace());
				
				honeyStack.setKey(FluidVariant.of(MIEFluids.HONEY.asFluid()));
				honeyStack.increment(honeyToCollect);
			}
			
			pumpingTicks = 0;
		}
		
		this.getInventory().autoExtractFluids(level, worldPosition, orientation.outputDirection);
		this.setChanged();
	}
	
	private void updateActive(boolean active)
	{
		isActiveComponent.updateActive(active, this);
	}
	
	private Optional<BeehiveBlockEntity> getHive()
	{
		BlockPos touchingBlockPos = worldPosition.relative(orientation.facingDirection);
		BlockEntity touchingBlockEntity = level.getBlockEntity(touchingBlockPos);
		return touchingBlockEntity instanceof BeehiveBlockEntity beehive ? Optional.of(beehive) : Optional.empty();
	}
}
