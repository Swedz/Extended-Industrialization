package net.swedz.extended_industrialization.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.EIClient;
import net.swedz.extended_industrialization.client.ber.tesla.TeslaPartRenderer;
import net.swedz.extended_industrialization.client.sound.TeslaCoilSingingSound;
import net.swedz.extended_industrialization.machines.blockentity.tesla.TeslaCoilMachineBlockEntity;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;
import net.swedz.tesseract.neoforge.proxy.ProxyEnvironment;

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
	public void startTeslaCoilSingingSound(TeslaCoilMachineBlockEntity machine)
	{
		Minecraft.getInstance().getSoundManager().queueTickingSound(new TeslaCoilSingingSound(machine));
	}
}
