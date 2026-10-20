---
navigation:
  title: "Многоблочные механизмы с пакетной обработкой"
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

# Многоблочные механизмы с пакетной обработкой

Некоторые многоблочные механизмы могут работать как обычный механизм своего типа, но с множителем количества входных
ресурсов, обрабатываемых одновременно. Иными словами, если многоблочный механизм способен выполнять определённый тип
рецепта партиями до Y, за один запуск он может потребить от 1x до Yx наборов входных ресурсов, а затем выдать результат
в соответствии с размером партии. Потребление ЭЕ/т также умножается на количество одновременно выполняемых партий.

Количество партий зависит от конкретного многоблочного механизма. Точные значения указаны во всплывающей подсказке
предмета механизма.

Как и обычные механизмы, эти многоблочные механизмы не могут одновременно выполнять более одного рецепта.

## Большая печь

Количество партий, которые может выполнять Большая электрическая печь, определяется используемыми в многоблочной
конструкции обмотками, аналогично Электрической доменной печи. Размер партии для каждой обмотки указан в её всплывающей подсказке.

<Row>
	<RecipeFor id="extended_industrialization:large_steam_furnace" />
	<RecipeFor id="extended_industrialization:large_electric_furnace" />
</Row>

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:large_steam_furnace" />
    <MultiblockShape controller="extended_industrialization:large_electric_furnace" x="-6" y="-1" z="-6" />
</GameScene>

## Большой измельчитель

<Row>
	<RecipeFor id="extended_industrialization:large_steam_macerator" />
	<RecipeFor id="extended_industrialization:large_electric_macerator" />
</Row>

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:large_steam_macerator" />
    <MultiblockShape controller="extended_industrialization:large_electric_macerator" x="-6" z="-6" />
</GameScene>

## Массив обработки

Массив обработки может выполнять рецепты любого одноблочного электрического механизма, установленного в его интерфейсе, обрабатывая ресурсы партиями.
Максимальный размер партии ограничен размером массива и количеством установленных в него механизмов.

<RecipeFor id="extended_industrialization:processing_array" />

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:processing_array" />
    <MultiblockShape controller="extended_industrialization:processing_array" useBigShape={true} x="-6" z="-8" />
</GameScene>