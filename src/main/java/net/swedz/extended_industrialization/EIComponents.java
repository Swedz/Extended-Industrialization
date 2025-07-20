package net.swedz.extended_industrialization;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.component.RainbowDataComponent;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.extended_industrialization.item.machineconfig.MachineConfig;
import net.swedz.extended_industrialization.item.teslalinkable.SelectedTeslaNetwork;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.function.Supplier;

public final class EIComponents
{
	private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, EI.ID);
	
	public static final Supplier<DataComponentType<Boolean>>               HIDE_BAR               = create("hide_bar", Codec.BOOL, ByteBufCodecs.BOOL);
	public static final Supplier<DataComponentType<Integer>>               SOLAR_TICKS            = create("solar_ticks", ExtraCodecs.POSITIVE_INT, ByteBufCodecs.VAR_INT);
	public static final Supplier<DataComponentType<MachineConfig>>         MACHINE_CONFIG         = create("machine_config", MachineConfig.CODEC, MachineConfig.STREAM_CODEC);
	public static final Supplier<DataComponentType<ElectricToolItem.Mode>> ELECTRIC_TOOL_MODE     = create("electric_tool_mode", CodecHelper.forLowercaseEnum(ElectricToolItem.Mode.class), CodecHelper.forLowercaseEnumStream(ElectricToolItem.Mode.class));
	public static final Supplier<DataComponentType<Integer>>               ELECTRIC_TOOL_SPEED    = create("electric_tool_speed", ExtraCodecs.intRange(ElectricToolItem.SPEED_MIN, ElectricToolItem.SPEED_MAX), ByteBufCodecs.VAR_INT);
	public static final Supplier<DataComponentType<Boolean>>               ACTIVATED              = create("activated", Codec.BOOL, ByteBufCodecs.BOOL);
	public static final Supplier<DataComponentType<RainbowDataComponent>>  RAINBOW                = create("rainbow", RainbowDataComponent.CODEC, RainbowDataComponent.STREAM_CODEC);
	public static final Supplier<DataComponentType<SelectedTeslaNetwork>>  SELECTED_TESLA_NETWORK = create("selected_tesla_network", SelectedTeslaNetwork.CODEC, SelectedTeslaNetwork.STREAM_CODEC);
	public static final Supplier<DataComponentType<Boolean>>               MEOW                   = create("meow", Codec.BOOL, ByteBufCodecs.BOOL);
	
	public static void init(IEventBus bus)
	{
		COMPONENTS.register(bus);
	}
	
	private static <D> DeferredHolder<DataComponentType<?>, DataComponentType<D>> create(String name, Codec<D> codec, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec)
	{
		return COMPONENTS.registerComponentType(name, (b) -> b.persistent(codec).networkSynchronized(streamCodec));
	}
}
