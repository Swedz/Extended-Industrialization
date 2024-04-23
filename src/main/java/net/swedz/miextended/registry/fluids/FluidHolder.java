package net.swedz.miextended.registry.fluids;

import aztech.modern_industrialization.definition.FluidLike;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.miextended.registry.api.CommonCapabilities;
import net.swedz.miextended.registry.api.CommonModelBuilders;
import net.swedz.miextended.registry.api.RegisteredObjectHolder;
import net.swedz.miextended.registry.api.registerable.SimpleRegisterableWrapper;
import net.swedz.miextended.registry.blocks.BlockHolder;
import net.swedz.miextended.registry.items.ItemHolder;

import java.util.function.BiFunction;
import java.util.function.Function;

public class FluidHolder<F extends Fluid, FT extends FluidType, FB extends Block, FBI extends BucketItem> extends RegisteredObjectHolder<Fluid, F, FluidHolder<F, FT, FB, FBI>> implements FluidLike
{
	private final SimpleRegisterableWrapper<Fluid, F>      registerableFluid;
	private final SimpleRegisterableWrapper<FluidType, FT> registerableFluidType;
	
	private final BlockHolder<FB> blockHolder;
	private final ItemHolder<FBI> bucketItemHolder;
	
	public FluidHolder(ResourceLocation location, String englishName,
					   DeferredRegister<Fluid> registerFluids, Function<FluidHolder<F, FT, FB, FBI>, F> creatorFluid,
					   DeferredRegister<FluidType> registerFluidTypes, Function<FluidHolder<F, FT, FB, FBI>, FT> creatorFluidType,
					   DeferredRegister.Blocks registerBlocks, BiFunction<FluidHolder<F, FT, FB, FBI>, BlockBehaviour.Properties, FB> creatorFluidBlock,
					   DeferredRegister.Items registerItems, BiFunction<FluidHolder<F, FT, FB, FBI>, Item.Properties, FBI> creatorBucketItem)
	{
		super(location, englishName);
		this.registerableFluid = new SimpleRegisterableWrapper<>(registerFluids, () -> creatorFluid.apply(this));
		this.registerableFluidType = new SimpleRegisterableWrapper<>(registerFluidTypes, () -> creatorFluidType.apply(this));
		this.blockHolder = new BlockHolder<>(location, englishName, registerBlocks, (p) -> creatorFluidBlock.apply(this, p));
		this.bucketItemHolder = new ItemHolder<>(new ResourceLocation(location.getNamespace(), location.getPath() + "_bucket"), englishName + " Bucket", registerItems, (p) -> creatorBucketItem.apply(this, p))
				.withModel(CommonModelBuilders::generated)
				.withCapabilities(CommonCapabilities::bucketItem);
	}
	
	public SimpleRegisterableWrapper<Fluid, F> registerableFluid()
	{
		return registerableFluid;
	}
	
	public SimpleRegisterableWrapper<FluidType, FT> registerableFluidType()
	{
		return registerableFluidType;
	}
	
	public BlockHolder<FB> block()
	{
		return blockHolder;
	}
	
	public ItemHolder<FBI> bucketItem()
	{
		return bucketItemHolder;
	}
	
	@Override
	public FluidHolder<F, FT, FB, FBI> register()
	{
		this.guaranteeUnlocked();
		
		registerableFluid.registerSimple(identifier, DeferredRegister::register);
		registerableFluidType.registerSimple(identifier, DeferredRegister::register);
		blockHolder.register();
		bucketItemHolder.register();
		
		this.lock();
		return this;
	}
	
	@Override
	public F get()
	{
		return registerableFluid.getOrThrow();
	}
	
	@Override
	public Fluid asFluid()
	{
		return this.get();
	}
}
