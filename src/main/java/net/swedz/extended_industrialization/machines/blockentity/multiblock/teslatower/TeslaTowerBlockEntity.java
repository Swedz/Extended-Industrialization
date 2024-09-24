package net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower;

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
import net.swedz.extended_industrialization.EI;
import net.swedz.tesseract.neoforge.compat.mi.helper.CommonGuiComponents;
import net.swedz.tesseract.neoforge.compat.mi.machine.blockentity.multiblock.BasicMultiblockMachineBlockEntity;

import java.util.List;

public final class TeslaTowerBlockEntity extends BasicMultiblockMachineBlockEntity implements EnergyListComponentHolder
{
	private final RedstoneControlComponent redstoneControl;
	
	private final List<EnergyComponent> energyInputs = Lists.newArrayList();
	
	public TeslaTowerBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("tesla_tower"), false).backgroundHeight(200).build(),
				SHAPES.shapeTemplates()
		);
		
		redstoneControl = new RedstoneControlComponent();
		
		this.registerComponents(redstoneControl);
		
		// TODO display more data on this screen
		this.registerGuiComponent(CommonGuiComponents.standardMultiblockScreen(this, isActive));
		
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
	
	// TODO custom linking checks for cable tier of hatches, they must all match
	
	@Override
	public void tick()
	{
		super.tick();
		
		// TODO
	}
	
	private static final TeslaTowerShapes SHAPES = new TeslaTowerShapes();
	
	public static void registerTieredShapes()
	{
		SHAPES.register();
	}
}
