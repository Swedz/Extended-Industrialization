package net.swedz.extended_industrialization.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIArmorMaterials;
import net.swedz.extended_industrialization.EITags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.function.Function;

import static net.minecraft.client.renderer.RenderStateShard.*;

@Mixin(HumanoidArmorLayer.class)
public class NanoArmorRenderLayerColorMixin
{
	@Unique
	private static final Function<ResourceLocation, RenderType> ARMOR_CUTOUT_WITH_TRANSPARENCY = Util.memoize(
			(id) -> createArmorCutoutWithTransparency("armor_cutout_with_transparency", id, false)
	);
	
	@Unique
	private static RenderType createArmorCutoutWithTransparency(String name, ResourceLocation id, boolean equalDepthTest) {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(id, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setDepthTestState(equalDepthTest ? EQUAL_DEPTH_TEST : LEQUAL_DEPTH_TEST)
				.createCompositeState(true);
		return RenderType.create(name, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, state);
	}
	
	@ModifyConstant(
			method = "renderArmorPiece",
			constant = @Constant(intValue = DyedItemColor.LEATHER_COLOR)
	)
	private int getDefaultDyeColor(int leatherColor,
								   @Local ItemStack stack)
	{
		return stack.is(EITags.NANO_ARMOR) ? EIArmorMaterials.NANO_COLOR : leatherColor;
	}
	
	@WrapOperation(
			method = "renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/Model;ILnet/minecraft/resources/ResourceLocation;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/RenderType;armorCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
			)
	)
	private RenderType renderModel(ResourceLocation location, Operation<RenderType> original)
	{
		return location.equals(EI.id("textures/models/armor/nano_layer_1.png")) ?
				ARMOR_CUTOUT_WITH_TRANSPARENCY.apply(location) :
				original.call(location);
	}
}
