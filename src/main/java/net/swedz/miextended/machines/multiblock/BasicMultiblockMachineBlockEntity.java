package net.swedz.miextended.machines.multiblock;

import aztech.modern_industrialization.api.machine.component.InventoryAccess;
import aztech.modern_industrialization.api.machine.holder.MultiblockInventoryComponentHolder;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.ActiveShapeComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.util.Tickable;

public abstract class BasicMultiblockMachineBlockEntity extends MultiblockMachineBlockEntity implements Tickable, MultiblockInventoryComponentHolder
{
	private ShapeMatcher shapeMatcher;
	
	protected OperatingState operatingState = OperatingState.NOT_MATCHED;
	
	protected final ActiveShapeComponent         activeShape;
	protected final MultiblockInventoryComponent inventory;
	protected final IsActiveComponent            isActive;
	
	public BasicMultiblockMachineBlockEntity(BEP bep, MachineGuiParameters guiParams, ShapeTemplate[] shapeTemplates)
	{
		super(bep, guiParams, new OrientationComponent.Params(false, false, false));
		
		this.activeShape = new ActiveShapeComponent(shapeTemplates);
		this.inventory = new MultiblockInventoryComponent();
		this.isActive = new IsActiveComponent();
		
		this.registerComponents(activeShape, isActive);
	}
	
	public void onSuccessfulMatch(ShapeMatcher shapeMatcher)
	{
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide)
		{
			return;
		}
		
		this.link();
	}
	
	private void link()
	{
		if(shapeMatcher == null)
		{
			shapeMatcher = new ShapeMatcher(level, worldPosition, orientation.facingDirection, this.getActiveShape());
			shapeMatcher.registerListeners(level);
		}
		if(shapeMatcher.needsRematch())
		{
			operatingState = OperatingState.NOT_MATCHED;
			shapeValid.shapeValid = false;
			shapeMatcher.rematch(level);
			
			if(shapeMatcher.isMatchSuccessful())
			{
				inventory.rebuild(shapeMatcher);
				
				this.onSuccessfulMatch(shapeMatcher);
				
				shapeValid.shapeValid = true;
				operatingState = OperatingState.TRYING_TO_RESUME;
			}
			
			if(shapeValid.update())
			{
				this.sync(false);
			}
		}
	}
	
	@Override
	public void unlink()
	{
		if(shapeMatcher != null)
		{
			shapeMatcher.unlinkHatches();
			shapeMatcher.unregisterListeners(level);
			shapeMatcher = null;
		}
	}
	
	@Override
	public ShapeTemplate getActiveShape()
	{
		return activeShape.getActiveShape();
	}
	
	@Override
	public MIInventory getInventory()
	{
		return MIInventory.EMPTY;
	}
	
	@Override
	public InventoryAccess getMultiblockInventoryComponent()
	{
		return inventory;
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		return new MachineModelClientData(null, orientation.facingDirection).active(isActive.isActive);
	}
	
	public enum OperatingState
	{
		/**
		 * Shape is not matched, don't do anything.
		 */
		NOT_MATCHED,
		/**
		 * Trying to resume a recipe but the output might not fit anymore.
		 * We wait until the output fits again before resuming normal operation.
		 */
		TRYING_TO_RESUME,
		/**
		 * Normal operation.
		 */
		NORMAL_OPERATION
	}
}
