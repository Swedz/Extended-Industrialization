package net.swedz.extended_industrialization;

import aztech.modern_industrialization.api.energy.CableTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.material.Fluid;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.tesseract.neoforge.api.WorldPos;
import net.swedz.tesseract.neoforge.compat.mi.component.craft.multiplied.EuCostTransformer;
import net.swedz.tesseract.neoforge.lang.annotation.LangKey;
import net.swedz.tesseract.neoforge.lang.annotation.Parsed;
import net.swedz.tesseract.neoforge.lang.annotation.ParsedDecimal;
import net.swedz.tesseract.neoforge.lang.annotation.WithStyle;
import net.swedz.tesseract.neoforge.tooltip.Parser;

public interface EIText
{
	@LangKey(text = "Activated")
	@WithStyle("green")
	MutableComponent activated();
	
	@LangKey(text = "Blazing Essence is used in the brewery to brew potions.")
	MutableComponent blazingEssenceUses1();
	
	@LangKey(text = "1mb of Blazing Essence is used every time the brewery brews a set of potions.")
	MutableComponent blazingEssenceUses2();
	
	@LangKey(text = "Brews %s potions at a time.")
	@WithStyle("tooltip")
	MutableComponent breweryBrewsMultiple(
			@WithStyle("highlighted") int amount
	);
	
	@LangKey(text = "Requires %s to brew potions.")
	@WithStyle("tooltip")
	MutableComponent breweryRequiresBlazingEssence(
			Fluid fluid
	);
	
	@LangKey(text = "Calcification: %s %%")
	MutableComponent calcificationPercentage(
			int percentage
	);
	
	@LangKey(text = "Runs LEF in batches of up to %s at %s the EU cost.")
	@WithStyle("tooltip")
	MutableComponent coilsLEFTier(
			@WithStyle("highlighted") int batchSize,
			@Parsed("percentage") @WithStyle("highlighted") float euCostMultiplier
	);
	
	@LangKey(text = "Blue: ")
	MutableComponent colorBlue();
	
	@LangKey(text = "Green: ")
	MutableComponent colorGreen();
	
	@LangKey(text = "Red: ")
	MutableComponent colorRed();
	
	@LangKey(text = "The block at %s is not a network part.")
	MutableComponent commandTeslaNetworkDumpCantHaveNetwork(
			BlockPos pos
	);
	
	@LangKey(text = "Chunk at %s is not loaded.")
	MutableComponent commandTeslaNetworkDumpChunkNotLoaded();
	
	@LangKey(text = "No existing network could be found for %s.")
	MutableComponent commandTeslaNetworkDumpNoNetwork(
			WorldPos pos
	);
	
	@LangKey(text = "Dumping data on network of %s:")
	MutableComponent commandTeslaNetworkDumpResult1(
			BlockPos pos
	);
	
	@LangKey(text = "* Key: %s")
	MutableComponent commandTeslaNetworkDumpResult2(
			WorldPos pos
	);
	
	@LangKey(text = "* Transmitter: %s")
	MutableComponent commandTeslaNetworkDumpResult3(
			Component text
	);
	
	@LangKey(text = "* Receiver Count: %s / %s (linked / loaded)")
	MutableComponent commandTeslaNetworkDumpResult4(
			int count,
			int loadedCount
	);
	
	@LangKey(text = "Not loaded")
	MutableComponent commandTeslaNetworkDumpResultNoTransmitter();
	
	@LangKey(text = "%s\n  * Ticking: %s\n  * Voltage: %s")
	MutableComponent commandTeslaNetworkDumpResultYesTransmitter(
			WorldPos pos,
			boolean ticking,
			Component text
	);
	
	@LangKey(text = "Configure")
	MutableComponent configurationPanel();
	
	@LangKey(text = "Click to open machine configuration panel.")
	@WithStyle("tooltip_subtext")
	MutableComponent configurationPanelDescription();
	
