package net.swedz.extended_industrialization.machines.guicomponent.teslanetwork;

import aztech.modern_industrialization.client.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.client.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.client.machines.gui.MachineScreen;
import aztech.modern_industrialization.client.util.RenderHelper;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;
import net.swedz.tesseract.neoforge.helper.ComponentHelper;

import java.util.List;
import java.util.Optional;

public final class TeslaNetworkBarClient extends GuiComponentClient<TeslaNetworkBar.Params, Optional<TeslaNetworkBar.Data>>
{
	public TeslaNetworkBarClient(TeslaNetworkBar.Params params, Optional<TeslaNetworkBar.Data> data)
	{
		super(params, data);
	}
	
	@Override
	public ClientComponentRenderer createRenderer(MachineScreen machineScreen)
	{
		return new ClientComponentRenderer()
		{
			private static final ResourceLocation TESLA_NETWORK_BAR = EI.id("textures/gui/container/tesla_network_bar.png");
			
			private final int WIDTH = 18, HEIGHT = 18;
			
			@Override
			public void renderBackground(GuiGraphics graphics, int x, int y)
			{
				int iconIndex = data.map(TeslaNetworkBar.Data::iconIndex).orElse(0);
				graphics.blit(
						TESLA_NETWORK_BAR,
						x + params.renderX(), y + params.renderY(),
						iconIndex * 18, 0, WIDTH, HEIGHT, 18 * 7, 18
				);
			}
			
			@Override
			public boolean renderTooltip(MachineScreen screen, Font font, GuiGraphics graphics, int x, int y, int mouseX, int mouseY)
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
						graphics.renderTooltip(font, lines.stream().map((c) -> ComponentHelper.stripStyle(c.getVisualOrderText())).toList(), mouseX, mouseY);
						return true;
					}
				}
				return false;
			}
		};
	}
}
