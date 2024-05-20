package net.swedz.extended_industrialization.registry.api;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.swedz.extended_industrialization.registry.blocks.BlockHolder;
import net.swedz.extended_industrialization.registry.items.ItemHolder;

import java.util.function.Consumer;

public final class CommonModelBuilders
{
	public static Consumer<ItemModelBuilder> generated(ItemHolder item, String texture)
	{
		return (builder) -> builder
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture("layer0", new ResourceLocation(item.identifier().modId(), "item/" + texture));
	}
	
	public static Consumer<ItemModelBuilder> generated(ItemHolder item)
	{
		return generated(item, item.identifier().id());
	}
	
	public static Consumer<ItemModelBuilder> handheld(ItemHolder item, String texture)
	{
		return (builder) -> builder
				.parent(new ModelFile.UncheckedModelFile("item/handheld"))
				.texture("layer0", new ResourceLocation(item.identifier().modId(), "item/" + texture));
	}
	
	public static Consumer<ItemModelBuilder> handheld(ItemHolder item)
	{
		return handheld(item, item.identifier().id());
	}
	
	public static Consumer<ItemModelBuilder> block(ItemHolder item)
	{
		return (builder) -> builder
				.parent(new ModelFile.UncheckedModelFile("%s:block/%s".formatted(item.identifier().modId(), item.identifier().id())));
	}
	
	public static Consumer<BlockStateProvider> blockstateOnly(BlockHolder block)
	{
		return (builder) -> builder
				.simpleBlock(block.get(), builder.models().getExistingFile(builder.modLoc("block/%s".formatted(block.identifier().id()))));
	}
	
	public static Consumer<BlockStateProvider> blockCubeAll(BlockHolder block)
	{
		return (builder) -> builder
				.simpleBlockWithItem(block.get(), builder.cubeAll(block.get()));
	}
}
