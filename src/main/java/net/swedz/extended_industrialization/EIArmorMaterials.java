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

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;

public final class EIArmorMaterials
{
	private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, EI.ID);
	
	public static final Holder<ArmorMaterial> NANO = create(
			"nano",
			(id) -> new ArmorMaterial(
					Util.make(new EnumMap<>(ArmorItem.Type.class), map ->
					{
						for(ArmorItem.Type type : ArmorItem.Type.values())
						{
							map.put(type, 8);
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
					2,
					0.1f
			)
	);
	
	public static final int NANO_COLOR = getNanoColor(DyeColor.LIME);
	
	public static int getNanoColor(DyeColor dyeColor)
	{
		if(!Arrays.asList(DyeColor.values()).contains(dyeColor))
		{
			return dyeColor.getTextColor();
		}
		return 0xFF000000 ^ switch (dyeColor)
		{
			case WHITE -> 0xFFFFFF;
			case ORANGE -> 0xFF7F00;
			case MAGENTA -> 0xFF00FF;
			case LIGHT_BLUE -> 0x00FFFF;
			case YELLOW -> 0xFFFF00;
			case LIME -> 0x00FF00;
			case PINK -> 0xFFB4FF;
			case GRAY -> 0x555555;
			case LIGHT_GRAY -> 0xAAAAAA;
			case CYAN -> 0x00AAAA;
			case PURPLE -> 0xAA00AA;
			case BLUE -> 0x0000FF;
			case BROWN -> 0x8B4513;
			case GREEN -> 0x00AA00;
			case RED -> 0xFF0000;
			case BLACK -> 0x000000;
		};
	}
	
	public static void init(IEventBus bus)
	{
		MATERIALS.register(bus);
	}
	
	private static Holder<ArmorMaterial> create(String name, Function<ResourceLocation, ArmorMaterial> creator)
	{
		return MATERIALS.register(name, creator);
	}
}
