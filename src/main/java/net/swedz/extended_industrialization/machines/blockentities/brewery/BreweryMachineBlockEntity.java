package net.swedz.extended_industrialization.machines.blockentities.brewery;

import aztech.modern_industrialization.api.machine.component.CrafterAccess;
import aztech.modern_industrialization.api.machine.holder.CrafterComponentHolder;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.MachineInventoryComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.AutoExtract;
import aztech.modern_industrialization.machines.guicomponents.ProgressBar;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.swedz.extended_industrialization.machines.components.craft.ModularCrafterAccessBehavior;
import net.swedz.extended_industrialization.machines.components.craft.potion.PotionCrafterComponent;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class BreweryMachineBlockEntity extends MachineBlockEntity implements Tickable, CrafterComponentHolder, ModularCrafterAccessBehavior
{
	protected static final int STEAM_SLOT_X = 143;
	protected static final int STEAM_SLOT_Y = 27;
	
	protected static final int BLAZING_ESSENCE_SLOT_X = 8;
	protected static final int BLAZING_ESSENCE_SLOT_Y = 27;
	
	protected static final int WATER_SLOT_X = 26;
	protected static final int WATER_SLOT_Y = 27;
	
	protected static final int INPUT_BOTTLE_SLOTS_X = 8;
	protected static final int INPUT_BOTTLE_SLOTS_Y = 47;
	
	protected static final int INPUT_REAGENT_SLOTS_X = 53;
	protected static final int INPUT_REAGENT_SLOTS_Y = 27;
	
	protected static final int OUTPUT_SLOTS_X = 116;
	protected static final int OUTPUT_SLOTS_Y = 47;
	
	protected static final int PROGRESS_BAR_X = 78;
	protected static final int PROGRESS_BAR_Y = 63;
	
	protected final MachineTier tier;
	protected final int         capacity;
	
	protected final MachineInventoryComponent inventory;
	protected final PotionCrafterComponent    crafter;
	
	protected IsActiveComponent isActiveComponent;
	
	public BreweryMachineBlockEntity(BEP bep, String blockName, MachineTier tier, int capacity)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(blockName, true).backgroundHeight(206).build(),
				new OrientationComponent.Params(true, true, false)
		);
		
		this.tier = tier;
		this.capacity = capacity;
		
		Pair<MachineInventoryComponent, PotionCrafterComponent.Params> builtInventory = this.buildInventory();
		this.inventory = builtInventory.getFirst();
		this.crafter = new PotionCrafterComponent(builtInventory.getSecond(), inventory, this);
		
		this.isActiveComponent = new IsActiveComponent();
		this.registerGuiComponent(new ProgressBar.Server(
				new ProgressBar.Parameters(PROGRESS_BAR_X, PROGRESS_BAR_Y, "triple_arrow"),
				crafter::getProgress
		));
		
		this.registerComponents(isActiveComponent, inventory, crafter);
		
		this.registerGuiComponent(new AutoExtract.Server(orientation));
	}
	
	protected abstract Pair<MachineInventoryComponent, PotionCrafterComponent.Params> buildInventory();
	
	@Override
	public MIInventory getInventory()
	{
		return inventory.inventory;
	}
	
	@Override
	public CrafterAccess getCrafterComponent()
	{
		return crafter;
	}
	
	@Override
	public Level getCrafterWorld()
	{
		return level;
	}
	
	@Override
	public UUID getOwnerUuid()
	{
		return placedBy.placerId;
	}
	
	@Override
	public long getBaseRecipeEu()
	{
		return tier.getBaseEu();
	}
	
	@Override
	public long getMaxRecipeEu()
	{
		return tier.getMaxEu();
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData();
		data.isActive = isActiveComponent.isActive;
		orientation.writeModelData(data);
		return data;
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide)
		{
			return;
		}
		
		this.updateActive(crafter.tickRecipe());
		
		if(orientation.extractItems)
		{
			this.getInventory().autoExtractItems(level, worldPosition, orientation.outputDirection);
		}
		this.setChanged();
	}
	
	private void updateActive(boolean active)
	{
		isActiveComponent.updateActive(active, this);
	}
	
	@Override
	public List<Component> getTooltips()
	{
		return Collections.emptyList();
	}
}