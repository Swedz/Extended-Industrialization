package net.swedz.extended_industrialization.machines.blockentity;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.CableTierHolder;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.AutoExtract;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerComponent;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGui;
import net.swedz.tesseract.neoforge.compat.mi.helper.transfer.MIEnergyTransferCache;
import net.swedz.tesseract.neoforge.helper.transfer.FluidTransferCache;
import net.swedz.tesseract.neoforge.helper.transfer.ItemTransferCache;

import java.util.List;

import static net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine.*;

public final class MachineChainerMachineBlockEntity extends MachineBlockEntity implements Tickable, CableTierHolder
{
	private final RedstoneControlComponent redstoneControl;
	private final CasingComponent          casing;
	
	private final ChainerComponent chainer;
	
	private final ItemTransferCache     transferItem;
	private final FluidTransferCache    transferFluid;
	private final MIEnergyTransferCache transferEnergy;
	
	private int tick;
	private int lastRebuildTick  = -1;
	private int rebuildsThisTick = 0;
	
	private boolean needsRebuild;
	
	public MachineChainerMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("machine_chainer"), false).backgroundHeight(175).build(),
				new OrientationComponent.Params(true, true, true, true)
		);
		
		redstoneControl = new RedstoneControlComponent();
		
		chainer = new ChainerComponent(
				this,
				EI.config().machineChainerMaxConnections(),
				() -> redstoneControl.doAllowNormalOperation(this)
		);
		
		casing = new CasingComponent((from, to) -> chainer.invalidate());
		
		transferItem = new ItemTransferCache(chainer::itemHandler);
		transferFluid = new FluidTransferCache(chainer::fluidHandler);
		transferEnergy = new MIEnergyTransferCache(chainer::extractableEnergyHandler);
		
		this.registerGuiComponent(new SlotPanel(this)
				.withRedstoneControl(redstoneControl)
				.withCasing(casing));
		
		this.registerGuiComponent(new AutoExtract(orientation));
		
		this.registerGuiComponent(new ModularMultiblockGui(
				11,
				50,
				(content) ->
				{
					ChainerLinks links = chainer.links();
					
					if(!links.hasConnections() && links.failPosition().isPresent())
					{
						content.add(EI.text().machineChainerProblemAt(links.failPosition().get()), RED);
					}
					else
					{
						content.add(EI.text().machineChainerConnectedMachines(links.count(), links.maxConnections()));
					}
				},
				List::of
		));
		
		this.registerComponents(chainer, redstoneControl, casing);
	}
	
	public ChainerComponent getChainerComponent()
	{
		return chainer;
	}
	
	public void buildLinks()
	{
		rebuildsThisTick++;
		
		if(tick == lastRebuildTick)
		{
			needsRebuild = true;
			return;
		}
		
		needsRebuild = false;
		
		if(!level.isClientSide())
		{
			chainer.unregisterListeners();
			chainer.invalidate();
			chainer.registerListeners();
		}
		
		this.invalidateCapabilities();
		
		this.setChanged();
		if(!level.isClientSide())
		{
			this.sync();
		}
		
		lastRebuildTick = tick;
	}
	
	@Override
	public CableTier getCableTier()
	{
		return casing.getCableTier();
	}
	
	public long getMaxTransfer()
	{
		int multiplier = EI.config().machineChainerMaxTransferMultiplier();
		return multiplier == 0 ? Long.MAX_VALUE : (this.getCableTier().getMaxTransfer() * multiplier);
	}
	
	@Override
	public MIInventory getInventory()
	{
		return MIInventory.EMPTY;
	}
	
	@Override
	public MachineModelClientData getMachineModelData()
	{
		var data = new MachineModelClientData(casing.getCasing());
		orientation.writeModelData(data);
		return data;
	}
	
	@Override
	public void setLevel(Level level)
	{
		super.setLevel(level);
		
		if(!level.isClientSide())
		{
			needsRebuild = true;
		}
	}
	
	@Override
	public void setRemoved()
	{
		super.setRemoved();
		
		if(!level.isClientSide())
		{
			chainer.unregisterListeners();
			chainer.clear();
		}
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			return;
		}
		
		if(++tick % (10 * 20) == 0)
		{
			needsRebuild = true;
		}
		
		if(needsRebuild)
		{
			this.buildLinks();
		}
		
		if(redstoneControl.doAllowNormalOperation(this))
		{
			if(orientation.extractItems)
			{
				transferItem.autoExtract(level, worldPosition, orientation.outputDirection);
			}
			if(orientation.extractFluids)
			{
				transferFluid.autoExtract(level, worldPosition, orientation.outputDirection);
			}
			if(transferEnergy.autoExtract(level, worldPosition, orientation.outputDirection, this.getCableTier(), this.getMaxTransfer()))
			{
				this.setChanged();
			}
		}
		
		if(rebuildsThisTick >= 10)
		{
			EI.LOGGER.warn("Prevented Machine Chainer in dimension '{}' at ({}) from rebuilding links {} times in the same tick!", level.dimension().location(), worldPosition.toShortString(), rebuildsThisTick);
		}
		rebuildsThisTick = 0;
	}
	
	@Override
	protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face)
	{
		var result = super.useItemOn(player, hand, face);
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
	
	public static void registerCapabilities(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
		{
			event.registerBlockEntity(
					Capabilities.ItemHandler.BLOCK, bet,
					(be, direction) -> ((MachineChainerMachineBlockEntity) be).getChainerComponent().itemHandler()
			);
			event.registerBlockEntity(
					Capabilities.FluidHandler.BLOCK, bet,
					(be, direction) -> ((MachineChainerMachineBlockEntity) be).getChainerComponent().fluidHandler()
			);
			event.registerBlockEntity(
					EnergyApi.SIDED, bet,
					(be, direction) ->
					{
						var machine = (MachineChainerMachineBlockEntity) be;
						var chainer = machine.getChainerComponent();
						return machine.orientation.outputDirection == direction ? chainer.extractableEnergyHandler() : chainer.insertableEnergyHandler();
					}
			);
		});
	}
}
