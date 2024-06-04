package net.swedz.extended_industrialization.machines.recipe.condition;

import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.swedz.extended_industrialization.mixin.mi.accessor.AbstractCraftingMultiblockBlockEntityAccessor;
import net.swedz.extended_industrialization.EIText;

import java.util.List;

public record EBFCoilProcessCondition(ElectricBlastFurnaceBlockEntity.Tier coilTier) implements MachineProcessCondition
{
	public static final Codec<EBFCoilProcessCondition> CODEC = RecordCodecBuilder.create(
			(g) -> g.group(
					StringRepresentable.fromValues(() -> ElectricBlastFurnaceBlockEntity.tiers.stream().map(WrappedEBFCoilTier::new).toList().toArray(new WrappedEBFCoilTier[0]))
							.fieldOf("coil")
							.forGetter((c) -> new WrappedEBFCoilTier(c.coilTier()))
			).apply(g, (wrappedTier) -> new EBFCoilProcessCondition(wrappedTier.coilTier()))
	);
	
	@Override
	public boolean canProcessRecipe(Context context, MachineRecipe recipe)
	{
		if(context.getBlockEntity() instanceof ElectricBlastFurnaceBlockEntity ebf)
		{
			AbstractCraftingMultiblockBlockEntityAccessor multiblock = (AbstractCraftingMultiblockBlockEntityAccessor) ebf;
			int activeShapeIndex = multiblock.getActiveShape().getActiveShapeIndex();
			ElectricBlastFurnaceBlockEntity.Tier ebfTier = ElectricBlastFurnaceBlockEntity.tiers.get(activeShapeIndex);
			return coilTier.maxBaseEu() <= ebfTier.maxBaseEu();
		}
		return false;
	}
	
	@Override
	public void appendDescription(List<Component> list)
	{
		list.add(EIText.RECIPE_REQUIRES_COIL.text(coilTier.getDisplayName()));
	}
	
	@Override
	public Codec<? extends MachineProcessCondition> codec(boolean syncToClient)
	{
		return CODEC;
	}
	
	private record WrappedEBFCoilTier(ElectricBlastFurnaceBlockEntity.Tier coilTier) implements StringRepresentable
	{
		@Override
		public String getSerializedName()
		{
			return coilTier.coilBlockId().toString();
		}
	}
}