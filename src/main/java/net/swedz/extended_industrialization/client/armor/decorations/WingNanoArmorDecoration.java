package net.swedz.extended_industrialization.client.armor.decorations;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIClientShaders;
import net.swedz.extended_industrialization.client.armor.NanoArmorLayer;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import static net.minecraft.client.renderer.RenderStateShard.*;

public final class WingNanoArmorDecoration<T extends LivingEntity> extends NanoArmorDecoration<T>
{
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(EI.id("nano_armor"), "wing");
	
	private static float WING_ROTATION = 0.2f;
	
	public static LayerDefinition createLayer()
	{
		var deformation = new CubeDeformation(0);
		var mesh = createMesh(deformation, 0);
		var root = createHumanoidAlias(mesh);
		root.addOrReplaceChild(
				"left_wing_front",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.mirror()
						.addBox(
								0, -12, 4,
								32, 32, 0,
								Set.of(Direction.NORTH)
						),
				PartPose.rotation(0, -WING_ROTATION, 0)
		);
		root.addOrReplaceChild(
				"left_wing_back",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(
								0, -12, 4,
								32, 32, 0,
								Set.of(Direction.SOUTH)
						),
				PartPose.rotation(0, -WING_ROTATION, 0)
		);
		root.addOrReplaceChild(
				"right_wing_front",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.mirror()
						.addBox(
								0, -12, 4,
								-32, 32, 0,
								Set.of(Direction.SOUTH)
						),
				PartPose.rotation(0, WING_ROTATION, 0)
		);
		root.addOrReplaceChild(
				"right_wing_back",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(
								0, -12, 4,
								-32, 32, 0,
								Set.of(Direction.NORTH)
						),
				PartPose.rotation(0, WING_ROTATION, 0)
		);
		return LayerDefinition.create(mesh, 32, 32);
	}
	
	private static PartDefinition createHumanoidAlias(MeshDefinition mesh)
	{
		var root = mesh.getRoot();
		root.addOrReplaceChild("left_wing_front", new CubeListBuilder(), PartPose.ZERO);
		root.addOrReplaceChild("left_wing_back", new CubeListBuilder(), PartPose.ZERO);
		root.addOrReplaceChild("right_wing_front", new CubeListBuilder(), PartPose.ZERO);
		root.addOrReplaceChild("right_wing_back", new CubeListBuilder(), PartPose.ZERO);
		return root;
	}
	
	private static final BiFunction<ResourceLocation, Boolean, RenderType> WING = Util.memoize(WingNanoArmorDecoration::createWing);
	
	private static RenderType createWing(ResourceLocation id, boolean glow)
	{
		var state = RenderType.CompositeState.builder()
				.setShaderState(glow ? EIClientShaders.ARMOR_CUTOUT_GLOW : RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(id, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setDepthTestState(LEQUAL_DEPTH_TEST)
				.createCompositeState(false);
		return RenderType.create("wing", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, false, state);
	}
	
	private static ResourceLocation getWingTexture(int layerIndex)
	{
		return EI.id("textures/models/armor/nano_decorations/wing%s.png".formatted(layerIndex == 1 ? "_overlay" : ""));
	}
	
	public ModelPart leftWingFront, leftWingBack, rightWingFront, rightWingBack;
	
	public WingNanoArmorDecoration(ModelPart root)
	{
		super(root, EquipmentSlot.CHEST);
		this.leftWingFront = root.getChild("left_wing_front");
		this.leftWingBack = root.getChild("left_wing_back");
		this.rightWingFront = root.getChild("right_wing_front");
		this.rightWingBack = root.getChild("right_wing_back");
	}
	
	@Override
	protected boolean shouldRender(T entity, EquipmentSlot slot, ItemStack stack)
	{
		return NanoArmorLayer.isQuantumArmor(stack);
	}
	
	@Override
	public void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, EquipmentSlot slot, int packedLight,
					   ArmorMaterial.Layer armorLayer, int armorLayerIndex, int armorLayerColor, boolean armorLayerIsColored)
	{
		ResourceLocation texture = getWingTexture(armorLayerIndex);
		RenderType renderType = WING.apply(texture, armorLayerIsColored);
		VertexConsumer buffer = bufferSource.getBuffer(renderType);
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
		return List.of(leftWingFront, leftWingBack, rightWingFront, rightWingBack);
	}
	
	@Override
	public void copyFrom(HumanoidModel model)
	{
		leftWingFront.copyFrom(model.body);
		leftWingFront.yRot = -WING_ROTATION;
		leftWingBack.copyFrom(model.body);
		leftWingBack.yRot = -WING_ROTATION;
		rightWingFront.copyFrom(model.body);
		rightWingFront.yRot = WING_ROTATION;
		rightWingBack.copyFrom(model.body);
		rightWingBack.yRot = WING_ROTATION;
	}
}