	@LangKey(text = "%s \u2764")
	MutableComponent damage(
			Component text
	);
	
	default MutableComponent damage(
			int amount
	)
	{
		return this.damage(Component.literal(String.valueOf(amount)));
	}
	
	default MutableComponent damage(
			float amount
	)
	{
		return this.damage(Parser.FLOAT.parse(amount, 1));
	}
	
	@LangKey(text = "\u221E")
	@WithStyle("highlighted")
	MutableComponent infinity();
	
	@LangKey(text = "Deactivated")
	@WithStyle("red")
	MutableComponent deactivated();
	
	@LangKey(text = "- Can be dyed and trimmed!")
	@WithStyle("tooltip")
	MutableComponent dyeableAndTrimmableHelp();
	
	@LangKey(text = "- Can be dyed!")
	@WithStyle("tooltip")
	MutableComponent dyeableHelp();
	
	@LangKey(text = "Disabled 3x3 Mining")
	MutableComponent electricTool3By3ToggledOff();
	
	@LangKey(text = "Enabled 3x3 Mining")
	MutableComponent electricTool3By3ToggledOn();
	
	@LangKey(text = "Tool configuration:")
	@WithStyle("tooltip")
	MutableComponent electricToolHelp1();
	
	@LangKey(text = "- Press %s + %s to swap between Fortune/Looting and Silk Touch.")
	@WithStyle("tooltip")
	MutableComponent electricToolHelp2FortuneLooting(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "- Press %s + %s to swap between Fortune and Silk Touch.")
	@WithStyle("tooltip")
	MutableComponent electricToolHelp2FortuneSilkTouch(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "- Press %s + %s to swap between Looting and Beheading.")
	@WithStyle("tooltip")
	MutableComponent electricToolHelp2LootingBeheading(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "- Use %s + %s to change mining speed.")
	@WithStyle("tooltip")
	MutableComponent electricToolHelp3(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "- Press %s while held or %s while hovered to toggle 3x3 mining.")
	@WithStyle("tooltip")
	MutableComponent electricToolHelp4(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "Insert an enchantment module to make the machine use the enchantment.")
	@WithStyle("tooltip")
	MutableComponent enchantmentModuleInput();
	
	@LangKey(text = "Can be used in the %s.")
	@WithStyle("tooltip")
	MutableComponent enchantmentModuleMachine(
			@Parsed("block") @WithStyle("highlighted") ResourceLocation blockId
	);
	
	record EnchantmentWithLevelField(
			HolderLookup.Provider registry,
			ResourceKey<Enchantment> enchantment,
			int level
	)
	{
	}
	
	@LangKey(text = "Applies %s in the machine for %s.")
	@WithStyle("tooltip")
	@Deprecated
	MutableComponent enchantmentModuleSingleValue(
			@WithStyle("highlighted") EnchantmentWithLevelField enchantment,
			@Parsed("eu_per_tick") @WithStyle("highlighted") long euPerTick
	);
	
	default MutableComponent enchantmentModuleSingleValue(
			HolderLookup.Provider registry,
			ResourceKey<Enchantment> enchantment,
			int level,
			long euPerTick
	)
	{
		return this.enchantmentModuleSingleValue(new EnchantmentWithLevelField(registry, enchantment, level), euPerTick);
	}
	
	record EnchantmentField(
			HolderLookup.Provider registry,
			ResourceKey<Enchantment> enchantment
	)
	{
	}
	
	@LangKey(text = "Voltage determines the level of %s applied in the machine.")
	@WithStyle("tooltip")
	@Deprecated
	MutableComponent enchantmentModuleValues(
			@WithStyle("highlighted") EnchantmentField enchantment
	);
	
	default MutableComponent enchantmentModuleValues(
			HolderLookup.Provider registry,
			ResourceKey<Enchantment> enchantment
	)
	{
		return this.enchantmentModuleValues(new EnchantmentField(registry, enchantment));
	}
	
