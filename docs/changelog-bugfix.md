# Ice and Fire Community Edition 2.2.2 — Minecraft 1.21.1

Changes since 2.2.1-bugfix. Loader: NeoForge.

- Restored original dragon corpse harvesting and multipart selection by reverting the damage-indicator changes ([#284](https://github.com/IAFEnvoy/IceAndFire-CE/issues/284)). Indicator targeting suppression is no longer included.
- Fixed fire, ice and lightning transformations for gravel and cobblestone using shared material tags, including tagged blocks from other mods ([#302](https://github.com/IAFEnvoy/IceAndFire-CE/issues/302)).
- Added sand and red-sand transformation into the corresponding elemental gravel. Suspicious sand and other sand blocks with block entities remain unchanged.

Elemental gravel retains its existing natural reversion to vanilla gravel, including gravel produced from sand or red sand.
