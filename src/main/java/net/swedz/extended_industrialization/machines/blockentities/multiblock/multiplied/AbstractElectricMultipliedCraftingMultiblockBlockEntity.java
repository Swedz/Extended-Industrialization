package net.swedz.extended_industrialization.machines.blockentities.multiblock.multiplied;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.blockentities.hatches.EnergyHatch;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.swedz.extended_industrialization.api.EILubricantHelper;
import net.swedz.extended_industrialization.api.CableTierHolder;
import net.swedz.extended_industrialization.machines.guicomponents.exposecabletier.ExposeCableTierGui;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public abstract class AbstractElectricMultipliedCraftingMultiblockBlockEntity extends AbstractMultipliedCraftingMultiblockBlockEntity implements EnergyListComponentHolder, CableTierHolder
{
	private final MachineTier machineTier;
	
	private final UpgradeComponent         upgrades;
	private final RedstoneControlComponent redstoneControl;
	
	private final List<EnergyComponent> energyInputs = Lists.newArrayList();
	
	private CableTier cableTier = CableTier.LV;
	
	public AbstractElectricMultipliedCraftingMultiblockBlockEntity(BEP bep, String name, ShapeTemplate[] shapeTemplates,
																   MachineTier machineTier)
	{
		super(bep, name, shapeTemplates);
		
		this.machineTier = machineTier;
		
		this.upgrades = new UpgradeComponent();
		this.redstoneControl = new RedstoneControlComponent();
		
		this.registerComponents(upgrades, redstoneControl);
		
		this.registerGuiComponent(new SlotPanel.Server(this)
				.withRedstoneControl(redstoneControl)
				.withUpgrades(upgrades));
		
		this.registerGuiComponent(new ExposeCableTierGui.Server(this));
	}
	
	@Override
	public List<? extends EnergyAccess> getEnergyComponents()
	{
		return energyInputs;
	}
	
	@Override
	public CableTier getCableTier()
	{
		return cableTier;
	}
	
	@Override
	public void onSuccessfulMatch(ShapeMatcher shapeMatcher)
	{
		energyInputs.clear();
		cableTier = CableTier.LV;
		for(HatchBlockEntity hatch : shapeMatcher.getMatchedHatches())
		{
			hatch.appendEnergyInputs(energyInputs);
			
			if(hatch instanceof EnergyHatch)
			{
				CableTierHolder energyHatch = (CableTierHolder) hatch;
				if(cableTier.eu < energyHatch.getCableTier().eu)
				{
					cableTier = energyHatch.getCableTier();
				}
			}
		}
	}
	
	@Override
	protected InteractionResult onUse(Player player, InteractionHand hand, Direction face)
	{
		InteractionResult result = super.onUse(player, hand, face);
		if(!result.consumesAction())
		{
			result = EILubricantHelper.onUse(crafter, player, hand);
		}
		if(!result.consumesAction())
		{
			result = mapComponentOrDefault(UpgradeComponent.class, upgrade -> upgrade.onUse(this, player, hand), result);
		}
		if(!result.consumesAction())
		{
			result = redstoneControl.onUse(this, player, hand);
		}
		return result;
	}
	
	@Override
	public boolean isEnabled()
	{
		return redstoneControl.doAllowNormalOperation(this);
	}
	
	@Override
	public long consumeEu(long max, Simulation simulation)
	{
		long total = 0;
		for(EnergyComponent energyComponent : energyInputs)
		{
			total += energyComponent.consumeEu(max - total, simulation);
		}
		return total;
	}
	
	@Override
	public long getBaseRecipeEu()
	{
		return machineTier.getBaseEu();
	}
	
	@Override
	public long getMaxRecipeEu()
	{
		return machineTier.getMaxEu() + upgrades.getAddMaxEUPerTick();
	}
}
