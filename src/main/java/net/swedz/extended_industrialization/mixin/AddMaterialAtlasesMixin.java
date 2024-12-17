package net.swedz.extended_industrialization.mixin;

import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Mixin(ModelManager.class)
public class AddMaterialAtlasesMixin
{
	@Shadow
	@Final
	@Mutable
	private static Map<ResourceLocation, ResourceLocation> VANILLA_ATLASES;
	
	@Inject(
			method = "<clinit>",
			at = @At("TAIL")
	)
	private static void init(CallbackInfo callback)
	{
		var newMap = new HashMap<>(VANILLA_ATLASES);
		newMap.put(EI.id("textures/atlas/quantum.png"), EI.id("quantum"));
		VANILLA_ATLASES = Collections.unmodifiableMap(newMap);
	}
}
