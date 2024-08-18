package net.swedz.extended_industrialization.items.machineconfig;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.DropableComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.BiConsumer;

public record MachineConfigPanel(
		Map<SlotPanel.SlotType, ItemStack> slotItems
) implements MachineConfigApplicable<MachineBlockEntity>
{
	public static final Codec<MachineConfigPanel> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.unboundedMap(
							ExtraCodecs.idResolverCodec(Enum::ordinal, (i) -> SlotPanel.SlotType.values()[i], -1),
							ItemStack.CODEC
					).fieldOf("items").forGetter(MachineConfigPanel::slotItems)
			)
			.apply(instance, MachineConfigPanel::new));
	
	public static MachineConfigPanel from(MachineBlockEntity machine)
	{
		Map<SlotPanel.SlotType, ItemStack> slotItems = Maps.newHashMap();
		
		BiConsumer<SlotPanel.SlotType, DropableComponent> action = (slotType, component) ->
		{
			if(!component.getDrop().isEmpty())
			{
				slotItems.put(slotType, component.getDrop());
			}
		};
		
		machine.forComponentType(RedstoneControlComponent.class, (component) -> action.accept(SlotPanel.SlotType.REDSTONE_MODULE, component));
		machine.forComponentType(UpgradeComponent.class, (component) -> action.accept(SlotPanel.SlotType.UPGRADES, component));
		machine.forComponentType(CasingComponent.class, (component) -> action.accept(SlotPanel.SlotType.CASINGS, component));
		
		return new MachineConfigPanel(slotItems);
	}
	
	@Override
	public boolean matches(MachineBlockEntity target)
	{
		return target.mapComponentOrDefault(SlotPanel.Server.class, (component) -> true, false);
	}
	
	@Override
	public boolean apply(Player player, MachineBlockEntity target, Simulation simulation)
	{
		// TODO
		return false;
	}
}
