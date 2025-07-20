package net.swedz.extended_industrialization.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.renderable.BakedModelRenderable;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIClientSheets;
import net.swedz.extended_industrialization.component.RainbowDataComponent;
import net.swedz.extended_industrialization.entity.NanoSaberSweepEntity;
import org.joml.Vector4f;

public final class NanoSaberSweepEntityRenderer extends EntityRenderer<NanoSaberSweepEntity>
{
	private final RandomSource random;
	
	public NanoSaberSweepEntityRenderer(EntityRendererProvider.Context context)
	{
		super(context);
		random = RandomSource.create();
	}
	
	@Override
	public ResourceLocation getTextureLocation(NanoSaberSweepEntity nanoSaberSweepEntity)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void render(NanoSaberSweepEntity entity, float entityYaw, float partialTick, PoseStack matrices, MultiBufferSource bufferSource, int packedLight)
	{
		var modelManager = Minecraft.getInstance().getModelManager();
		var model = modelManager.getModel(ModelResourceLocation.standalone(EI.id("entity/nano_saber_sweep")));
		
		matrices.pushPose();
		
		Vec3 movement = entity.getDeltaMovement().normalize();
		float yaw = (float) Math.atan2(movement.x(), movement.z());
		float pitch = (float) Math.asin(-movement.y());
		matrices.mulPose(Axis.YP.rotation(yaw));
		matrices.mulPose(Axis.XP.rotation(pitch));
		matrices.mulPose(Axis.YP.rotationDegrees(180));
		
		int color = entity.isRainbow() ?
				RainbowDataComponent.getCurrentRainbowColor() :
				entity.getColor() | 0xFF000000;
		float red = FastColor.ARGB32.red(color) / 255f;
		float green = FastColor.ARGB32.green(color) / 255f;
		float blue = FastColor.ARGB32.blue(color) / 255f;
		
		BakedModelRenderable.of(model).render(
				matrices, bufferSource,
				(__) -> RenderType.entityCutout(EIClientSheets.NANO_SABER_SWEEP),
				packedLight, OverlayTexture.NO_OVERLAY, partialTick,
				new BakedModelRenderable.Context(
						null,
						new Direction[1],
						random,
						42L,
						ModelData.EMPTY,
						new Vector4f(red, green, blue, 1)
				)
		);
		
		matrices.popPose();
	}
}
