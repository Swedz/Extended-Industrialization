---
navigation:
  title: "Фермер"
  icon: "steam_farmer"
  parent: extended_industrialization:machines.md
categories:
  - machines
item_ids:
  - extended_industrialization:steam_farmer
  - extended_industrialization:electric_farmer
---

# Фермер

<Row>
	<RecipeFor id="extended_industrialization:steam_farmer" />
	<RecipeFor id="extended_industrialization:electric_farmer" />
</Row>

Фермер не выполняет обычные рецепты. Вместо этого он вспахивает и увлажняет почву, высаживает и удобряет растения, а
также собирает урожай, растения и деревья. Для работы фермера необязательно включать все задачи; иногда некоторые из
них лучше отключить. Например, при работе с деревьями не требуется вспахивать или увлажнять почву.

Фермер не работает без достаточного количества энергии. Паровому фермеру требуется подача пара, эквивалентная 32 ЭЕ/т,
а электрическому - 64 ЭЕ/т.

Блоки земли, входящие в конструкцию многоблочного фермера, можно заменить любыми блоками с меткой
`#extended_industrialization:farmer_dirt`.

## Задачи

### Вспашка

Если эта задача включена в настройках формы многоблочной конструкции, блоки земли в её составе превращаются в пашню, как при использовании мотыги. Для вспашки требуется подача воды.

### Увлажнение

Если вода подаётся фермеру через жидкостный шлюз ввода, вся пашня будет оставаться увлажнённой без размещения воды рядом.

### Посадка

Если в предметный шлюз ввода подать пригодные для посадки предметы, фермер высадит их на подходящие блоки в своей
рабочей области. Можно высаживать только предметы с меткой `#extended_industrialization:farmer_plantable`.

### Удобрение

Удобрение доступно только электрическому фермеру. Если через жидкостный шлюз ввода подать подходящее жидкое удобрение
(см. EMI), фермер применяет к растениям в рабочей области действие, подобное костной муке.
Оно действует и на растения, которые обычно нельзя удобрять костной мукой, например кактусы и сахарный тростник.

### Сбор

Когда растение полностью созревает (например, пшеница вырастает до конца или саженец превращается в дерево) и в
предметном шлюзе вывода достаточно места, фермер ломает его и сохраняет полученные предметы.

Предметы с меткой `#extended_industrialization:farmer_voidable` имеют низкий приоритет при помещении в предметные шлюзы
вывода. Если при сборе для них не хватает места, они уничтожаются. Например, это относится к
<ItemLink id="minecraft:stick" /> и <ItemLink id="minecraft:apple" />: можно закрепить слоты шлюза или шлюзов вывода
только за брёвнами и саженцами и не хранить эти побочные предметы.

## Паровой фермер

<GameScene zoom="1" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:steam_farmer" />
    <MultiblockShape controller="extended_industrialization:steam_farmer" useBigShape={true} x="-10" z="-10" />
</GameScene>

## Электрический фермер

<GameScene zoom="1" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:electric_farmer" />
    <MultiblockShape controller="extended_industrialization:electric_farmer" useBigShape={true} x="-12" z="-12" />
</GameScene>