package net.swedz.extended_industrialization;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.swedz.extended_industrialization.item.ToggleableItem;
import net.swedz.extended_industrialization.network.packet.ToggleToggleableItemPacket;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public final class EIKeybinds
{
	public static final class Registry
	{
		private static final Set<Keybind> MAPPINGS = Sets.newHashSet();
		
		private static void init(RegisterKeyMappingsEvent event)
		{
			MAPPINGS.forEach((m) -> event.register(m.holder().get()));
		}
		
		private static void include(Keybind mapping)
		{
			MAPPINGS.add(mapping);
		}
		
		public static Set<Keybind> getMappings()
		{
			return Collections.unmodifiableSet(MAPPINGS);
		}
	}
	
	public static void init(RegisterKeyMappingsEvent event)
	{
		Registry.init(event);
	}
	
	public static void init(IEventBus bus)
	{
		bus.addListener(RegisterKeyMappingsEvent.class, EIKeybinds::init);
	}
	
	public static final String CATEGORY = Util.makeDescriptionId("key.categories", EI.id(EI.ID));
	
	public static final Keybind TOGGLE_HELMET_ABILITY = create(
			"toggle_helmet_ability",
			"Toggle Helmet Ability",
			(id) -> new KeyMapping(
					id,
					InputConstants.Type.KEYSYM,
					GLFW.GLFW_KEY_B,
					CATEGORY
			),
			toggleableItemAction(EquipmentSlot.HEAD)
	);
	
	private static Keybind create(String id, String englishName, Function<String, KeyMapping> creator, Runnable action)
	{
		String descriptionId = Util.makeDescriptionId("key", EI.id(id));
		Keybind keybind = new Keybind(descriptionId, englishName, Lazy.of(() -> creator.apply(descriptionId)), action);
		Registry.include(keybind);
		return keybind;
	}
	
	private static Runnable toggleableItemAction(EquipmentSlot slot)
	{
		return () ->
		{
			Player player = Minecraft.getInstance().player;
			ItemStack stack = player.getItemBySlot(slot);
			if(stack.getItem() instanceof ToggleableItem item)
			{
				boolean activated = !item.isActivated(stack);
				item.setActivated(player, stack, activated);
				new ToggleToggleableItemPacket(slot, activated).sendToServer();
			}
		};
	}
	
	public record Keybind(String descriptionId, String englishName, Lazy<KeyMapping> holder, Runnable action)
	{
	}
}
