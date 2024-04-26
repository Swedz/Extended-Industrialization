package net.swedz.extended_industrialization.machines.guicomponents.recipeefficiency;

import aztech.modern_industrialization.machines.GuiComponents;
import aztech.modern_industrialization.machines.gui.GuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.machines.components.craft.CrafterAccessWithBehavior;

public final class ModularRecipeEfficiencyBar
{
	public static final class Server implements GuiComponent.Server<Data>
	{
		private final Parameters                params;
		private final CrafterAccessWithBehavior crafter;
		
		public Server(Parameters params, CrafterAccessWithBehavior crafter)
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
	
	private static final class Data
	{
		final boolean hasActiveRecipe;
		final int     efficiencyTicks;
		final int     maxEfficiencyTicks;
		final long    currentRecipeEu;
		final long    baseRecipeEu;
		final long    maxRecipeEu;
		
		private Data()
		{
			this.hasActiveRecipe = false;
			this.efficiencyTicks = 0;
			this.maxEfficiencyTicks = 0;
			this.currentRecipeEu = 0;
			this.baseRecipeEu = 0;
			this.maxRecipeEu = 0;
		}
		
		private Data(int efficiencyTicks, int maxEfficiencyTicks, long currentRecipeEu, long baseRecipeEu, long maxRecipeEu)
		{
			this.efficiencyTicks = efficiencyTicks;
			this.maxEfficiencyTicks = maxEfficiencyTicks;
			this.hasActiveRecipe = true;
			this.currentRecipeEu = currentRecipeEu;
			this.baseRecipeEu = baseRecipeEu;
			this.maxRecipeEu = maxRecipeEu;
		}
	}
	
	public static class Parameters
	{
		public final int renderX, renderY;
		
		public Parameters(int renderX, int renderY)
		{
			this.renderX = renderX;
			this.renderY = renderY;
		}
	}
}
