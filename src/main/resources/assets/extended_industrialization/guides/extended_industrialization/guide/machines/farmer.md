---
navigation:
  title: "Farmer"
  icon: "steam_farmer"
  parent: extended_industrialization:machines.md
categories:
  - machines
item_ids:
  - extended_industrialization:steam_farmer
  - extended_industrialization:electric_farmer
---

# Farmer

<Row>
	<RecipeFor id="extended_industrialization:steam_farmer" />
	<RecipeFor id="extended_industrialization:electric_farmer" />
</Row>

The farmer is not a typical machine in that it runs recipes, but instead tills and hydrates soil, plants, fertilizes,
and harvests crops, plants, and trees. Not all of these tasks are required to be used in order for the farmer to work,
in fact, some times you may want to avoid certain tasks. For example, when working with trees, you would not want the
farmer to till or hydrate soil.

The farmer will not operate unless it is provided with sufficient energy. The steam farmer requires 32 EU/t of steam,
and the electric farmer requires 64 EU/t.

The dirt required as part of the farmer multiblock may be replaced with any block that has the tag
`#extended_industrialization:farmer_dirt`.

## Tasks

### Tilling Soil

When enabled in the multiblock shape settings, the dirt blocks that are part of the multiblock will be converted into
farmland as if a hoe was used on it.

### Hydrating Soil

When the farmer is provided with water through an input hatch, it will ensure that all farmland is kept hydrated without
the need for water to be placed nearby.

### Planting

When the farmer is provided with plantable items in an input hatch, it will plant them on any suitable blocks in its
area of operation. Only items with the `#extended_industrialization:farmer_plantable` item tag can be planted.

### Fertilizing

Fertilizing is exclusive to the electric farmer. When provided with an acceptable fluid fertilizer (as shown in EMI) in
an input hatch, the farmer will apply a bonemeal-like affect to plants in its area of operation. This bonemeal-like
effect can apply to plants that normally cannot be bonemealed, such as cactus and sugar cane.

### Harvesting

When there is a plant that has reached full maturity (such as fully grown wheat, or a sapling has grown into a tree),
and there is sufficient output space in an output hatch, it will be broken and stored.

Items with the tag `#extended_industrialization:farmer_voidable` have a low priority to be stored in output hatches, and
if there is no space for it when it would be dropped by a harvested block, it will instead be discarded. An example of
this is <ItemLink id="minecraft:stick" />s and <ItemLink id="minecraft:apple" />s, which would mean you could lock the
output slots of your output hatch(es) to simply logs and saplings, and not need to worry about storing those items.

## Steam Farmer

<GameScene zoom="1" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:steam_farmer" />
    <MultiblockShape controller="extended_industrialization:steam_farmer" useBigShape={true} x="-10" z="-10" />
</GameScene>

## Electric Farmer

<GameScene zoom="1" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:electric_farmer" />
    <MultiblockShape controller="extended_industrialization:electric_farmer" useBigShape={true} x="-12" z="-12" />
</GameScene>