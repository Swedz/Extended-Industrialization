package net.swedz.extended_industrialization.datagen.server.provider.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EITags;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public final class ItemTagDatagenProvider extends ItemTagsProvider
{
	public ItemTagDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider(), CompletableFuture.completedFuture(TagLookup.empty()), EI.ID, event.getExistingFileHelper());
	}
	
	private void addFarmerVoidableDropTag()
	{
		this.tag(EITags.Items.FARMER_VOIDABLE)
				.add(
						Items.STICK,
						Items.APPLE,
						Items.MOSS_CARPET,
						Items.MANGROVE_ROOTS
				)
				.addTags(
						ItemTags.SAPLINGS
				)
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("rootsclassic", "berries"));
	}
	
	private void addFarmerStandardPlantableTag()
	{
		this.tag(EITags.Items.FARMER_PLANTABLE)
				.add(Items.KELP)
				.addTags(
						Tags.Items.SEEDS,
						Tags.Items.CROPS,
						ItemTags.VILLAGER_PLANTABLE_SEEDS,
						ItemTags.SAPLINGS
				)
				.remove(
						Items.BEETROOT,
						Items.WHEAT
				);
	}
	
	private void addEnchantmentModuleTags()
	{
		this.tag(EITags.Items.EnchantmentModules.FARMER)
				.add(EIItems.SILK_TOUCH_MODULE.asItem())
				.add(EIItems.LOOTING_MODULE.asItem());
		
		this.tag(EITags.Items.EnchantmentModules.LETHAL_TESLA_COIL)
				.add(EIItems.LOOTING_MODULE.asItem());
	}
	
	private void addGeneratedRecipesBlacklistTags()
	{
		this.tag(EITags.GeneratedRecipesBlacklist.CANNING_FOOD)
				.add(EIItems.CANNED_FOOD.asItem())
				.add(Items.OMINOUS_BOTTLE);
	}
	
	private IntrinsicTagAppender<Item> curiosTag(String path)
	{
		return this.tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", path)));
	}
	
	private void addCuriosTags()
	{
		this.curiosTag("shoulders")
				.add(EIItems.ROBOT_AUTO_FEEDER.asItem());
		
		this.curiosTag("belt")
				.add(EIItems.TESLA_HANDHELD_RECEIVER.asItem());
		
		this.curiosTag("hands")
				.add(EIItems.TESLA_HANDHELD_RECEIVER.asItem());
	}
	
	@Override
	protected void addTags(HolderLookup.Provider provider)
	{
		for(ItemHolder<?> item : EIItems.values().stream().sorted(Comparator.comparing((item) -> item.identifier().id())).toList())
		{
			for(TagKey<Item> tag : item.tags())
			{
				this.tag(tag).add(item.asItem());
			}
		}
		
		this.addFarmerVoidableDropTag();
		this.addFarmerStandardPlantableTag();
		
		this.addEnchantmentModuleTags();
		
		this.addGeneratedRecipesBlacklistTags();
		
		this.addCuriosTags();
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
