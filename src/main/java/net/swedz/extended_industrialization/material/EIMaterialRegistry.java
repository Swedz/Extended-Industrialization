package net.swedz.extended_industrialization.material;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIBlocks;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.tesseract.neoforge.material.MaterialRegistry;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

public final class EIMaterialRegistry extends MaterialRegistry
{
	private static final EIMaterialRegistry INSTANCE = new EIMaterialRegistry();
	
	public static EIMaterialRegistry get()
	{
		return INSTANCE;
	}
	
	public static void init()
	{
		EIMaterials.init();
	}
	
	private EIMaterialRegistry()
	{
	}
	
	@Override
	public String modId()
	{
		return EI.ID;
	}
	
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
	public void onBlockRegister(BlockHolder holder)
	{
		EIBlocks.Registry.include(holder);
	}
	
	@Override
	public void onBlockEntityRegister(BlockEntityType<?> type)
	{
	}
	
	@Override
	public void onItemRegister(ItemHolder holder)
	{
		EIItems.Registry.include(holder);
	}
}
