package net.swedz.extended_industrialization.client.ber.beacon;

import aztech.modern_industrialization.client.machines.multiblocks.MultiblockMachineBER;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.beacon.ElectricBeaconMachineBlockEntity;

public final class ElectricBeaconBlockEntityRenderer extends MultiblockMachineBER
{
	public ElectricBeaconBlockEntityRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(MultiblockMachineBlockEntity machine, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
	{
		super.render(machine, partialTick, poseStack, bufferSource, light, overlay);
		
		if(!(machine instanceof ElectricBeaconMachineBlockEntity beacon))
		{
			throw new UnsupportedOperationException("ElectricBeaconBlockEntityRenderer requires block entity type of ElectricBeaconMachineBlockEntity");
		}
		
		if(!beacon.isActive())
		{
			return;
		}
		
		long gameTime = beacon.getLevel().getGameTime();
		
		var sections = beacon.getBeamSections();
		int offsetY = 0;
		for(int index = 0; index < sections.size(); index++)
		{
			var section = sections.get(index);
			BeaconRenderer.renderBeaconBeam(
					poseStack,
					bufferSource,
					BeaconRenderer.BEAM_LOCATION,
					partialTick,
					1,
					gameTime,
					offsetY,
					index == sections.size() - 1 ? 1024 : section.height(),
					section.color(),
					0.2f,
					0.25f
			);
			offsetY += section.height();
		}
	}
}
