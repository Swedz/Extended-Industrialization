package net.swedz.extended_industrialization.machines.component.farmer.harvesting;

import aztech.modern_industrialization.api.energy.CableTier;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.swedz.extended_industrialization.datamap.EnchantmentModule;

import java.util.Optional;
import java.util.UUID;

public record HarvestingContext(
		Level level,
		BlockPos pos,
		BlockState state,
		UUID harvestingOwnerUUID,
		Optional<EnchantmentModule> enchantment,
		CableTier tier
)
{
	public Optional<FakePlayer> harvestingOwner()
	{
		if(harvestingOwnerUUID == null)
		{
			return Optional.empty();
		}
		var fakePlayer = FakePlayerFactory.get(
				(ServerLevel) level,
				new GameProfile(harvestingOwnerUUID, "Farmer")
		);
		return Optional.of(fakePlayer);
	}
	
	public ItemStack enchantedItem()
	{
		var item = ItemStack.EMPTY;
		if(enchantment.isPresent())
		{
			item = new ItemStack(Items.DIAMOND_AXE);
			enchantment.get().applyEnchantment(level.registryAccess(), item, tier);
		}
		return item;
	}
}
