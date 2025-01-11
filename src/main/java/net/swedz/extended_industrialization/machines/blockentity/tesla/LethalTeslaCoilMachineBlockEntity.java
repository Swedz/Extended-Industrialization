package net.swedz.extended_industrialization.machines.blockentity.tesla;

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
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIClientConfig;
import net.swedz.extended_industrialization.EIDamageTypes;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaArcBehavior;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaArcBehaviorHolder;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaArcs;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaPlasmaBehavior;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaPlasmaBehaviorHolder;
import net.swedz.tesseract.neoforge.capabilities.CapabilitiesListeners;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.slotpanel.ModularSlotPanel;

import java.util.List;
import java.util.Set;

import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.*;

public final class LethalTeslaCoilMachineBlockEntity extends MachineBlockEntity implements Tickable, EnergyComponentHolder, TeslaArcBehaviorHolder, TeslaPlasmaBehaviorHolder
{
	private final IsActiveComponent isActive;
	
	private final RedstoneControlComponent redstoneControl;
	private final CasingComponent          casing;
	
	private final EnergyComponent energy;
	private final MIEnergyStorage insertable;
	
	private final TeslaArcs arcs;
	
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
		
		arcs = new TeslaArcs(
				0.25f, 3, 3, 1, 3, 2, 5,
				() ->
				{
					double radius = 0.35;
					boolean side = TeslaArcs.RANDOM.nextBoolean();
					double x = (side ? radius : radius * TeslaArcs.RANDOM.nextDouble()) * (TeslaArcs.RANDOM.nextBoolean() ? 1 : -1);
					double z = (!side ? radius : radius * TeslaArcs.RANDOM.nextDouble()) * (TeslaArcs.RANDOM.nextBoolean() ? 1 : -1);
					return Vec3.upFromBottomCenterOf(Vec3i.ZERO, 1).add(x, -0.2, z);
				},
				Set.of(Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)
		);
		
		this.registerComponents(isActive, redstoneControl, casing, energy);
		
		this.registerGuiComponent(new EnergyBar.Server(new EnergyBar.Parameters(61, 34), energy::getEu, energy::getCapacity));
		
		this.registerGuiComponent(new ModularSlotPanel.Server(this, 0)
				.withRedstoneModule(redstoneControl)
				.withCasings(casing));
	}
	
	@Override
	public TeslaArcBehavior getTeslaArcBehavior()
	{
		return new TeslaArcBehavior()
		{
			@Override
			public boolean shouldRender()
			{
				return isActive.isActive;
			}
			
			@Override
			public TeslaArcs getArcs()
			{
				return arcs;
			}
		};
	}
	
	@Override
	public TeslaPlasmaBehavior getTeslaPlasmaBehavior()
	{
		return new TeslaPlasmaBehavior()
		{
			@Override
			public boolean shouldRender()
			{
				return isActive.isActive;
			}
			
			@Override
			public Vec3 getOffset()
			{
				return new Vec3(-0.05f / 2f, -0.05f / 2f, -0.05f / 2f);
			}
			
			@Override
			public ResourceLocation getModelLocation()
			{
				return EI.id("tesla_plasma/tesla_coil");
			}
			
			@Override
			public float getModelScale()
			{
				return 1.05f;
			}
			
			@Override
			public float getSpeed()
			{
				return 100f;
			}
			
			@Override
			public float getTextureScale()
			{
				return 32f;
			}
		};
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
	
	private float getDamageAmount()
	{
		// TODO scale damage based on voltage
		return 4;
	}
	
	private long tick;
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			if(EIClientConfig.renderTeslaAnimations)
			{
				arcs.tick();
			}
			return;
		}
		
		boolean active = false;
		
		if(redstoneControl.doAllowNormalOperation(this))
		{
			active = energy.consumeEu(1, Simulation.ACT) > 0;
			if(active && tick++ % 10L == 0)
			{
				var source = EIDamageTypes.tesla(level, worldPosition.getCenter());
				float damage = this.getDamageAmount();
				var entities = level.getEntities(null, this.getDamageArea());
				for(var entity : entities)
				{
					// TODO scale eu cost based on voltage
					if(energy.consumeEu(1, Simulation.ACT) > 0)
					{
						entity.hurt(source, damage);
					}
					else
					{
						break;
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
		return List.of(
				line(EIText.TESLA_LETHAL_COIL_HELP_1).arg(EI.config().lethalTeslaCoil().range()),
				line(EIText.TESLA_LETHAL_COIL_HELP_2)
		);
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		CapabilitiesListeners.register(EI.ID, (event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) -> ((LethalTeslaCoilMachineBlockEntity) be).insertable));
	}
}
