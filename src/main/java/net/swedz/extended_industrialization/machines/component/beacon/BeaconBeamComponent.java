package net.swedz.extended_industrialization.machines.component.beacon;

import aztech.modern_industrialization.machines.MachineComponent;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.swedz.tesseract.api.Assert;

import java.util.Collections;
import java.util.List;

public final class BeaconBeamComponent implements MachineComponent.ClientOnly
{
	private List<Section> beamSections = Lists.newArrayList();
	
	private boolean needsRematch = true;
	
	public BeaconBeamComponent()
	{
	}
	
	public boolean needsRematch()
	{
		return needsRematch;
	}
	
	public void requestRematch()
	{
		needsRematch = true;
	}
	
	public List<Section> getBeamSections()
	{
		return beamSections;
	}
	
	public boolean isActive()
	{
		return !beamSections.isEmpty();
	}
	
	private List<Section> calculateBeamSections(Level level, BlockPos controllerPos)
	{
		List<Section> sections = Lists.newArrayList();
		
		int maxHeight = level.getHeight(Heightmap.Types.WORLD_SURFACE, controllerPos.getX(), controllerPos.getZ());
		
		var currentSection = new Section(DyeColor.WHITE.getTextureDiffuseColor());
		sections.add(currentSection);
		
		var pos = controllerPos.above();
		for(int y = controllerPos.getY() + 1; y <= maxHeight; y++)
		{
			var state = level.getBlockState(pos);
			var colorMultiplier = state.getBeaconColorMultiplier(level, pos, controllerPos);
			
			if(colorMultiplier != null)
			{
				if(sections.size() <= 1)
				{
					currentSection = new Section(colorMultiplier);
					sections.add(currentSection);
				}
				else if(colorMultiplier == currentSection.color())
				{
					currentSection.increaseHeight();
				}
				else
				{
					currentSection = new Section(FastColor.ARGB32.average(currentSection.color(), colorMultiplier));
					sections.add(currentSection);
				}
			}
			else
			{
				if(state.getLightBlock(level, pos) >= 15 && !state.is(Blocks.BEDROCK))
				{
					return List.of();
				}
				
				currentSection.increaseHeight();
			}
			
			pos = pos.above();
		}
		
		return Collections.unmodifiableList(sections);
	}
	
	public void rematch(Level level, BlockPos controllerPos)
	{
		Assert.that(!level.isClientSide());
		
		beamSections = this.calculateBeamSections(level, controllerPos);
		needsRematch = false;
	}
	
	@Override
	public void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		var beamSectionsTag = new ListTag();
		for(var section : beamSections)
		{
			beamSectionsTag.add(new IntArrayTag(new int[]{section.color(), section.height()}));
		}
		tag.put("beam_sections", beamSectionsTag);
	}
	
	@Override
	public void readClientNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		List<Section> sections = Lists.newArrayList();
		var beamSectionsTag = tag.getList("beam_sections", Tag.TAG_INT_ARRAY);
		for(int index = 0; index < beamSectionsTag.size(); index++)
		{
			var beamSectionTag = beamSectionsTag.getIntArray(index);
			sections.add(new Section(beamSectionTag[0], beamSectionTag[1]));
		}
		beamSections = Collections.unmodifiableList(sections);
	}
	
	public static final class Section
	{
		private final int color;
		
		private int height;
		
		private Section(int color, int height)
		{
			this.color = color;
			this.height = height;
		}
		
		private Section(int color)
		{
			this(color, 1);
		}
		
		public int color()
		{
			return color;
		}
		
		public int height()
		{
			return height;
		}
		
		private void increaseHeight()
		{
			height++;
		}
	}
}
