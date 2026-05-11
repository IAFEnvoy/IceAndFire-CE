# Ice and Fire CE — Example Structure Generation Datapack

This datapack shows every configurable field for each Ice and Fire CE structure.
All values shown are the defaults — copying a file into your datapack and editing
a value will override that structure's settings for that world.

## Installation

Place the `example-datapack` folder (or a copy of it) inside your world's
`datapacks/` directory, then run `/reload` or start the world.

## How `generate_chance` works

Each time a candidate chunk is tested for a structure, a random double in
`[0.0, 1.0)` is drawn. If the value is **less than** `generate_chance` the
structure is allowed to place (subject to other constraints such as biome and
spacing). So:

| `generate_chance` | Meaning                       |
|-------------------|-------------------------------|
| `0.0`             | Never generates               |
| `0.2`             | ~20% of candidate chunks      |
| `0.5`             | ~50% of candidate chunks (default) |
| `1.0`             | Every candidate chunk         |

"Candidate chunks" are already filtered by the structure's placement rules
(biome, minimum spacing between structures, etc.), so setting `generate_chance`
to `1.0` does not flood the world — it just removes the extra probability gate.

---

## Field reference

### `iceandfire:dragon_cave` — fire / ice / lightning

`generate_chance` lives at the **top level** of the JSON (not inside a
`"generation"` block).

| Field | Type | Description |
|---|---|---|
| `generate_chance` | float 0–1 | Extra per-chunk probability gate. Default `0.5`. |
| `biomes` | biome tag | Which biomes the cave may generate in. |
| `step` | string | Worldgen step. Dragon caves use `underground_structures`. |
| `entity_type` | entity id | Dragon entity spawned inside the cave. |
| `stalactite_block` | block id | Block used to build stalactites on the cave ceiling. |
| `stalactite_max_height` | int | Maximum height of a stalactite (in blocks). |
| `treasure_pile_block` | block id | Block scattered as treasure piles on the cave floor. |
| `palette` | block id list | Two blocks used for cave walls: `[primary, secondary]`. |
| `ore_tag` | block tag | Ore veins that can appear in cave walls. |
| `loot_table_male` | resource location | Chest loot table when the resident dragon is male. |
| `loot_table_female` | resource location | Chest loot table when the resident dragon is female. |
| `spawn_overrides` | object | Vanilla structure mob spawn overrides (usually empty). |

---

### `iceandfire:dragon_roost` — fire / ice / lightning

`generate_chance` lives at the **top level** of the JSON.

| Field | Type | Description |
|---|---|---|
| `generate_chance` | float 0–1 | Extra per-chunk probability gate. Default `0.5`. |
| `biomes` | biome tag | Which biomes the roost may generate in. |
| `step` | string | Worldgen step. Roosts use `surface_structures`. |
| `terrain_adaptation` | string | How vanilla terrain adapts around the structure. One of: `none`, `beard_thin`, `beard_box`, `bury`, `encapsulate`. |
| `dragon_type` | entity id | Dragon entity spawned at the roost. |
| `loot_table` | resource location | Loot table for treasure chests at the roost. |
| `treasure_block` | block id | Block used for treasure piles. |
| `pile_block` | block id *(optional)* | Decorative pile block (e.g. ash, ice) scattered around the roost. Omit to disable. |
| `generate_spires` | boolean | Whether to generate rocky spires around the roost footprint. Default `false` (enabled for lightning by default). |
| `block_transform` | list of `{from, to}` | Terrain replacement rules. Each entry replaces every occurrence of `from` block within the roost footprint with `to`. Used to give each dragon type a distinct visual theme. |
| `spawn_overrides` | object | Vanilla structure mob spawn overrides (usually empty). |

---

### All jigsaw structures

Applies to: `cyclops_cave`, `hydra_cave`, `siren_island`, `pixie_village`,
`graveyard`, `mausoleum`, `gorgon_temple`, `dread_ruin`, `dread_portal`.

`generate_chance` lives inside a nested **`"generation"`** object.

| Field | Type | Description |
|---|---|---|
| `generation.generate_chance` | float 0–1 | Extra per-chunk probability gate. Default `0.5`, except `dread_portal` which defaults to `0.2`. Omit the entire `"generation"` block to use the default. |
| `biomes` | biome tag | Which biomes the structure may generate in. |
| `step` | string | Worldgen step (usually `surface_structures`). |
| `terrain_adaptation` | string | Terrain shaping: `none`, `beard_thin`, `beard_box`, `bury`, `encapsulate`. |
| `start_pool` | resource location | Root jigsaw pool. Controls which structure pieces can generate. |
| `size` | int 0–30 | Jigsaw depth — how many additional pieces can branch from the start piece. |
| `start_height` | height provider | Y position of the first piece. `{"absolute": 0}` means at terrain surface level when combined with `project_start_to_heightmap`. |
| `project_start_to_heightmap` | heightmap type *(optional)* | If present, snaps the starting Y to this heightmap before applying `start_height`. `WORLD_SURFACE_WG` is the standard choice for surface structures. |
| `max_distance_from_center` | int 1–128 | Maximum distance (in blocks) that any jigsaw piece may be placed from the structure origin. |
| `spawn_overrides` | object | Vanilla structure mob spawn overrides (usually empty). |
