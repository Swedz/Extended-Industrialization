package net.swedz.extended_industrialization.mixin.mi.constantefficiency;

import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.swedz.extended_industrialization.api.ConstantEfficiencyHelper;
import net.swedz.extended_industrialization.EIConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CrafterComponent.class)
public class ConstantEfficiencyCrafterComponentMixin
{
	@Shadow
	@Final
	private MachineProcessCondition.Context conditionContext;
	
	@Shadow
	private int efficiencyTicks;
	
	@Shadow
	private int maxEfficiencyTicks;
	
	@Shadow
	private long recipeMaxEu;
	
	@Shadow
	private RecipeHolder<MachineRecipe> activeRecipe;
	
	@Redirect(
			method = {"getRecipeMaxEfficiencyTicks", "tickRecipe", "getRecipeMaxEu"},
			at = @At(
					value = "INVOKE",
					target = "Laztech/modern_industrialization/machines/components/CrafterComponent$Behavior;getMaxRecipeEu()J"
			)
	)
	private long getMaxRecipeEu(CrafterComponent.Behavior behavior)
	{
		return ConstantEfficiencyHelper.getActualMaxRecipeEu(conditionContext.getBlockEntity(), behavior);
	}
	
	@Inject(
			method = "decreaseEfficiencyTicks",
			at = @At("HEAD"),
			cancellable = true
	)
	private void decreaseEfficiencyTicks(CallbackInfo callback)
	{
		if(EIConfig.machineEfficiencyHack.forceMaxEfficiency())
		{
			callback.cancel();
		}
	}
	
	@Inject(
			method = "increaseEfficiencyTicks",
			at = @At("HEAD"),
			cancellable = true
	)
	private void increaseEfficiencyTicks(int increment, CallbackInfo callback)
	{
		if(EIConfig.machineEfficiencyHack.forceMaxEfficiency())
		{
			callback.cancel();
		}
	}
	
	@Inject(
			method = "tickRecipe",
			at = @At("HEAD")
	)
	private void tickRecipe(CallbackInfoReturnable<Boolean> callback)
	{
		if(EIConfig.machineEfficiencyHack.forceMaxEfficiency())
		{
			efficiencyTicks = activeRecipe != null ? maxEfficiencyTicks : 0;
		}
	}
	
	@Inject(
			method = "tickRecipe",
			at = @At(
					value = "INVOKE",
					target = "Laztech/modern_industrialization/machines/components/CrafterComponent;clearActiveRecipeIfPossible()V"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void tickRecipeReset(CallbackInfoReturnable<Boolean> cir, boolean isActive, boolean isEnabled, boolean recipeStarted, long eu, boolean finishedRecipe)
	{
		if(EIConfig.machineEfficiencyHack.forceMaxEfficiency() && eu < recipeMaxEu)
		{
			efficiencyTicks = 0;
		}
	}
	
	@Inject(
			method = "readNbt",
			at = @At("RETURN")
	)
	private void readNbt(net.minecraft.nbt.CompoundTag tag, boolean isUpgradingMachine, CallbackInfo callback)
	{
		if(EIConfig.machineEfficiencyHack.forceMaxEfficiency())
		{
			efficiencyTicks = activeRecipe != null ? maxEfficiencyTicks : 0;
		}
	}
}
