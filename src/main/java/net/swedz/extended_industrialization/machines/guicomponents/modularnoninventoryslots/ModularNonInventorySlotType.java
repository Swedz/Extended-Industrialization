package net.swedz.extended_industrialization.machines.guicomponents.modularnoninventoryslots;

import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.blockentities.ElectricCraftingMachineBlockEntity;
import com.google.common.collect.Maps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.text.EIText;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public final class ModularNonInventorySlotType
{
	private static final Map<ResourceLocation, ModularNonInventorySlotType> SLOTS = Maps.newHashMap();
	
	public static final ModularNonInventorySlotType MACHINE = register(
			EI.id("machine"),
			(stack) ->
					stack.getItem() instanceof BlockItem blockItem &&
					blockItem.getBlock() instanceof MachineBlock machineBlock &&
					machineBlock.getBlockEntityInstance() instanceof ElectricCraftingMachineBlockEntity,
			0, 0,
			EIText.ADVANCED_ASSEMBLER_MACHINE_INPUT.text().withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xA9A9A9)))
	);
	
	private static ModularNonInventorySlotType register(
			ResourceLocation id,
			Predicate<ItemStack> insertionChecker,
			int u, int v,
			Component tooltip
	)
	{
		ModularNonInventorySlotType type = new ModularNonInventorySlotType(id, insertionChecker, u, v, tooltip);
		SLOTS.put(id, type);
		return type;
	}
	
	public static ModularNonInventorySlotType get(ResourceLocation id)
	{
		if(!SLOTS.containsKey(id))
		{
			throw new IllegalArgumentException("No slot type is registered for that id");
		}
		return SLOTS.get(id);
	}
	
	private final ResourceLocation id;
	private final Predicate<ItemStack> insertionChecker;
	private final int u;
	private final int v;
	private final Component tooltip;
	
	private ModularNonInventorySlotType(
			ResourceLocation id,
			Predicate<ItemStack> insertionChecker,
			int u, int v,
			Component tooltip
	)
	{
		this.id = id;
		this.insertionChecker = insertionChecker;
		this.u = u;
		this.v = v;
		this.tooltip = tooltip;
	}
	
	public ResourceLocation id()
	{
		return id;
	}
	
	public Predicate<ItemStack> insertionChecker()
	{
		return insertionChecker;
	}
	
	public int u()
	{
		return u;
	}
	
	public int v()
	{
		return v;
	}
	
	public Component tooltip()
	{
		return tooltip;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this) return true;
		if(obj == null || obj.getClass() != this.getClass()) return false;
		ModularNonInventorySlotType other = (ModularNonInventorySlotType) obj;
		return Objects.equals(this.id, other.id);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}
	
	@Override
	public String toString()
	{
		return id.toString();
	}
}
