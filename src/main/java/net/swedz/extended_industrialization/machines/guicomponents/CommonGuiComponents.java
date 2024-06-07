package net.swedz.extended_industrialization.machines.guicomponents;

import aztech.modern_industrialization.machines.components.ActiveShapeComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import net.minecraft.network.chat.Component;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock.ModularMultiblockGui;
import net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock.ModularMultiblockGuiLine;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public final class CommonGuiComponents
{
	public static ShapeSelection.Server rangedShapeSelection(MultiblockMachineBlockEntity machine, ActiveShapeComponent activeShape, List<? extends Component> translations, boolean useArrows)
	{
		return new ShapeSelection.Server(
				new ShapeSelection.Behavior()
				{
					@Override
					public void handleClick(int line, int delta)
					{
						activeShape.incrementShape(machine, delta);
					}
					
					@Override
					public int getCurrentIndex(int line)
					{
						return activeShape.getActiveShapeIndex();
					}
				},
				new ShapeSelection.LineInfo(translations.size(), translations, useArrows)
		);
	}
	
	public static ModularMultiblockGui.Server standardMultiblockScreen(MultiblockMachineBlockEntity machine, IsActiveComponent isActive, int height)
	{
		return new ModularMultiblockGui.Server(height, () ->
		{
			List<ModularMultiblockGuiLine> text = Lists.newArrayList();
			
			boolean shapeValid = machine.isShapeValid();
			boolean active = isActive.isActive;
			
			text.add(shapeValid ? new ModularMultiblockGuiLine(EIText.MULTIBLOCK_SHAPE_VALID.text()) : new ModularMultiblockGuiLine(EIText.MULTIBLOCK_SHAPE_INVALID.text(), 0xFF0000));
			if(shapeValid)
			{
				text.add(active ? new ModularMultiblockGuiLine(EIText.MULTIBLOCK_STATUS_ACTIVE.text()) : new ModularMultiblockGuiLine(EIText.MULTIBLOCK_STATUS_INACTIVE.text(), 0xFF0000));
			}
			
			return text;
		});
	}
	
	public static ModularMultiblockGui.Server standardMultiblockScreen(MultiblockMachineBlockEntity machine, IsActiveComponent isActive)
	{
		return standardMultiblockScreen(machine, isActive, ModularMultiblockGui.H);
	}
}
