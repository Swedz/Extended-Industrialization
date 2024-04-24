package net.swedz.extended_industrialization.mixin.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.swedz.extended_industrialization.api.event.FarmlandLoseMoistureEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FarmBlock.class)
public class FarmlandLoseMoistureEventMixin
{
	@Inject(
			method = "randomTick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
					ordinal = 0,
					shift = At.Shift.BEFORE
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	private void beforeSetBlock(BlockState state, ServerLevel level, BlockPos pos, RandomSource random,
								CallbackInfo callback,
								int moistureLevel)
	{
		int moistureBefore = moistureLevel + 1;
		FarmlandLoseMoistureEvent event = new FarmlandLoseMoistureEvent(level, pos, state, moistureBefore, moistureLevel);
		NeoForge.EVENT_BUS.post(event);
		if(event.isCanceled())
		{
			callback.cancel();
		}
	}
}
