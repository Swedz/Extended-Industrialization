package net.swedz.extended_industrialization.machines.guicomponents.recipeefficiency;

import aztech.modern_industrialization.machines.GuiComponents;
import aztech.modern_industrialization.machines.gui.GuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.machines.components.craft.ModularCrafterAccess;

public final class ModularRecipeEfficiencyBar
{
	public static final class Server implements GuiComponent.Server<Data>
	{
		private final Parameters           params;
		private final ModularCrafterAccess crafter;
		
		public Server(Parameters params, ModularCrafterAccess crafter)
		{
			this.params = params;
			this.crafter = crafter;
		}
		
		@Override
		public Data copyData()
		{
			if(crafter.hasActiveRecipe())
			{
				return new Data(
						crafter.getEfficiencyTicks(),
						crafter.getMaxEfficiencyTicks(),
						crafter.getCurrentRecipeEu(),
						crafter.getBaseRecipeEu(),
						crafter.getBehavior().getMaxRecipeEu()
				);
			}
			else
			{
				return new Data();
			}
		}
		
		@Override
		public boolean needsSync(Data cachedData)
		{
			if(!crafter.hasActiveRecipe())
			{
				return cachedData.hasActiveRecipe;
			}
			else
			{
				return crafter.getEfficiencyTicks() != cachedData.efficiencyTicks || crafter.getMaxEfficiencyTicks() != cachedData.maxEfficiencyTicks
					   || crafter.getCurrentRecipeEu() != cachedData.currentRecipeEu || crafter.getBaseRecipeEu() != cachedData.baseRecipeEu
					   || crafter.getBehavior().getMaxRecipeEu() != cachedData.maxRecipeEu;
			}
		}
		
		@Override
		public void writeInitialData(FriendlyByteBuf buf)
		{
			buf.writeInt(params.renderX);
			buf.writeInt(params.renderY);
			writeCurrentData(buf);
		}
		
		@Override
		public void writeCurrentData(FriendlyByteBuf buf)
		{
			if(crafter.hasActiveRecipe())
			{
				buf.writeBoolean(true);
				buf.writeInt(crafter.getEfficiencyTicks());
				buf.writeInt(crafter.getMaxEfficiencyTicks());
				buf.writeLong(crafter.getCurrentRecipeEu());
				buf.writeLong(crafter.getBaseRecipeEu());
			}
			else
			{
				buf.writeBoolean(false);
			}
			buf.writeLong(crafter.getBehavior().getMaxRecipeEu());
		}
		
		@Override
		public ResourceLocation getId()
		{
			return GuiComponents.RECIPE_EFFICIENCY_BAR;
		}
	}
	
	private record Data(
			boolean hasActiveRecipe, int efficiencyTicks, int maxEfficiencyTicks, long currentRecipeEu,
			long baseRecipeEu, long maxRecipeEu
	)
	{
		private Data(int efficiencyTicks, int maxEfficiencyTicks, long currentRecipeEu, long baseRecipeEu, long maxRecipeEu)
		{
			this(true, efficiencyTicks, maxEfficiencyTicks, currentRecipeEu, baseRecipeEu, maxRecipeEu);
		}
		
		private Data()
		{
			this(false, 0, 0, 0, 0, 0);
		}
	}
	
	public record Parameters(int renderX, int renderY)
	{
	}
}
