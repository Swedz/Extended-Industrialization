package net.swedz.extended_industrialization.client.nanosuit.decorations;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.client.nanosuit.NanoArmorLayer;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;
import net.swedz.extended_industrialization.item.nanosuit.decoration.NanoSuitDecoration;

import java.util.List;

public final class MeowNanoSuitDecorationModel<T extends LivingEntity> extends NanoSuitDecorationModel<T>
{
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(EI.id("nano_armor"), "meow");
	
	public static LayerDefinition createLayer()
	{
		var deformation = new CubeDeformation(0);
		var mesh = createMesh(deformation, 0);
		var root = createHumanoidAlias(mesh);
		
		var head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		
		head.addOrReplaceChild(
				"left_bottom_ear",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(
								-4 - 5f / 8f, -11 - 2f / 8f, -2 - 3f / 8f,
								3, 2, 1,
								new CubeDeformation(3f / 8f, 2f / 8f, 1f / 8f)
						),
				PartPose.ZERO
		);
		head.addOrReplaceChild(
				"left_top_ear",
				CubeListBuilder.create()
						.texOffs(0, 3)
						.addBox(
								-3 - 5f / 8f, -12 - 5f / 8f, -2 - 3f / 8f,
								1, 1, 1,
								new CubeDeformation(1f / 8f, 1f / 8f, 1f / 8f)
						),
				PartPose.ZERO
		);
		
		head.addOrReplaceChild(
				"right_bottom_ear",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(
								-4 - 3f / 8f + 6, -11 - 2f / 8f, -2 - 3f / 8f,
								3, 2, 1,
								new CubeDeformation(3f / 8f, 2f / 8f, 1f / 8f)
						),
				PartPose.ZERO
		);
		head.addOrReplaceChild(
				"right_top_ear",
				CubeListBuilder.create()
						.texOffs(0, 3)
						.addBox(
								-3 - 3f / 8f + 6, -12 - 5f / 8f, -2 - 3f / 8f,
								1, 1, 1,
								new CubeDeformation(1f / 8f, 1f / 8f, 1f / 8f)
						),
				PartPose.ZERO
		);
		
		head.addOrReplaceChild(
				"left_whisker_1",
				CubeListBuilder.create()
						.texOffs(0, 5)
						.addBox(
								0, 0, 0,
								3, 1, 0
						),
				PartPose.offset(
						-7.5f, -2.5f, -5.1f
				)
		);
		head.addOrReplaceChild(
				"left_whisker_2",
				CubeListBuilder.create()
						.texOffs(0, 5)
						.addBox(
								-3, -1, 0,
								3, 1, 0
						),
				PartPose.offsetAndRotation(
						-4.5f, -2.6f, -5.1f,
						0, 0, (float) Math.toRadians(15)
				)
		);
		head.addOrReplaceChild(
				"left_whisker_3",
				CubeListBuilder.create()
						.texOffs(0, 5)
						.addBox(
								-3, 0, 0,
								3, 1, 0
						),
				PartPose.offsetAndRotation(
						-4.5f, -1.4f, -5.1f,
						0, 0, (float) Math.toRadians(-15)
				)
		);
		
		head.addOrReplaceChild(
				"right_whisker_1",
				CubeListBuilder.create()
						.mirror()
						.texOffs(0, 5)
						.addBox(
								0, 0, 0,
								3, 1, 0
						),
				PartPose.offset(
						4.5f, -2.5f, -5.1f
				)
		);
		head.addOrReplaceChild(
				"right_whisker_2",
				CubeListBuilder.create()
						.mirror()
						.texOffs(0, 5)
						.addBox(
								0, 0, 0,
								3, 1, 0
						),
				PartPose.offsetAndRotation(
						4.5f, -1.4f, -5.1f,
						0, 0, (float) Math.toRadians(15)
				)
		);
		head.addOrReplaceChild(
				"right_whisker_3",
				CubeListBuilder.create()
						.mirror()
						.texOffs(0, 5)
						.addBox(
								0, -1, 0,
								3, 1, 0
						),
				PartPose.offsetAndRotation(
						4.5f, -2.6f, -5.1f,
						0, 0, (float) Math.toRadians(-15)
				)
		);
		
		return LayerDefinition.create(mesh, 64, 32);
	}
	
	private static PartDefinition createHumanoidAlias(MeshDefinition mesh)
	{
		var root = mesh.getRoot();
		var head = root.addOrReplaceChild("head", new CubeListBuilder(), PartPose.ZERO);
		head.addOrReplaceChild("left_bottom_ear", new CubeListBuilder(), PartPose.ZERO);
		head.addOrReplaceChild("left_top_ear", new CubeListBuilder(), PartPose.ZERO);
		head.addOrReplaceChild("right_bottom_ear", new CubeListBuilder(), PartPose.ZERO);
		head.addOrReplaceChild("right_top_ear", new CubeListBuilder(), PartPose.ZERO);
		head.addOrReplaceChild("left_whisker_1", new CubeListBuilder(), PartPose.ZERO);
		head.addOrReplaceChild("left_whisker_2", new CubeListBuilder(), PartPose.ZERO);
		head.addOrReplaceChild("left_whisker_3", new CubeListBuilder(), PartPose.ZERO);
		head.addOrReplaceChild("right_whisker_1", new CubeListBuilder(), PartPose.ZERO);
		head.addOrReplaceChild("right_whisker_2", new CubeListBuilder(), PartPose.ZERO);
		head.addOrReplaceChild("right_whisker_3", new CubeListBuilder(), PartPose.ZERO);
		return root;
	}
	
	private static ResourceLocation getTexture(boolean quantum, int layerIndex)
	{
		return EI.id("textures/models/armor/nano_decorations/meow%s%s.png".formatted(quantum ? "_quantum" : "", layerIndex == 1 ? "_overlay" : ""));
	}
	
	public MeowNanoSuitDecorationModel(ModelPart root)
	{
		super(root, NanoSuitDecoration.MEOW);
	}
	
	@Override
	public void render(T entity, EquipmentSlot slot, ItemStack stack, NanoSuitArmorItem item,
					   PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
					   ArmorMaterial.Layer armorLayer, int armorLayerIndex, int armorLayerColor, boolean armorLayerIsColored)
	{
		var texture = getTexture(item.isQuantum(), armorLayerIndex);
		var renderType = NanoArmorLayer.decorationRenderType(armorLayerIndex, item, texture, armorLayerIsColored);
		var buffer = bufferSource.getBuffer(renderType);
		this.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, armorLayerColor);
	}
	
	@Override
	protected Iterable<ModelPart> headParts()
	{
		return List.of(head);
	}
	
	@Override
	protected Iterable<ModelPart> bodyParts()
	{
		return List.of();
	}
}
