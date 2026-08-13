---
navigation:
  title: "Electric Beacon"
  icon: "electric_beacon"
  parent: extended_industrialization:machines.md
categories:
  - machines
item_ids:
  - extended_industrialization:electric_beacon
---

# Electric Beacon

<RecipeFor id="extended_industrialization:electric_beacon" />

The Electric Beacon consumes potions and provides the effect to all nearby players at the cost of 256 EU/t per effect.
The duration that a potion lasts in the beacon is determined by its shortest effect, which is then halved. In most
cases, a potion will only have one effect. For example, an 8 minute speed potion can be consumed by the beacon which
will then provide the effect in the area for a total of 4 minutes costing 256 EU/t. Similarly, a 40 second turtle
master potion will only last 20 seconds but costs 512 EU/t, since it has two effects (slowness and resistance). Because
potions are consumed by the beacon, it is recommended to create some sort of automation loop using the
[Brewery](brewery.md) to keep the beacon running indefinitely.

The range of the beacon is larger than that of a normal beacon, as determined by the amount of layers beneath it,
excluding the steel casing.

| Layers | Range |
|--------|-------|
| 1      | 30    |
| 2      | 50    |
| 3      | 70    |
| 4      | 90    |

<GameScene zoom="1.5" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:electric_beacon" />
    <MultiblockShape controller="extended_industrialization:electric_beacon" useBigShape={true} x="-9" y="2" z="-9" />
</GameScene>