	@LangKey(text = "Not Tilling")
	MutableComponent farmerNotTilling();
	
	@LangKey(text = "Alternating Lines")
	MutableComponent farmerPlantingAlternatingLines();
	
	@LangKey(text = "As Needed")
	MutableComponent farmerPlantingAsNeeded();
	
	@LangKey(text = "Quadrants")
	MutableComponent farmerPlantingQuadrants();
	
	@LangKey(text = "  - %s: %s")
	@WithStyle("tooltip")
	MutableComponent farmerTask(
			@WithStyle("highlighted") Component name,
			Component description
	);
	
	@LangKey(text = "Fertilizing")
	MutableComponent farmerTaskFertilizing();
	
	@LangKey(text = "When supplied with a valid fluid fertilizer, it will randomly bonemeal crops and saplings.")
	MutableComponent farmerTaskFertilizingDescription();
	
	@LangKey(text = "Harvesting")
	MutableComponent farmerTaskHarvesting();
	
	@LangKey(text = "When there is enough output space provided, it will harvest fully grown crops and trees.")
	MutableComponent farmerTaskHarvestingDescription();
	
	@LangKey(text = "Hydrating")
	MutableComponent farmerTaskHydrating();
	
	@LangKey(text = "When supplied with water, tilled soil will be hydrated.")
	MutableComponent farmerTaskHydratingDescription();
	
	@LangKey(text = "Planting")
	MutableComponent farmerTaskPlanting();
	
	@LangKey(text = "When supplied with crops or saplings, it will plant them on valid soil. Using different planting modes will plant them in different arrangements.")
	MutableComponent farmerTaskPlantingDescription();
	
	@LangKey(text = "Tilling")
	MutableComponent farmerTaskTilling();
	
	@LangKey(text = "When enabled, dirt blocks will be turned into farmland. This will not work unless water is supplied.")
	MutableComponent farmerTaskTillingDescription();
	
	@LangKey(text = "Can perform the following tasks using %s:")
	@WithStyle("tooltip")
	MutableComponent farmerTaskTooltip(
			@Parsed("eu_per_tick") @WithStyle("highlighted") long euPerTick
	);
	
	@LangKey(text = "Tilling")
	MutableComponent farmerTilling();
	
	@LangKey(text = "Fluid Fertilizers")
	MutableComponent fluidFertilizers();
	
	@LangKey(text = "Consumes: %smb")
	MutableComponent fluidFertilizersConsumes(
			int mb
	);
	
	@LangKey(text = "Cycle Time: %ss")
	MutableComponent fluidFertilizersTime(
			@ParsedDecimal(2) float seconds
	);
	
	@LangKey(text = "Generating: %s EU/t")
	MutableComponent generatingEuPerTick(
			long amount
	);
	
	@LangKey(text = "When placed facing into a beehive, honey will be extracted in fluid form.")
	@WithStyle("tooltip")
	MutableComponent honeyExtractorHelp();
	
	@LangKey(text = "Alt")
	MutableComponent keyAlt();
	
	@LangKey(text = "Mouse Scroll")
	MutableComponent keyMouseScroll();
	
	@LangKey(text = "Batch size and cost is determined by coil used.")
	@WithStyle("tooltip")
	MutableComponent machineBatcherCoils();
	
	@LangKey(text = "Connected Machines: %s / %s")
	MutableComponent machineChainerConnectedMachines(
			int machineCount,
			int maxMachines
	);
	
	@LangKey(text = "Connects up to %s consecutive machines in a straight line in the direction it is facing.")
	@WithStyle("tooltip")
	MutableComponent machineChainerHelp1(
			@WithStyle("highlighted") int maxConnections
	);
	
	@LangKey(text = "Accepts items, fluids, and energy and distributes them to connected machines.")
	@WithStyle("tooltip")
	MutableComponent machineChainerHelp2();
	
