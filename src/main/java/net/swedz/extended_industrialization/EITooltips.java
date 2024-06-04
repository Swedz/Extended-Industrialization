package net.swedz.extended_industrialization;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.blockentities.hatches.EnergyHatch;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.swedz.extended_industrialization.api.CableTierHolder;
import net.swedz.extended_industrialization.api.ConstantEfficiencyHelper;
import net.swedz.extended_industrialization.machines.blockentities.multiblock.LargeElectricFurnaceBlockEntity;
import net.swedz.extended_industrialization.machines.components.craft.multiplied.EuCostTransformer;
import net.swedz.extended_industrialization.items.PhotovoltaicCellItem;
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
	
	public static final Parser<Long> TICKS_TO_MINUTES_PARSER = (ticks) ->
	{
		float minutes = (float) ticks / (60 * 20);
		return Component.literal("%.2f".formatted(minutes)).withStyle(NUMBER_TEXT);
	};
	
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
				if(item instanceof PhotovoltaicCellItem photovoltaicCell)
				{
					int euPerTick = photovoltaicCell.getEuPerTick();
					List<Component> lines = Lists.newArrayList();
					lines.add(DEFAULT_PARSER.parse(EIText.PHOTOVOLTAIC_CELL_EU.text(EU_PER_TICK_PARSER.parse(euPerTick))));
					if(!photovoltaicCell.lastsForever())
					{
						int solarTicksRemaining = photovoltaicCell.getSolarTicksRemaining(itemStack);
						lines.add(DEFAULT_PARSER.parse(EIText.PHOTOVOLTAIC_CELL_REMAINING_OPERATION_TIME_MINUTES.text(TICKS_TO_MINUTES_PARSER.parse((long) solarTicksRemaining))));
					}
					else
					{
						lines.add(DEFAULT_PARSER.parse(EIText.PHOTOVOLTAIC_CELL_REMAINING_OPERATION_TIME.text(Component.literal("\u221E").withStyle(NUMBER_TEXT))));
					}
					return Optional.of(lines);
				}
				return Optional.empty();
			});
	
	public static final TooltipAttachment STEAM_CHAINSAW = TooltipAttachment.ofMultilines(
			EIItems.STEAM_CHAINSAW,
			List.of(
					EIText.STEAM_CHAINSAW_1.text(),
					EIText.STEAM_CHAINSAW_2.text(),
					EIText.STEAM_CHAINSAW_3.text(),
					EIText.STEAM_CHAINSAW_4.text()
			)
	);
	
	public static final TooltipAttachment MACHINE_CONFIG_CARD = TooltipAttachment.ofMultilines(
			EIItems.MACHINE_CONFIG_CARD,
			List.of(
					EIText.MACHINE_CONFIG_CARD_HELP_1.text(),
					EIText.MACHINE_CONFIG_CARD_HELP_2.text(),
					EIText.MACHINE_CONFIG_CARD_HELP_3.text(),
					EIText.MACHINE_CONFIG_CARD_HELP_4.text(),
					EIText.MACHINE_CONFIG_CARD_HELP_5.text(),
					EIText.MACHINE_CONFIG_CARD_HELP_6.text()
			)
	);
	
	public static final TooltipAttachment MACHINE_HULL_AND_ENERGY_HATCH_VOLTAGE = TooltipAttachment.ofMultilines(
			(itemStack, item) ->
			{
				List<Component> lines = Lists.newArrayList();
				
				CableTier tier;
				if(item instanceof BlockItem blockItem &&
						blockItem.getBlock() instanceof MachineBlock machineBlock &&
						machineBlock.getBlockEntityInstance() instanceof EnergyHatch energyHatch)
				{
					tier = ((CableTierHolder) energyHatch).getCableTier();
				}
				else
				{
					tier = CasingComponent.getCasingTier(item);
				}
				
				if(tier != null)
				{
					if(EIConfig.displayMachineVoltage)
					{
						lines.add(DEFAULT_PARSER.parse(EIText.MACHINE_VOLTAGE_RECIPES.text(Component.translatable(tier.shortEnglishKey()))));
					}
					if(EIConfig.machineEfficiencyHack.useVoltageForEfficiency())
					{
						lines.add(DEFAULT_PARSER.parse(EIText.MACHINE_VOLTAGE_RUNS_AT.text(EU_PER_TICK_PARSER.parse(ConstantEfficiencyHelper.getRecipeEu(tier)))));
					}
				}
				
				return lines.isEmpty() ? Optional.empty() : Optional.of(lines);
			}
	);
	
	public static void init()
	{
	}
}
