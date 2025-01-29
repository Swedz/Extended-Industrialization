package net.swedz.extended_industrialization.machines.blockentity.tesla;

import aztech.modern_industrialization.MITooltips;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyComponentHolder;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import com.google.common.collect.Lists;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EISounds;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.EITooltips;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaBehavior;
import net.swedz.extended_industrialization.machines.component.tesla.AestheticTeslaCoilComponent;
import net.swedz.extended_industrialization.machines.component.tesla.LethalTeslaCoilComponent;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaBuzzingComponent;
import net.swedz.extended_industrialization.proxy.EIProxy;
import net.swedz.tesseract.neoforge.capabilities.CapabilitiesListeners;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.configurationpanel.ConfigurationPanelBuilder;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.slotpanel.ModularSlotPanel;
import net.swedz.tesseract.neoforge.compat.mi.tooltip.MIParser;
import net.swedz.tesseract.neoforge.proxy.Proxies;
import org.joml.Vector3f;

import java.util.List;

import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.*;

public final class LethalTeslaCoilMachineBlockEntity extends MachineBlockEntity implements Tickable, EnergyComponentHolder, TeslaBehavior
{
	private static final long DAMAGE_INTERVAL = 20L;
	
	public static long getEnergyCost(CableTier tier)
	{
		return tier.eu / 8L;
	}
	
	public static float getDamageAmount(CableTier tier)
	{
		return (float) Mth.clamp(EI.config().lethalTeslaCoil().damage().get(tier), 0, Integer.MAX_VALUE);
	}
	
	private final IsActiveComponent isActive;
	
	private final RedstoneControlComponent redstoneControl;
	private final CasingComponent          casing;
	
	private final EnergyComponent energy;
	private final MIEnergyStorage insertable;
	
	private final LethalTeslaCoilComponent    lethal;
	private final TeslaBuzzingComponent       buzzing;
	private final AestheticTeslaCoilComponent aesthetic;
	
	public LethalTeslaCoilMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("lethal_tesla_coil"), false).build(),
				new OrientationComponent.Params(false, false, false)
		);
		
		isActive = new IsActiveComponent();
		
		redstoneControl = new RedstoneControlComponent();
		casing = new CasingComponent();
		
		energy = new EnergyComponent(this, () -> 30 * 20 * casing.getCableTier().eu);
		insertable = energy.buildInsertable(casing::canInsertEu);
		
		lethal = new LethalTeslaCoilComponent(
				this,
				() -> getDamageAmount(casing.getCableTier()),
				energy,
				() -> getEnergyCost(casing.getCableTier()),
				() -> EI.config().lethalTeslaCoil().range(),
				() -> DAMAGE_INTERVAL
		);
		buzzing = new TeslaBuzzingComponent(
				this,
				EISounds.TESLA_COIL_LOOP.get(), SoundSource.BLOCKS,
				lethal::hasNearbyEntities,
				() -> 1f
		);
		aesthetic = new AestheticTeslaCoilComponent();
		
		this.registerComponents(isActive, redstoneControl, casing, energy, lethal, buzzing, aesthetic);
		
		this.registerGuiComponent(new EnergyBar.Server(new EnergyBar.Parameters(81, 34), energy::getEu, energy::getCapacity));
		
		this.registerGuiComponent(new ModularSlotPanel.Server(this, 0)
				.withRedstoneModule(redstoneControl)
				.withCasings(casing));
		
		var configPanel = new ConfigurationPanelBuilder(
				EIText.CONFIGURATION_PANEL.text(),
				EIText.CONFIGURATION_PANEL_DESCRIPTION.text().withStyle(MITooltips.DEFAULT_STYLE.withItalic(true)),
				(lineIndex, delta) -> this.sync()
		);
		aesthetic.appendSelectionPanel(this, configPanel);
		this.registerGuiComponent(configPanel.build());
	}
	
	@Override
	public boolean shouldTeslaRender()
	{
		return isActive.isActive;
	}
	
	@Override
	public ResourceLocation getTeslaModelLocation()
	{
		return EI.id("tesla/lethal_tesla_coil");
	}
	
	@Override
	public Vector3f getTeslaColor()
	{
		return aesthetic.getColor();
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData(casing.getCasing());
		data.isActive = isActive.isActive;
		orientation.writeModelData(data);
		return data;
	}
	
	@Override
	public MIInventory getInventory()
	{
		return MIInventory.EMPTY;
	}
	
	@Override
	public EnergyAccess getEnergyComponent()
	{
		return energy;
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			Proxies.get(EIProxy.class).tickTesla(worldPosition);
			buzzing.tick();
			return;
		}
		
		boolean active = false;
		
		if(redstoneControl.doAllowNormalOperation(this))
		{
			active = lethal.tick();
		}
		
		isActive.updateActive(active, this);
	}
	
	@Override
	protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face)
	{
		ItemInteractionResult result = super.useItemOn(player, hand, face);
		if(!result.consumesAction())
		{
			result = redstoneControl.onUse(this, player, hand);
		}
		if(!result.consumesAction())
		{
			result = casing.onUse(this, player, hand);
		}
		if(!result.consumesAction())
		{
			result = aesthetic.onUse(this, player, hand);
		}
		return result;
	}
	
	@Override
	public List<Component> getTooltips()
	{
		List<Component> lines = Lists.newArrayList();
		lines.add(line(EIText.TESLA_LETHAL_COIL_HELP_1).arg(EI.config().lethalTeslaCoil().range()));
		lines.add(line(EIText.TESLA_LETHAL_COIL_VALUES));
		for(CableTier tier : CableTier.allTiers())
		{
			lines.add(line(EIText.TESLA_LETHAL_COIL_VOLTAGE_VALUE)
					.arg(tier, MIParser.CABLE_TIER_SHORT.withStyle(MITooltips.HIGHLIGHT_STYLE))
					.arg(getDamageAmount(tier), EITooltips.DAMAGE_PARSER)
					.arg(getEnergyCost(tier), MITooltips.EU_PARSER));
		}
		return lines;
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		CapabilitiesListeners.register(EI.ID, (event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) -> ((LethalTeslaCoilMachineBlockEntity) be).insertable));
	}
}