	@LangKey(text = "Can connect to other machine chainers, but it must not link back to itself.")
	@WithStyle("tooltip")
	MutableComponent machineChainerHelp3();
	
	@LangKey(text = "Problem at: %s")
	MutableComponent machineChainerProblemAt(
			BlockPos pos
	);
	
	@LangKey(text = "Failed to apply machine configuration to machine.")
	MutableComponent machineConfigCardApplyFailed();
	
	@LangKey(text = "Applied machine configuration to machine from card.")
	MutableComponent machineConfigCardApplySuccess();
	
	@LangKey(text = "Cleared machine configuration from card.")
	MutableComponent machineConfigCardClear();
	
	@LangKey(text = "Configured (%s)")
	MutableComponent machineConfigCardConfigured(
			Item item
	);
	
	@LangKey(text = "- Press %s + %s on a machine to save its settings in the card.")
	@WithStyle("tooltip")
	MutableComponent machineConfigCardHelp1(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "- Use %s on a machine to apply the settings from the card.")
	@WithStyle("tooltip")
	MutableComponent machineConfigCardHelp2(
			@Parsed("keybind") @WithStyle("highlighted") String keybind
	);
	
	@LangKey(text = "- (Optional) Hold in off-hand when placing machines to automatically apply settings.")
	@WithStyle("tooltip")
	MutableComponent machineConfigCardHelp3();
	
	@LangKey(text = "- Clear using %s + %s on air.")
	@WithStyle("tooltip")
	MutableComponent machineConfigCardHelp4(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "Saved machine configuration to card.")
	MutableComponent machineConfigCardSave();
	
	@LangKey(text = "Manure can be collected by placing a Waste Collector underneath an animal.")
	MutableComponent manureObtaining();
	
	@LangKey(text = "Meow :3")
	@WithStyle("tooltip_subtext")
	MutableComponent meow();
	
	@LangKey(text = "I love mulch!")
	@WithStyle("tooltip_subtext")
	MutableComponent mulchGangForLife0();
	
	@LangKey(text = "Mulch is my favorite food!")
	@WithStyle("tooltip_subtext")
	MutableComponent mulchGangForLife1();
	
	@LangKey(text = "- Press %s to make a long ranged sweep attack.")
	@WithStyle("tooltip")
	MutableComponent nanoSaberHelp(
			@Parsed("keybind") @WithStyle("highlighted") String keybind
	);
	
	@LangKey(text = "Creative Flight: %s")
	@WithStyle("tooltip")
	MutableComponent nanoSuitCreativeFlight(
			@Parsed("activated") boolean activated
	);
	
	@LangKey(text = "Armor information:")
	@WithStyle("tooltip")
	MutableComponent nanoSuitHelp1();
	
	@LangKey(text = "- Press %s while equipped or %s while hovered to toggle Creative Flight.")
	@WithStyle("tooltip")
	MutableComponent nanoSuitHelpCreativeFlight(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "- Press %s while equipped or %s while hovered to toggle Night Vision.")
	@WithStyle("tooltip")
	MutableComponent nanoSuitHelpNightVision(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "- Press %s while equipped or %s while hovered to toggle the Speed Boost.")
	@WithStyle("tooltip")
	MutableComponent nanoSuitHelpSpeed(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "- Press %s while equipped or %s while hovered to toggle the Step Boost.")
	@WithStyle("tooltip")
	MutableComponent nanoSuitHelpStep(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "Night Vision: %s")
	@WithStyle("tooltip")
	MutableComponent nanoSuitNightVision(
			@Parsed("activated") boolean activated
	);
	
	@LangKey(text = "Disabled Night Vision")
	MutableComponent nanoSuitNightVisionToggledOff();
	
	@LangKey(text = "Enabled Night Vision")
	MutableComponent nanoSuitNightVisionToggledOn();
	
	@LangKey(text = "Speed: %s")
	@WithStyle("tooltip")
	MutableComponent nanoSuitSpeed(
			@Parsed("activated") boolean activated
	);
	
