package net.swedz.miextended.machines.blockentities.multiblock.farmer;

import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.MIIdentifier;
import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.swedz.miextended.machines.components.farmer.FarmerComponent;
import net.swedz.miextended.machines.multiblock.BasicMultiblockMachineBlockEntity;
import net.swedz.miextended.machines.multiblock.members.PredicateSimpleMember;
import net.swedz.miextended.text.MIEText;
import org.apache.commons.compress.utils.Lists;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public abstract class FarmerBlockEntity extends BasicMultiblockMachineBlockEntity
{
	private static final int MAX_HEIGHT              = 4;
	private static final int RADIUS_START            = 3;
	private static final int RADIUS_LEVEL_MULTIPLIER = 2;
	
	private static final ShapeTemplate[]  SHAPE_TEMPLATES;
	private static final List<BlockPos>[] DIRT_POSITIONS;
	
	protected final long euCost;
	
	protected final FarmerComponent farmer;
	
	public FarmerBlockEntity(BEP bep, String blockId, long euCost, FarmerComponent.PlantingMode defaultPlantingMode, boolean canChoosePlantingMode, int maxOperationsPerTask)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(blockId, false).backgroundHeight(128).build(),
				SHAPE_TEMPLATES
		);
		
		this.euCost = euCost;
		
		this.farmer = new FarmerComponent(inventory, isActive, defaultPlantingMode, maxOperationsPerTask);
		
		List<ShapeSelection.LineInfo> lines = Lists.newArrayList();
		lines.add(new ShapeSelection.LineInfo(
				4,
				List.of(MIText.ShapeTextSmall.text(), MIText.ShapeTextMedium.text(), MIText.ShapeTextLarge.text(), MIText.ShapeTextExtreme.text()),
				true
		));
		lines.add(new ShapeSelection.LineInfo(
				2,
				List.of(MIEText.FARMER_NOT_TILLING.text(), MIEText.FARMER_TILLING.text()),
				true
		));
		if(canChoosePlantingMode)
		{
			lines.add(new ShapeSelection.LineInfo(
					FarmerComponent.PlantingMode.values().length,
					Stream.of(FarmerComponent.PlantingMode.values()).map(FarmerComponent.PlantingMode::textComponent).toList(),
					true
			));
		}
		this.registerGuiComponent(new ShapeSelection.Server(
				new ShapeSelection.Behavior()
				{
					@Override
					public void handleClick(int line, int delta)
					{
						if(line == 0)
						{
							activeShape.incrementShape(FarmerBlockEntity.this, delta);
						}
						else if(line == 1)
						{
							farmer.tilling = !farmer.tilling;
						}
						else if(line == 2)
						{
							farmer.plantingMode = FarmerComponent.PlantingMode.values()[farmer.plantingMode.ordinal() + delta];
						}
					}
					
					@Override
					public int getCurrentIndex(int line)
					{
						if(line == 0)
						{
							return activeShape.getActiveShapeIndex();
						}
						else if(line == 1)
						{
							return farmer.tilling ? 1 : 0;
						}
						else if(line == 2)
						{
							return farmer.plantingMode.ordinal();
						}
						throw new IllegalStateException();
					}
				},
				lines.toArray(new ShapeSelection.LineInfo[0])
		));
	}
	
	public abstract long consumeEu(long max);
	
	@Override
	public void onLink(ShapeMatcher shapeMatcher)
	{
		farmer.registerListeners(level, shapeMatcher);
	}
	
	@Override
	public void onUnlink(ShapeMatcher shapeMatcher)
	{
		farmer.unregisterListeners(level, shapeMatcher);
	}
	
	@Override
	public void onSuccessfulMatch(ShapeMatcher shapeMatcher)
	{
		List<BlockPos> offsets = DIRT_POSITIONS[activeShape.getActiveShapeIndex()];
		farmer.fromOffsets(worldPosition, orientation.facingDirection, offsets);
		
		farmer.updateStackListeners();
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if(level.isClientSide)
		{
			return;
		}
		
		if(this.isShapeValid())
		{
			long eu = this.consumeEu(euCost);
			boolean active = eu > 0;
			this.updateActive(active);
			
			if(active)
			{
				farmer.tick();
			}
		}
		else
		{
			this.updateActive(false);
		}
	}
	
	private void updateActive(boolean active)
	{
		isActive.updateActive(active, this);
	}
	
	public static void registerReiShapes(String machine)
	{
		for(ShapeTemplate shapeTemplate : SHAPE_TEMPLATES)
		{
			ReiMachineRecipes.registerMultiblockShape(machine, shapeTemplate);
		}
	}
	
	static
	{
		SHAPE_TEMPLATES = new ShapeTemplate[MAX_HEIGHT];
		DIRT_POSITIONS = new List[MAX_HEIGHT];
		
		SimpleMember bronzePlatedBricks = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(new MIIdentifier("bronze_plated_bricks")));
		SimpleMember bronzePipe = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(new MIIdentifier("bronze_machine_casing_pipe")));
		SimpleMember dirt = new PredicateSimpleMember((state) -> state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND), Blocks.DIRT);
		
		HatchFlags hatchFlags = new HatchFlags.Builder().with(HatchType.ENERGY_INPUT, HatchType.ITEM_INPUT, HatchType.ITEM_OUTPUT, HatchType.FLUID_INPUT).build();
		
		for(int i = 0; i < MAX_HEIGHT; i++)
		{
			ShapeTemplate.Builder builder = new ShapeTemplate.Builder(MachineCasings.BRONZE);
			
			int height = i + 4;
			for(int y = 0; y > -height; y--)
			{
				boolean bottom = y == -height + 1;
				boolean top = y == 0;
				boolean topSecond = y == -1;
				boolean middle = !bottom && !top && !topSecond;
				builder.add3by3(y, middle ? bronzePipe : bronzePlatedBricks, middle, middle ? hatchFlags : null);
				if(middle)
				{
					builder.add(0, y, 1, bronzePipe);
				}
			}
			
			List<BlockPos> dirtBlocks = Lists.newArrayList();
			double maxDistance = (RADIUS_START + 1.5) + (i * RADIUS_LEVEL_MULTIPLIER);
			int maxDistanceRounded = (int) Math.ceil(maxDistance);
			int maxDistanceSquared = (int) Math.pow(maxDistance, 2);
			for(int x = -maxDistanceRounded; x <= maxDistanceRounded; x++)
			{
				for(int z = -maxDistanceRounded; z <= maxDistanceRounded; z++)
				{
					if(Math.abs(x) <= 1 && Math.abs(z) <= 1)
					{
						continue;
					}
					int distance = (int) (Math.pow(x, 2) + Math.pow(z, 2));
					if(distance < maxDistanceSquared)
					{
						builder.add(x, -1, z + 1, dirt);
						dirtBlocks.add(new BlockPos(x, -1, z + 1));
					}
				}
			}
			
			SHAPE_TEMPLATES[i] = builder.build();
			DIRT_POSITIONS[i] = Collections.unmodifiableList(dirtBlocks);
		}
	}
}
