---
navigation:
  title: "Солнечная энергия"
  icon: "lv_solar_panel"
  parent: extended_industrialization:machines.md
categories:
  - machines
item_ids:
  - extended_industrialization:bronze_solar_boiler
  - extended_industrialization:steel_solar_boiler
  - extended_industrialization:lv_solar_panel
  - extended_industrialization:mv_solar_panel
  - extended_industrialization:hv_solar_panel
  - extended_industrialization:lv_photovoltaic_cell
  - extended_industrialization:mv_photovoltaic_cell
  - extended_industrialization:hv_photovoltaic_cell
---

# Солнечная энергия

## Солнечные котлы

Солнечные котлы работают подобно обычным бронзовым и стальным паровым котлам, но не расходуют топливо. Взамен им нужен
прямой доступ к солнечному свету сверху, а энергии они производят вдвое меньше, чем обычный котёл соответствующего типа.
Вода по-прежнему необходима.

Со временем солнечный котёл покрывается накипью и теряет до 33% эффективности. Удар топором по солнечному котлу
сбрасывает накипь. Накипи можно полностью избежать, если вместо обычной воды подавать дистиллированную воду.

<Row>
	<RecipeFor id="extended_industrialization:bronze_solar_boiler" />
	<RecipeFor id="extended_industrialization:steel_solar_boiler" />
</Row>

## Солнечные панели

Солнечные панели вырабатывают энергию под прямым солнечным светом, если в них установлен фотовольтаический элемент
соответствующего уровня. Со временем фотовольтаический элемент изнашивается и в конце концов ломается. Поэтому для
стабильной выработки энергии днём необходимо наладить постоянное производство фотовольтаических элементов. При подаче
дистиллированной воды срок службы фотовольтаического элемента увеличивается вдвое, а суммарная выработка энергии за срок его службы - в 1,5 раза.

<Row>
	<RecipeFor id="extended_industrialization:lv_solar_panel" />
	<RecipeFor id="extended_industrialization:mv_solar_panel" />
	<RecipeFor id="extended_industrialization:hv_solar_panel" />
</Row>

<Row>
	<Recipe id="extended_industrialization:photovoltaic_cell/lv" />
	<Recipe id="extended_industrialization:photovoltaic_cell/mv" />
	<Recipe id="extended_industrialization:photovoltaic_cell/hv" />
</Row>