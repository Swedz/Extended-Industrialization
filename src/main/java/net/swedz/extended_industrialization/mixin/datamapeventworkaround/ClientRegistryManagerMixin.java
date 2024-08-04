package net.swedz.extended_industrialization.mixin.datamapeventworkaround;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.network.payload.RegistryDataMapSyncPayload;
import net.neoforged.neoforge.registries.BaseMappedRegistry;
import net.neoforged.neoforge.registries.ClientRegistryManager;
import net.swedz.extended_industrialization.machines.blockentities.multiblock.LargeElectricFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientRegistryManager.class)
public class ClientRegistryManagerMixin
{
	@ModifyArg(
			method = "handleDataMapSync",
			at = @At(
					value = "INVOKE",
					target = "Lnet/neoforged/neoforge/network/handling/ISynchronizedWorkHandler;submitAsync(Ljava/lang/Runnable;)Ljava/util/concurrent/CompletableFuture;"
			)
	)
	private static Runnable handleDataMapSync(Runnable task,
											  @Local(name = "payload") RegistryDataMapSyncPayload payload)
	{
		return () ->
		{
			task.run();
			
			BaseMappedRegistry registry = (BaseMappedRegistry) Minecraft.getInstance().level.registryAccess().registryOrThrow(payload.registryKey());
			if(registry.key() == Registries.BLOCK)
			{
				LargeElectricFurnaceBlockEntity.initTiers();
			}
		};
	}
}
