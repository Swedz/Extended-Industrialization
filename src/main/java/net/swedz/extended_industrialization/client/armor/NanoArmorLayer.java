package net.swedz.extended_industrialization.client.armor;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
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
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIClientShaders;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.client.armor.decorations.NanoArmorDecoration;
import net.swedz.extended_industrialization.client.shader.AtlasTextureStateShard;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.client.renderer.RenderStateShard.*;

public final class NanoArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends HumanoidArmorLayer<T, M, NanoArmorModel<T>>
{
	private static final BiFunction<ResourceLocation, Boolean, RenderType> ARMOR_CUTOUT_WITH_TRANSPARENCY = Util.memoize(NanoArmorLayer::armorCutoutWithTransparency);
	
	private static RenderType armorCutoutWithTransparency(ResourceLocation id, boolean glow)
	{
		var state = RenderType.CompositeState.builder()
				.setShaderState(glow ? EIClientShaders.ARMOR_CUTOUT_GLOW : RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(id, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(NO_OVERLAY)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setDepthTestState(LEQUAL_DEPTH_TEST)
				.createCompositeState(false);
		return RenderType.create("armor_cutout_with_transparency", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, state);
	}
	
	private static final Function<ResourceLocation, RenderType> QUANTUM = Util.memoize(NanoArmorLayer::createQuantum);
	
	private static RenderStateShard.EmptyTextureStateShard quantumTexture(ResourceLocation maskTexture)
	{
		List<ResourceLocation> sprites = Lists.newArrayList();
		for(int i = 1; i <= 5; i++)
		{
			sprites.add(EI.id("shaders/quantum/%d".formatted(i)));
		}
		return new AtlasTextureStateShard(maskTexture, EI.id("textures/atlas/quantum.png"), sprites, false, false);
	}
	
	public static RenderType createQuantum(ResourceLocation id)
	{
		var state = RenderType.CompositeState.builder()
				.setShaderState(EIClientShaders.QUANTUM)
				.setTextureState(quantumTexture(id))
				.setCullState(NO_CULL)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.createCompositeState(false);
		return RenderType.create("quantum", EIClientShaders.QUANTUM_VERTEX_FORMAT, VertexFormat.Mode.QUADS, 1536, false, false, state);
	}
	
	public static boolean isQuantumArmor(ItemStack stack)
	{
		return stack.is(EIItems.QUANTUM_NANO_HELMET.asItem()) ||
			   stack.is(EIItems.QUANTUM_NANO_CHESTPLATE.asItem()) ||
			   stack.is(EIItems.QUANTUM_NANO_LEGGINGS.asItem()) ||
			   stack.is(EIItems.QUANTUM_NANO_BOOTS.asItem());
	}
	
	private final List<NanoArmorDecoration> decorations;
	
	public NanoArmorLayer(RenderLayerParent<T, M> renderer, NanoArmorModel<T> innerModel, NanoArmorModel<T> outerModel, List<NanoArmorDecoration> decorations, ModelManager modelManager)
	{
		super(renderer, innerModel, outerModel, modelManager);
		this.decorations = decorations;
	}
	
	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity,
					   float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float headPitch)
	{
		this.renderArmorPiece(poseStack, bufferSource, entity, EquipmentSlot.CHEST, packedLight, this.getArmorModel(EquipmentSlot.CHEST), limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch);
		this.renderArmorPiece(poseStack, bufferSource, entity, EquipmentSlot.LEGS, packedLight, this.getArmorModel(EquipmentSlot.LEGS), limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch);
		this.renderArmorPiece(poseStack, bufferSource, entity, EquipmentSlot.FEET, packedLight, this.getArmorModel(EquipmentSlot.FEET), limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch);
		this.renderArmorPiece(poseStack, bufferSource, entity, EquipmentSlot.HEAD, packedLight, this.getArmorModel(EquipmentSlot.HEAD), limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch);
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
			for(NanoArmorDecoration<T> decoration : decorations)
			{
				this.getParentModel().copyPropertiesTo(decoration);
				decoration.copyFrom(this.getParentModel());
				decoration.setAllVisible(decoration.test(entity, slot, stack));
			}
			
			boolean usesInnerModel = this.usesInnerModel(slot);
			boolean isQuantum = isQuantumArmor(stack);
			
			ArmorMaterial armorMaterial = item.getMaterial().value();
			IClientItemExtensions extensions = IClientItemExtensions.of(stack);
			int fallbackColor = extensions.getDefaultDyeColor(stack);
			
			for(int layerIndex = 0; layerIndex < armorMaterial.layers().size(); ++layerIndex)
			{
				ArmorMaterial.Layer armorMaterialLayer = armorMaterial.layers().get(layerIndex);
				int layerColor = extensions.getArmorLayerTintColor(stack, entity, armorMaterialLayer, layerIndex, fallbackColor);
				if(layerColor != 0)
				{
					boolean isColored = layerColor != -1;
					for(NanoArmorDecoration<T> decoration : decorations)
					{
						if(decoration.test(entity, slot, stack))
						{
							decoration.render(entity, poseStack, bufferSource, slot, packedLight, armorMaterialLayer, layerIndex, layerColor, isColored);
						}
					}
					ResourceLocation texture = ClientHooks.getArmorTexture(entity, stack, armorMaterialLayer, usesInnerModel, slot);
					RenderType renderType = layerIndex == 1 && isQuantum ?
							QUANTUM.apply(texture) :
							ARMOR_CUTOUT_WITH_TRANSPARENCY.apply(texture, isColored);
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
}
