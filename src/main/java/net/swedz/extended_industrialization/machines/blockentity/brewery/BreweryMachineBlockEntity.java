package net.swedz.extended_industrialization.machines.blockentity.brewery;

import aztech.modern_industrialization.api.machine.component.CrafterAccess;
import aztech.modern_industrialization.api.machine.holder.CrafterComponentHolder;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.MachineInventoryComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.AutoExtract;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIMachines;

import java.util.List;
import java.util.UUID;

public abstract class BreweryMachineBlockEntity extends MachineBlockEntity implements Tickable, CrafterComponentHolder, CrafterComponent.Behavior
{
	protected final MachineTier tier;
	
	protected final MachineInventoryComponent inventory;
	protected final CrafterComponent          crafter;
	
	protected IsActiveComponent isActiveComponent;
	
	public BreweryMachineBlockEntity(BEP bep, MachineTier tier, MachineGuiParameters guiParams, MachineInventoryComponent inventory)
	{
		super(bep, guiParams, new OrientationComponent.Params(true, true, false));
		
		this.tier = tier;
		
		this.inventory = inventory;
		this.crafter = new CrafterComponent(this, inventory, this);
		
		this.isActiveComponent = new IsActiveComponent();
		
		this.registerComponents(isActiveComponent, inventory, crafter);
		
		this.registerGuiComponent(new AutoExtract(orientation));
	}
	
	@Override
	public MIInventory getInventory()
	{
		return inventory.inventory;
	}
	
	@Override
	public CrafterAccess getCrafterComponent()
	{
		return crafter;
	}
	
	@Override
	public MachineRecipeType recipeType()
	{
		return EIMachines.RecipeTypes.BREWERY;
	}
	
	@Override
	public ServerLevel getCrafterWorld()
	{
		return (ServerLevel) level;
	}
	
	@Override
	public UUID getOwnerUuid()
	{
		return placedBy.placerId;
	}
	
	@Override
	public long getBaseRecipeEu()
	{
		return tier.getBaseEu();
	}
	
	@Override
	public long getMaxRecipeEu()
	{
		return tier.getMaxEu();
	}
	
	@Override
	public MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData();
		data.isActive = isActiveComponent.isActive;
		orientation.writeModelData(data);
		return data;
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide)
		{
			return;
		}
		
		this.updateActive(crafter.tickRecipe());
		
		if(orientation.extractItems)
		{
			this.getInventory().autoExtractItems(level, worldPosition, orientation.outputDirection);
		}
		this.setChanged();
	}
	
	private void updateActive(boolean active)
	{
		isActiveComponent.updateActive(active, this);
	}
	
	@Override
	public List<Component> getTooltips()
	{
		return List.of(
				EI.text().breweryRequiresBlazingEssence(EIFluids.BLAZING_ESSENCE.asFluid()),
				EI.text().breweryBrewsMultiple(4)
		);
	}
}