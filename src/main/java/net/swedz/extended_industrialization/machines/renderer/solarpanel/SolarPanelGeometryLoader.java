package net.swedz.extended_industrialization.machines.renderer.solarpanel;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.swedz.extended_industrialization.EI;

public final class SolarPanelGeometryLoader implements IGeometryLoader<SolarPanelUnbakedModel>
{
	public static final SolarPanelGeometryLoader INSTANCE = new SolarPanelGeometryLoader();
	
	public static final ResourceLocation ID = EI.id("solar_panel");
	
	private SolarPanelGeometryLoader()
	{
	}
	
	@Override
	public SolarPanelUnbakedModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException
	{
		return new SolarPanelUnbakedModel(jsonObject);
	}
}
