package net.swedz.extended_industrialization.machines.blockentity.tesla;

import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaBehavior;
import net.swedz.extended_industrialization.machines.component.tesla.AestheticTeslaCoilComponent;
import net.swedz.extended_industrialization.machines.component.tesla.ForceHideTeslaComponent;
import net.swedz.extended_industrialization.proxy.EIProxy;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.configurationpanel.ConfigurationPanelBuilder;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.slotpanel.ModularSlotPanel;
import net.swedz.tesseract.neoforge.proxy.Proxies;
import org.joml.Vector3f;

import java.util.List;

public final class TeslaParticleGeneratorMachineBlockEntity extends MachineBlockEntity implements Tickable, TeslaBehavior
{
	private final IsActiveComponent isActive;
	
	private final RedstoneControlComponent redstoneControl;
	
	private final AestheticTeslaCoilComponent aesthetic;
	private final ForceHideTeslaComponent     forceHideRender;
	
	public TeslaParticleGeneratorMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("tesla_particle_generator"), false).build(),
				new OrientationComponent.Params(false, false, false)
		);
		
		isActive = new IsActiveComponent();
		
		redstoneControl = new RedstoneControlComponent();
		
		aesthetic = new AestheticTeslaCoilComponent()
				.with(EI.id("tesla/tesla_particle_generator/small"), EI.text().teslaParticleGeneratorSizeSmall())
				.with(EI.id("tesla/tesla_particle_generator/medium"), EI.text().teslaParticleGeneratorSizeMedium())
				.with(EI.id("tesla/tesla_particle_generator/large"), EI.text().teslaParticleGeneratorSizeLarge())
				.with(EI.id("tesla/tesla_particle_generator/extreme"), EI.text().teslaParticleGeneratorSizeExtreme())
				.with(EI.id("tesla/tesla_particle_generator/immense"), EI.text().teslaParticleGeneratorSizeImmense());
		
		forceHideRender = new ForceHideTeslaComponent();
		
		this.registerComponents(isActive, redstoneControl, aesthetic, forceHideRender);
		
		this.registerGuiComponent(new ModularSlotPanel.Server(this, 0)
				.withRedstoneModule(redstoneControl));
		
		var configPanel = new ConfigurationPanelBuilder(
				EI.text().configurationPanel(),
				EI.text().configurationPanelDescription(),
				(lineIndex, delta) -> this.sync()
		);
		aesthetic.appendSelectionPanel(this, configPanel);
		this.registerGuiComponent(configPanel.build());
	}
	
	@Override
	public boolean shouldTeslaRender()
	{
		return isActive.isActive && !forceHideRender.isHidden();
	}
	
	@Override
	public ResourceLocation getTeslaModelLocation()
	{
		return aesthetic.getSelectedModel();
	}
	
	@Override
	public Vector3f getTeslaColor()
	{
		return aesthetic.getColor();
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData();
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
	public void setRemoved()
	{
		super.setRemoved();
		
		if(level.isClientSide())
		{
			Proxies.get(EIProxy.class).removeTesla(worldPosition);
		}
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			Proxies.get(EIProxy.class).tickTesla(worldPosition);
			return;
		}
		
		isActive.updateActive(redstoneControl.doAllowNormalOperation(this), this);
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
			result = aesthetic.onUse(this, player, hand);
		}
		return result;
	}
	
	@Override
	public List<Component> getTooltips()
	{
		return List.of(
				EI.text().teslaParticleGeneratorHelp()
		);
	}
}
