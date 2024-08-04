package net.swedz.extended_industrialization.mixin.datamapeventworkaround;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.BaseMappedRegistry;
import net.neoforged.neoforge.registries.DataMapLoader;
import net.swedz.extended_industrialization.machines.blockentities.multiblock.LargeElectricFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataMapLoader.class)
public class DataMapLoaderMixin
{
	@Inject(
			method = "apply(Lnet/neoforged/neoforge/registries/BaseMappedRegistry;Lnet/neoforged/neoforge/registries/DataMapLoader$LoadResult;)V",
			at = @At("TAIL")
	)
	private void apply(CallbackInfo callback,
					   @Local(name = "registry") BaseMappedRegistry registry)
	{
		if(registry.key() == Registries.BLOCK)
		{
			LargeElectricFurnaceBlockEntity.initTiers();
		}
	}
}
