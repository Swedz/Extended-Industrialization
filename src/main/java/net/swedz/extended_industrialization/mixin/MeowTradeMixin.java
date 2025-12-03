package net.swedz.extended_industrialization.mixin;

import net.minecraft.world.entity.animal.Cat;
import net.swedz.extended_industrialization.entity.goal.MeowTradeGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cat.class)
public class MeowTradeMixin
{
	@Inject(
			method = "registerGoals",
			at = @At("TAIL")
	)
	private void registerGoals(CallbackInfo callback)
	{
		var cat = (Cat) (Object) this;
		cat.goalSelector.addGoal(4, new MeowTradeGoal(cat));
	}
}
