package net.swedz.extended_industrialization.client.nanosuit;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.swedz.extended_industrialization.client.nanosuit.decorations.NanoSuitDecorationModel;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;

import java.util.List;

import static net.swedz.extended_industrialization.EIClientRenderTypes.*;

public final class NanoArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends HumanoidArmorLayer<T, M, NanoArmorModel<T>>
{
	public static RenderType renderType(boolean useQuantumShader, boolean cull, int layerIndex, NanoSuitArmorItem item, ResourceLocation texture, boolean isColored)
	{
		if(layerIndex == 1)
		{
			return (useQuantumShader && item.isQuantum()) ? NANO_QUANTUM.apply(texture) : RenderType.armorCutoutNoCull(texture);
		}
		else
		{
			return (cull ? ARMOR_CUTOUT_CULL_WITH_TRANSPARENCY : ARMOR_CUTOUT_NO_CULL_WITH_TRANSPARENCY).apply(texture, isColored);
		}
	}
	
	public static RenderType armorRenderType(int layerIndex, NanoSuitArmorItem item, ResourceLocation texture, boolean isColored)
	{
		return renderType(true, false, layerIndex, item, texture, isColored);
	}
	
	public static RenderType decorationRenderType(int layerIndex, NanoSuitArmorItem item, ResourceLocation texture, boolean isColored)
	{
		return renderType(false, true, layerIndex, item, texture, isColored);
	}
	
	private final List<NanoSuitDecorationModel> decorations;
	
	public NanoArmorLayer(RenderLayerParent<T, M> renderer, NanoArmorModel<T> innerModel, NanoArmorModel<T> outerModel, List<NanoSuitDecorationModel> decorations, ModelManager modelManager)
	{
		super(renderer, innerModel, outerModel, modelManager);
		this.decorations = decorations;
	}
	
	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity,
					   float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float headPitch)
	{
		// This order is intentional, the head needs to render after the chestplate or else the chest becomes invisible behind the visor
		this.renderArmorPiece(poseStack, bufferSource, entity, EquipmentSlot.CHEST, packedLight, this.getArmorModel(EquipmentSlot.CHEST), limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch);
		this.renderArmorPiece(poseStack, bufferSource, entity, EquipmentSlot.HEAD, packedLight, this.getArmorModel(EquipmentSlot.HEAD), limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch);
		this.renderArmorPiece(poseStack, bufferSource, entity, EquipmentSlot.LEGS, packedLight, this.getArmorModel(EquipmentSlot.LEGS), limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch);
		this.renderArmorPiece(poseStack, bufferSource, entity, EquipmentSlot.FEET, packedLight, this.getArmorModel(EquipmentSlot.FEET), limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch);
		
		this.renderArmorPieceDecorations(poseStack, bufferSource, entity, EquipmentSlot.HEAD, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch);
		this.renderArmorPieceDecorations(poseStack, bufferSource, entity, EquipmentSlot.CHEST, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch);
		this.renderArmorPieceDecorations(poseStack, bufferSource, entity, EquipmentSlot.LEGS, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch);
		this.renderArmorPieceDecorations(poseStack, bufferSource, entity, EquipmentSlot.FEET, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch);
	}
	
	private void renderArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, T entity, EquipmentSlot slot, int packedLight, NanoArmorModel<T> model,
								  float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float headPitch)
	{
		ItemStack stack = entity.getItemBySlot(slot);
		if(stack.getItem() instanceof NanoSuitArmorItem item && item.getEquipmentSlot() == slot)
		{
			this.getParentModel().copyPropertiesTo(model);
			this.setPartVisibility(model, slot);
			model.slot = slot;
			
			boolean usesInnerModel = this.usesInnerModel(slot);
			
			ArmorMaterial armorMaterial = item.getMaterial().value();
			IClientItemExtensions extensions = IClientItemExtensions.of(stack);
			int fallbackColor = extensions.getDefaultDyeColor(stack);
			
			for(int layerIndex = armorMaterial.layers().size() - 1; layerIndex >= 0; --layerIndex)
			{
				ArmorMaterial.Layer armorMaterialLayer = armorMaterial.layers().get(layerIndex);
				int layerColor = extensions.getArmorLayerTintColor(stack, entity, armorMaterialLayer, layerIndex, fallbackColor);
				if(layerColor != 0)
				{
					boolean isColored = layerColor != -1;
					ResourceLocation texture = ClientHooks.getArmorTexture(entity, stack, armorMaterialLayer, usesInnerModel, slot);
					RenderType renderType = armorRenderType(layerIndex, item, texture, isColored);
					VertexConsumer buffer = bufferSource.getBuffer(renderType);
					model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, layerColor);
				}
			}
			
			ArmorTrim armorTrim = stack.get(DataComponents.TRIM);
			if(armorTrim != null)
			{
				this.renderTrim(item.getMaterial(), poseStack, bufferSource, packedLight, armorTrim, model, usesInnerModel);
			}
			
			if(stack.hasFoil())
			{
				this.renderGlint(poseStack, bufferSource, packedLight, model);
			}
		}
	}
	
	private void renderArmorPieceDecorations(PoseStack poseStack, MultiBufferSource bufferSource, T entity, EquipmentSlot slot, int packedLight,
											 float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float headPitch)
	{
		ItemStack stack = entity.getItemBySlot(slot);
		if(stack.getItem() instanceof NanoSuitArmorItem item && item.getEquipmentSlot() == slot)
		{
			for(NanoSuitDecorationModel<T> decoration : decorations)
			{
				this.getParentModel().copyPropertiesTo(decoration);
				boolean shouldRender = decoration.test(entity, slot, stack);
				decoration.setAllVisible(shouldRender);
				
				if(shouldRender)
				{
					ArmorMaterial armorMaterial = item.getMaterial().value();
					IClientItemExtensions extensions = IClientItemExtensions.of(stack);
					int fallbackColor = extensions.getDefaultDyeColor(stack);
					
					for(int layerIndex = armorMaterial.layers().size() - 1; layerIndex >= 0; --layerIndex)
					{
						ArmorMaterial.Layer armorMaterialLayer = armorMaterial.layers().get(layerIndex);
						int layerColor = extensions.getArmorLayerTintColor(stack, entity, armorMaterialLayer, layerIndex, fallbackColor);
						if(layerColor != 0)
						{
							boolean isColored = layerColor != -1;
							decoration.render(entity, slot, stack, item, poseStack, bufferSource, packedLight, armorMaterialLayer, layerIndex, layerColor, isColored);
						}
					}
				}
			}
		}
	}
}