	@LangKey(text = "Disabled Speed Boost")
	MutableComponent nanoSuitSpeedToggledOff();
	
	@LangKey(text = "Enabled Speed Boost")
	MutableComponent nanoSuitSpeedToggledOn();
	
	@LangKey(text = "Step: %s")
	@WithStyle("tooltip")
	MutableComponent nanoSuitStep(
			@Parsed("activated") boolean activated
	);
	
	@LangKey(text = "Disabled Step Boost")
	MutableComponent nanoSuitStepToggledOff();
	
	@LangKey(text = "Enabled Step Boost")
	MutableComponent nanoSuitStepToggledOn();
	
	@LangKey(text = "Will produce up to %s when placed in a Solar Panel.")
	@WithStyle("tooltip")
	MutableComponent photovoltaicCellEU(
			@Parsed("eu_per_tick") @WithStyle("highlighted") long euPerTick
	);
	
	@LangKey(text = "Remaining Operation Time: %s")
	@WithStyle("tooltip")
	MutableComponent photovoltaicCellRemainingOperationTime(
			@WithStyle("highlighted") Component text
	);
	
	@LangKey(text = "Remaining Operation Time: %s minute(s)")
	@WithStyle("tooltip")
	MutableComponent photovoltaicCellRemainingOperationTimeMinutes(
			@Parsed("ticks_to_minutes") @WithStyle("highlighted") long ticks
	);
	
	@LangKey(text = "Priority: %s")
	MutableComponent priority(
			int priority
	);
	
	@LangKey(text = "Batch size is determined by the amount of machines provided to it.")
	@WithStyle("tooltip")
	MutableComponent processingArrayBatchSize();
	
	@LangKey(text = "Runs at %s the EU cost.")
	@WithStyle("tooltip")
	MutableComponent processingArrayEuCostMultiplier(
			@WithStyle("highlighted") EuCostTransformer euCostTransformer
	);
	
	@LangKey(text = "Insert electric crafting machines to run in parallel.")
	@WithStyle("tooltip")
	MutableComponent processingArrayMachineInput();
	
	@LangKey(text = "Can run recipes of any single block electric crafting machine provided to it in batches.")
	@WithStyle("tooltip")
	MutableComponent processingArrayRecipe();
	
	@LangKey(text = "Machines: %s")
	MutableComponent processingArraySize(
			int machines
	);
	
	@LangKey(text = "Rainbow")
	@WithStyle("rainbow")
	MutableComponent rainbow();
	
	@LangKey(text = "Automatically grabs Canned Food from your inventory and feeds it to you.")
	@WithStyle("tooltip")
	MutableComponent robotAutoFeederHelp1();
	
	@LangKey(text = "Works with item containing items such as backpacks.")
	@WithStyle("tooltip")
	MutableComponent robotAutoFeederHelp2();
	
	@LangKey(text = "Will calcify and lose efficiency over time to a minimum of %s efficiency when not using %s. Using an axe on the boiler will reset its calcification.")
	@WithStyle("tooltip")
	MutableComponent solarBoilerCalcification(
			@Parsed("percentage") @WithStyle("highlighted") float efficiency,
			@WithStyle("highlighted") Fluid fluid
	);
	
	@LangKey(text = "Solar Efficiency: %s %%")
	MutableComponent solarEfficiency(
			int percentage
	);
	
	@LangKey(text = "By supplying %s to the Solar Panel, the Photovoltaic Cell in its slot will last 2x as long and produce 1.5x as much energy!")
	@WithStyle("tooltip")
	MutableComponent solarPanelDistilledWater(
			@WithStyle("highlighted") Fluid fluid
	);
	
	@LangKey(text = "To produce energy, the Solar Panel needs a matching tier Photovoltaic Cell in its inventory.")
	@WithStyle("tooltip")
	MutableComponent solarPanelPhotovoltaicCell();
	
