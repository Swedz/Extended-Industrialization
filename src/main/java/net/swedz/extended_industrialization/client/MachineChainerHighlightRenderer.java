package net.swedz.extended_industrialization.client;

import aztech.modern_industrialization.MITags;
import aztech.modern_industrialization.machines.MachineBlockEntityRenderer;
import aztech.modern_industrialization.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
	public AABB getRenderBoundingBox(MachineChainerMachineBlockEntity machine)
	{
		MachineChainerComponent component = machine.getChainerComponent();
		BlockPos endPos = component.links().position(component.getConnectedMachineCount() + 1);
		return new AABB(
				Vec3.atLowerCornerOf(machine.getBlockPos()),
				Vec3.atLowerCornerOf(endPos).add(1, 1, 1)
		);
	}
	
	@Override
	public void render(MachineChainerMachineBlockEntity machine, float tickDelta, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		super.render(machine, tickDelta, matrices, buffer, light, overlay);
		
		if(isHoldingMachine(machine))
		{
			this.renderPositionAfter(machine, tickDelta, matrices, buffer, light, overlay);
		}
		
		if(isHoldingWrench())
		{
			this.renderNumbers(machine, tickDelta, matrices, buffer, light, overlay);
		}
	}
	
	private void renderPositionAfter(MachineChainerMachineBlockEntity machine, float tickDelta, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		BlockPos originPos = machine.getBlockPos();
		MachineChainerComponent component = machine.getChainerComponent();
		
		if(component.getMaxConnectedMachinesCount() > component.getConnectedMachineCount())
		{
			BlockPos pos = component.links().positionAfter();
			BlockPos offset = pos.subtract(originPos);
			
			matrices.pushPose();
			matrices.translate((float) offset.getX(), (float) offset.getY(), (float) offset.getZ());
			matrices.translate(-0.005, -0.005, -0.005);
			matrices.scale(1.01f, 1.01f, 1.01f);
			RenderHelper.drawOverlay(matrices, buffer, 111f / 256, 1f, 111f / 256, RenderHelper.FULL_LIGHT, overlay);
			matrices.popPose();
		}
	}
	
	private void renderNumbers(MachineChainerMachineBlockEntity machine, float tickDelta, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		BlockPos originPos = machine.getBlockPos();
		MachineChainerComponent component = machine.getChainerComponent();
		
		Direction facing = Direction.fromYRot(Minecraft.getInstance().player.yHeadRot);
		
		for(int i = 1; i <= component.links().count(); i++)
		{
			BlockPos pos = component.links().position(i);
			BlockPos offset = pos.subtract(originPos);
			
			String text = Integer.toString(i);
			float textX = Minecraft.getInstance().font.width(text) / 2f;
			float textY = Minecraft.getInstance().font.lineHeight / 2f;
			
			matrices.pushPose();
			
			matrices.translate((float) offset.getX(), (float) offset.getY() + 1.01, (float) offset.getZ());
			matrices.translate(-0.005, -0.005, -0.005);
			matrices.translate(0.5, 0, 0.5);
			matrices.scale(0.03f, 0.03f, 0.03f);
			matrices.mulPose(Axis.YP.rotationDegrees(180));
			matrices.mulPose(facing.getRotation());
			
			matrices.pushPose();
			matrices.translate(1, 1, 0.01);
			Minecraft.getInstance().font.drawInBatch(
					text, -textX, -textY,
					0x000000, false,
					matrices.last().pose(), buffer,
					Font.DisplayMode.NORMAL, 0x000000,
					RenderHelper.FULL_LIGHT
			);
			matrices.popPose();
			
			Minecraft.getInstance().font.drawInBatch(
					text, -textX, -textY,
					0x6FFF6F, false,
					matrices.last().pose(), buffer,
					Font.DisplayMode.NORMAL, 0x000000,
					RenderHelper.FULL_LIGHT
			);
			
			matrices.popPose();
		}
	}
	
	private static boolean isHoldingMachine(MachineChainerMachineBlockEntity machine)
	{
		Player player = Minecraft.getInstance().player;
		return machine.getChainerComponent().links().test(player.getMainHandItem()).isSuccess() ||
			   machine.getChainerComponent().links().test(player.getOffhandItem()).isSuccess();
	}
	
	private static boolean isHoldingWrench()
	{
		Player player = Minecraft.getInstance().player;
		return player.getMainHandItem().is(MITags.WRENCHES) ||
			   player.getOffhandItem().is(MITags.WRENCHES);
	}
}
