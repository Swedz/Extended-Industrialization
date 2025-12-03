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

public final class WingNanoSuitDecorationModel<T extends LivingEntity> extends NanoSuitDecorationModel<T>
{
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(EI.id("nano_armor"), "wing");
	
	public static LayerDefinition createLayer()
	{
		float wingRotation = 0.2f;
		
		var deformation = new CubeDeformation(0);
		var mesh = createMesh(deformation, 0);
		var root = createHumanoidAlias(mesh);
		
		var body = root.getChild("body");
		
		body.addOrReplaceChild(
				"left_wing",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.mirror()
						.addBox(
								0, -12, 5,
								32, 32, 0
						),
				PartPose.rotation(0, -wingRotation, 0)
		);
		
		body.addOrReplaceChild(
				"right_wing",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.mirror()
						.addBox(
								0, -12, 5,
								-32, 32, 0
						),
				PartPose.rotation(0, wingRotation, 0)
		);
		
		return LayerDefinition.create(mesh, 64, 32);
	}
	
	private static PartDefinition createHumanoidAlias(MeshDefinition mesh)
	{
		var root = mesh.getRoot();
		var body = root.addOrReplaceChild("body", new CubeListBuilder(), PartPose.ZERO);
		body.addOrReplaceChild("left_wing", new CubeListBuilder(), PartPose.ZERO);
		body.addOrReplaceChild("right_wing", new CubeListBuilder(), PartPose.ZERO);
		return root;
	}
	
	private static ResourceLocation getTexture(int layerIndex)
	{
		return EI.id("textures/models/armor/nano_decorations/wing%s.png".formatted(layerIndex == 1 ? "_overlay" : ""));
	}
	
	public WingNanoSuitDecorationModel(ModelPart root)
	{
		super(root, NanoSuitDecoration.WING);
	}
	
	@Override
	public void render(T entity, EquipmentSlot slot, ItemStack stack, NanoSuitArmorItem item,
					   PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
					   ArmorMaterial.Layer armorLayer, int armorLayerIndex, int armorLayerColor, boolean armorLayerIsColored)
	{
		var texture = getTexture(armorLayerIndex);
		var renderType = NanoArmorLayer.decorationRenderType(armorLayerIndex, item, texture, armorLayerIsColored);
		var buffer = bufferSource.getBuffer(renderType);
		this.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, armorLayerColor);
	}
	
	@Override
	protected Iterable<ModelPart> headParts()
	{
		return List.of();
	}
	
	@Override
	protected Iterable<ModelPart> bodyParts()
	{
		return List.of(body);
	}
}
