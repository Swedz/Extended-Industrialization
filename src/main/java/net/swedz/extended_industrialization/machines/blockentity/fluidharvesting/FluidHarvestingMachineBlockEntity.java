package net.swedz.extended_industrialization.machines.blockentity.fluidharvesting;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.AutoExtract;
import aztech.modern_industrialization.machines.guicomponents.ProgressBar;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.machines.component.fluidharvesting.FluidHarvestingBehavior;
import net.swedz.extended_industrialization.machines.component.fluidharvesting.FluidHarvestingBehaviorCreator;
import net.swedz.tesseract.neoforge.compat.mi.api.MachineTierHolder;
import net.swedz.tesseract.neoforge.compat.mi.helper.EuConsumerBehavior;

public abstract class FluidHarvestingMachineBlockEntity extends MachineBlockEntity implements Tickable, MachineTierHolder
{
	protected static final int OUTPUT_SLOT_X = 110;
	protected static final int OUTPUT_SLOT_Y = 30;
	
	protected final MachineTier tier;
	
	protected final long euCost;
	
	protected final FluidHarvestingBehaviorCreator behaviorCreator;
	
	protected FluidHarvestingBehavior behavior;
	
	protected final IsActiveComponent isActiveComponent;
	
	protected int pumpingTicks;
	
	public FluidHarvestingMachineBlockEntity(BEP bep, ResourceLocation blockName, MachineTier tier, long euCost, FluidHarvestingBehaviorCreator behaviorCreator)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(blockName, false).build(),
				new OrientationComponent.Params(true, false, true)
		);
		
		this.tier = tier;
		
		this.euCost = euCost;
		this.behaviorCreator = behaviorCreator;
		
		this.isActiveComponent = new IsActiveComponent();
		this.registerGuiComponent(new ProgressBar(
				new ProgressBar.Params(79, 29, "extract"),
				() -> (float) pumpingTicks / this.getFluidHarvestingBehavior().totalPumpingTicks()
		));
		
		this.registerGuiComponent(new AutoExtract(orientation));
		
		this.registerComponents(isActiveComponent, new MachineComponent()
		{
			@Override
			public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
			{
				tag.putInt("pumpingTicks", pumpingTicks);
			}
			
			@Override
			public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
			{
				pumpingTicks = tag.getInt("pumpingTicks");
			}
		});
	}
	
	@Override
	public MachineTier getMachineTier()
	{
		return tier;
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
	
	public abstract boolean isEnabled();
	
	@Override
	public void tick()
	{
		if(level.isClientSide)
		{
			return;
		}
		
		boolean active = false;
		
		if(this.isEnabled() && this.getFluidHarvestingBehavior().canOperate())
		{
			long eu = this.getFluidHarvestingBehavior().consumeEu(euCost);
			active = eu > 0;
			pumpingTicks += active ? 1 : 0;
			this.updateActive(active);
			
			if(pumpingTicks == this.getFluidHarvestingBehavior().totalPumpingTicks())
			{
				this.getFluidHarvestingBehavior().operate();
				pumpingTicks = 0;
			}
		}
		
		if(orientation.extractFluids)
		{
			this.getInventory().autoExtractFluids(level, worldPosition, orientation.outputDirection);
		}
		
		this.updateActive(active);
		this.setChanged();
	}
	
	private void updateActive(boolean active)
	{
		isActiveComponent.updateActive(active, this);
	}
	
	@Override
	public MachineModelClientData getMachineModelData()
	{
		var data = new MachineModelClientData();
		data.isActive = isActiveComponent.isActive;
		orientation.writeModelData(data);
		return data;
	}
}
