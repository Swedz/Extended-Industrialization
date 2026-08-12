package net.swedz.extended_industrialization.machines.component.beacon;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIItemStorage;
import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.swedz.extended_industrialization.EITags;
import net.swedz.tesseract.api.Assert;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class BeaconEffectComponent implements MachineComponent
{
	// This should not be any larger than 6, because then there will be effects not shown in the GUI
	private static final int MAX_EFFECTS = 3;
	
	private List<Effect> activeEffects = List.of();
	
	private int totalEffectTicks     = 0;
	private int remainingEffectTicks = 0;
	
	public List<Effect> getActiveEffects()
	{
		return activeEffects;
	}
	
	public int getTotalTicks()
	{
		return totalEffectTicks;
	}
	
	public int getRemainingTicks()
	{
		return remainingEffectTicks;
	}
	
	public void tick(Level level, BlockPos controllerPos, MultiblockInventoryComponent inventory, int size)
	{
		Assert.that(!level.isClientSide());
		
		this.tryConsumePotion(inventory);
		this.tryApplyEffects(level, controllerPos, size);
	}
	
	private void tryConsumePotion(MultiblockInventoryComponent inventory)
	{
		if(remainingEffectTicks <= 0)
		{
			if(this.canConsumePotion(inventory))
			{
				var result = this.takeItemInput(inventory.getItemInputs(), false);
				if(result.isPresent())
				{
					this.putItemOutput(inventory.getItemOutputs(), false);
					activeEffects = result.get().effects();
					totalEffectTicks = result.get().ticks();
					remainingEffectTicks = result.get().ticks();
				}
			}
			if(remainingEffectTicks <= 0)
			{
				activeEffects = List.of();
				totalEffectTicks = 0;
				remainingEffectTicks = 0;
			}
		}
	}
	
	private void tryApplyEffects(Level level, BlockPos controllerPos, int size)
	{
		if(!activeEffects.isEmpty())
		{
			if(level.getGameTime() % (4 * 20) == 0)
			{
				double range = (size * 10) + 10;
				var area = new AABB(controllerPos).inflate(range).expandTowards(0, level.getHeight(), 0);
				var players = level.getEntitiesOfClass(Player.class, area);
				
				int duration = (9 + (size * 2)) * 20;
				
				for(var player : players)
				{
					for(var effect : activeEffects)
					{
						player.addEffect(new MobEffectInstance(effect.effect(), duration, effect.amplifier(), true, true));
					}
				}
			}
			
			remainingEffectTicks--;
		}
	}
	
	private boolean canConsumePotion(MultiblockInventoryComponent inventory)
	{
		return this.takeItemInput(inventory.getItemInputs(), true).isPresent() &&
			   this.putItemOutput(inventory.getItemOutputs(), true);
	}
	
	private Optional<EffectResult> takeItemInput(List<ConfigurableItemStack> inputSlots, boolean simulate)
	{
		for(var stack : inputSlots)
		{
			if(stack.isEmpty() || stack.getAmount() <= 0)
			{
				continue;
			}
			
			var potionContentsOptional = stack.getResource().getComponentsPatch().get(DataComponents.POTION_CONTENTS);
			if(potionContentsOptional != null && potionContentsOptional.isPresent())
			{
				var potionContents = potionContentsOptional.get();
				if(!potionContents.hasEffects())
				{
					continue;
				}
				
				var result = this.calculateEffectResult(potionContents);
				if(result.effects().isEmpty() ||
				   result.effects().size() > MAX_EFFECTS)
				{
					continue;
				}
				
				if(!simulate)
				{
					stack.decrement(1);
				}
				
				return Optional.of(result);
			}
		}
		return Optional.empty();
	}
	
	private boolean putItemOutput(List<ConfigurableItemStack> outputSlots, boolean simulate)
	{
		var storage = new MIItemStorage(outputSlots);
		try(var transaction = Transaction.openRoot())
		{
			boolean success = storage.insertAllSlot(ItemVariant.of(Items.GLASS_BOTTLE), 1, transaction) == 1;
			if(!simulate)
			{
				transaction.commit();
			}
			return success;
		}
	}
	
	private EffectResult calculateEffectResult(PotionContents potionContents)
	{
		int ticks = 0;
		List<Effect> effects = Lists.newArrayList();
		for(var potionEffect : potionContents.getAllEffects())
		{
			if(potionEffect.getEffect().value().isInstantenous() ||
			   potionEffect.getEffect().is(EITags.MobEffects.ELECTRIC_BEACON_BLACKLIST))
			{
				continue;
			}
			
			effects.add(new Effect(potionEffect.getEffect(), potionEffect.getAmplifier()));
			
			int duration;
			if(potionEffect.isInfiniteDuration())
			{
				duration = 10 * 60 * 20;
			}
			else
			{
				duration = potionEffect.getDuration();
			}
			
			if(ticks == 0 || duration < ticks)
			{
				ticks = duration;
			}
		}
		return new EffectResult(effects, (int) Math.ceil(ticks / 2f));
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		var effectsTag = new ListTag();
		for(var effect : activeEffects)
		{
			var effectTag = new CompoundTag();
			effectTag.putString("effect", effect.effect().getKey().location().toString());
			effectTag.putInt("amplifier", effect.amplifier());
			effectsTag.add(effectTag);
		}
		tag.put("beacon_effects", effectsTag);
		
		tag.putInt("total_beacon_effect_ticks", totalEffectTicks);
		tag.putInt("remaining_beacon_effect_ticks", remainingEffectTicks);
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
		List<Effect> activeEffects = Lists.newArrayList();
		var effectsTag = tag.getList("beacon_effects", Tag.TAG_COMPOUND);
		for(int index = 0; index < effectsTag.size(); index++)
		{
			var effectTag = effectsTag.getCompound(index);
			var effectId = ResourceLocation.tryParse(effectTag.getString("effect"));
			if(effectId == null)
			{
				continue;
			}
			var effect = registries.holder(ResourceKey.create(Registries.MOB_EFFECT, effectId));
			if(effect.isEmpty())
			{
				continue;
			}
			int amplifier = effectTag.getInt("amplifier");
			activeEffects.add(new Effect(effect.get(), amplifier));
		}
		this.activeEffects = Collections.unmodifiableList(activeEffects);
		
		totalEffectTicks = tag.getInt("total_beacon_effect_ticks");
		remainingEffectTicks = tag.getInt("remaining_beacon_effect_ticks");
	}
	
	public record Effect(
			Holder<MobEffect> effect,
			int amplifier
	)
	{
		public static final StreamCodec<RegistryFriendlyByteBuf, Effect> STREAM_CODEC = StreamCodec.composite(
				CodecHelper.forRegistryHolderStream(BuiltInRegistries.MOB_EFFECT),
				Effect::effect,
				ByteBufCodecs.INT,
				Effect::amplifier,
				Effect::new
		);
	}
	
	private record EffectResult(
			List<Effect> effects,
			int ticks
	)
	{
	}
}
