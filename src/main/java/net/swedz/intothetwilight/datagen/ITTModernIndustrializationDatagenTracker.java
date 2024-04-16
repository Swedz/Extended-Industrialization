package net.swedz.intothetwilight.datagen;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Set;

public final class ITTModernIndustrializationDatagenTracker
{
	private static final Set<String> BLOCK_IDS = Sets.newHashSet();
	
	public static Set<String> trackedBlocks()
	{
		return Collections.unmodifiableSet(BLOCK_IDS);
	}
	
	public static void trackBlock(String id)
	{
		BLOCK_IDS.add(id);
	}
}