	@LangKey(text = "Energy generation rates are determined by how high the sun is in the sky and if the sky is visible.")
	@WithStyle("tooltip")
	MutableComponent solarPanelSunlight();
	
	@LangKey(text = "- Press %s on still or flowing water to fill.")
	@WithStyle("tooltip")
	MutableComponent steamChainsaw1(
			@Parsed("keybind") @WithStyle("highlighted") String keybind
	);
	
	@LangKey(text = "- Place fuel inside the chainsaw using %s.")
	@WithStyle("tooltip")
	MutableComponent steamChainsaw2(
			@Parsed("keybind") @WithStyle("highlighted") String keybind
	);
	
	@LangKey(text = "- Toggle Silk Touch with %s + %s.")
	@WithStyle("tooltip")
	MutableComponent steamChainsaw3(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "Cleared selection from tesla calibrator.")
	MutableComponent teslaCalibratorClear();
	
	@LangKey(text = "- Press %s + %s on a transmitter to save its position in the calibrator.")
	@WithStyle("tooltip")
	MutableComponent teslaCalibratorHelp1(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "- Use %s on a Tesla Receiver to link it to the selected transmitter.")
	@WithStyle("tooltip")
	MutableComponent teslaCalibratorHelp2(
			@Parsed("keybind") @WithStyle("highlighted") String keybind
	);
	
	@LangKey(text = "- (Optional) Hold in off-hand when placing receivers to automatically link.")
	@WithStyle("tooltip")
	MutableComponent teslaCalibratorHelp3();
	
	@LangKey(text = "- Clear using %s + %s on air.")
	@WithStyle("tooltip")
	MutableComponent teslaCalibratorHelp4(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "Linked to %s")
	@WithStyle("tooltip")
	MutableComponent teslaCalibratorLinked(
			WorldPos pos
	);
	
	@LangKey(text = "Failed to link receiver because no transmitter is selected.")
	MutableComponent teslaCalibratorLinkFailedNoSelection();
	
	@LangKey(text = "Linked receiver to selected transmitter.")
	MutableComponent teslaCalibratorLinkSuccess();
	
	@LangKey(text = "Selected transmitter for calibration.")
	MutableComponent teslaCalibratorSelected();
	
	@LangKey(text = "Wirelessly transmits energy to linked receivers within %s blocks.")
	@WithStyle("tooltip")
	MutableComponent teslaCoilHelp1(
			@WithStyle("highlighted") int range
	);
	
	@LangKey(text = "Voltage of energy transmitted is set by the hull provided. Higher voltages have an increased passive drain.")
	@WithStyle("tooltip")
	MutableComponent teslaCoilHelp2();
	
	@LangKey(text = "Cleared selected transmitter.")
	MutableComponent teslaHandheldClear();
	
	@LangKey(text = "Receives energy from a linked transmitter within range and charges items while in your inventory.")
	@WithStyle("tooltip")
	MutableComponent teslaHandheldHelp1();
	
	@LangKey(text = "Tesla Calibration:")
	@WithStyle("tooltip")
	MutableComponent teslaHandheldHelp2();
	
	@LangKey(text = "- Press %s on a transmitter to link the receiver to it.")
	@WithStyle("tooltip")
	MutableComponent teslaHandheldHelp3(
			@Parsed("keybind") @WithStyle("highlighted") String keybind
	);
	
	@LangKey(text = "- Clear using %s + %s on air.")
	@WithStyle("tooltip")
	MutableComponent teslaHandheldHelp4(
			@Parsed("keybind") @WithStyle("highlighted") String keybind1,
			@Parsed("keybind") @WithStyle("highlighted") String keybind2
	);
	
	@LangKey(text = "Linked to %s")
	@WithStyle("tooltip")
	MutableComponent teslaHandheldLinked(
			WorldPos pos
	);
	
	@LangKey(text = "Selected transmitter for receiving.")
	MutableComponent teslaHandheldSelected();
	
