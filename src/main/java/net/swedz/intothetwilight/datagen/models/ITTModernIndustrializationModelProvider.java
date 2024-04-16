package net.swedz.intothetwilight.datagen.models;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.datagen.model.BaseModelProvider;
import aztech.modern_industrialization.datagen.model.MachineModelsToGenerate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.swedz.intothetwilight.datagen.ITTModernIndustrializationDatagenTracker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public final class ITTModernIndustrializationModelProvider extends BaseModelProvider
{
	public ITTModernIndustrializationModelProvider(PackOutput output, ExistingFileHelper exFileHelper)
	{
		super(output, exFileHelper);
	}
	
	@Override
	protected void registerStatesAndModels()
	{
		try
		{
			// This is super hacky, but I need it for now
			Field fieldProps = MachineModelsToGenerate.class.getDeclaredField("props");
			fieldProps.setAccessible(true);
			Map<String, ?> props = (Map<String, ?>) fieldProps.get(null);
			Class<?> classMachineModelBuilder = Class.forName("aztech.modern_industrialization.datagen.model.MachineModelBuilder");
			Class<?> classMachineModelProperties = Class.forName("aztech.modern_industrialization.datagen.model.MachineModelsToGenerate$MachineModelProperties");
			Constructor<?> constructor = classMachineModelBuilder.getDeclaredConstructor(classMachineModelProperties, ModelBuilder.class, ExistingFileHelper.class);
			constructor.setAccessible(true);
			
			for(String id : ITTModernIndustrializationDatagenTracker.trackedBlocks())
			{
				Object block = props.get(id);
				this.simpleBlockWithItem(BuiltInRegistries.BLOCK.get(MI.id(id)), this.models()
						.getBuilder(id)
						.customLoader((bmb, exFile) ->
						{
							try
							{
								return (CustomLoaderBuilder) constructor.newInstance(block, bmb, exFile);
							}
							catch (InstantiationException | IllegalAccessException | InvocationTargetException ex)
							{
								throw new RuntimeException(ex);
							}
						})
						.end());
			}
			
			fieldProps.setAccessible(false);
			constructor.setAccessible(false);
		}
		catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException ex)
		{
			ex.printStackTrace();
		}
	}
}
