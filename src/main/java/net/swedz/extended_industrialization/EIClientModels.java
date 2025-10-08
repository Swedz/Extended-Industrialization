package net.swedz.extended_industrialization;

import com.google.common.collect.Lists;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.swedz.extended_industrialization.client.nanosuit.NanoArmorLayer;
import net.swedz.extended_industrialization.client.nanosuit.NanoArmorModel;
import net.swedz.extended_industrialization.client.nanosuit.decorations.MeowNanoSuitDecorationModel;
import net.swedz.extended_industrialization.client.nanosuit.decorations.NanoSuitDecorationModel;
import net.swedz.extended_industrialization.client.nanosuit.decorations.WingNanoSuitDecorationModel;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@EventBusSubscriber(modid = EI.ID, value = Dist.CLIENT)
public final class EIClientModels
{
	private static NanoArmorModel                NANO_ARMOR_INNER;
	private static NanoArmorModel                NANO_ARMOR_OUTER;
	private static List<NanoSuitDecorationModel> NANO_ARMOR_DECORATIONS;
	
	@SubscribeEvent
	private static void registerEntityLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
	{
		event.registerLayerDefinition(NanoArmorModel.INNER_LAYER, () -> NanoArmorModel.createLayer(LayerDefinitions.INNER_ARMOR_DEFORMATION));
		event.registerLayerDefinition(NanoArmorModel.OUTER_LAYER, () -> NanoArmorModel.createLayer(LayerDefinitions.OUTER_ARMOR_DEFORMATION));
		event.registerLayerDefinition(WingNanoSuitDecorationModel.LAYER, WingNanoSuitDecorationModel::createLayer);
		event.registerLayerDefinition(MeowNanoSuitDecorationModel.LAYER, MeowNanoSuitDecorationModel::createLayer);
	}
	
	private static void addGlobalLayer(EntityRenderersEvent.AddLayers event,
									   Function<LivingEntityRenderer, RenderLayer> layer,
									   Predicate<LivingEntityRenderer> rendererFilter)
	{
		for(var entityType : event.getEntityTypes())
		{
			if(event.getRenderer(entityType) instanceof LivingEntityRenderer renderer &&
			   rendererFilter.test(renderer))
			{
				renderer.addLayer(layer.apply(renderer));
			}
		}
		for(var skin : event.getSkins())
		{
			if(event.getSkin(skin) instanceof LivingEntityRenderer renderer)
			{
				renderer.addLayer(layer.apply(renderer));
			}
		}
	}
	
	@SubscribeEvent
	private static void registerEntityLayers(EntityRenderersEvent.AddLayers event)
	{
		var context = event.getContext();
		
		NANO_ARMOR_INNER = new NanoArmorModel(context.bakeLayer(NanoArmorModel.INNER_LAYER));
		NANO_ARMOR_OUTER = new NanoArmorModel(context.bakeLayer(NanoArmorModel.OUTER_LAYER));
		NANO_ARMOR_DECORATIONS = Lists.newArrayList();
		NANO_ARMOR_DECORATIONS.add(new WingNanoSuitDecorationModel(context.bakeLayer(WingNanoSuitDecorationModel.LAYER)));
		NANO_ARMOR_DECORATIONS.add(new MeowNanoSuitDecorationModel(context.bakeLayer(MeowNanoSuitDecorationModel.LAYER)));
		NANO_ARMOR_DECORATIONS = Collections.unmodifiableList(NANO_ARMOR_DECORATIONS);
		addGlobalLayer(
				event,
				(r) -> new NanoArmorLayer<>(r, NANO_ARMOR_INNER, NANO_ARMOR_OUTER, NANO_ARMOR_DECORATIONS, context.getModelManager()),
				(r) -> r.getModel() instanceof HumanoidModel<?>
		);
	}
}
