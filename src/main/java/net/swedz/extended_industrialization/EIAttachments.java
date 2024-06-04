package net.swedz.extended_industrialization;

import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class EIAttachments
{
	public static final class Registry
	{
		public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, EI.ID);
		
		private static void init(IEventBus bus)
		{
			ATTACHMENT_TYPES.register(bus);
		}
	}
	
	public static final Supplier<AttachmentType<Integer>> SOLAR_TICKS = create("solar_ticks", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());
	
	public static void init(IEventBus bus)
	{
		Registry.init(bus);
	}
	
	private static <T> Supplier<AttachmentType<T>> create(String name, Supplier<AttachmentType<T>> sup)
	{
		return Registry.ATTACHMENT_TYPES.register(name, sup);
	}
}
