package net.swedz.extended_industrialization.machines.blockentity;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiver;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiverComponent;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGui;

import static net.swedz.extended_industrialization.EITooltips.*;
import static net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine.*;
import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MIParser.*;

public final class TeslaReceiverMachineBlockEntity extends MachineBlockEntity implements TeslaReceiver.Delegate, Tickable
{
	private final IsActiveComponent isActive;
	
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
		
		isActive = new IsActiveComponent();
		
		redstoneControl = new RedstoneControlComponent();
		casing = new CasingComponent()
		{
			@Override
			protected void setCasingStack(ItemStack stack)
			{
				super.setCasingStack(stack);
				
				if(level != null && !level.isClientSide())
				{
					receiver.addToNetwork();
				}
			}
		};
		
		receiver = new TeslaReceiverComponent(this, () -> redstoneControl.doAllowNormalOperation(this), casing::getCableTier);
		
		this.registerComponents(isActive, redstoneControl, casing, receiver);
		
		this.registerGuiComponent(new ModularMultiblockGui.Server(0, 60, (content) ->
		{
			if(this.hasNetwork())
			{
				TeslaNetwork network = this.getNetwork();
				
				content.add(EIText.TESLA_RECEIVER_LINKED.arg(this.getNetworkKey(), TESLA_NETWORK_KEY_PARSER), WHITE, true);
				
				if(network.isTransmitterLoaded())
				{
					ReceiveCheckResult result = this.checkReceiveFrom(network);
					if(result.isFailure())
					{
						if(result == ReceiveCheckResult.MISMATCHING_VOLTAGE)
						{
							content.add(EIText.TESLA_RECEIVER_MISMATCHING_VOLTAGE.arg(network.getCableTier(), CABLE_TIER_SHORT), RED);
						}
						else if(result == ReceiveCheckResult.TOO_FAR)
						{
							content.add(EIText.TESLA_RECEIVER_TOO_FAR, RED);
						}
					}
				}
				else
				{
					content.add(EIText.TESLA_RECEIVER_UNLOADED_TRANSMITTER, RED);
				}
			}
			else
			{
				content.add(EIText.TESLA_RECEIVER_NO_LINK, RED);
			}
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
		data.isActive = isActive.isActive;
		orientation.writeModelData(data);
		return data;
	}
	
	@Override
	public TeslaReceiver getDelegateReceiver()
	{
		return receiver;
	}
	
	@Override
	public void setLevel(Level level)
	{
		super.setLevel(level);
		
		if(level.isClientSide())
		{
			return;
		}
		
		receiver.addToNetwork();
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
	public void tick()
	{
		if(level.isClientSide())
		{
			return;
		}
		
		if(this.hasNetwork() && this.getNetwork().isTransmitterLoaded())
		{
			TeslaNetwork network = this.getNetwork();
			isActive.updateActive(network.isTransmitterLoaded() && this.checkReceiveFrom(network).isSuccess(), this);
		}
		else
		{
			isActive.updateActive(false, this);
		}
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
