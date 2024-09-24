package net.swedz.extended_industrialization.client.model.chainer;

import aztech.modern_industrialization.machines.models.MachineOverlaysJson;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

public final class MachineChainerOverlaysJson implements MachineOverlaysJson
{
	private ResourceLocation front;
	private ResourceLocation back;
	private ResourceLocation up;
	private ResourceLocation down;
	private ResourceLocation left;
	private ResourceLocation right;
	private ResourceLocation output;
	private ResourceLocation item_auto;
	private ResourceLocation fluid_auto;
	
	@Override
	public Material[] toSpriteIds()
	{
		return new Material[]{
				select(front),
				select(back),
				select(up),
				select(down),
				select(left),
				select(right),
				select(output),
				select(item_auto),
				select(fluid_auto),
		};
	}
	
	@Override
	public int[] getOutputSpriteIndexes()
	{
		return new int[]{6, 7, 8};
	}
}
