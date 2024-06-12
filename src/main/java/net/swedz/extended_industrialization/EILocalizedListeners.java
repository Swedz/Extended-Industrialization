package net.swedz.extended_industrialization;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.swedz.tesseract.neoforge.event.FarmlandLoseMoistureEvent;
import net.swedz.tesseract.neoforge.event.TreeGrowthEvent;
import net.swedz.tesseract.neoforge.localizedlistener.LocalizedListeners;

public final class EILocalizedListeners extends LocalizedListeners
{
	public static final EILocalizedListeners INSTANCE = new EILocalizedListeners();
	
	@Override
	protected void initListeners()
	{
		this.withListener(
				BlockEvent.NeighborNotifyEvent.class,
				(event) -> event.getLevel() instanceof Level,
				(event) -> (Level) event.getLevel(),
				(event) -> new ChunkPos(event.getPos())
		);
		
		this.withListener(
				BlockEvent.FarmlandTrampleEvent.class,
				(event) -> event.getLevel() instanceof Level,
				(event) -> (Level) event.getLevel(),
				(event) -> new ChunkPos(event.getPos())
		);
		
		this.withListener(
				FarmlandLoseMoistureEvent.class,
				(event) -> event.getLevel() instanceof Level,
				(event) -> (Level) event.getLevel(),
				(event) -> new ChunkPos(event.getPos())
		);
		
		this.withListener(
				TreeGrowthEvent.class,
				(event) -> event.getLevel() instanceof Level,
				(event) -> (Level) event.getLevel(),
				(event) -> new ChunkPos(event.getPos())
		);
	}
}
