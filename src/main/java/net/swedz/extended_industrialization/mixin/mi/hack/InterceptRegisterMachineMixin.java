package net.swedz.extended_industrialization.mixin.mi.hack;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.init.MachineRegistrationHelper;
import aztech.modern_industrialization.util.MobSpawning;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.hook.mi.hack.FakedMachineModelBuilder;
import net.swedz.extended_industrialization.hook.mi.tracker.MIHookTracker;
import net.swedz.extended_industrialization.api.registry.CommonModelBuilders;
import net.swedz.extended_industrialization.api.registry.holder.BlockHolder;
import net.swedz.extended_industrialization.EIBlocks;
import net.swedz.extended_industrialization.api.registry.SortOrder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(
		value = MachineRegistrationHelper.class,
		remap = false
)
public class InterceptRegisterMachineMixin
{
	@Inject(
			method = "registerMachine",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void registerMachine(String englishName, String id,
										Function<BEP, MachineBlockEntity> factory,
										Consumer<BlockEntityType<?>>[] extraRegistrators,
										CallbackInfoReturnable<Supplier<BlockEntityType<?>>> callback)
	{
		if(MIHookTracker.isOpen())
		{
			BlockEntityType<?>[] bet = new BlockEntityType[1];
			BiFunction<BlockPos, BlockState, MachineBlockEntity> ctor = (pos, state) -> factory.apply(new BEP(bet[0], pos, state));
			
			BlockHolder blockHolder = EIBlocks
					.create(
							id, englishName,
							(p) -> new MachineBlock(ctor, p),
							BlockItem::new,
							SortOrder.MACHINES
					)
					.withProperties((p) -> p
							.mapColor(MapColor.METAL)
							.destroyTime(4)
							.requiresCorrectToolForDrops()
							.isValidSpawn(MobSpawning.NO_SPAWN))
					.withModel((holder) -> (provider) ->
					{
						MIHookTracker.MachineModelProperties machineModelProperties = MIHookTracker.MACHINE_MODELS.get(id);
						if(machineModelProperties == null)
						{
							CommonModelBuilders.blockstateOnly(holder).accept(provider);
							return;
						}
						provider.simpleBlockWithItem(BuiltInRegistries.BLOCK.get(EI.id(id)), provider.models()
								.getBuilder(id)
								.customLoader((bmb, exFile) -> new FakedMachineModelBuilder<>(machineModelProperties, bmb, exFile))
								.end());
					})
					.register();
			
			callback.setReturnValue(EIBlocks.Registry.BLOCK_ENTITIES.register(id, () ->
			{
				Block block = blockHolder.get();
				
				bet[0] = BlockEntityType.Builder.of(ctor::apply, block).build(null);
				
				for(Consumer<BlockEntityType<?>> extraRegistrator : extraRegistrators)
				{
					extraRegistrator.accept(bet[0]);
				}
				
				return bet[0];
			}));
		}
	}
}
