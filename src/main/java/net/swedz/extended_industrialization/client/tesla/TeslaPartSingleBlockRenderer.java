package net.swedz.extended_industrialization.client.tesla;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class TeslaPartSingleBlockRenderer extends MachineBlockEntityRenderer<MachineBlockEntity>
{
	public TeslaPartSingleBlockRenderer(BlockEntityRendererProvider.Context ctx)
	{
		super(ctx);
	}
	
	@Override
	public void render(MachineBlockEntity machine, float partialTick, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		super.render(machine, partialTick, matrices, buffer, light, overlay);
		
		TeslaPartRenderer.render(machine, partialTick, matrices, buffer, light, overlay);
	}
}
