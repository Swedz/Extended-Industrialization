package net.swedz.extended_industrialization.machines.guicomponents;

import aztech.modern_industrialization.machines.components.ActiveShapeComponent;
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import net.minecraft.network.chat.Component;

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
}
