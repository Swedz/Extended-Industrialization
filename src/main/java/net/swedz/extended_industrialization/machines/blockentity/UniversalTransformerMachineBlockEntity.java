package net.swedz.extended_industrialization.machines.blockentity;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyComponentHolder;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.TransformerTierComponent;
import net.swedz.extended_industrialization.machines.guicomponent.universaltransformer.UniversalTransformerSlots;

public final class UniversalTransformerMachineBlockEntity extends MachineBlockEntity implements EnergyComponentHolder
{
	private final RedstoneControlComponent redstoneControl;
	
	private final EnergyComponent energy;
	private final MIEnergyStorage insertable;
	private final MIEnergyStorage extractable;
	
	private final TransformerTierComponent transformerFrom;
	private final TransformerTierComponent transformerTo;
	
	public UniversalTransformerMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("universal_transformer"), false).build(),
				new OrientationComponent.Params(true, false, false)
		);
		
		transformerFrom = new TransformerTierComponent(true);
		transformerTo = new TransformerTierComponent(false);
		
		redstoneControl = new RedstoneControlComponent();
		
		energy = new EnergyComponent(this, () -> 200 * Math.min(transformerFrom.getTier().getEu(), transformerTo.getTier().getEu()));
		insertable = energy.buildInsertable(transformerFrom::canInsertEu);
		extractable = energy.buildExtractable((tier) -> transformerTo.canInsertEu(tier) && redstoneControl.doAllowNormalOperation(this));
		
		this.registerComponents(transformerFrom, transformerTo, energy, redstoneControl);
		
		EnergyBar.Parameters energyBarParams = new EnergyBar.Parameters(76, 39);
		this.registerGuiComponent(new EnergyBar.Server(energyBarParams, energy::getEu, energy::getCapacity));
		
		this.registerGuiComponent(new SlotPanel.Server(this)
				.withRedstoneControl(redstoneControl));
		
		this.registerGuiComponent(new UniversalTransformerSlots.Server(this, transformerFrom, transformerTo));
	}
	
	@Override
	public EnergyAccess getEnergyComponent()
	{
		return energy;
	}
	
	@Override
	public MIInventory getInventory()
	{
		return MIInventory.EMPTY;
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData();
		orientation.writeModelData(data);
		return data;
	}
	
	private boolean isExtractableOnOutputDirection()
	{
		return transformerFrom.getTier().getEu() < transformerTo.getTier().getEu();
	}
	
	@Override
	protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face)
	{
		var energyItem = player.getItemInHand(hand).getCapability(EnergyApi.ITEM);
		int stackSize = player.getItemInHand(hand).getCount();
		if(energyItem != null)
		{
			if(!player.level().isClientSide())
			{
				boolean insertedSomething = false;
				
				for(int i = 0; i < 10000; ++i)
				{
					try (Transaction transaction = Transaction.openOuter())
					{
						long inserted = energyItem.receive(energy.getEu() / stackSize, false);
						
						if(inserted == 0)
						{
							break;
						}
						else
						{
							insertedSomething = true;
						}
						
						energy.consumeEu(inserted * stackSize, Simulation.ACT);
						transaction.commit();
					}
				}
				
				if(!insertedSomething)
				{
					for(int i = 0; i < 10000; ++i)
					{
						try (Transaction transaction = Transaction.openOuter())
						{
							long extracted = energyItem.extract(energy.getRemainingCapacity() / stackSize, false);
							
							if(extracted == 0)
							{
								break;
							}
							
							energy.insertEu(extracted * stackSize, Simulation.ACT);
							transaction.commit();
						}
					}
				}
			}
			return ItemInteractionResult.sidedSuccess(player.level().isClientSide());
		}
		return super.useItemOn(player, hand, face);
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) ->
				{
					UniversalTransformerMachineBlockEntity machine = (UniversalTransformerMachineBlockEntity) be;
					if(machine.isExtractableOnOutputDirection())
					{
						return machine.orientation.outputDirection == direction ? machine.extractable : machine.insertable;
					}
					else
					{
						return machine.orientation.outputDirection == direction ? machine.insertable : machine.extractable;
					}
				}));
	}
}
