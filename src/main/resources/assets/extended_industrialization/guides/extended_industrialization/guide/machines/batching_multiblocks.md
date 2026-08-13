---
navigation:
  title: "Batching Multiblocks"
  icon: "processing_array"
  parent: extended_industrialization:machines.md
categories:
  - machines
item_ids:
  - extended_industrialization:large_steam_furnace
  - extended_industrialization:large_steam_macerator
  - extended_industrialization:large_electric_furnace
  - extended_industrialization:large_electric_macerator
  - extended_industrialization:processing_array
---

# Batching Multiblocks

Certain multiblocks are capable of functioning as a normal machine of its type, but with a multiplier applied to the
amount of inputs it can process at once. This is to say that a multiblock that is capable of running a given recipe
type in batches of Y, could consume anywhere between 1x-Yx as many inputs at once, and then produce the results
according to its batch size. The amount of EU/t consumed by the machine will also be multiplied by the amount of
batches it is currently running.

The amount of batches a batching multiblock can run varies. To see specific values, refer to the tooltip on the item of
the machine.

Exactly how other machines operate, these cannot run more than one recipe at a time.

## Large Furnace

The amount of batches the Large Electric Furnace can run is determined by the coils used in the multiblock, similar to
how the Electric Blast Furnace is constructed. For the size of batches a coil provides in the furnace, refer to the
tooltip on the coil in question.

<Row>
	<RecipeFor id="extended_industrialization:large_steam_furnace" />
	<RecipeFor id="extended_industrialization:large_electric_furnace" />
</Row>

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:large_steam_furnace" />
    <MultiblockShape controller="extended_industrialization:large_electric_furnace" x="-6" y="-1" z="-6" />
</GameScene>

## Large Macerator

<Row>
	<RecipeFor id="extended_industrialization:large_steam_macerator" />
	<RecipeFor id="extended_industrialization:large_electric_macerator" />
</Row>

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:large_steam_macerator" />
    <MultiblockShape controller="extended_industrialization:large_electric_macerator" x="-6" z="-6" />
</GameScene>

## Processing Array

The Processing Array is capable of running batches of any non-multiblock electric crafting machine that is provided to
it in its interface. The size of batches the Processing Array can run is limited by its size and the amount of machines
placed into it.

<RecipeFor id="extended_industrialization:processing_array" />

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:processing_array" />
    <MultiblockShape controller="extended_industrialization:processing_array" useBigShape={true} x="-6" z="-8" />
</GameScene>