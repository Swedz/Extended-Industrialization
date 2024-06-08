package net.swedz.extended_industrialization.machines.blockentities.multiblock;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import com.google.common.collect.Maps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.datamaps.LargeElectricFurnaceTier;
import net.swedz.tesseract.neoforge.TesseractText;
import net.swedz.tesseract.neoforge.compat.mi.component.craft.multiplied.EuCostTransformer;
import net.swedz.tesseract.neoforge.compat.mi.component.craft.multiplied.EuCostTransformers;
import net.swedz.tesseract.neoforge.compat.mi.helper.CommonGuiComponents;
import net.swedz.tesseract.neoforge.compat.mi.machine.blockentity.multiblock.multiplied.AbstractElectricMultipliedCraftingMultiblockBlockEntity;
import org.apache.commons.compress.utils.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static aztech.modern_industrialization.MITooltips.*;
import static aztech.modern_industrialization.machines.models.MachineCasings.*;
import static net.swedz.tesseract.neoforge.compat.mi.builtinhook.TesseractMITooltips.*;

public final class LargeElectricFurnaceBlockEntity extends AbstractElectricMultipliedCraftingMultiblockBlockEntity
{
	public LargeElectricFurnaceBlockEntity(BEP bep)
	{
		super(bep, "large_electric_furnace", SHAPE_TEMPLATES, MachineTier.LV);
		
		List<Component> tierComponents = TIERS.stream().map(LargeElectricFurnaceBlockEntity.Tier::getDisplayName).toList();
		
		this.registerGuiComponent(CommonGuiComponents.rangedShapeSelection(this, activeShape, tierComponents, true));
	}
	
	public Tier getActiveTier()
	{
		return TIERS.get(activeShape.getActiveShapeIndex());
	}
	
	@Override
	public MachineRecipeType getRecipeType()
	{
		return MIMachineRecipeTypes.FURNACE;
	}
	
	@Override
	public int getMaxMultiplier()
	{
		return this.getActiveTier().batchSize();
	}
	
	@Override
	public EuCostTransformer getEuCostTransformer()
	{
		return EuCostTransformers.percentage(() -> this.getActiveTier().euCostMultiplier());
	}
	
	@Override
	public List<Component> getTooltips()
	{
		return List.of(
				DEFAULT_PARSER.parse(TesseractText.MI_MACHINE_BATCHER_RECIPE.text(MACHINE_RECIPE_TYPE_PARSER.parse(true, this.getRecipeType()))),
				DEFAULT_PARSER.parse(EIText.MACHINE_BATCHER_COILS.text())
		);
	}
	
	private static List<Tier>                  TIERS           = List.of();
	private static Map<ResourceLocation, Tier> TIERS_BY_COIL   = Collections.unmodifiableMap(Maps.newHashMap());
	private static ShapeTemplate[]             SHAPE_TEMPLATES = new ShapeTemplate[0];
	
	public static List<Tier> getTiers()
	{
		return TIERS;
	}
	
	public static Map<ResourceLocation, Tier> getTiersByCoil()
	{
		return TIERS_BY_COIL;
	}
	
	public record Tier(ResourceLocation blockId, int batchSize, float euCostMultiplier)
	{
		public String getTranslationKey()
		{
			return "lef_tier.%s.%s.%s".formatted(EI.ID, blockId.getNamespace(), blockId.getPath());
		}
		
		public Component getDisplayName()
		{
			return Component.translatable(getTranslationKey());
		}
	}
	
	public static void initTiers()
	{
		List<Tier> tiers = Lists.newArrayList();
		LargeElectricFurnaceTier.getAll().forEach((block, tier) ->
				tiers.add(new Tier(block.location(), tier.batchSize(), tier.euCostMultiplier())));
		tiers.sort(Comparator.comparingInt(Tier::batchSize));
		
		TIERS = Collections.unmodifiableList(tiers);
		TIERS_BY_COIL = TIERS.stream().collect(Collectors.toMap(LargeElectricFurnaceBlockEntity.Tier::blockId, Function.identity()));
		
		SHAPE_TEMPLATES = new ShapeTemplate[TIERS.size()];
		
		SimpleMember heatproofMachineCasing = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("heatproof_machine_casing")));
		HatchFlags hatches = new HatchFlags.Builder().with(HatchType.ITEM_INPUT, HatchType.ITEM_OUTPUT, HatchType.ENERGY_INPUT).build();
		
		for(int i = 0; i < TIERS.size(); i++)
		{
			Tier tier = TIERS.get(i);
			SimpleMember coil = SimpleMember.forBlockId(tier.blockId());
			ShapeTemplate shape = new ShapeTemplate.Builder(HEATPROOF)
					.add3by3(0, heatproofMachineCasing, false, hatches)
					.add3by3(1, coil, true, HatchFlags.NO_HATCH)
					.add3by3(2, heatproofMachineCasing, false, hatches)
					.build();
			SHAPE_TEMPLATES[i] = shape;
		}
		
		registerReiShapes();
	}
	
	private static void registerReiShapes()
	{
		ReiMachineRecipes.multiblockShapes.removeIf((e) -> e.getA().equals("large_electric_furnace"));
		for(ShapeTemplate shapeTemplate : SHAPE_TEMPLATES)
		{
			ReiMachineRecipes.registerMultiblockShape("large_electric_furnace", shapeTemplate);
		}
	}
}
