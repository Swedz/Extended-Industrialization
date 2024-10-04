package net.swedz.extended_industrialization;

import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineBlockEntityRenderer;
import aztech.modern_industrialization.machines.blockentities.multiblocks.LargeTankMultiblockBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBER;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockTankBER;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.swedz.extended_industrialization.client.MachineChainerHighlightRenderer;
import net.swedz.extended_industrialization.client.NanoGravichestplateHudRenderer;
import net.swedz.extended_industrialization.client.tesla.TeslaPartMultiblockRenderer;
import net.swedz.extended_industrialization.client.tesla.TeslaPartSingleBlockRenderer;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.extended_industrialization.item.SteamChainsawItem;
import net.swedz.extended_industrialization.item.machineconfig.MachineConfigCardItem;
import net.swedz.extended_industrialization.item.tooltip.MachineConfigCardTooltipComponent;
import net.swedz.extended_industrialization.item.tooltip.SteamChainsawTooltipComponent;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkPart;
import net.swedz.extended_industrialization.network.packet.ModifyElectricToolSpeedPacket;
import net.swedz.tesseract.neoforge.item.DynamicDyedItem;

@Mod(value = EI.ID, dist = Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = EI.ID, bus = EventBusSubscriber.Bus.MOD)
public final class EIClient
{
	public EIClient(IEventBus bus)
	{
		EIKeybinds.init(bus);
		
		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, (event) ->
		{
			for(EIKeybinds.Keybind keybind : EIKeybinds.Registry.getMappings())
			{
				while(keybind.holder().get().consumeClick())
				{
					keybind.action().run();
				}
			}
		});
		
		NeoForge.EVENT_BUS.addListener(InputEvent.MouseScrollingEvent.class, (event) ->
		{
			if(Screen.hasAltDown())
			{
				Player player = Minecraft.getInstance().player;
				ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
				if(stack.getItem() instanceof ElectricToolItem)
				{
					boolean increase = event.getScrollDeltaY() > 0;
					int speed = ElectricToolItem.getToolSpeed(stack);
					if(increase ? speed < ElectricToolItem.SPEED_MAX : speed > ElectricToolItem.SPEED_MIN)
					{
						new ModifyElectricToolSpeedPacket(increase).sendToServer();
					}
					event.setCanceled(true);
				}
			}
		});
	}
	
	@SubscribeEvent
	private static void onRegisterColorItems(RegisterColorHandlersEvent.Item event)
	{
		event.register(
				(stack, color) -> color > 0 ? -1 : DyedItemColor.getOrDefault(stack, ((DynamicDyedItem) stack.getItem()).getDefaultDyeColor()),
				EIItems.ULTIMATE_LASER_DRILL,
				EIItems.NANO_HELMET,
				EIItems.NANO_CHESTPLATE,
				EIItems.NANO_GRAVICHESTPLATE,
				EIItems.NANO_LEGGINGS,
				EIItems.NANO_BOOTS
		);
	}
	
	/**
	 * Taken from {@link aztech.modern_industrialization.MIClient#registerBlockEntityRenderers(FMLClientSetupEvent)}. This is needed to make multiblocks render their layout when holding a wrench.
	 */
	@SubscribeEvent
	private static void registerBlockEntityRenderers(FMLClientSetupEvent event)
	{
		for(DeferredHolder<Block, ? extends Block> blockDef : EIBlocks.Registry.BLOCKS.getEntries())
		{
			if(blockDef.get() instanceof MachineBlock machine)
			{
				MachineBlockEntity blockEntity = machine.getBlockEntityInstance();
				BlockEntityType type = blockEntity.getType();
				
				if(blockEntity instanceof MachineChainerMachineBlockEntity)
				{
					BlockEntityRenderers.register(type, MachineChainerHighlightRenderer::new);
				}
				else if(blockEntity instanceof TeslaNetworkPart)
				{
					if(blockEntity instanceof MultiblockMachineBlockEntity)
					{
						BlockEntityRenderers.register(type, TeslaPartMultiblockRenderer::new);
					}
					else
					{
						BlockEntityRenderers.register(type, TeslaPartSingleBlockRenderer::new);
					}
				}
				else if(blockEntity instanceof LargeTankMultiblockBlockEntity)
				{
					BlockEntityRenderers.register(type, MultiblockTankBER::new);
				}
				else if(blockEntity instanceof MultiblockMachineBlockEntity)
				{
					BlockEntityRenderers.register(type, MultiblockMachineBER::new);
				}
				else
				{
					BlockEntityRenderers.register(type, (c) -> new MachineBlockEntityRenderer<>(c));
				}
			}
		}
	}
	
	@SubscribeEvent
	private static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event)
	{
		event.register(SteamChainsawItem.SteamChainsawTooltipData.class, SteamChainsawTooltipComponent::new);
		event.register(MachineConfigCardItem.TooltipData.class, MachineConfigCardTooltipComponent::new);
	}
	
	@SubscribeEvent
	private static void registerGuiLayers(RegisterGuiLayersEvent event)
	{
		event.registerAbove(VanillaGuiLayers.SELECTED_ITEM_NAME, EI.id("nano_gravichestplate_activation_status"), NanoGravichestplateHudRenderer::render);
	}
}
