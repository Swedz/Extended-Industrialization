package net.swedz.miextended.machines.components.farmer;

enum PlantableType
{
	NOT_PLANTABLE(false),
	VANILLA_CROP(true),
	VANILLA_SAPLING(true),
	NEOFORGE_PLANTABLE(false);
	
	private final boolean vanillaBlock;
	
	PlantableType(boolean vanillaBlock)
	{
		this.vanillaBlock = vanillaBlock;
	}
	
	public boolean isVanillaBlock()
	{
		return vanillaBlock;
	}
}
