---
navigation:
  title: "Соединитель механизмов"
  icon: "machine_chainer"
  parent: extended_industrialization:machines.md
categories:
  - machines
item_ids:
  - extended_industrialization:machine_chainer
  - extended_industrialization:machine_chainer_relay
---

# Соединитель механизмов

<GameScene zoom="2" interactive={true} fullWidth={true}>
	<ImportStructure src="machine_chainer_example.nbt" />
	<IsometricCamera yaw="180" pitch="30" />
</GameScene>

<RecipeFor id="extended_industrialization:machine_chainer" />

Соединитель механизмов может связывать механизмы, бочки и другие блоки-хранилища с меткой
`#extended_industrialization:machine_chainer/linkable`, расположенные по прямой на расстоянии до 64 блоков. Хранилища
подключённых блоков объединяются в одно общее хранилище соединителя. Соединитель можно направить в любую сторону, в том числе вверх и вниз.

Соединитель передаёт предметы, жидкости и энергию. Передача энергии, однако, ограничена утроенной пропускной
способностью одного кабеля соответствующего уровня и не работает с энергией связанных механизмов, напряжение которых
не совпадает с напряжением соединителя.

## Реле соединителя механизмов

Реле - блок без собственного хранилища, который можно подключить к соединителю. Его можно использовать как промежуточный блок в цепочке механизмов,
не устанавливая механизм в каждый промежуток.

<RecipeFor id="extended_industrialization:machine_chainer_relay" />