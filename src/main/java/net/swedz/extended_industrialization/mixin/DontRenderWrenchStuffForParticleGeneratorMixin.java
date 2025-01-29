package net.swedz.extended_industrialization.mixin;

import aztech.modern_industrialization.machines.MachineOverlayClient;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.swedz.extended_industrialization.machines.blockentity.tesla.TeslaParticleGeneratorMachineBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
		value = MachineOverlayClient.class,
		remap = false
)
public class DontRenderWrenchStuffForParticleGeneratorMixin
{
	@Inject(
			method = "onBlockOutline",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void onBlockOutline(RenderHighlightEvent.Block event, CallbackInfo callback)
	{
		var target = event.getTarget();
		var pos = target.getBlockPos();
		var be = Minecraft.getInstance().level.getBlockEntity(pos);
		if(be instanceof TeslaParticleGeneratorMachineBlockEntity)
		{
			callback.cancel();
		}
	}
}
