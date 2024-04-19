package net.swedz.miextended.machines.guicomponents;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.machines.components.ActiveShapeComponent;
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;

import java.util.stream.IntStream;

public final class CommonGuiComponents
{
	public static ShapeSelection.Server rangedShapeSelection(MultiblockMachineBlockEntity machine, ActiveShapeComponent activeShape, int maxHeight)
	{
		return new ShapeSelection.Server(
				new ShapeSelection.Behavior()
				{
					@Override
					public void handleClick(int clickedLine, int delta)
					{
						activeShape.incrementShape(machine, delta);
					}
					
					@Override
					public int getCurrentIndex(int line)
					{
						return activeShape.getActiveShapeIndex();
					}
				},
				new ShapeSelection.LineInfo(
						maxHeight,
						IntStream.range(1, maxHeight + 1).mapToObj(MIText.ShapeTextHeight::text).toList(),
						false
				)
		);
	}
}
