package net.swedz.extended_industrialization.compat.mi;

import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.EIBlocks;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EIOtherRegistries;
import net.swedz.extended_industrialization.EISortOrder;
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHookRegistry;
import net.swedz.tesseract.neoforge.compat.mi.hook.TesseractMIHookEntrypoint;
import net.swedz.tesseract.neoforge.registry.SortOrder;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

@TesseractMIHookEntrypoint
public final class EIMIHookRegistry implements MIHookRegistry
{
	@Override
	public DeferredRegister.Blocks blockRegistry()
	{
		return EIBlocks.Registry.BLOCKS;
	}
	
	@Override
	public DeferredRegister<BlockEntityType<?>> blockEntityRegistry()
	{
		return EIBlocks.Registry.BLOCK_ENTITIES;
	}
	
	@Override
	public DeferredRegister.Items itemRegistry()
	{
		return EIItems.Registry.ITEMS;
	}
	
	@Override
	public DeferredRegister<RecipeSerializer<?>> recipeSerializerRegistry()
	{
		return EIOtherRegistries.RECIPE_SERIALIZERS;
	}
	
	@Override
	public DeferredRegister<RecipeType<?>> recipeTypeRegistry()
	{
		return EIOtherRegistries.RECIPE_TYPES;
	}
	
	@Override
	public void onBlockRegister(BlockHolder blockHolder)
	{
		EIBlocks.Registry.include(blockHolder);
	}
	
	@Override
	public void onBlockEntityRegister(BlockEntityType<?> blockEntityType)
	{
	}
	
	@Override
	public void onItemRegister(ItemHolder itemHolder)
	{
		EIItems.Registry.include(itemHolder);
	}
	
	@Override
	public void onMachineRecipeTypeRegister(MachineRecipeType machineRecipeType)
	{
	}
	
	@Override
	public SortOrder sortOrderMachines()
	{
		return EISortOrder.MACHINES;
	}
}
