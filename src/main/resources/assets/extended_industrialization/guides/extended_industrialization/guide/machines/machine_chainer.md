---
navigation:
  title: "Machine Chainer"
  icon: "machine_chainer"
  parent: extended_industrialization:machines.md
categories:
  - machines
item_ids:
  - extended_industrialization:machine_chainer
  - extended_industrialization:machine_chainer_relay
---

# Machine Chainer

<GameScene zoom="2" interactive={true} fullWidth={true}>
	<ImportStructure src="machine_chainer_example.nbt" />
	<IsometricCamera yaw="180" pitch="30" />
</GameScene>

The Machine Chainer can connect to many machines, barrels, or any other inventory block tagged as
`#extended_industrialization:machine_chainer/linkable` in a straight line up to 64 blocks. Connected inventories are
combined into a single shared inventory as the chainer. The chainer can be oriented to face any direction, including up
and down.

The chainer supports items, fluids, and energy transfer! However, energy transfer is restricted to 3x the transfer rate
of a single cable of its respective tier, and cannot interact with the energy of linked machines that do not match its
voltage.

## Machine Chainer Relay

The relay is a block that can be linked to by the chainer, but has no inventory. It can be used as filler in your
chained machines while not requiring that you place a machine in a gap.

<RecipeFor id="extended_industrialization:machine_chainer_relay" />