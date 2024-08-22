package net.swedz.extended_industrialization;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.extended_industrialization.item.machineconfig.MachineConfig;

import java.util.function.Supplier;

public final class EIComponents
{
	private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(EI.ID);
	
	public static final Supplier<DataComponentType<Boolean>>       HIDE_BAR            = COMPONENTS.registerComponentType(
			"hide_bar",
			(b) -> b.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
	);
	public static final Supplier<DataComponentType<Integer>>       SOLAR_TICKS         = COMPONENTS.registerComponentType(
			"solar_ticks",
			(b) -> b.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	public static final Supplier<DataComponentType<MachineConfig>> MACHINE_CONFIG      = COMPONENTS.registerComponentType(
			"machine_config",
			(b) -> b.persistent(MachineConfig.CODEC).networkSynchronized(MachineConfig.STREAM_CODEC)
	);
	public static final Supplier<DataComponentType<Integer>>       ELECTRIC_TOOL_SPEED = COMPONENTS.registerComponentType(
			"electric_tool_speed",
			(b) -> b.persistent(ExtraCodecs.intRange(ElectricToolItem.SPEED_MIN, ElectricToolItem.SPEED_MAX)).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	
	public static void init(IEventBus bus)
	{
		COMPONENTS.register(bus);
	}
}
