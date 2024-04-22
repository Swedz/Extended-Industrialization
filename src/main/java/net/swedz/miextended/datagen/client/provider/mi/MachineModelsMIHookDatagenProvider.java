package net.swedz.miextended.datagen.client.provider.mi;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.models.MachineUnbakedModel;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;

public final class MachineModelsMIHookDatagenProvider extends BlockStateProvider
{
	public MachineModelsMIHookDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), MI.ID, event.getExistingFileHelper());
	}
	
	@Override
	protected void registerStatesAndModels()
	{
		for(String id : MIHookTracker.MACHINE_MODELS.keySet())
		{
			MIHookTracker.MachineModelProperties machineModelProperties = MIHookTracker.MACHINE_MODELS.get(id);
			this.simpleBlockWithItem(BuiltInRegistries.BLOCK.get(MI.id(id)), models()
					.getBuilder(id)
					.customLoader((bmb, exFile) -> new MachineModelBuilder<>(machineModelProperties, bmb, exFile))
					.end());
		}
	}
	
	private static final class MachineModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
	{
		private final MIHookTracker.MachineModelProperties props;
		
		private MachineModelBuilder(MIHookTracker.MachineModelProperties props, T parent, ExistingFileHelper existingFileHelper)
		{
			super(MachineUnbakedModel.LOADER_ID, parent, existingFileHelper, false);
			this.props = props;
		}
		
		@Override
		public JsonObject toJson(JsonObject json)
		{
			JsonObject ret = super.toJson(json);
			props.addToMachineJson(ret);
			return ret;
		}
	}
}
