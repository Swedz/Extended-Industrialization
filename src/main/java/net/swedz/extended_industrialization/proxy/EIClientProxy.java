package net.swedz.extended_industrialization.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.EIClient;
import net.swedz.extended_industrialization.client.ber.tesla.TeslaPartRenderer;
import net.swedz.extended_industrialization.client.sound.TeslaCoilLoopSound;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;
import net.swedz.tesseract.neoforge.proxy.ProxyEnvironment;

import java.util.function.Supplier;

@ProxyEntrypoint(environment = ProxyEnvironment.CLIENT)
public class EIClientProxy extends EIProxy
{
	@Override
	public void tickTesla(BlockPos blockPos)
	{
		if(EIClient.config().renderTeslaAnimations())
		{
			var arcInstance = TeslaPartRenderer.getArcInstance(blockPos);
			if(arcInstance != null)
			{
				arcInstance.tick();
			}
		}
	}
	
	@Override
	public void createTeslaArc(BlockPos blockPos, Vec3 target)
	{
		if(EIClient.config().renderTeslaAnimations())
		{
			var arcInstance = TeslaPartRenderer.getArcInstance(blockPos);
			if(arcInstance != null)
			{
				arcInstance.createArc(arcInstance.closestOrigin(target), target);
			}
		}
	}
	
	@Override
	public void startTeslaCoilLoopSound(BlockPos origin, SoundEvent sound, SoundSource source, Supplier<Boolean> shouldStop, Supplier<Float> getPitch, Runnable onStop)
	{
		Minecraft.getInstance().getSoundManager().queueTickingSound(new TeslaCoilLoopSound(origin, sound, source, shouldStop, getPitch, onStop));
	}
}
