package net.swedz.extended_industrialization.machines.blockentity.multiblock;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.datamap.LargeElectricFurnaceTier;
import net.swedz.tesseract.neoforge.compat.mi.TesseractMI;
import net.swedz.tesseract.neoforge.compat.mi.component.craft.multiplied.EuCostTransformer;
import net.swedz.tesseract.neoforge.compat.mi.component.craft.multiplied.EuCostTransformers;
import net.swedz.tesseract.neoforge.compat.mi.helper.CommonGuiComponents;
import net.swedz.tesseract.neoforge.compat.mi.machine.blockentity.multiblock.multiplied.AbstractElectricMultipliedCraftingMultiblockBlockEntity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static aztech.modern_industrialization.machines.models.MachineCasings.*;

public final class LargeElectricFurnaceBlockEntity extends AbstractElectricMultipliedCraftingMultiblockBlockEntity
{
	public LargeElectricFurnaceBlockEntity(BEP bep)
	{
		super(bep, EI.id("large_electric_furnace"), SHAPE_TEMPLATES, MachineTier.LV);
		
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
				TesseractMI.text().machineBatcherRecipe(true, this.getRecipeType()),
				EI.text().machineBatcherCoils()
		);
	}
	
	public static final List<Tier> DEFAULT_TIERS = List.of(
			new Tier(MI.id("cupronickel_coil"), 16, 0.75f),
			new Tier(MI.id("kanthal_coil"), 32, 0.75f)
	);
	
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
	
	static
	{
		// Initialize using default tiers so that GuideME can read it in dev before loading into a world
		initTiers(DEFAULT_TIERS);
	}
	
	private static void initTiers(List<Tier> tiers)
	{
		TIERS = Collections.unmodifiableList(tiers);
		TIERS_BY_COIL = TIERS.stream().collect(Collectors.toMap(LargeElectricFurnaceBlockEntity.Tier::blockId, Function.identity()));
		
		SHAPE_TEMPLATES = new ShapeTemplate[TIERS.size()];
		
		var heatproofMachineCasing = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("heatproof_machine_casing")));
		var hatches = new HatchFlags.Builder().with(HatchTypes.ITEM_INPUT, HatchTypes.ITEM_OUTPUT, HatchTypes.ENERGY_INPUT).build();
		
		for(int i = 0; i < TIERS.size(); i++)
		{
			var tier = TIERS.get(i);
			var coil = SimpleMember.forBlockId(tier.blockId());
			var shape = new ShapeTemplate.Builder(HEATPROOF)
					.add3by3(0, heatproofMachineCasing, false, hatches)
					.add3by3(1, coil, true, HatchFlags.NO_HATCH)
					.add3by3(2, heatproofMachineCasing, false, hatches)
					.build();
			SHAPE_TEMPLATES[i] = shape;
		}
		
		registerReiShapes();
	}
	
	public static void initTiersFromDatamap()
	{
		List<Tier> tiers = Lists.newArrayList();
		LargeElectricFurnaceTier.getAll().forEach((block, tier) ->
				tiers.add(new Tier(block.location(), tier.batchSize(), tier.euCostMultiplier())));
		tiers.sort(Comparator.comparingInt(Tier::batchSize));
		initTiers(tiers);
	}
	
	private static void registerReiShapes()
	{
		ReiMachineRecipes.multiblockShapes.removeIf((shape) -> shape.machine().equals(EI.id("large_electric_furnace")));
		int index = 0;
		for(var shapeTemplate : SHAPE_TEMPLATES)
		{
			ReiMachineRecipes.registerMultiblockShape(EI.id("large_electric_furnace"), shapeTemplate, "" + index);
			index++;
		}
	}
}
