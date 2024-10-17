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
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.api.WorldPos;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiverState;
import net.swedz.tesseract.neoforge.helper.ComponentHelper;

import java.util.List;
import java.util.Optional;

import static aztech.modern_industrialization.MITooltips.*;
import static net.swedz.extended_industrialization.EITooltips.*;
import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MIParser.*;

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
			boolean transmitter = buf.readBoolean();
			if(transmitter)
			{
				int receivers = buf.readVarInt();
				long energyTransmitting = buf.readVarLong();
				CableTier cableTier = CableTier.getTier(buf.readUtf());
				long energyDrain = buf.readVarLong();
				long energyConsuming = buf.readVarLong();
				data = Optional.of(new TeslaNetworkBar.TransmitterData(receivers, energyTransmitting, cableTier, energyDrain, energyConsuming));
			}
			else
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
						iconIndex * 18, 0, WIDTH, HEIGHT, 18 * 6, 18
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
							lines.add(EIText.TESLA_NETWORK_TRANSMITTER_RECEIVERS.arg(transmitter.receivers()));
							lines.add(EIText.TESLA_NETWORK_TRANSMITTER_TRANSMITTING.arg(transmitter.energyTransmitting(), EU_PER_TICK_PARSER).arg(transmitter.cableTier(), CABLE_TIER_SHORT));
							lines.add(EIText.TESLA_NETWORK_TRANSMITTER_DRAIN.arg(transmitter.energyDrain(), EU_PER_TICK_PARSER));
							lines.add(EIText.TESLA_NETWORK_TRANSMITTER_CONSUMING.arg(transmitter.energyConsuming(), EU_PER_TICK_PARSER));
						}
						else if(data.get() instanceof TeslaNetworkBar.ReceiverData receiver)
						{
							if(receiver.linked().isPresent())
							{
								lines.add(EIText.TESLA_NETWORK_RECEIVER_LINKED.arg(receiver.linked().get(), TESLA_NETWORK_KEY_PARSER));
							}
							if(receiver.state().isFailure())
							{
								switch (receiver.state())
								{
									case NO_LINK -> lines.add(EIText.TESLA_NETWORK_RECEIVER_NO_LINK.text());
									case UNLOADED_TRANSMITTER -> lines.add(EIText.TESLA_NETWORK_RECEIVER_UNLOADED.text());
									case MISMATCHING_VOLTAGE -> lines.add(EIText.TESLA_NETWORK_RECEIVER_MISMATCHING_VOLTAGE.arg(receiver.networkCableTier().orElseThrow(), CABLE_TIER_SHORT));
									case TOO_FAR -> lines.add(EIText.TESLA_NETWORK_RECEIVER_TOO_FAR.text());
								}
							}
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
