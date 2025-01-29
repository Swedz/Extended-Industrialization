package net.swedz.extended_industrialization.client.model.tesla;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.IModelBuilder;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import net.swedz.extended_industrialization.EI;
import net.swedz.tesseract.neoforge.api.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public final class TeslaUnbakedModel implements IUnbakedGeometry<TeslaUnbakedModel>
{
	public static final ResourceLocation LOADER_ID                = EI.id("tesla");
	public static final IGeometryLoader<TeslaUnbakedModel> LOADER = (json, context) ->
	{
		Plasma plasma = json.has("plasma") ? Plasma.deserialize(json.getAsJsonObject("plasma"), context) : null;
		Arcs arcs = json.has("arcs") ? Arcs.deserialize(json.getAsJsonObject("arcs"), context) : null;
		return new TeslaUnbakedModel(plasma, arcs);
	};
	
	public record Plasma(
			List<BlockElement> elements,
			Vec3 offset,
			float scale, float speed, float textureScale
	)
	{
		private static Plasma deserialize(JsonObject json, JsonDeserializationContext context)
		{
			Assert.that(json.has("elements"), "A tesla model's plasma must have an \"elements\" member.", JsonParseException::new);
			List<BlockElement> elements = Lists.newArrayList();
			for(JsonElement element : GsonHelper.getAsJsonArray(json, "elements"))
			{
				elements.add(context.deserialize(element, BlockElement.class));
			}
			
			Assert.that(json.has("offset"), "A tesla model's plasma must have an \"offset\" member.", JsonParseException::new);
			Vec3 offset = context.deserialize(json.get("offset"), Vec3.class);
			
			Assert.that(json.has("scale"), "A tesla model's plasma must have a \"scale\" member.", JsonParseException::new);
			float scale = json.get("scale").getAsFloat();
			Assert.that(scale > 0, "A tesla model's plasma must have a > 0 \"scale\" member.", JsonParseException::new);
			
			Assert.that(json.has("speed"), "A tesla model's plasma must have a \"speed\" member.", JsonParseException::new);
			float speed = json.get("speed").getAsFloat();
			Assert.that(speed >= 0, "A tesla model's plasma must have a non-negative \"speed\" member.", JsonParseException::new);
			
			Assert.that(json.has("texture_scale"), "A tesla model's plasma must have a \"texture_scale\" member.", JsonParseException::new);
			float textureScale = json.get("texture_scale").getAsFloat();
			Assert.that(textureScale > 0, "A tesla model's plasma must have a > 0 \"texture_scale\" member.", JsonParseException::new);
			
			return new Plasma(elements, offset, scale, speed, textureScale);
		}
		
		public Vec3 worldOffset(Vec3 machinePosition, Direction machineDirection)
		{
			return convertToWorld(machinePosition, machineDirection, offset);
		}
	}
	
	public record ArcBounds(Vec3 min, Vec3 max)
	{
		private static ArcBounds deserialize(JsonObject json, JsonDeserializationContext context)
		{
			Assert.that(json.has("min") && json.has("max"), "A tesla model's arc bounds must have both a \"min\" and \"max\" member.", JsonParseException::new);
			Vec3 min = context.deserialize(json.get("min"), Vec3.class);
			Vec3 max = context.deserialize(json.get("max"), Vec3.class);
			return new ArcBounds(min, max);
		}
		
		public AABB toAABB()
		{
			return new AABB(min, max);
		}
	}
	
	public record Arcs(
			Optional<ArcBounds> randomBoundsInclude,
			Optional<ArcBounds> randomBoundsExclude,
			int attachToNearbyEntitiesRange,
			List<Vec3> origins,
			float widthScale,
			float minVariance, float maxVariance,
			int duration, int count,
			int minSegments, int maxSegments, int segmentSplits
	)
	{
		private static Arcs deserialize(JsonObject json, JsonDeserializationContext context)
		{
			Optional<ArcBounds> randomBoundsInclude = Optional.empty();
			Optional<ArcBounds> randomBoundsExclude = Optional.empty();
			if(json.has("random_bounds"))
			{
				JsonObject randomBoundsJson = json.getAsJsonObject("random_bounds");
				Assert.that(
						randomBoundsJson.has("include") && randomBoundsJson.has("exclude"),
						"A tesla model's arc random bounds must have both an \"include\" and \"exclude\" member.", JsonParseException::new
				);
				randomBoundsInclude = Optional.of(ArcBounds.deserialize(randomBoundsJson.getAsJsonObject("include"), context));
				randomBoundsExclude = Optional.of(ArcBounds.deserialize(randomBoundsJson.getAsJsonObject("exclude"), context));
			}
			
			int attachToNearbyEntities = 0;
			if(json.has("attach_to_nearby_entities"))
			{
				attachToNearbyEntities = json.get("attach_to_nearby_entities").getAsInt();
				Assert.that(attachToNearbyEntities >= 0, "A tesla model's arcs must have a non-negative \"attach_to_nearby_entities\" member.", JsonParseException::new);
			}
			
			Assert.that(json.has("origins"), "A tesla model's arcs must have an \"origins\" member.", JsonParseException::new);
			List<Vec3> origins = Lists.newArrayList();
			for(JsonElement element : GsonHelper.getAsJsonArray(json, "origins"))
			{
				origins.add(context.deserialize(element, Vec3.class));
			}
			
			Assert.that(json.has("width_scale"), "A tesla model's arcs must have a \"width_scale\" member.", JsonParseException::new);
			float widthScale = json.get("width_scale").getAsFloat();
			
			Assert.that(json.has("min_variance"), "A tesla model's arcs must have a \"min_variance\" member.", JsonParseException::new);
			float minVariance = json.get("min_variance").getAsFloat();
			Assert.that(json.has("max_variance"), "A tesla model's arcs must have a \"max_variance\" member.", JsonParseException::new);
			float maxVariance = json.get("max_variance").getAsFloat();
			
			Assert.that(json.has("duration"), "A tesla model's arcs must have a \"duration\" member.", JsonParseException::new);
			int duration = json.get("duration").getAsInt();
			Assert.that(duration >= 0, "A tesla model's arcs must have a non-negative \"duration\".", JsonParseException::new);
			Assert.that(attachToNearbyEntities == 0 || duration == 0, "A tesla model's arcs must have a \"duration\" = 0 when \"attach_to_nearby_entities\" is > 0.", JsonParseException::new);
			
			Assert.that(json.has("count"), "A tesla model's arcs must have a \"count\" member.", JsonParseException::new);
			int count = json.get("count").getAsInt();
			Assert.that(count > 0, "A tesla model's arcs must have a > 0 \"count\" member.", JsonParseException::new);
			
			Assert.that(json.has("min_segments"), "A tesla model's arcs must have a \"min_segments\" member.", JsonParseException::new);
			int minSegments = json.get("min_segments").getAsInt();
			Assert.that(minSegments > 0, "A tesla model's arcs must have a > 0 \"min_segments\" member.", JsonParseException::new);
			
			Assert.that(json.has("max_segments"), "A tesla model's arcs must have a \"max_segments\" member.", JsonParseException::new);
			int maxSegments = json.get("max_segments").getAsInt();
			Assert.that(maxSegments > 0, "A tesla model's arcs must have a > 0 \"max_segments\" member.", JsonParseException::new);
			
			Assert.that(json.has("segment_splits"), "A tesla model's arcs must have a \"segment_splits\" member.", JsonParseException::new);
			int segmentSplits = json.get("segment_splits").getAsInt();
			Assert.that(segmentSplits > 0, "A tesla model's arcs must have a > 0 \"segment_splits\" member.", JsonParseException::new);
			
			return new Arcs(randomBoundsInclude, randomBoundsExclude, attachToNearbyEntities, origins, widthScale, minVariance, maxVariance, duration, count, minSegments, maxSegments, segmentSplits);
		}
		
		public Arcs
		{
			Assert.that(randomBoundsInclude.isPresent() == randomBoundsExclude.isPresent());
		}
		
		public boolean hasRandomBounds()
		{
			return randomBoundsInclude.isPresent();
		}
		
		public AABB worldIncludeBounds(Vec3 machinePosition, Direction machineDirection)
		{
			return convertToWorld(machinePosition, machineDirection, randomBoundsInclude.orElseThrow().toAABB());
		}
		
		public AABB worldExcludeBounds(Vec3 machinePosition, Direction machineDirection)
		{
			return convertToWorld(machinePosition, machineDirection, randomBoundsExclude.orElseThrow().toAABB());
		}
		
		// TODO this is not a very elegant solution ... should be fixed up
		public Vec3 worldRandomPointInBounds(Random random, Vec3 machinePosition, Direction machineDirection)
		{
			Assert.that(this.hasRandomBounds());
			
			AABB include = this.worldIncludeBounds(machinePosition, machineDirection);
			AABB exclude = this.worldExcludeBounds(machinePosition, machineDirection);
			
			while(true)
			{
				double randomX = random.nextDouble(include.minX, include.maxX);
				double randomY = random.nextDouble(include.minY, include.maxY);
				double randomZ = random.nextDouble(include.minZ, include.maxZ);
				var randomPos = new Vec3(randomX, randomY, randomZ);
				if(!exclude.contains(randomPos))
				{
					return randomPos;
				}
			}
		}
		
		public boolean attachesToNearbyEntities()
		{
			return attachToNearbyEntitiesRange > 0;
		}
		
		public AABB worldNearbyEntitiesBounds(Vec3 machinePosition, Direction machineDirection)
		{
			int range = attachToNearbyEntitiesRange;
			return convertToWorld(machinePosition, machineDirection, new AABB(
					new Vec3(-range, -range, -range).subtract(0.5, 0.5, 0.5),
					new Vec3(range, range, range).add(0.5, 0.5, 0.5)
			));
		}
		
		public List<Vec3> worldOrigins(Vec3 machinePosition, Direction machineDirection)
		{
			List<Vec3> converted = Lists.newArrayList();
			for(Vec3 origin : origins)
			{
				converted.add(convertToWorld(machinePosition, machineDirection, origin));
			}
			return Collections.unmodifiableList(converted);
		}
		
		public float randomVariance(Random random)
		{
			return minVariance == maxVariance ? minVariance : random.nextFloat(minVariance, maxVariance);
		}
	}
	
	private static Vec3 convertToWorld(Vec3 machinePosition, Direction machineDirection, Vec3 position)
	{
		Vec3 rotatedPos = switch (machineDirection)
		{
			case NORTH -> new Vec3(-position.x(), position.y(), position.z());
			case SOUTH -> new Vec3(position.x(), position.y(), -position.z());
			case EAST -> new Vec3(-position.z(), position.y(), -position.x());
			case WEST -> new Vec3(position.z(), position.y(), position.x());
			default ->
					throw new IllegalArgumentException("Unsupported machine direction: %s".formatted(machineDirection.toString()));
		};
		return rotatedPos.add(machinePosition);
	}
	
	private static AABB convertToWorld(Vec3 machinePosition, Direction machineDirection, AABB box)
	{
		var min = convertToWorld(machinePosition, machineDirection, box.getMinPosition());
		var max = convertToWorld(machinePosition, machineDirection, box.getMaxPosition());
		return new AABB(min, max);
	}
	
	private final Plasma plasma;
	private final Arcs   arcs;
	
	private TeslaUnbakedModel(Plasma plasma, Arcs arcs)
	{
		this.plasma = plasma;
		this.arcs = arcs;
	}
	
	private void addQuads(IGeometryBakingContext context, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState)
	{
		var postTransform = QuadTransformers.empty();
		var rootTransform = context.getRootTransform();
		if(!rootTransform.isIdentity())
		{
			postTransform = UnbakedGeometryHelper.applyRootTransform(modelState, rootTransform);
		}
		
		if(plasma != null)
		{
			for(BlockElement element : plasma.elements())
			{
				for(Direction direction : element.faces.keySet())
				{
					var face = element.faces.get(direction);
					var sprite = spriteGetter.apply(context.getMaterial(face.texture()));
					var quad = BlockModel.bakeFace(element, face, sprite, direction, modelState);
					postTransform.processInPlace(quad);
					
					if(face.cullForDirection() == null)
					{
						modelBuilder.addUnculledFace(quad);
					}
					else
					{
						modelBuilder.addCulledFace(modelState.getRotation().rotateTransform(face.cullForDirection()), quad);
					}
				}
			}
		}
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter,
						   ModelState modelState, ItemOverrides overrides)
	{
		List<BakedQuad> unculledFaces = Lists.newArrayList();
		Map<Direction, List<BakedQuad>> culledFaces = Maps.newEnumMap(Direction.class);
		for(Direction direction : Direction.values())
		{
			culledFaces.put(direction, Lists.newArrayList());
		}
		IModelBuilder<?> builder = new IModelBuilder()
		{
			@Override
			public IModelBuilder addCulledFace(Direction facing, BakedQuad quad)
			{
				culledFaces.get(facing).add(quad);
				return this;
			}
			
			@Override
			public IModelBuilder addUnculledFace(BakedQuad quad)
			{
				unculledFaces.add(quad);
				return this;
			}
			
			@Override
			public BakedModel build()
			{
				return new TeslaBakedModel(unculledFaces, culledFaces, plasma, arcs);
			}
		};
		this.addQuads(context, builder, baker, spriteGetter, modelState);
		return builder.build();
	}
}
