package net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import com.google.common.collect.Lists;
import net.minecraft.world.level.Level;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkKey;
import net.swedz.extended_industrialization.machines.component.tesla.transmitter.TeslaTransmitter;
import net.swedz.extended_industrialization.machines.component.tesla.transmitter.TeslaTransmitterComponent;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGui;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine;
import net.swedz.tesseract.neoforge.compat.mi.machine.blockentity.multiblock.BasicMultiblockMachineBlockEntity;

import java.util.List;

public final class TeslaTowerBlockEntity extends BasicMultiblockMachineBlockEntity implements EnergyListComponentHolder, TeslaTransmitter.Delegate
{
	private final RedstoneControlComponent redstoneControl;
	
	private final List<EnergyComponent> energyInputs = Lists.newArrayList();
	
	private final TeslaTransmitterComponent transmitter;
	
	public TeslaTowerBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("tesla_tower"), false).backgroundHeight(200).build(),
				SHAPES.shapeTemplates()
		);
		
		redstoneControl = new RedstoneControlComponent();
		
		transmitter = new TeslaTransmitterComponent(this, energyInputs);
		
		this.registerComponents(redstoneControl);
		
		this.registerGuiComponent(new ModularMultiblockGui.Server(0, ModularMultiblockGui.HEIGHT, () ->
		{
			List<ModularMultiblockGuiLine> text = Lists.newArrayList();
			
			text.add(this.isShapeValid() ? new ModularMultiblockGuiLine(MIText.MultiblockShapeValid.text()) : new ModularMultiblockGuiLine(MIText.MultiblockShapeInvalid.text(), 0xFF0000));
			
			if(this.isShapeValid())
			{
				if(transmitter.hasNetwork())
				{
					TeslaNetwork network = level.getServer().getTeslaNetworks().get(transmitter.getNetworkKey());
					
					text.add(new ModularMultiblockGuiLine(EIText.TESLA_TRANSMITTER_HAS_NETWORK.text()));
					
					text.add(new ModularMultiblockGuiLine(EIText.TESLA_TRANSMITTER_RECEIVERS.text(network.size())));
				}
				else
				{
					text.add(new ModularMultiblockGuiLine(EIText.TESLA_TRANSMITTER_NO_NETWORK.text(), 0xFF0000));
				}
			}
			
			return text;
		}));
		
		this.registerGuiComponent(SHAPES.createShapeSelectionGuiComponent(this, activeShape, true));
		
		this.registerGuiComponent(new SlotPanel.Server(this)
				.withRedstoneControl(redstoneControl));
	}
	
	@Override
	public List<? extends EnergyAccess> getEnergyComponents()
	{
		return energyInputs;
	}
	
	@Override
	public void onSuccessfulMatch(ShapeMatcher shapeMatcher)
	{
		energyInputs.clear();
		for(HatchBlockEntity hatch : shapeMatcher.getMatchedHatches())
		{
			hatch.appendEnergyInputs(energyInputs);
		}
	}
	
	@Override
	public TeslaTransmitter getDelegateTransmitter()
	{
		return transmitter;
	}
	
	@Override
	public void setLevel(Level level)
	{
		super.setLevel(level);
		
		transmitter.setNetwork(new TeslaNetworkKey(level, worldPosition));
	}
	
	@Override
	protected ShapeMatcher createShapeMatcher()
	{
		return new SameCableTierShapeMatcher(level, worldPosition, orientation.facingDirection, this.getActiveShape());
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if(level.isClientSide())
		{
			return;
		}
		
		if(redstoneControl.doAllowNormalOperation(this))
		{
			// TODO limit transmit rate
			transmitter.transmitEnergy(Long.MAX_VALUE);
		}
	}
	
	private static final TeslaTowerShapes SHAPES = new TeslaTowerShapes();
	
	public static void registerTieredShapes()
	{
		SHAPES.register();
	}
}
