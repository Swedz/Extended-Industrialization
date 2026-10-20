---
navigation:
  title: "Тесла"
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

# Тесла

Катушки и приёмники Теслы позволяют передавать ЭЕ беспроводным способом, но с дополнительными затратами. В одной сети
Теслы может быть только один передатчик (Катушка Теслы или Башня Теслы), а встроенного ограничения на количество
Приёмников Теслы нет. У каждого передатчика своя дальность и пассивный расход.

## Калибратор Теслы

Чтобы связать передатчик Теслы с приёмниками, сначала нажмите **<KeyBind id="key.sneak" />** +
**<KeyBind id="key.use" />** по передатчику, держа в руке Калибратор Теслы. Затем нажмите
**<KeyBind id="key.use" />** калибратором по любому приёмнику, чтобы связать его.

<RecipeFor id="extended_industrialization:tesla_calibrator" />

## Передатчики Теслы

Передатчики Теслы служат источниками всех сетей Теслы.

Передатчик не может передавать энергию приёмнику с другим напряжением. Например, Катушка Теслы с Усовершенствованным
корпусом механизма не может передавать энергию Приёмнику Теслы без корпуса, но может передавать её приёмнику с таким же
Усовершенствованным корпусом механизма.

Пассивный расход Катушки Теслы в ЭЕ/т определяется напряжением установленного в неё корпуса или отсутствием корпуса.

<RecipeFor id="extended_industrialization:tesla_coil" />

Напряжение, передаваемое Башней Теслы, определяется используемыми энергетическими шлюзами ввода.

Пассивный расход в ЭЕ/т, предел передачи и дальность Башни Теслы определяются используемыми обмотками. Точные значения
каждой обмотки указаны в её всплывающей подсказке.

<Row>
	<GameScene zoom="0.75" interactive={true} fullWidth={false}>
		<MultiblockShape controller="extended_industrialization:tesla_tower" />
	</GameScene>
	<RecipeFor id="extended_industrialization:tesla_tower" />
</Row>

## Приёмники Теслы

Приёмники Теслы служат точками назначения энергии, передаваемой передатчиком.

Приёмник Теслы накапливает полученную энергию и выдаёт её через сторону вывода. Энергию также можно извлекать кабелями,
как из любого другого блока, выдающего энергию.

<RecipeFor id="extended_industrialization:tesla_receiver" />

Приёмный шлюз Теслы получает энергию так же, как обычный Приёмник Теслы, но одновременно служит энергетическим шлюзом
ввода многоблочного механизма. Поэтому вместо связки из приёмника и отдельного энергетического шлюза ввода достаточно
одного приёмного шлюза.

<RecipeFor id="extended_industrialization:lv_tesla_receiver_hatch" />