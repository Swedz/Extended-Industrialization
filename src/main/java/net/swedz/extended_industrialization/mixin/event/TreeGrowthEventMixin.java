package net.swedz.extended_industrialization.mixin.event;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.neoforged.neoforge.common.NeoForge;
import net.swedz.extended_industrialization.api.event.TreeGrowthEvent;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

@Mixin(TreeFeature.class)
public class TreeGrowthEventMixin
{
	@Inject(
			method = "place",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;encapsulatingPositions(Ljava/lang/Iterable;)Ljava/util/Optional;",
					shift = At.Shift.BEFORE
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void onTreeGrowth(FeaturePlaceContext<TreeConfiguration> context,
							  CallbackInfoReturnable<Boolean> callback,
							  WorldGenLevel level, RandomSource random, BlockPos blockPos,
							  TreeConfiguration config,
							  Set<BlockPos> trunkPositions,
							  Set<BlockPos> branchPositions,
							  Set<BlockPos> foliagePositions,
							  Set<BlockPos> decoratorPositions,
							  BiConsumer<BlockPos, BlockState> trunkPlacer,
							  BiConsumer<BlockPos, BlockState> branchPlacer,
							  FoliagePlacer.FoliageSetter foliageSetter,
							  BiConsumer<BlockPos, BlockState> decoratorPlacer,
							  boolean success)
	{
		List<BlockPos> positions = Lists.newArrayList();
		positions.addAll(trunkPositions);
		positions.addAll(branchPositions);
		positions.addAll(foliagePositions);
		positions.addAll(decoratorPositions);
		TreeGrowthEvent event = new TreeGrowthEvent(level, blockPos, level.getBlockState(blockPos), positions);
		NeoForge.EVENT_BUS.post(event);
	}
}
