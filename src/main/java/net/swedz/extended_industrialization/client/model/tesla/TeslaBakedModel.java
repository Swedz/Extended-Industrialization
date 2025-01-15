package net.swedz.extended_industrialization.client.model.tesla;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.List;
import java.util.Map;

public final class TeslaBakedModel implements IDynamicBakedModel
{
	private final List<BakedQuad>                 unculledFaces;
	private final Map<Direction, List<BakedQuad>> culledFaces;
	private final TeslaUnbakedModel.Plasma        plasma;
	private final TeslaUnbakedModel.Arcs          arcs;
	
	TeslaBakedModel(List<BakedQuad> unculledFaces, Map<Direction, List<BakedQuad>> culledFaces, TeslaUnbakedModel.Plasma plasma, TeslaUnbakedModel.Arcs arcs)
	{
		this.unculledFaces = unculledFaces;
		this.culledFaces = culledFaces;
		this.plasma = plasma;
		this.arcs = arcs;
	}
	
	public TeslaUnbakedModel.Plasma plasma()
	{
		return plasma;
	}
	
	public TeslaUnbakedModel.Arcs arcs()
	{
		return arcs;
	}
	
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction direction, RandomSource random, ModelData data, RenderType renderType)
	{
		return direction == null ? unculledFaces : culledFaces.get(direction);
	}
	
	@Override
	public boolean useAmbientOcclusion()
	{
		return false;
	}
	
	@Override
	public boolean isGui3d()
	{
		return false;
	}
	
	@Override
	public boolean usesBlockLight()
	{
		return false;
	}
	
	@Override
	public boolean isCustomRenderer()
	{
		return false;
	}
	
	@Override
	public TextureAtlasSprite getParticleIcon()
	{
		return null;
	}
	
	@Override
	public ItemOverrides getOverrides()
	{
		return ItemOverrides.EMPTY;
	}
}
