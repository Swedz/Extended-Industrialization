package net.swedz.extended_industrialization.machines.blockentities.fluidharvesting;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.AutoExtract;
import aztech.modern_industrialization.machines.guicomponents.ProgressBar;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.api.EuConsumerBehavior;

public abstract class FluidHarvestingMachineBlockEntity extends MachineBlockEntity implements Tickable
{
	protected static final int OUTPUT_SLOT_X = 110;
	protected static final int OUTPUT_SLOT_Y = 30;
	
	protected final long euCost;
	
	protected final FluidHarvestingBehaviorCreator behaviorCreator;
	
	protected FluidHarvestingBehavior behavior;
	
	protected final IsActiveComponent isActiveComponent;
	
	protected int pumpingTicks;
	
	public FluidHarvestingMachineBlockEntity(BEP bep, String blockName, long euCost, FluidHarvestingBehaviorCreator behaviorCreator)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(blockName, false).build(),
				new OrientationComponent.Params(true, false, true)
		);
		
		this.euCost = euCost;
		this.behaviorCreator = behaviorCreator;
		
		this.isActiveComponent = new IsActiveComponent();
		this.registerGuiComponent(new ProgressBar.Server(
				new ProgressBar.Parameters(79, 29, "extract"),
				() -> (float) pumpingTicks / this.getFluidHarvestingBehavior().totalPumpingTicks()
		));
		
		this.registerGuiComponent(new AutoExtract.Server(orientation));
		
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
	
	protected abstract EuConsumerBehavior createEuConsumerBehavior();
	
	public FluidHarvestingBehavior getFluidHarvestingBehavior()
	{
		if(behavior == null)
		{
			behavior = behaviorCreator.create(this, this.createEuConsumerBehavior());
		}
		return behavior;
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide)
		{
			return;
		}
		
		ConfigurableFluidStack fluidStack = this.getFluidHarvestingBehavior().getMachineBlockFluidStack();
		if(fluidStack.getRemainingSpace() < FluidType.BUCKET_VOLUME / 5)
		{
			this.updateActive(false);
			return;
		}
		
		if(!this.getFluidHarvestingBehavior().canOperate())
		{
			pumpingTicks = 0;
			this.updateActive(false);
			return;
		}
		
		long eu = this.getFluidHarvestingBehavior().consumeEu(euCost);
		boolean active = eu > 0;
		pumpingTicks += active ? 1 : 0;
		this.updateActive(active);
		
		if(pumpingTicks == this.getFluidHarvestingBehavior().totalPumpingTicks())
		{
			this.getFluidHarvestingBehavior().operate();
			pumpingTicks = 0;
		}
		
		if(orientation.extractFluids)
		{
			this.getInventory().autoExtractFluids(level, worldPosition, orientation.outputDirection);
		}
		this.setChanged();
	}
	
	private void updateActive(boolean active)
	{
		isActiveComponent.updateActive(active, this);
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData();
		data.isActive = isActiveComponent.isActive;
		orientation.writeModelData(data);
		return data;
	}
}
