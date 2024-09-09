package net.swedz.extended_industrialization.machines.blockentity;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.AutoExtract;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import net.swedz.extended_industrialization.EIMachines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LargeConfigurableChestMachineBlockEntity extends MachineBlockEntity implements Tickable
{
	private final MIInventory inventory;
	
	public LargeConfigurableChestMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder("large_configurable_chest", true).backgroundHeight(180 + 54).build(),
				new OrientationComponent.Params(true, true, false)
		);
		
		List<ConfigurableItemStack> stacks = new ArrayList<>();
		for(int i = 0; i < 54; i++)
		{
			stacks.add(ConfigurableItemStack.standardIOSlot(true));
		}
		SlotPositions itemPositions = new SlotPositions.Builder().addSlots(8, 30, 9, 6).build();
		inventory = new MIInventory(stacks, Collections.emptyList(), itemPositions, SlotPositions.empty());
		
		this.registerGuiComponent(new AutoExtract.Server(orientation));
		this.registerComponents(inventory);
	}
	
	@Override
	public MIInventory getInventory()
	{
		return inventory;
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData(EIMachines.Casings.LARGE_STEEL_CRATE);
		orientation.writeModelData(data);
		return data;
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			return;
		}
		
		if(orientation.extractItems)
		{
			inventory.autoExtractItems(level, worldPosition, orientation.outputDirection);
		}
	}
}
