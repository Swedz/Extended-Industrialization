package net.swedz.extended_industrialization.compat.continuity;

import com.mojang.logging.LogUtils;
import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.fml.ModList;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

/**
 * <p>This is a bit of a hack to safely unwrap models wrapped by Continuity. Without unwrapping the model, there can
 * be issues with mismatching model instances.</p>
 *
 * <p>This is taken from Modern Industrialization's
 * {@link aztech.modern_industrialization.machines.MachineBlockEntityRenderer}.</p>
 */
public final class ContinuityModelUnwrapper
{
	private static final MethodHandle UNWRAP_BAKED_MODEL;
	
	static
	{
		MethodHandle unwrapBakedModel = null;
		if(ModList.get().isLoaded("fabric_renderer_api_v1"))
		{
			try
			{
				var wrapperBakedModel = Class.forName("net.fabricmc.fabric.api.renderer.v1.model.WrapperBakedModel");
				var unwrap = wrapperBakedModel.getMethod("unwrap", BakedModel.class);
				unwrapBakedModel = MethodHandles.lookup().unreflect(unwrap);
			}
			catch (ReflectiveOperationException ex)
			{
				LogUtils.getLogger().error("Failed to reflect WrapperBakedModel.unwrap method", ex);
			}
		}
		UNWRAP_BAKED_MODEL = unwrapBakedModel;
	}
	
	public static BakedModel unwrap(BakedModel model)
	{
		if(UNWRAP_BAKED_MODEL != null)
		{
			try
			{
				model = (BakedModel) UNWRAP_BAKED_MODEL.invokeExact(model);
			}
			catch (Throwable ex)
			{
				throw new RuntimeException("Failed to unwrap model", ex);
			}
		}
		return model;
	}
}
