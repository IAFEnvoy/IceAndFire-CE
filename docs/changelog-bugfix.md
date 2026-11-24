# Ice and Fire Community Edition 1.3.2 — Minecraft 1.20.1

Changes since 1.3.1-bugfix. Loader: Fabric and Forge.

- Restored original dragon corpse harvesting and multipart selection by reverting the damage-indicator changes ([#284](https://github.com/IAFEnvoy/IceAndFire-CE/issues/284)). Indicator targeting suppression is no longer included.
- Fixed fire, ice and lightning transformations for gravel and cobblestone using shared material tags, including tagged blocks from other mods ([#302](https://github.com/IAFEnvoy/IceAndFire-CE/issues/302)).
- Added sand and red-sand transformation into the corresponding elemental gravel. Suspicious sand and other sand blocks with block entities remain unchanged.
- Unified external tag reads under `iceandfire:compat/...`. Moved vanilla defaults into compat tags and kept `c`/`forge` exports limited to concrete mod entries, preventing reverse tag dependencies.

Elemental gravel retains its existing natural reversion to vanilla gravel, including gravel produced from sand or red sand.
