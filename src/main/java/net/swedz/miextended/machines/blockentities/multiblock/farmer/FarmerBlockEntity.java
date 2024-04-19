package net.swedz.miextended.machines.blockentities.multiblock.farmer;

import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.MIIdentifier;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.swedz.miextended.machines.guicomponents.CommonGuiComponents;
import net.swedz.miextended.machines.multiblock.BasicMultiblockMachineBlockEntity;
import net.swedz.miextended.machines.multiblock.members.PredicateSimpleMember;

public class FarmerBlockEntity extends BasicMultiblockMachineBlockEntity
{
	private static final int MAX_HEIGHT              = 4;
	private static final int RADIUS_START            = 3;
	private static final int RADIUS_LEVEL_MULTIPLIER = 2;
	
	private static final ShapeTemplate[] SHAPE_TEMPLATES;
	
	public FarmerBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder("farmer", false).backgroundHeight(128).build(),
				SHAPE_TEMPLATES
		);
		
		this.registerGuiComponent(CommonGuiComponents.rangedShapeSelection(this, activeShape, MAX_HEIGHT));
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		isActive.updateActive(false, this);
	}
	
	public static void registerReiShapes()
	{
		for(ShapeTemplate shapeTemplate : SHAPE_TEMPLATES)
		{
			ReiMachineRecipes.registerMultiblockShape("farmer", shapeTemplate);
		}
	}
	
	static
	{
		SHAPE_TEMPLATES = new ShapeTemplate[MAX_HEIGHT];
		
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
			
			int maxDistance = (int) Math.pow((RADIUS_START + 2) + (i * RADIUS_LEVEL_MULTIPLIER), 2);
			for(int x = -maxDistance; x <= maxDistance; x++)
			{
				for(int z = -maxDistance; z <= maxDistance; z++)
				{
					if(Math.abs(x) <= 1 && Math.abs(z) <= 1)
					{
						continue;
					}
					int distance = (int) (Math.pow(x, 2) + Math.pow(z, 2));
					if(distance < maxDistance)
					{
						builder.add(x, -1, z + 1, dirt);
					}
				}
			}
			
			SHAPE_TEMPLATES[i] = builder.build();
		}
	}
}
