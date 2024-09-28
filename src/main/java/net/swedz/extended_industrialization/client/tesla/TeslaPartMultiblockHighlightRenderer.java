package net.swedz.extended_industrialization.client.tesla;

import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBER;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class TeslaPartMultiblockHighlightRenderer extends MultiblockMachineBER
{
	public TeslaPartMultiblockHighlightRenderer(BlockEntityRendererProvider.Context ctx)
	{
		super(ctx);
	}
	
	@Override
	public void render(MultiblockMachineBlockEntity machine, float tickDelta, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		super.render(machine, tickDelta, matrices, buffer, light, overlay);
		TeslaPartHighlightRenderer.render(machine, tickDelta, matrices, buffer, light, overlay);
	}
}
