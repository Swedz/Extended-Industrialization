package net.swedz.extended_industrialization.tooltips;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.datamaps.PhotovoltaicCell;
import net.swedz.extended_industrialization.machines.blockentities.multiblock.LargeElectricFurnaceBlockEntity;
import net.swedz.extended_industrialization.machines.components.craft.multiplied.EuCostTransformer;
import net.swedz.extended_industrialization.registry.items.EIItems;
import net.swedz.extended_industrialization.text.EIText;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;

import static aztech.modern_industrialization.MITooltips.*;

public final class EITooltips
{
	public static final Parser<MutableComponent> MULCH_GANG_FOR_LIFE_PARSER = (component) ->
			component.withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
	
	public static final Parser<Float> RATIO_PERCENTAGE_PARSER = (ratio) ->
			Component.literal("%d%%".formatted((int) (ratio * 100))).withStyle(NUMBER_TEXT);
	
	public static final Parser<MachineRecipeType> MACHINE_RECIPE_TYPE_PARSER = (recipeType) ->
			Component.translatable("recipe_type.%s.%s".formatted(recipeType.getId().getNamespace(), recipeType.getPath())).withStyle(NUMBER_TEXT);
	
	public static final Parser<EuCostTransformer> EU_COST_TRANSFORMER_PARSER = (euCostTransformer) ->
			euCostTransformer.text().withStyle(NUMBER_TEXT);
	
	public static final Parser<Long> TICKS_TO_HOURS_PARSER = (ticks) ->
			Component.literal("%.1f".formatted((float) ticks / (60 * 60 * 20))).withStyle(NUMBER_TEXT);
	
	public static final TooltipAttachment ENERGY_STORED_ITEM = TooltipAttachment.of(
			(itemStack, item) ->
			{
				if(BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(EI.ID))
				{
					var energyStorage = itemStack.getCapability(EnergyApi.ITEM);
					if(energyStorage != null)
					{
						long capacity = energyStorage.getCapacity();
						if(capacity > 0)
						{
							return Optional.of(new Line(MIText.EnergyStored)
									.arg(new NumberWithMax(energyStorage.getAmount(), capacity), EU_MAXED_PARSER).build());
						}
					}
				}
				return Optional.empty();
			}).noShiftRequired();
	
	public static final TooltipAttachment MULCH_GANG_FOR_LIFE = TooltipAttachment.ofMultilines(
			EIItems.MULCH,
			List.of(
					MULCH_GANG_FOR_LIFE_PARSER.parse(EIText.MULCH_GANG_FOR_LIFE_0.text()),
					MULCH_GANG_FOR_LIFE_PARSER.parse(EIText.MULCH_GANG_FOR_LIFE_1.text())
			)
	).noShiftRequired();
	
	public static final TooltipAttachment COILS = TooltipAttachment.of(
			(itemStack, item) ->
			{
				if(item instanceof BlockItem blockItem && LargeElectricFurnaceBlockEntity.getTiersByCoil().containsKey(BuiltInRegistries.BLOCK.getKey(blockItem.getBlock())))
				{
					LargeElectricFurnaceBlockEntity.Tier tier = LargeElectricFurnaceBlockEntity.getTiersByCoil()
							.get(BuiltInRegistries.BLOCK.getKey(((BlockItem) itemStack.getItem()).getBlock()));
					int batchSize = tier.batchSize();
					float euCostMultiplier = tier.euCostMultiplier();
					return Optional.of(DEFAULT_PARSER.parse(EIText.COILS_LEF_TIER.text(DEFAULT_PARSER.parse(batchSize), RATIO_PERCENTAGE_PARSER.parse(euCostMultiplier))));
				}
				else
				{
					return Optional.empty();
				}
			});
	
	public static final TooltipAttachment PHOTOVOLTAIC_CELLS = TooltipAttachment.ofMultilines(
			(itemStack, item) ->
			{
				PhotovoltaicCell photovoltaicCell = PhotovoltaicCell.getFor(item);
				if(photovoltaicCell != null)
				{
					int euPerTick = photovoltaicCell.euPerTick();
					List<Component> lines = Lists.newArrayList();
					lines.add(DEFAULT_PARSER.parse(EIText.PHOTOVOLTAIC_CELL_EU.text(EU_PER_TICK_PARSER.parse(euPerTick))));
					if(item.canBeDepleted())
					{
						int ticksRemaining = item.canBeDepleted() ? item.getMaxDamage(itemStack) - item.getDamage(itemStack) : 0; // TODO improve this
						lines.add(DEFAULT_PARSER.parse(EIText.PHOTOVOLTAIC_CELL_DURATION_IN_HOURS.text(TICKS_TO_HOURS_PARSER.parse((long) ticksRemaining))));
					}
					else
					{
						lines.add(DEFAULT_PARSER.parse(EIText.PHOTOVOLTAIC_CELL_DURATION_INDEFINITE.text()));
					}
					return Optional.of(lines);
				}
				return Optional.empty();
			});
	
	public static void init()
	{
	}
}
