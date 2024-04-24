package net.swedz.extended_industrialization.datagen.client.provider.mi;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.models.MachineBakedModel;
import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.UseBlockModelUnbakedModel;
import aztech.modern_industrialization.materials.MIMaterials;
import aztech.modern_industrialization.materials.part.MIParts;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.hook.mi.MIMachineHook;

public final class MachineCasingModelsMIHookDatagenProvider extends ModelProvider<BlockModelBuilder>
{
	public MachineCasingModelsMIHookDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), MI.ID, MachineBakedModel.CASING_FOLDER, BlockModelBuilder::new, event.getExistingFileHelper());
	}
	
	@Override
	protected void registerModels()
	{
		this.imitateBlock(MIMachineHook.Casings.BRONZE_PIPE, MIMaterials.BRONZE.getPart(MIParts.MACHINE_CASING_PIPE).asBlock());
	}
	
	private void imitateBlock(MachineCasing casing, Block block)
	{
		getBuilder(casing.name)
				.customLoader((bmb, existingFileHelper) -> new UseBlockModelModelBuilder<>(block, bmb, existingFileHelper));
	}
	
	private void cubeBottomTop(MachineCasing casing, String side, String bottom, String top)
	{
		cubeBottomTop(casing.name, MI.id(side), MI.id(bottom), MI.id(top));
	}
	
	private void cubeAll(MachineCasing casing, String side)
	{
		cubeAll(casing.name, MI.id(side));
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
	
	private static final class UseBlockModelModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
	{
		private final Block targetBlock;
		
		private UseBlockModelModelBuilder(Block targetBlock, T parent, ExistingFileHelper existingFileHelper)
		{
			super(UseBlockModelUnbakedModel.LOADER_ID, parent, existingFileHelper, false);
			this.targetBlock = targetBlock;
		}
		
		@Override
		public JsonObject toJson(JsonObject json)
		{
			JsonObject ret = super.toJson(json);
			ret.addProperty("block", BuiltInRegistries.BLOCK.getKey(targetBlock).toString());
			return ret;
		}
	}
}
