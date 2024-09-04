package net.swedz.extended_industrialization.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Optional;

@EventBusSubscriber(modid = EI.ID, value = Dist.CLIENT)
public final class ElectricToolHighlight
{
	@SubscribeEvent
	private static void onBlockHighlight(RenderHighlightEvent.Block event)
	{
		Player player = Minecraft.getInstance().player;
		Level level = player.level();
		ItemStack stack = player.getMainHandItem();
		if(stack.getItem() instanceof ElectricToolItem tool)
		{
			Optional<ElectricToolItem.Area> optionalArea = tool.getArea(level, player, stack, true);
			if(optionalArea.isPresent())
			{
				ElectricToolItem.Area area = optionalArea.get();
				BlockPos center = area.center();
				
				MutableObject<VoxelShape> fullShape = new MutableObject<>(Shapes.empty());
				
				ElectricToolItem.forEachMineableBlock(level, area, player, (pos, state) ->
				{
					float destroyProgress = state.getDestroyProgress(player, level, pos);
					if(!((double) destroyProgress <= 1.0E-9))
					{
						VoxelShape blockShape = state.getShape(level, pos, CollisionContext.of(event.getCamera().getEntity()));
						blockShape = blockShape.move(pos.getX() - center.getX(), pos.getY() - center.getY(), pos.getZ() - center.getZ());
						fullShape.setValue(Shapes.joinUnoptimized(fullShape.getValue(), blockShape, BooleanOp.OR));
					}
				});
				
				if(fullShape.getValue() != Shapes.empty())
				{
					LevelRenderer.renderShape(
							event.getPoseStack(),
							event.getMultiBufferSource().getBuffer(RenderType.lines()),
							fullShape.getValue(),
							(double) center.getX() - event.getCamera().getPosition().x(),
							(double) center.getY() - event.getCamera().getPosition().y(),
							(double) center.getZ() - event.getCamera().getPosition().z(),
							0.0F, 0.0F, 0.0F, 0.4F
					);
					event.setCanceled(true);
				}
			}
		}
	}
}
