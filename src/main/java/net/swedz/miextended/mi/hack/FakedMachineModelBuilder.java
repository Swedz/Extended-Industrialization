package net.swedz.miextended.mi.hack;

import aztech.modern_industrialization.machines.models.MachineUnbakedModel;
import com.google.gson.JsonObject;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;

public final class FakedMachineModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
	private final MIHookTracker.MachineModelProperties props;
	
	public FakedMachineModelBuilder(MIHookTracker.MachineModelProperties props, T parent, ExistingFileHelper existingFileHelper)
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
