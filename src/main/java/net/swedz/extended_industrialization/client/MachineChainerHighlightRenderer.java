package net.swedz.extended_industrialization.client;

import aztech.modern_industrialization.machines.MachineBlockEntityRenderer;
import aztech.modern_industrialization.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.component.chainer.MachineChainerComponent;

public final class MachineChainerHighlightRenderer extends MachineBlockEntityRenderer<MachineChainerMachineBlockEntity>
{
	public MachineChainerHighlightRenderer(BlockEntityRendererProvider.Context ctx)
	{
		super(ctx);
	}
	
	@Override
	public boolean shouldRenderOffScreen(MachineChainerMachineBlockEntity machine)
	{
		return true;
	}
	
	@Override
	public void render(MachineChainerMachineBlockEntity machine, float tickDelta, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		super.render(machine, tickDelta, matrices, buffer, light, overlay);
		
		BlockPos originPos = machine.getBlockPos();
		MachineChainerComponent component = machine.getChainerComponent();
		
		if(isHoldingMachine(machine))
		{
			if(component.getMaxConnectedMachinesCount() > component.getConnectedMachineCount())
			{
				BlockPos placePos = component.links().getJustOutside();
				BlockPos offset = placePos.subtract(originPos);
				
				matrices.pushPose();
				matrices.translate((float) offset.getX(), (float) offset.getY(), (float) offset.getZ());
				matrices.translate(-0.005, -0.005, -0.005);
				matrices.scale(1.01f, 1.01f, 1.01f);
				RenderHelper.drawOverlay(matrices, buffer, 111f / 256, 1f, 111f / 256, 15728880, overlay);
				matrices.popPose();
			}
		}
	}
	
	private static boolean isHoldingMachine(MachineChainerMachineBlockEntity machine)
	{
		Player player = Minecraft.getInstance().player;
		return machine.getChainerComponent().links().test(player.getMainHandItem()).isSuccess() ||
			   machine.getChainerComponent().links().test(player.getOffhandItem()).isSuccess();
	}
}
