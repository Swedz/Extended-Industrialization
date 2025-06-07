package net.swedz.extended_industrialization.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
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
	public void init()
	{
		NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, true, (PlayerInteractEvent.LeftClickBlock event) ->
		{
			if(event.getUseItem().isFalse() || event.getSide().isServer())
			{
				return;
			}
			ELECTRIC_TOOL_BREAKING_SIDE = event.getFace();
		});
	}
	
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
	
	@Override
	public void removeTesla(BlockPos blockPos)
	{
		TeslaPartRenderer.removeArcInstance(blockPos);
	}
	
	private static Direction ELECTRIC_TOOL_BREAKING_SIDE;
	
	@Override
	public boolean shouldCauseElectricToolBreakReset()
	{
		return Minecraft.getInstance().isSameThread() &&
			   Minecraft.getInstance().hitResult instanceof BlockHitResult hitResult &&
			   hitResult.getDirection() != ELECTRIC_TOOL_BREAKING_SIDE;
	}
}
