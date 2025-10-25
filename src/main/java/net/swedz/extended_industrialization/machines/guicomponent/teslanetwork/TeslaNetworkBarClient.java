package net.swedz.extended_industrialization.machines.guicomponent.teslanetwork;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.machines.gui.MachineScreen;
import aztech.modern_industrialization.util.RenderHelper;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.tesla.network.receiver.TeslaReceiverState;
import net.swedz.tesseract.neoforge.api.WorldPos;
import net.swedz.tesseract.neoforge.helper.ComponentHelper;

import java.util.List;
import java.util.Optional;

public final class TeslaNetworkBarClient implements GuiComponentClient
{
	private final TeslaNetworkBar.Parameters params;
	
	private Optional<TeslaNetworkBar.Data> data = Optional.empty();
	
	public TeslaNetworkBarClient(RegistryFriendlyByteBuf buf)
	{
		this.params = new TeslaNetworkBar.Parameters(buf.readVarInt(), buf.readVarInt());
		this.readCurrentData(buf);
	}
	
	@Override
	public void readCurrentData(RegistryFriendlyByteBuf buf)
	{
		if(buf.readBoolean())
		{
			int type = buf.readVarInt();
			if(type == 0)
			{
				int receivers = buf.readVarInt();
				long energyTransmitting = buf.readVarLong();
				CableTier cableTier = CableTier.getTier(buf.readUtf());
				long energyDrain = buf.readVarLong();
				long energyConsuming = buf.readVarLong();
				data = Optional.of(new TeslaNetworkBar.TransmitterData(receivers, energyTransmitting, cableTier, energyDrain, energyConsuming));
			}
			else if(type == 1)
			{
				TeslaReceiverState state = buf.readEnum(TeslaReceiverState.class);
				Optional<WorldPos> linked = buf.readOptional(WorldPos.STREAM_CODEC);
				Optional<CableTier> networkCableTier = Optional.empty();
				if(linked.isPresent() && buf.readBoolean())
				{
					networkCableTier = Optional.of(CableTier.getTier(buf.readUtf()));
				}
				data = Optional.of(new TeslaNetworkBar.ReceiverData(state, linked, networkCableTier));
			}
			else if(type == 2)
			{
				int note = buf.readVarInt();
				long energyConsuming = buf.readVarLong();
				data = Optional.of(new TeslaNetworkBar.SingingData(note, energyConsuming));
			}
		}
		else
		{
			data = Optional.empty();
		}
	}
	
	@Override
	public ClientComponentRenderer createRenderer(MachineScreen machineScreen)
	{
		return new ClientComponentRenderer()
		{
			private static final ResourceLocation TESLA_NETWORK_BAR = EI.id("textures/gui/container/tesla_network_bar.png");
			
			private final int WIDTH = 18, HEIGHT = 18;
			
			@Override
			public void renderBackground(GuiGraphics guiGraphics, int x, int y)
			{
				int iconIndex = data.map(TeslaNetworkBar.Data::iconIndex).orElse(0);
				guiGraphics.blit(
						TESLA_NETWORK_BAR,
						x + params.renderX(), y + params.renderY(),
						iconIndex * 18, 0, WIDTH, HEIGHT, 18 * 7, 18
				);
			}
			
			@Override
			public void renderTooltip(MachineScreen screen, Font font, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY)
			{
				if(RenderHelper.isPointWithinRectangle(params.renderX(), params.renderY(), WIDTH, HEIGHT, mouseX - x, mouseY - y))
				{
					List<Component> lines = Lists.newArrayList();
					if(data.isPresent())
					{
						if(data.get() instanceof TeslaNetworkBar.TransmitterData transmitter)
						{
							lines.add(EI.text().teslaNetworkTransmitterReceivers(transmitter.receivers()));
							lines.add(EI.text().teslaNetworkTransmitterTransmitting(transmitter.energyTransmitting(), transmitter.cableTier()));
							lines.add(EI.text().teslaNetworkTransmitterDrain(transmitter.energyDrain()));
							lines.add(EI.text().teslaNetworkTransmitterConsuming(transmitter.energyConsuming()));
						}
						else if(data.get() instanceof TeslaNetworkBar.ReceiverData receiver)
						{
							if(receiver.linked().isPresent())
							{
								lines.add(EI.text().teslaNetworkReceiverLinked(receiver.linked().get()));
							}
							if(receiver.state().isFailure())
							{
								switch (receiver.state())
								{
									case NO_LINK -> EI.text().teslaNetworkReceiverNoLink();
									case UNLOADED_TRANSMITTER -> EI.text().teslaNetworkReceiverUnloaded();
									case MISMATCHING_VOLTAGE ->
											EI.text().teslaNetworkReceiverMismatchingVoltage(receiver.networkCableTier().orElseThrow());
									case TOO_FAR -> EI.text().teslaNetworkReceiverTooFar();
								}
							}
						}
						else if(data.get() instanceof TeslaNetworkBar.SingingData singing)
						{
							lines.add(EI.text().teslaNetworkSingingNote(singing.getReadableNote()));
							lines.add(EI.text().teslaNetworkTransmitterConsuming(singing.energyConsuming()));
						}
					}
					if(!lines.isEmpty())
					{
						guiGraphics.renderTooltip(font, lines.stream().map((c) -> ComponentHelper.stripStyle(c.getVisualOrderText())).toList(), mouseX, mouseY);
					}
				}
			}
		};
	}
}
