package net.swedz.extended_industrialization;

import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.swedz.extended_industrialization.client.armor.NanoArmorLayer;
import net.swedz.extended_industrialization.client.armor.NanoArmorModel;

import java.util.function.Function;

@EventBusSubscriber(modid = EI.ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class EIClientModels
{
	public static NanoArmorModel NANO_ARMOR_INNER;
	public static NanoArmorModel NANO_ARMOR_OUTER;
	
	@SubscribeEvent
	private static void registerEntityLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
	{
		event.registerLayerDefinition(NanoArmorModel.INNER_LAYER, () -> NanoArmorModel.createLayer(LayerDefinitions.INNER_ARMOR_DEFORMATION));
		event.registerLayerDefinition(NanoArmorModel.OUTER_LAYER, () -> NanoArmorModel.createLayer(LayerDefinitions.OUTER_ARMOR_DEFORMATION));
	}
	
	private static void addGlobalLayer(EntityRenderersEvent.AddLayers event, Function<LivingEntityRenderer, RenderLayer> layer)
	{
		for(var entityType : event.getEntityTypes())
		{
			if(event.getRenderer(entityType) instanceof LivingEntityRenderer renderer)
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
		addGlobalLayer(event, (r) -> new NanoArmorLayer<>(r, NANO_ARMOR_INNER, NANO_ARMOR_OUTER, context.getModelManager()));
	}
}
