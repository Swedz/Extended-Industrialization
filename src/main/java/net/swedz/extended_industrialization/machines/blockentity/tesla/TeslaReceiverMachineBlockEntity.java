package net.swedz.extended_industrialization.machines.blockentity.tesla;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.helper.EnergyHelper;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaPlasmaBehavior;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaPlasmaBehaviorHolder;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiver;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiverComponent;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiverState;
import net.swedz.extended_industrialization.machines.guicomponent.teslanetwork.TeslaNetworkBar;
import net.swedz.tesseract.neoforge.capabilities.CapabilitiesListeners;

import java.util.List;
import java.util.Optional;

import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.*;

public final class TeslaReceiverMachineBlockEntity extends MachineBlockEntity implements TeslaReceiver.Delegate, Tickable, TeslaPlasmaBehaviorHolder
{
	private final IsActiveComponent isActive;
	
	private final RedstoneControlComponent redstoneControl;
	private final CasingComponent          casing;
	
	private final EnergyComponent energy;
	private final MIEnergyStorage insertable;
	private final MIEnergyStorage extractable;
	
	private final TeslaReceiverComponent receiver;
	
	public TeslaReceiverMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("tesla_receiver"), false).build(),
				new OrientationComponent.Params(true, false, false)
		);
		
		isActive = new IsActiveComponent();
		
		redstoneControl = new RedstoneControlComponent();
		casing = new CasingComponent(this::onCasingUpdate);
		
		energy = new EnergyComponent(this, () -> 30 * 20 * casing.getCableTier().eu);
		insertable = energy.buildInsertable(casing::canInsertEu);
		extractable = energy.buildExtractable(casing::canInsertEu);
		
		receiver = new TeslaReceiverComponent(
				this,
				insertable,
				() -> redstoneControl.doAllowNormalOperation(this),
				casing::getCableTier
		);
		
		this.registerComponents(isActive, redstoneControl, casing, energy, receiver);
		
		this.registerGuiComponent(new EnergyBar.Server(new EnergyBar.Parameters(61, 34), energy::getEu, energy::getCapacity));
		
		this.registerGuiComponent(new TeslaNetworkBar.Server(
				new TeslaNetworkBar.Parameters(101, 34),
				() ->
				{
					if(this.hasNetwork())
					{
						TeslaNetwork network = this.getNetwork();
						if(network.isTransmitterLoaded())
						{
							TeslaReceiverState state = this.checkReceiveFrom(network);
							return Optional.of(new TeslaNetworkBar.ReceiverData(state, Optional.of(this.getNetworkKey()), Optional.of(network.getCableTier())));
						}
						else
						{
							return Optional.of(new TeslaNetworkBar.ReceiverData(TeslaReceiverState.UNLOADED_TRANSMITTER, Optional.of(this.getNetworkKey()), Optional.empty()));
						}
					}
					else
					{
						return Optional.of(new TeslaNetworkBar.ReceiverData(TeslaReceiverState.NO_LINK, Optional.empty(), Optional.empty()));
					}
				}
		));
		
		this.registerGuiComponent(new SlotPanel.Server(this)
				.withRedstoneControl(redstoneControl)
				.withCasing(casing));
	}
	
	private void onCasingUpdate(CableTier from, CableTier to)
	{
		if(level != null && !level.isClientSide())
		{
			receiver.addToNetwork();
		}
	}
	
	@Override
	public TeslaPlasmaBehavior getTeslaPlasmaBehavior()
	{
		return new TeslaPlasmaBehavior()
		{
			@Override
			public boolean shouldRender()
			{
				return isActive.isActive;
			}
			
			@Override
			public Vec3 getOffset()
			{
				return new Vec3(-0.05f / 2f, -0.05f / 2f, -0.05f / 2f);
			}
			
			@Override
			public ResourceLocation getModelLocation()
			{
				return EI.id("tesla_plasma/tesla_coil");
			}
			
			@Override
			public float getModelScale()
			{
				return 1.05f;
			}
			
			@Override
			public float getSpeed()
			{
				return 100f;
			}
			
			@Override
			public float getTextureScale()
			{
				return 32f;
			}
		};
	}
	
	@Override
	public TeslaReceiver getDelegateReceiver()
	{
		return receiver;
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
	public MIInventory getInventory()
	{
		return MIInventory.EMPTY;
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
		
		if(redstoneControl.doAllowNormalOperation(this))
		{
			EnergyHelper.autoOutput(this, orientation, casing.getCableTier(), extractable);
		}
	}
	
	@Override
	protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face)
	{
		ItemInteractionResult result = super.useItemOn(player, hand, face);
		if(!result.consumesAction())
		{
			result = redstoneControl.onUse(this, player, hand);
		}
		if(!result.consumesAction())
		{
			result = casing.onUse(this, player, hand);
		}
		return result;
	}
	
	@Override
	public List<Component> getTooltips()
	{
		return List.of(
				line(EIText.TESLA_RECEIVER_HELP_1),
				line(EIText.TESLA_RECEIVER_HELP_2)
		);
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		CapabilitiesListeners.register(EI.ID, (event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) ->
				{
					TeslaReceiverMachineBlockEntity machine = (TeslaReceiverMachineBlockEntity) be;
					return machine.orientation.outputDirection == direction ? machine.extractable : null;
				}));
	}
}
