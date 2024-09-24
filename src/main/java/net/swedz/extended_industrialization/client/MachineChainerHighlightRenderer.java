package net.swedz.extended_industrialization.client;

import aztech.modern_industrialization.MITags;
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
import net.swedz.extended_industrialization.client.model.ModularMachineBlockEntityRenderer;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;

public final class MachineChainerHighlightRenderer extends ModularMachineBlockEntityRenderer<MachineChainerMachineBlockEntity>
{
	private static final int COLOR_SUCCESS = 0x6FFF6F;
	private static final int COLOR_FAILURE = 0xFF6F6F;
	
	private static final String ARROW_LEFT = "\u2190";
	private static final String ARROW_UP = "\u2191";
	private static final String ARROW_RIGHT = "\u2192";
	private static final String ARROW_DOWN = "\u2193";
	
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
		ChainerLinks links = machine.getChainerComponent().links();
		boolean hasConnections = links.hasConnections();
		boolean hasFailure = links.failPosition().isPresent();
		if(hasConnections || hasFailure)
		{
			BlockPos endPos = hasFailure ?
					links.failPosition().get() :
					links.position(links.count() + 1);
			return new AABB(
					Vec3.atLowerCornerOf(machine.getBlockPos()),
					Vec3.atLowerCornerOf(endPos).add(1, 1, 1)
			);
		}
		else
		{
			return super.getRenderBoundingBox(machine);
		}
	}
	
	@Override
	public void render(MachineChainerMachineBlockEntity machine, float tickDelta, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		super.render(machine, tickDelta, matrices, buffer, light, overlay);
		
		BlockPos originPos = machine.getBlockPos();
		ChainerLinks links = machine.getChainerComponent().links();
		
		boolean holdingWrench = isHoldingWrench();
		boolean holdingMachine = isHoldingMachine(machine);
		
		if(holdingWrench || holdingMachine)
		{
			if(links.hasFailure())
			{
				if(holdingWrench)
				{
					this.renderPosition(
							machine, tickDelta, matrices, buffer, light, overlay,
							links.failPosition().orElseThrow(),
							1f, 111f / 256, 111f / 256
					);
				}
			}
			else if(links.maxConnections() > links.count())
			{
				if(holdingMachine)
				{
					this.renderPosition(
							machine, tickDelta, matrices, buffer, light, overlay,
							links.positionAfter(),
							111f / 256, 1f, 111f / 256
					);
				}
			}
		}
		
		if(holdingWrench)
		{
			int count = links.count();
			int color = COLOR_SUCCESS;
			if(links.hasFailure())
			{
				count = links.failPositionOffset();
				color = COLOR_FAILURE;
			}
			
			this.renderNumbers(
					machine, tickDelta, matrices, buffer, light, overlay,
					count, color
			);
		}
	}
	
	private void renderPosition(MachineChainerMachineBlockEntity machine, float tickDelta, PoseStack matrices, MultiBufferSource buffer, int light, int overlay,
								BlockPos pos, float red, float green, float blue)
	{
		BlockPos originPos = machine.getBlockPos();
		BlockPos offset = pos.subtract(originPos);
		
		matrices.pushPose();
		matrices.translate((float) offset.getX(), (float) offset.getY(), (float) offset.getZ());
		matrices.translate(-0.005, -0.005, -0.005);
		matrices.scale(1.01f, 1.01f, 1.01f);
		RenderHelper.drawOverlay(matrices, buffer, red, green, blue, RenderHelper.FULL_LIGHT, overlay);
		matrices.popPose();
	}
	
	private Direction pickNumberRenderFace(MachineChainerMachineBlockEntity machine)
	{
		int playerY = Minecraft.getInstance().player.blockPosition().getY();
		int machineY = machine.getBlockPos().getY();
		
		if(playerY == machineY || playerY == machineY - 1)
		{
			return Direction.fromYRot(Minecraft.getInstance().player.yHeadRot).getOpposite();
		}
		else if(playerY < machineY)
		{
			return Direction.DOWN;
		}
		else
		{
			return Direction.UP;
		}
	}
	
	private String pickArrowSymbol(MachineChainerMachineBlockEntity machine,
								   Direction playerDirection, Direction renderDirection)
	{
		ChainerLinks links = machine.getChainerComponent().links();
		Direction machineDirection = links.direction();
		
		Direction playerDirectionLeft = playerDirection.getCounterClockWise();
		Direction playerDirectionRight = playerDirection.getClockWise();
		
		String arrow = "";
		if(renderDirection != machineDirection && renderDirection != machineDirection.getOpposite())
		{
			if(playerDirection == machineDirection)
			{
				arrow = renderDirection == Direction.DOWN ? ARROW_DOWN : ARROW_UP;
			}
			else if(playerDirection == machineDirection.getOpposite())
			{
				arrow = renderDirection == Direction.DOWN ? ARROW_UP : ARROW_DOWN;
			}
			else if(playerDirectionLeft == machineDirection)
			{
				arrow = ARROW_LEFT;
			}
			else if(playerDirectionRight == machineDirection)
			{
				arrow = ARROW_RIGHT;
			}
			else if(machineDirection == Direction.UP)
			{
				arrow = ARROW_UP;
			}
			else if(machineDirection == Direction.DOWN)
			{
				arrow = ARROW_DOWN;
			}
		}
		return arrow;
	}
	
	private void renderNumbers(MachineChainerMachineBlockEntity machine, float tickDelta, PoseStack matrices, MultiBufferSource buffer, int light, int overlay,
							   int count, int color)
	{
		if(count <= 0)
		{
			return;
		}
		
		BlockPos originPos = machine.getBlockPos();
		ChainerLinks links = machine.getChainerComponent().links();
		
		Direction playerDirection = Direction.fromYRot(Minecraft.getInstance().player.yHeadRot);
		Direction renderDirection = this.pickNumberRenderFace(machine);
		String arrow = this.pickArrowSymbol(machine, playerDirection, renderDirection);
		
		for(int i = 1; i <= count; i++)
		{
			BlockPos pos = links.position(i);
			BlockPos offset = pos.subtract(originPos);
			Vec3 center = Vec3.atCenterOf(offset);
			
			matrices.pushPose();
			
			matrices.translate(
					(float) center.x() + (renderDirection.getStepX() * 0.51f),
					(float) center.y() + (renderDirection.getStepY() * 0.51f),
					(float) center.z() + (renderDirection.getStepZ() * 0.51f)
			);
			matrices.translate(-0.005, -0.005, -0.005);
			matrices.scale(0.03f, 0.03f, 0.03f);
			matrices.mulPose(playerDirection.getOpposite().getRotation());
			matrices.mulPose(renderDirection.getRotation());
			if(renderDirection == Direction.NORTH)
			{
				matrices.mulPose(Axis.ZP.rotationDegrees(180));
			}
			else if(renderDirection == Direction.WEST)
			{
				matrices.mulPose(Axis.ZN.rotationDegrees(90));
			}
			else if(renderDirection == Direction.EAST)
			{
				matrices.mulPose(Axis.ZP.rotationDegrees(90));
			}
			
			this.renderCenteredText(Integer.toString(i), color, 0, 0, matrices, buffer);
			
			float arrowTextXOffset = 10;
			float arrowTextYOffset = 10;
			switch (arrow)
			{
				case ARROW_LEFT -> this.renderCenteredText(arrow, color, -arrowTextXOffset, 0, matrices, buffer);
				case ARROW_UP -> this.renderCenteredText(arrow, color, 0, -arrowTextYOffset, matrices, buffer);
				case ARROW_RIGHT -> this.renderCenteredText(arrow, color, arrowTextXOffset, 0, matrices, buffer);
				case ARROW_DOWN -> this.renderCenteredText(arrow, color, 0, arrowTextYOffset, matrices, buffer);
			}
			
			matrices.popPose();
		}
	}
	
	private void renderCenteredText(String text, int color, float x, float y,
									PoseStack matrices, MultiBufferSource buffer)
	{
		float textX = Minecraft.getInstance().font.width(text) / 2f;
		float textY = Minecraft.getInstance().font.lineHeight / 2f;
		
		matrices.pushPose();
		matrices.translate(1, 1, 0.01);
		Minecraft.getInstance().font.drawInBatch(
				text, -textX + x, -textY + y,
				0x000000, false,
				matrices.last().pose(), buffer,
				Font.DisplayMode.NORMAL, 0x000000,
				RenderHelper.FULL_LIGHT
		);
		matrices.popPose();
		
		Minecraft.getInstance().font.drawInBatch(
				text, -textX + x, -textY + y,
				color, false,
				matrices.last().pose(), buffer,
				Font.DisplayMode.NORMAL, 0x000000,
				RenderHelper.FULL_LIGHT
		);
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
