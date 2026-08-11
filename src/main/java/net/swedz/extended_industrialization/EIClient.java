package net.swedz.extended_industrialization;

import aztech.modern_industrialization.client.machines.MachineBlockEntityRenderer;
import aztech.modern_industrialization.client.machines.multiblocks.MultiblockMachineBER;
import aztech.modern_industrialization.client.machines.multiblocks.MultiblockTankBER;
import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.blockentities.multiblocks.LargeTankMultiblockBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMaterialAtlasesEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.swedz.extended_industrialization.client.NanoGravichestplateHudRenderer;
import net.swedz.extended_industrialization.client.ber.beacon.ElectricBeaconBlockEntityRenderer;
import net.swedz.extended_industrialization.client.ber.chainer.MachineChainerHighlightRenderer;
import net.swedz.extended_industrialization.client.ber.tesla.TeslaPartMultiblockRenderer;
import net.swedz.extended_industrialization.client.ber.tesla.TeslaPartSingleBlockRenderer;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaBehavior;
import net.swedz.extended_industrialization.client.entity.NanoSaberSweepEntityRenderer;
import net.swedz.extended_industrialization.client.model.chainer.MachineChainerUnbakedModel;
import net.swedz.extended_industrialization.client.model.tesla.TeslaParticleGeneratorModel;
import net.swedz.extended_industrialization.client.model.tesla.TeslaUnbakedModel;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.extended_industrialization.item.SteamChainsawItem;
import net.swedz.extended_industrialization.item.tooltip.SteamChainsawTooltipComponent;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.beacon.ElectricBeaconMachineBlockEntity;
import net.swedz.extended_industrialization.network.packet.ModifyElectricToolSpeedPacket;
import net.swedz.tesseract.api.Assert;
import net.swedz.tesseract.config.ConfigManager;
import net.swedz.tesseract.neoforge.config.ModConfigFileAccess;
import net.swedz.tesseract.neoforge.item.DynamicDyedItem;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

@Mod(value = EI.ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = EI.ID, value = Dist.CLIENT)
public final class EIClient
{
	public EIClient(IEventBus bus, ModContainer container)
	{
		setupConfig(bus, container);
		
		EIKeybinds.init(bus);
		
		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, (event) ->
		{
			for(var keybind : EIKeybinds.Registry.getMappings())
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
				var player = Minecraft.getInstance().player;
				var stack = player.getItemInHand(InteractionHand.MAIN_HAND);
				if(stack.getItem() instanceof ElectricToolItem tool && tool.getToolType().hasAdjustableSpeed())
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
	
	private static EIClientConfig CONFIG;
	
	public static EIClientConfig config()
	{
		Assert.notNull(CONFIG, "Config not yet loaded");
		return CONFIG;
	}
	
	private static void setupConfig(IEventBus bus, ModContainer container)
	{
		var file = new ModConfigFileAccess(container, ModConfig.Type.CLIENT);
		var instance = new ConfigManager(file)
				.build(EIClientConfig.class)
				.load();
		bus.addListener(FMLCommonSetupEvent.class, (event) -> instance.load(false));
		CONFIG = instance.config();
	}
	
	@SubscribeEvent
	private static void registerItemProperties(FMLClientSetupEvent event)
	{
		event.enqueueWork(() -> EIItems.values().forEach(ItemHolder::triggerClientRegistrationListener));
	}
	
	@SubscribeEvent
	private static void onRegisterColorItems(RegisterColorHandlersEvent.Item event)
	{
		event.register(
				(stack, color) -> color > 0 ? -1 : DyedItemColor.getOrDefault(stack, ((DynamicDyedItem) stack.getItem()).getDefaultDyeColor()),
				EIItems.NANO_SABER,
				EIItems.ULTIMATE_LASER_DRILL,
				EIItems.NANO_HELMET,
				EIItems.NANO_CHESTPLATE,
				EIItems.NANO_GRAVICHESTPLATE,
				EIItems.NANO_LEGGINGS,
				EIItems.NANO_BOOTS,
				EIItems.NANO_QUANTUM_HELMET,
				EIItems.NANO_QUANTUM_CHESTPLATE,
				EIItems.NANO_QUANTUM_LEGGINGS,
				EIItems.NANO_QUANTUM_BOOTS
		);
		event.register(
				(stack, color) -> color == 0 || color == 2 ? DyedItemColor.getOrDefault(stack, ((DynamicDyedItem) stack.getItem()).getDefaultDyeColor()) : -1,
				EIItems.NANO_QUANTUM_SABER
		);
	}
	
	@SubscribeEvent
	private static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event)
	{
		event.register(MachineChainerUnbakedModel.LOADER_ID, MachineChainerUnbakedModel.LOADER);
		event.register(TeslaUnbakedModel.LOADER_ID, TeslaUnbakedModel.LOADER);
		event.register(TeslaParticleGeneratorModel.LOADER_ID, TeslaParticleGeneratorModel.LOADER);
	}
	
	@SubscribeEvent
	private static void registerBlockEntityRenderers(FMLClientSetupEvent event)
	{
		for(var blockDef : EIBlocks.Registry.BLOCKS.getEntries())
		{
			if(blockDef.get() instanceof MachineBlock machine)
			{
				try
				{
					var blockEntity = machine.getBlockEntityInstance();
					var type = blockEntity.getType();
					
					BlockEntityRendererProvider provider = switch (blockEntity)
					{
						case ElectricBeaconMachineBlockEntity __ -> ElectricBeaconBlockEntityRenderer::new;
						case MachineChainerMachineBlockEntity __ -> MachineChainerHighlightRenderer::new;
						case TeslaBehavior __ -> switch (blockEntity)
						{
							case MultiblockMachineBlockEntity ___ -> TeslaPartMultiblockRenderer::new;
							default -> TeslaPartSingleBlockRenderer::new;
						};
						case LargeTankMultiblockBlockEntity __ -> MultiblockTankBER::new;
						case MultiblockMachineBlockEntity __ -> MultiblockMachineBER::new;
						default -> MachineBlockEntityRenderer::new;
					};
					BlockEntityRenderers.register(type, provider);
				}
				catch (Exception ex)
				{
					throw new RuntimeException("Failed to register BER for %s".formatted(blockDef.getId()), ex);
				}
			}
		}
	}
	
	@SubscribeEvent
	private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerEntityRenderer(EIEntities.NANO_SABER_SWEEP.get(), NanoSaberSweepEntityRenderer::new);
	}
	
