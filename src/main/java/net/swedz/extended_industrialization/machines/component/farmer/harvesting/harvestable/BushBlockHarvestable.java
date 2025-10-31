package net.swedz.extended_industrialization.machines.component.farmer.harvesting.harvestable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.HarvestingContext;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.LootTableHarvestableBehavior;

import java.util.List;
import java.util.Map;

/**
 * <p>This covers {@link CropBlock}, {@link NetherWartBlock}, {@link SweetBerryBushBlock}, or any other block that
 * inherits {@link BushBlock} and has an {@link IntegerProperty} with the name <code>age</code>.</p>
 */
@EventBusSubscriber(modid = EI.ID)
public final class BushBlockHarvestable implements LootTableHarvestableBehavior
{
	private static final Map<BlockState, IntegerProperty> AGEABLE_BLOCKS = Maps.newConcurrentMap();
	
	@SubscribeEvent
	private static void onLoad(FMLCommonSetupEvent event)
	{
		event.enqueueWork(() ->
		{
			// Cache the valid blocks so we dont have to search through all the properties of each block every time
			for(var block : BuiltInRegistries.BLOCK)
			{
				if(block instanceof BushBlock)
				{
					var defaultState = block.defaultBlockState();
					// Check that this block has the age property
					var ageProperty = getNamedIntegerProperty(defaultState, "age");
					if(ageProperty != null)
					{
						// Ignore blocks with a "stage" property, like the mangrove propagule (or other potential weird modded saplings)
						if(getNamedIntegerProperty(defaultState, "stage") != null)
						{
							continue;
						}
						for(var state : block.getStateDefinition().getPossibleStates())
						{
							AGEABLE_BLOCKS.put(state, ageProperty);
						}
					}
				}
			}
			EI.LOGGER.info("Discovered {} block states for BushBlockHarvestable", AGEABLE_BLOCKS.size());
		});
	}
	
	private static IntegerProperty getNamedIntegerProperty(BlockState state, String name)
	{
		for(var property : state.getProperties())
		{
			if(property.getName().equals(name) && property instanceof IntegerProperty ageProperty)
			{
				return ageProperty;
			}
		}
		return null;
	}
	
	@Override
	public boolean matches(HarvestingContext context)
	{
		return context.state().getBlock() instanceof BushBlock &&
			   AGEABLE_BLOCKS.containsKey(context.state());
	}
	
	@Override
	public boolean isFullyGrown(HarvestingContext context)
	{
		var property = AGEABLE_BLOCKS.get(context.state());
		return property != null &&
			   context.state().getValue(property) == property.max;
	}
	
	@Override
	public List<BlockPos> getBlocks(HarvestingContext context)
	{
		return Lists.newArrayList(context.pos());
	}
}
