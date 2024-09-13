package net.swedz.extended_industrialization.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.EIText;
import net.swedz.tesseract.neoforge.item.component.DataComponentTooltipProvider;
import net.swedz.tesseract.neoforge.item.component.TooltipAdder;

public record RainbowDataComponent(boolean value, boolean showInTooltip) implements DataComponentTooltipProvider
{
	public static final Codec<RainbowDataComponent>                CODEC        = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.BOOL.fieldOf("value").forGetter(RainbowDataComponent::value),
					Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(RainbowDataComponent::showInTooltip)
			)
			.apply(instance, RainbowDataComponent::new));
	public static final StreamCodec<ByteBuf, RainbowDataComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
	
	public static <I extends Item> void cauldronClearDyeAndRainbow(I item)
	{
		CauldronInteraction.WATER.map().put(item, (state, level, pos, player, hand, stack) ->
		{
			if(!stack.is(ItemTags.DYEABLE) &&
			   !stack.is(EITags.Items.RAINBOW_DYEABLE))
			{
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			}
			else if(!stack.has(DataComponents.DYED_COLOR) &&
					!stack.has(EIComponents.RAINBOW))
			{
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			}
			else
			{
				if(!level.isClientSide())
				{
					stack.remove(DataComponents.DYED_COLOR);
					stack.remove(EIComponents.RAINBOW);
					player.awardStat(Stats.CLEAN_ARMOR);
					LayeredCauldronBlock.lowerFillLevel(state, level, pos);
				}
				return ItemInteractionResult.sidedSuccess(level.isClientSide());
			}
		});
	}
	
	public static final long BASE_TRANSITION_SPEED = 1000;
	
	public static int getCurrentRainbowColor(long transitionSpeed)
	{
		long time = System.currentTimeMillis();
		float cyclePosition = (time % (transitionSpeed * 6)) / (float) transitionSpeed;
		float hue = (cyclePosition % 6) / 6F;
		return Mth.hsvToArgb(hue, 1f, 1f, 255);
	}
	
	public static int getCurrentRainbowColor()
	{
		return getCurrentRainbowColor(BASE_TRANSITION_SPEED);
	}
	
	@Override
	public void addToTooltip(Item.TooltipContext context, TooltipAdder tooltip, TooltipFlag flag)
	{
		if(showInTooltip && value)
		{
			tooltip.add(Component.translatable("item.color", EIText.RAINBOW.text().setStyle(Style.EMPTY.withColor(getCurrentRainbowColor()))).withStyle(ChatFormatting.GRAY));
		}
	}
}