	@SubscribeEvent
	private static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event)
	{
		event.register(SteamChainsawItem.SteamChainsawTooltipData.class, SteamChainsawTooltipComponent::new);
	}
	
	@SubscribeEvent
	private static void registerGuiLayers(RegisterGuiLayersEvent event)
	{
		event.registerAbove(VanillaGuiLayers.SELECTED_ITEM_NAME, EI.id("nano_gravichestplate_activation_status"), NanoGravichestplateHudRenderer::render);
	}
	
	@SubscribeEvent
	private static void registerAdditionalModels(ModelEvent.RegisterAdditional event)
	{
		event.register(ModelResourceLocation.standalone(EI.id("tesla/lethal_tesla_coil")));
		event.register(ModelResourceLocation.standalone(EI.id("tesla/tesla_coil")));
		event.register(ModelResourceLocation.standalone(EI.id("tesla/tesla_particle_generator/small")));
		event.register(ModelResourceLocation.standalone(EI.id("tesla/tesla_particle_generator/medium")));
		event.register(ModelResourceLocation.standalone(EI.id("tesla/tesla_particle_generator/large")));
		event.register(ModelResourceLocation.standalone(EI.id("tesla/tesla_particle_generator/extreme")));
		event.register(ModelResourceLocation.standalone(EI.id("tesla/tesla_particle_generator/immense")));
		event.register(ModelResourceLocation.standalone(EI.id("tesla/tesla_hatch")));
		event.register(ModelResourceLocation.standalone(EI.id("tesla/tesla_receiver")));
		event.register(ModelResourceLocation.standalone(EI.id("tesla/tesla_tower")));
		event.register(ModelResourceLocation.standalone(EI.id("entity/nano_saber_sweep")));
	}
	
	@SubscribeEvent
	private static void registerAtlases(RegisterMaterialAtlasesEvent event)
	{
		event.register(EIClientSheets.NANO_SABER_SWEEP, EI.id("nano_saber_sweep"));
	}
}
