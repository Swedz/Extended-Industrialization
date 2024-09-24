package net.swedz.extended_industrialization.client.model.chainer;

import aztech.modern_industrialization.machines.models.MachineCasing;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.swedz.extended_industrialization.client.model.ModularMachineBakedModel;

import java.util.Map;

public final class MachineChainerBakedModel extends ModularMachineBakedModel
{
	public MachineChainerBakedModel(MachineCasing baseCasing,
									int[] outputOverlayIndexes, TextureAtlasSprite[] defaultOverlays,
									Map<String, TextureAtlasSprite[]> tieredOverlays)
	{
		super(baseCasing, outputOverlayIndexes, defaultOverlays, tieredOverlays);
	}
	
	@Override
	public TextureAtlasSprite getSprite(TextureAtlasSprite[] sprites, Direction side, Direction facingDirection, boolean isActive)
	{
		int spriteId = 1;
		if(side == facingDirection)
		{
			spriteId = 0;
		}
		else if(side == facingDirection.getOpposite())
		{
			spriteId = 1;
		}
		else if(side.getAxis().isHorizontal())
		{
			if(facingDirection.getAxis().isVertical())
			{
				spriteId = facingDirection == Direction.UP ? 2 : 3;
			}
			else
			{
				spriteId = switch (facingDirection)
				{
					case NORTH -> side == Direction.WEST ? 4 : 5;
					case SOUTH -> side == Direction.WEST ? 5 : 4;
					case WEST -> side == Direction.NORTH ? 5 : 4;
					case EAST -> side == Direction.NORTH ? 4 : 5;
					default -> throw new IllegalStateException("Unexpected value: " + facingDirection);
				};
			}
		}
		else if(side.getAxis().isVertical() && facingDirection.getAxis().isHorizontal())
		{
			spriteId = switch (facingDirection)
			{
				case NORTH -> side == Direction.UP ? 2 : 3;
				case SOUTH -> side == Direction.UP ? 3 : 2;
				case WEST -> 4;
				case EAST -> 5;
				default -> throw new IllegalStateException("Unexpected value: " + facingDirection);
			};
		}
		return sprites[spriteId];
	}
}