package net.swedz.extended_industrialization.client.tesla;

import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBER;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class TeslaPartMultiblockRenderer extends MultiblockMachineBER
{
	public TeslaPartMultiblockRenderer(BlockEntityRendererProvider.Context ctx)
	{
		super(ctx);
	}
	
	@Override
	public void render(MultiblockMachineBlockEntity machine, float partialTick, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		super.render(machine, partialTick, matrices, buffer, light, overlay);
		
		TeslaPartRenderer.render(machine, partialTick, matrices, buffer, light, overlay);
	}
}