	@LangKey(text = "Removes the range limitation on a Tesla Tower and allows it to transmit energy across dimensions.")
	@WithStyle("tooltip")
	MutableComponent teslaInterdimensionalUpgradeHelp();
	
	@LangKey(text = "Deals damage to entities within %s blocks while powered.")
	@WithStyle("tooltip")
	MutableComponent teslaLethalCoilHelp1(
			@WithStyle("highlighted") int range
	);
	
	@LangKey(text = "Voltage determines the amount of damage dealt and energy required:")
	@WithStyle("tooltip")
	MutableComponent teslaLethalCoilValues();
	
	@LangKey(text = "Damages Players: No")
	MutableComponent teslaLethalCoilDamagesPlayersNo();
	
	@LangKey(text = "Damages Players: Yes")
	MutableComponent teslaLethalCoilDamagesPlayersYes();
	
	@LangKey(text = "Linked to %s")
	MutableComponent teslaNetworkReceiverLinked(
			WorldPos pos
	);
	
	@LangKey(text = "Cannot receive %s power")
	MutableComponent teslaNetworkReceiverMismatchingVoltage(
			@Parsed("short") CableTier tier
	);
	
	@LangKey(text = "Not linked to any transmitter")
	MutableComponent teslaNetworkReceiverNoLink();
	
	@LangKey(text = "Transmitter is too far")
	MutableComponent teslaNetworkReceiverTooFar();
	
	@LangKey(text = "Transmitter is not loaded")
	MutableComponent teslaNetworkReceiverUnloaded();
	
	@LangKey(text = "Note: %s")
	MutableComponent teslaNetworkSingingNote(
			String note
	);
	
	@LangKey(text = "Consuming: %s")
	MutableComponent teslaNetworkTransmitterConsuming(
			@Parsed("eu_per_tick") long euPerTick
	);
	
	@LangKey(text = "Drain: %s")
	MutableComponent teslaNetworkTransmitterDrain(
			@Parsed("eu_per_tick") long euPerTick
	);
	
	@LangKey(text = "Receivers: %s")
	MutableComponent teslaNetworkTransmitterReceivers(
			int count
	);
	
	@LangKey(text = "Transmitting: %s (%s)")
	MutableComponent teslaNetworkTransmitterTransmitting(
			@Parsed("eu_per_tick") long euPerTick, @Parsed("short") CableTier tier
	);
	
	@LangKey(text = "Generates arcs for aesthetic purposes only.")
	@WithStyle("tooltip")
	MutableComponent teslaParticleGeneratorHelp();
	
	@LangKey(text = "Extreme")
	MutableComponent teslaParticleGeneratorSizeExtreme();
	
	@LangKey(text = "Immense")
	MutableComponent teslaParticleGeneratorSizeImmense();
	
	@LangKey(text = "Large")
	MutableComponent teslaParticleGeneratorSizeLarge();
	
	@LangKey(text = "Medium")
	MutableComponent teslaParticleGeneratorSizeMedium();
	
	@LangKey(text = "Small")
	MutableComponent teslaParticleGeneratorSizeSmall();
	
	@LangKey(text = "Can receive energy from a linked transmitter.")
	@WithStyle("tooltip")
	MutableComponent teslaReceiverHelp1();
	
	@LangKey(text = "Must accept energy of the same voltage as the linked transmitter.")
	@WithStyle("tooltip")
	MutableComponent teslaReceiverHelp2();
	
	@LangKey(text = "Wirelessly transmits energy to linked receivers within range.")
	@WithStyle("tooltip")
	MutableComponent teslaTowerHelp1();
	
	@LangKey(text = "Energy transfer rate, range, and passive drain is determined by the windings used.")
	@WithStyle("tooltip")
	MutableComponent teslaTowerHelp2();
	
	@LangKey(text = "Voltage of energy transmitted is set by the energy hatches. All hatches must be the same tier.")
	@WithStyle("tooltip")
	MutableComponent teslaTowerHelp3();
	
