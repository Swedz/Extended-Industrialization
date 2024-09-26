package net.swedz.extended_industrialization.machines.blockentity;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import com.google.common.collect.Lists;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiver;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiverComponent;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGui;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine;

import java.util.List;

import static net.swedz.extended_industrialization.EITooltips.*;

public final class TeslaReceiverMachineBlockEntity extends MachineBlockEntity implements TeslaReceiver.Delegate
{
	private final RedstoneControlComponent redstoneControl;
	private final CasingComponent          casing;
	
	private final TeslaReceiverComponent receiver;
	
	public TeslaReceiverMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("tesla_receiver"), false).backgroundHeight(175).build(),
				new OrientationComponent.Params(true, false, false)
		);
		
		redstoneControl = new RedstoneControlComponent();
		casing = new CasingComponent()
		{
			@Override
			protected void setCasingStack(ItemStack stack)
			{
				super.setCasingStack(stack);
				
				// TODO this check doesnt work properly
				if(level != null && !level.isClientSide())
				{
					receiver.addToNetwork();
				}
			}
		};
		
		receiver = new TeslaReceiverComponent(this, () -> redstoneControl.doAllowNormalOperation(this), casing);
		
		this.registerComponents(redstoneControl, casing, receiver);
		
		this.registerGuiComponent(new ModularMultiblockGui.Server(0, 60, () ->
		{
			List<ModularMultiblockGuiLine> text = Lists.newArrayList();
			
			if(this.hasNetwork())
			{
				text.add(new ModularMultiblockGuiLine(EIText.TESLA_RECEIVER_LINKED.text(TESLA_NETWORK_KEY_PARSER.parse(this.getNetworkKey()).copy().setStyle(Style.EMPTY))));
				
				// TODO display EIText.TESLA_RECEIVER_MISMATCHING_VOLTAGE if the voltage of this receiver is not the same voltage as the network its linked to
			}
			else
			{
				text.add(new ModularMultiblockGuiLine(EIText.TESLA_RECEIVER_NO_LINK.text(), 0xFF0000));
			}
			
			return text;
		}));
		
		this.registerGuiComponent(new SlotPanel.Server(this)
				.withRedstoneControl(redstoneControl)
				.withCasing(casing));
	}
	
	@Override
	public MIInventory getInventory()
	{
		return MIInventory.EMPTY;
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData(casing.getCasing());
		// data.isActive = ...; // TODO check if the source is loaded
		orientation.writeModelData(data);
		return data;
	}
	
	@Override
	public TeslaReceiver getDelegateReceiver()
	{
		return receiver;
	}
	
	@Override
	public void setRemoved()
	{
		super.setRemoved();
		
		if(level.isClientSide())
		{
			return;
		}
		
		receiver.removeFromNetwork();
	}
	
	@Override
	public void clearRemoved()
	{
		super.clearRemoved();
		
		if(level.isClientSide())
		{
			return;
		}
		
		receiver.addToNetwork();
	}
	
	@Override
	public void onChunkUnloaded()
	{
		if(level.isClientSide())
		{
			return;
		}
		
		receiver.removeFromNetwork();
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) ->
				{
					TeslaReceiverMachineBlockEntity machine = (TeslaReceiverMachineBlockEntity) be;
					return machine.orientation.outputDirection == direction ? null : machine.receiver.insertable();
				}));
	}
}
