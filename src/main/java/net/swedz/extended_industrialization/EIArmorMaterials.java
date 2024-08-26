package net.swedz.extended_industrialization;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.tesseract.neoforge.helper.ColorHelper;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;

public final class EIArmorMaterials
{
	private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, EI.ID);
	
	public static final Holder<ArmorMaterial> NANO = create(
			"nano",
			(id) -> new ArmorMaterial(
					Util.make(new EnumMap<>(ArmorItem.Type.class), (map) ->
					{
						for(ArmorItem.Type type : ArmorItem.Type.values())
						{
							map.put(type, 6);
						}
					}),
					0,
					SoundEvents.ARMOR_EQUIP_NETHERITE,
					() ->
					{
						throw new UnsupportedOperationException("Cannot repair nano armor");
					},
					List.of(
							new ArmorMaterial.Layer(id, "", true),
							new ArmorMaterial.Layer(id, "_overlay", false)
					),
					3,
					0.1f
			)
	);
	
	public static final int NANO_COLOR = ColorHelper.getVibrantColor(DyeColor.LIME);
	
	public static void init(IEventBus bus)
	{
		MATERIALS.register(bus);
	}
	
	private static Holder<ArmorMaterial> create(String name, Function<ResourceLocation, ArmorMaterial> creator)
	{
		return MATERIALS.register(name, creator);
	}
}
