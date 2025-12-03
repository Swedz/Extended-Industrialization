package net.swedz.extended_industrialization.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This is functionally the same as {@link aztech.modern_industrialization.items.SteamDrillHooks#overrideDestroyProgress(Player, BlockGetter, CallbackInfoReturnable)}.
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public class ElectricToolBreakSpeedMixin
{
	@Unique
	private static final ThreadLocal<Boolean> CAN_OVERRIDE = ThreadLocal.withInitial(() -> true);
	
	@Unique
	private static float getDestroyProgressRaw(BlockState state, Player player, BlockGetter level, BlockPos pos)
	{
		CAN_OVERRIDE.set(false);
		try
		{
			return state.getDestroyProgress(player, level, pos);
		}
		finally
		{
			CAN_OVERRIDE.set(true);
		}
	}
	
	@Inject(
			method = "getDestroyProgress",
			at = @At("HEAD"),
			cancellable = true
	)
	public void getDestroyProgress(Player player, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> callback)
	{
		var stack = player.getMainHandItem();
		if(!(stack.getItem() instanceof ElectricToolItem tool) || !CAN_OVERRIDE.get())
		{
			return;
		}
		
		tool.getArea(level, player, stack, false).ifPresent((area) ->
		{
			var minProgress = new MutableFloat(Float.MAX_VALUE);
			var foundAny = new MutableBoolean(false);
			
			ElectricToolItem.forEachMineableBlock(level, area, player, (blockPos, state) ->
			{
				// Disable this hook during the call to avoid infinite recursion...
				float destroyProgress = getDestroyProgressRaw(state, player, level, blockPos);
				
				if(destroyProgress > 1e-9)
				{
					foundAny.setTrue();
					minProgress.setValue(Math.min(minProgress.getValue(), destroyProgress));
				}
			});
			
			if(foundAny.isTrue())
			{
				callback.setReturnValue(minProgress.getValue());
				callback.cancel();
			}
		});
	}
}
