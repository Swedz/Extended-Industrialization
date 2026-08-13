---
navigation:
  title: "Tesla"
  icon: "tesla_coil"
  parent: extended_industrialization:machines.md
categories:
  - machines
item_ids:
  - extended_industrialization:tesla_calibrator
  - extended_industrialization:tesla_handheld_receiver
  - extended_industrialization:tesla_interdimensional_upgrade
  - extended_industrialization:tesla_coil
  - extended_industrialization:tesla_receiver
  - extended_industrialization:lv_tesla_receiver_hatch
  - extended_industrialization:mv_tesla_receiver_hatch
  - extended_industrialization:hv_tesla_receiver_hatch
  - extended_industrialization:ev_tesla_receiver_hatch
  - extended_industrialization:superconductor_tesla_receiver_hatch
  - extended_industrialization:tesla_tower
  - extended_industrialization:aluminum_tesla_winding
  - extended_industrialization:annealed_copper_tesla_winding
  - extended_industrialization:copper_tesla_winding
  - extended_industrialization:electrum_tesla_winding
  - extended_industrialization:superconductor_tesla_winding
---

# Tesla

Tesla coils and receivers allow you to wirelessly transfer EU at a cost. A tesla network is limited to one transmitter
(Tesla Coil or Tesla Tower), and has no intrinsic limit on Tesla Receivers. Each transmitter will have its own defined
range and passive drain costs.

## Tesla Calibrator

To link a tesla transmitter to receivers, first press **<KeyBind id="key.sneak" />** + **<KeyBind id="key.use" />**
while holding a Tesla Calibrator on the transmitter. Then, simply press **<KeyBind id="key.use" />** with the calibrator
on any receiver to link it.

<RecipeFor id="extended_industrialization:tesla_calibrator" />

## Tesla Transmitters

Tesla transmitters are the source of every tesla network.

A transmitter cannot transmit energy to a receiver that is not of the exact same voltage. For example, a Tesla Coil
with an Advanced Machine Hull cannot transmit to a Tesla Receiver without a hull, but could to one that also has an
Advanced Machine Hull.

The passive EU/t drain cost of a Tesla Coil is determined by the voltage of the hull (or lackthereof) placed inside.

<RecipeFor id="extended_industrialization:tesla_coil" />

The voltage transmitted by a Tesla Tower is determined by the energy input hatches used.

The passive EU/t drain cost, transfer limit, and range of a Tesla Tower is determined by the windings used. Specifics
of each winding is described in their respective tooltips.

<Row>
	<GameScene zoom="0.75" interactive={true} fullWidth={false}>
		<MultiblockShape controller="extended_industrialization:tesla_tower" />
	</GameScene>
	<RecipeFor id="extended_industrialization:tesla_tower" />
</Row>

## Tesla Receivers

Tesla receivers are the destinations for your transmitted energy from your transmitter.

The Tesla Receiver stores energy received, and ejects it out of its output face. Energy may also be extracted from it
using cables, as you would with any other energy outputting block.

<RecipeFor id="extended_industrialization:tesla_receiver" />

The Tesla Receiver Hatch receives energy in the same way as a normal Tesla Receiver, however it functions as an energy
input hatch for a multiblock. Rather than needing a receiver and then also an energy input hatch to eject into, this
combines both into just a hatch.

<RecipeFor id="extended_industrialization:lv_tesla_receiver_hatch" />