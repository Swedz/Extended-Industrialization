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
import aztech.modern_industrialization.util.Simulation;
import aztech.modern_industrialization.util.Tickable;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIDamageTypes;
import net.swedz.extended_industrialization.EISounds;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.EITooltips;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaBehavior;
import net.swedz.extended_industrialization.network.packet.EntitiesElectrocutedPacket;
import net.swedz.extended_industrialization.proxy.EIProxy;
import net.swedz.tesseract.neoforge.capabilities.CapabilitiesListeners;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.slotpanel.ModularSlotPanel;
import net.swedz.tesseract.neoforge.compat.mi.tooltip.MIParser;
import net.swedz.tesseract.neoforge.proxy.Proxies;

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
		
		this.registerComponents(isActive, redstoneControl, casing, energy);
		
		this.registerGuiComponent(new EnergyBar.Server(new EnergyBar.Parameters(81, 34), energy::getEu, energy::getCapacity));
		
		this.registerGuiComponent(new ModularSlotPanel.Server(this, 0)
				.withRedstoneModule(redstoneControl)
				.withCasings(casing));
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
	
	private AABB getDamageArea()
	{
		int range = EI.config().lethalTeslaCoil().range();
		var center = worldPosition.getCenter();
		return new AABB(
				center.subtract(range, range, range),
				center.add(range, range, range)
		);
	}
	
	private List<Entity> getEntitiesInDamageArea()
	{
		return level.getEntities(
				(Entity) null,
				this.getDamageArea(),
				(entity) -> entity.isAlive() && entity instanceof LivingEntity && !(entity instanceof Player)
		);
	}
	
	private long tick;
	
	private boolean buzzing;
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			var proxy = Proxies.get(EIProxy.class);
			
			proxy.tickTesla(worldPosition);
			
			if(!buzzing && isActive.isActive)
			{
				var entities = this.getEntitiesInDamageArea();
				if(!entities.isEmpty())
				{
					buzzing = true;
					proxy.startTeslaCoilLoopSound(
							worldPosition, EISounds.TESLA_COIL_LOOP.get(), SoundSource.BLOCKS,
							() -> this.isRemoved() || !isActive.isActive || this.getEntitiesInDamageArea().isEmpty(),
							() -> 1f,
							() -> buzzing = false
					);
				}
			}
			return;
		}
		
		boolean active = false;
		
		if(redstoneControl.doAllowNormalOperation(this))
		{
			float damage = getDamageAmount(casing.getCableTier());
			if(damage > 0)
			{
				long energyCost = getEnergyCost(casing.getCableTier());
				active = energy.consumeEu(energyCost, Simulation.SIMULATE) == energyCost;
				if(active && tick++ % DAMAGE_INTERVAL == 0)
				{
					energy.consumeEu(energyCost, Simulation.ACT);
					var source = EIDamageTypes.tesla(level, worldPosition.getCenter());
					var entities = this.getEntitiesInDamageArea();
					if(!entities.isEmpty())
					{
						var entityIds = new IntArrayList();
						for(var entity : entities)
						{
							entity.hurt(source, damage);
							entityIds.add(entity.getId());
						}
						new EntitiesElectrocutedPacket(worldPosition, entityIds).broadcastToClients((ServerLevel) level, worldPosition, 32);
					}
				}
			}
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
