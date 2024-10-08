package net.swedz.extended_industrialization.machines.blockentity;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.AutoExtract;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.BufferedItemInventoryComponent;
import net.swedz.tesseract.neoforge.capabilities.CapabilitiesListeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ItemBufferMachineBlockEntity extends MachineBlockEntity implements Tickable
{
	private final MIInventory inventory;
	
	private final BufferedItemInventoryComponent bufferedInventory;
	
	public ItemBufferMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder("item_buffer", true).backgroundHeight(144).build(),
				new OrientationComponent.Params(true, true, false)
		);
		
		List<ConfigurableItemStack> stacks = new ArrayList<>();
		for(int i = 0; i < 9; i++)
		{
			stacks.add(ConfigurableItemStack.standardIOSlot(true));
		}
		SlotPositions itemPositions = new SlotPositions.Builder().addSlots(8, 30, 9, 1).build();
		inventory = new MIInventory(stacks, Collections.emptyList(), itemPositions, SlotPositions.empty());
		
		bufferedInventory = new BufferedItemInventoryComponent(inventory.itemStorage.itemHandler);
		
		this.registerGuiComponent(new AutoExtract.Server(orientation));
		this.registerComponents(inventory, bufferedInventory);
	}
	
	@Override
	public MIInventory getInventory()
	{
		return inventory;
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData(MachineCasings.STEEL);
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
			bufferedInventory.autoExtractItems(level, worldPosition, orientation.outputDirection);
		}
	}
	
	public static void registerItemApi(BlockEntityType<?> bet)
	{
		CapabilitiesListeners.register(EI.ID, (event) -> event.registerBlockEntity(
				Capabilities.ItemHandler.BLOCK, bet,
				(be, direction) -> ((ItemBufferMachineBlockEntity) be).bufferedInventory.itemHandler()
		));
	}
}
