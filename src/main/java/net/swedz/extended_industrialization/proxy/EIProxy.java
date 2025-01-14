package net.swedz.extended_industrialization.proxy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.machines.blockentity.tesla.TeslaCoilMachineBlockEntity;
import net.swedz.tesseract.neoforge.proxy.Proxy;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;

@ProxyEntrypoint
public class EIProxy implements Proxy
{
	public void tickTesla(BlockPos blockPos)
	{
	}
	
	public void createTeslaArc(BlockPos blockPos, Vec3 target)
	{
	}
	
	public void startTeslaCoilSingingSound(TeslaCoilMachineBlockEntity machine)
	{
	}
}
