package net.swedz.extended_industrialization.client.armor;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.swedz.extended_industrialization.EI;

import java.util.List;

public class NanoArmorModel<T extends LivingEntity> extends HumanoidArmorModel<T>
{
	public static final ModelLayerLocation INNER_LAYER = new ModelLayerLocation(EI.id("nano_armor"), "inner_armor");
	public static final ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(EI.id("nano_armor"), "outer_armor");
	
	public static LayerDefinition createLayer(CubeDeformation deformation)
	{
		var mesh = createMesh(deformation, 0);
		var root = createHumanoidAlias(mesh);
		root.addOrReplaceChild(
				"head",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(
								-4, -8, -4,
								8, 8, 8,
								deformation
						),
				PartPose.offset(0, 0, 0)
		);
		root.addOrReplaceChild(
				"hat",
				CubeListBuilder.create()
						.texOffs(32, 0)
						.addBox(
								-4, -8, -4,
								8, 8, 8,
								deformation.extend(0.5f)
						),
				PartPose.offset(0, 0, 0)
		);
		root.addOrReplaceChild(
				"body",
				CubeListBuilder.create()
						.texOffs(16, 16)
						.addBox(
								-4, 0, -2,
								8, 12, 4,
								deformation
						),
				PartPose.offset(0, 0, 0)
		);
		root.addOrReplaceChild(
				"left_arm",
				CubeListBuilder.create()
						.texOffs(40, 16)
						.mirror()
						.addBox(
								-1, -2, -2,
								4, 12, 4,
								deformation
						),
				PartPose.offset(5, 2, 0)
		);
		root.addOrReplaceChild(
				"right_arm",
				CubeListBuilder.create()
						.texOffs(40, 16)
						.addBox(
								-3, -2, -2,
								4, 12, 4,
								deformation
						),
				PartPose.offset(-5, 2, 0)
		);
		root.addOrReplaceChild(
				"left_leg",
				CubeListBuilder.create()
						.texOffs(0, 16)
						.mirror()
						.addBox(
								-2, 0, -2,
								4, 12, 4,
								deformation.extend(-0.1f)
						),
				PartPose.offset(1.9f, 12, 0)
		);
		root.addOrReplaceChild(
				"right_leg",
				CubeListBuilder.create()
						.texOffs(0, 16)
						.addBox(
								-2, 0, -2,
								4, 12, 4,
								deformation.extend(-0.1f)
						),
				PartPose.offset(-1.9f, 12, 0)
		);
		return LayerDefinition.create(mesh, 64, 32);
	}
	
	private static PartDefinition createHumanoidAlias(MeshDefinition mesh)
	{
		var root = mesh.getRoot();
		root.addOrReplaceChild("head", new CubeListBuilder(), PartPose.ZERO);
		root.addOrReplaceChild("body", new CubeListBuilder(), PartPose.ZERO);
		root.addOrReplaceChild("left_arm", new CubeListBuilder(), PartPose.ZERO);
		root.addOrReplaceChild("right_arm", new CubeListBuilder(), PartPose.ZERO);
		root.addOrReplaceChild("left_leg", new CubeListBuilder(), PartPose.ZERO);
		root.addOrReplaceChild("right_leg", new CubeListBuilder(), PartPose.ZERO);
		return root;
	}
	
	public EquipmentSlot slot;
	public ModelPart     root, head, body, leftArm, rightArm, leftLeg, rightLeg;
	
	public NanoArmorModel(ModelPart root)
	{
		super(root);
		this.root = root;
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.leftArm = root.getChild("left_arm");
		this.rightArm = root.getChild("right_arm");
		this.leftLeg = root.getChild("left_leg");
		this.rightLeg = root.getChild("right_leg");
	}
	
	@Override
	protected Iterable<ModelPart> headParts()
	{
		return slot == EquipmentSlot.HEAD ? List.of(head) : List.of();
	}
	
	@Override
	protected Iterable<ModelPart> bodyParts()
	{
		if(slot == EquipmentSlot.CHEST)
		{
			return List.of(body, leftArm, rightArm);
		}
		else if(slot == EquipmentSlot.LEGS)
		{
			return List.of(body, leftLeg, rightLeg);
		}
		else if(slot == EquipmentSlot.FEET)
		{
			return List.of(leftLeg, rightLeg);
		}
		return List.of();
	}
	
	public void copyFromDefault(HumanoidModel model)
	{
		body.copyFrom(model.body);
		head.copyFrom(model.head);
		leftArm.copyFrom(model.leftArm);
		rightArm.copyFrom(model.rightArm);
	}
}
