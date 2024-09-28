package net.swedz.extended_industrialization.client.tesla;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class TeslaPartSingleBlockHighlightRenderer extends MachineBlockEntityRenderer<MachineBlockEntity>
{
	public TeslaPartSingleBlockHighlightRenderer(BlockEntityRendererProvider.Context ctx)
	{
		super(ctx);
	}
	
	@Override
	public void render(MachineBlockEntity machine, float tickDelta, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		super.render(machine, tickDelta, matrices, buffer, light, overlay);
		TeslaPartHighlightRenderer.render(machine, tickDelta, matrices, buffer, light, overlay);
	}
}