	@LangKey(text = "All energy hatches must be of the same voltage.")
	MutableComponent teslaTowerMismatchingHatches();
	
	@LangKey(text = "No energy hatches provided")
	MutableComponent teslaTowerNoEnergyHatches();
	
	@LangKey(text = "Add tesla upgrades to increase maximum range.")
	@WithStyle("tooltip")
	MutableComponent teslaTowerUpgrade();
	
	@LangKey(text = "Area: %s")
	@WithStyle("tooltip")
	MutableComponent toolMiningArea(
			@WithStyle("highlighted") Component text
	);
	
	@LangKey(text = "1x1")
	MutableComponent toolMiningArea1By1();
	
	@LangKey(text = "3x3")
	MutableComponent toolMiningArea3By3();
	
	@LangKey(text = "Speed: %s")
	MutableComponent toolChangedMiningSpeed(
			@Parsed("percentage") float speed
	);
	
	@LangKey(text = "Speed: %s")
	@WithStyle("tooltip")
	MutableComponent toolMiningSpeed(
			@Parsed("percentage") @WithStyle("highlighted") float speed
	);
	
	@LangKey(text = "Mode: %s")
	@WithStyle("tooltip")
	MutableComponent toolMode(
			@WithStyle("highlighted") ElectricToolItem.Mode mode
	);
	
	@LangKey(text = "Beheading")
	MutableComponent toolModeBeheading();
	
	@LangKey(text = "Fortune")
	MutableComponent toolModeFortune();
	
	@LangKey(text = "Fortune & Looting")
	MutableComponent toolModeFortuneLooting();
	
	@LangKey(text = "Looting")
	MutableComponent toolModeLooting();
	
	@LangKey(text = "Silk Touch")
	MutableComponent toolModeSilkTouch();
	
	@LangKey(text = "Beheading mode enabled!")
	MutableComponent toolSwitchedBeheading();
	
	@LangKey(text = "Fortune mode enabled!")
	MutableComponent toolSwitchedFortune();
	
	@LangKey(text = "Looting mode enabled!")
	MutableComponent toolSwitchedLooting();
	
	@LangKey(text = "Silk Touch mode enabled!")
	MutableComponent toolSwitchedSilkTouch();
	
	@LangKey(text = "Hull for cable tier to convert from (LV by default).")
	@WithStyle("tooltip")
	MutableComponent universalTransformerFromTierInput();
	
	@LangKey(text = "Hull for cable tier to convert to (LV by default).")
	@WithStyle("tooltip")
	MutableComponent universalTransformerToTierInput();
	
	@LangKey(text = "  - %s: %s for %s")
	@WithStyle("tooltip")
	MutableComponent voltageValueForCost(
			@Parsed("short") @WithStyle("highlighted") CableTier tier,
			@Parsed("damage") @WithStyle("highlighted") float damageAmount,
			@Parsed("eu") @WithStyle("highlighted") long energyCost
	);
	
	@LangKey
	@WithStyle("tooltip")
	MutableComponent voltageValueForCost(
			@Parsed("short") @WithStyle("highlighted") CableTier tier,
			@Parsed("enchantment_level") @WithStyle("highlighted") int level,
			@Parsed("eu_per_tick") @WithStyle("highlighted") long euPerTick
	);
	
	@LangKey(text = "When placed underneath animals, manure will be collected.")
	@WithStyle("tooltip")
	MutableComponent wasteCollectorHelp();
	
	@LangKey(text = "Allows the Tesla Tower to transmit up to %s within %s blocks with a passive drain of %s.")
	@WithStyle("tooltip")
	MutableComponent windingsTeslaTowerTier(
			@Parsed("eu_per_tick") @WithStyle("highlighted") long maxTransfer,
			@WithStyle("highlighted") int maxDistance,
			@Parsed("eu_per_tick") @WithStyle("highlighted") long drain
	);
}
