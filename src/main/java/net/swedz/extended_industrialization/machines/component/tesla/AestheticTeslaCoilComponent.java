package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import com.google.common.collect.Lists;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.configurationpanel.ConfigurationPanelBuilder;
import net.swedz.tesseract.neoforge.helper.ColorHelper;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class AestheticTeslaCoilComponent implements IComponent
{
	private final List<Model> models = Lists.newArrayList();
	
	private float red = 1, green = 1, blue = 1;
	
	private int selectedModelIndex;
	
	public AestheticTeslaCoilComponent with(ResourceLocation path, Component text)
	{
		models.add(new Model(path, text));
		return this;
	}
	
	public float red()
	{
		return red;
	}
	
	public int redInt()
	{
		return (int) (red * 255);
	}
	
	public float green()
	{
		return green;
	}
	
	public int greenInt()
	{
		return (int) (green * 255);
	}
	
	public float blue()
	{
		return blue;
	}
	
	public int blueInt()
	{
		return (int) (blue * 255);
	}
	
	public Vector3f getColor()
	{
		return new Vector3f(red, green, blue);
	}
	
	public ResourceLocation getSelectedModel()
	{
		return models.get(selectedModelIndex).path();
	}
	
	public ItemInteractionResult onUse(MachineBlockEntity be, Player player, InteractionHand hand)
	{
		ItemStack stackInHand = player.getItemInHand(hand);
		if(stackInHand.isEmpty())
		{
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		if(stackInHand.getItem() instanceof DyeItem item)
		{
			int color = ColorHelper.getVibrantColor(item.getDyeColor());
			this.red = ((color >> 16) & 0xFF) / 255f;
			this.green = ((color >> 8) & 0xFF) / 255f;
			this.blue = (color & 0xFF) / 255f;
			be.setChanged();
			be.sync();
			return ItemInteractionResult.sidedSuccess(player.level().isClientSide());
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		CompoundTag colorTag = new CompoundTag();
		colorTag.putFloat("red", red);
		colorTag.putFloat("green", green);
		colorTag.putFloat("blue", blue);
		tag.put("tesla_color", colorTag);
		
		tag.putInt("tesla_selected_model", selectedModelIndex);
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
		if(tag.contains("tesla_color", Tag.TAG_COMPOUND))
		{
			CompoundTag colorTag = tag.getCompound("tesla_color");
			red = colorTag.getFloat("red");
			green = colorTag.getFloat("green");
			blue = colorTag.getFloat("blue");
		}
		else
		{
			red = 1;
			green = 1;
			blue = 1;
		}
		
		selectedModelIndex = tag.getInt("tesla_selected_model");
	}
	
	public record Model(ResourceLocation path, Component text)
	{
	}
	
	private List<Component> createColorTranslations(Component prefix)
	{
		List<Component> lines = Lists.newArrayList();
		for(int i = 0; i <= 17; i++)
		{
			int color = i * 15;
			lines.add(prefix.copy().append(Component.literal(String.valueOf(color))));
		}
		return lines;
	}
	
	private void appendColorLine(ConfigurationPanelBuilder builder, Component prefix, Supplier<Integer> colorIntGetter, Consumer<Float> colorSetter)
	{
		builder.add(
				this.createColorTranslations(prefix), false,
				(delta) ->
				{
					int color = Math.clamp(colorIntGetter.get() + (delta * 15L), 0, 255);
					colorSetter.accept(color / 255f);
				},
				() -> colorIntGetter.get() / 15
		);
	}
	
	public void appendSelectionPanel(MachineBlockEntity machine, ConfigurationPanelBuilder builder)
	{
		if(!models.isEmpty())
		{
			List<Component> modelLines = Lists.newArrayList();
			for(var model : models)
			{
				modelLines.add(model.text());
			}
			builder.add(
					modelLines, true,
					(delta) ->
					{
						int newIndex = selectedModelIndex + delta;
						if(newIndex >= 0 && newIndex < models.size())
						{
							selectedModelIndex = newIndex;
						}
					},
					() -> selectedModelIndex
			);
		}
		
		this.appendColorLine(builder, EI.text().colorRed(), this::redInt, (color) -> red = color);
		this.appendColorLine(builder, EI.text().colorGreen(), this::greenInt, (color) -> green = color);
		this.appendColorLine(builder, EI.text().colorBlue(), this::blueInt, (color) -> blue = color);
	}
}
