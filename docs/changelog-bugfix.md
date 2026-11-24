# Ice and Fire Community Edition 2.2.2 — Minecraft 1.21.1

Release prepared: 2026-11-24. Changes since the 2026-11-05 release. Loader: NeoForge.

## Added

- Added `worldgen.preventIceDragonStructuresInEnd`, enabled by default, to prevent new Ice Dragon Caves and Ice Dragon Roosts from generating in the End. Disable it to allow their previous generation behavior. Existing structures are unaffected.
- Added fire, ice and lightning transformations from sand and red sand into the corresponding elemental gravel. Sand blocks with block entities, including suspicious sand, remain unchanged.

## Fixed

- Fixed gravel and cobblestone transformations using shared material tags, including tagged blocks from other mods. Cobblestone no longer requires the vanilla base-stone tag or a matching translation name ([#302](https://github.com/IAFEnvoy/IceAndFire-CE/issues/302)).
- Restored dragon corpse harvesting through the body, head, wings and tail. Empty-hand loot collection, glass-bottle blood collection and final skull harvesting use the original interaction behavior.
- Prevented a single corpse-harvesting right-click from collecting twice through both hands on NeoForge.
- Fixed fire, ice and lightning dragons aiming away from dragonforge inputs at distant world coordinates because of coordinate rounding.
- Fixed Dragonsteel visors, shoulder plates, and lower leg plates being hidden inside the armor shell. Also restored Sea Serpent shoulder plates for all colors ([#273](https://github.com/IAFEnvoy/IceAndFire-CE/issues/273)).
- Fixed unridden hippogryphs rising indefinitely or dropping out of the sky. Improved takeoff, local flight destinations, descent, and landing, including behavior below Y=0 ([#291](https://github.com/IAFEnvoy/IceAndFire-CE/issues/291)).
- Fixed dragonforges selecting unavailable dragons or failing to receive breath. Corrected chained-dragon eligibility, element matching, range and line-of-sight checks, and stopping breath when the forge target becomes invalid ([#290](https://github.com/IAFEnvoy/IceAndFire-CE/issues/290)).
- Hardened Stymphalian Bird flight and following logic when the flock leader changes or disappears ([#295](https://github.com/IAFEnvoy/IceAndFire-CE/issues/295)).
- Fixed death worms being unable to move while ridden. Restored the original target-based turning, movement-key priorities, speed reductions, and rider jump handling ([#300](https://github.com/IAFEnvoy/IceAndFire-CE/issues/300)).

## Compatibility notes

- Withdrawn the earlier damage-indicator changes for [#284](https://github.com/IAFEnvoy/IceAndFire-CE/issues/284), including the optional indicator compatibility Mixin. Original corpse interaction and multipart selection behavior are restored; indicator suppression is no longer provided.
- Elemental gravel produced from gravel, sand or red sand retains its existing natural reversion to vanilla gravel. Elemental cobblestone reverts to vanilla cobblestone; original modded block identities are not stored.
- Death worms require adulthood and a fishing rod in each hand to mount. Wild worms and worms owned by another player remain rideable, matching the original mod.

## Validation

- Release JARs rebuilt on 2026-11-24. Internal mod versions, Minecraft dependency metadata, SHA-256 checksums and exclusion of regression fixtures were checked.
- Latest gameplay regression result: 25/25 server GameTests passed, including three-element block conversion, forge behavior and creature checks. These gameplay tests ran during the preceding fixes and were not repeated for this packaging pass.
- The earlier corpse-interaction validation passed 72 integrated-client cases for this Minecraft version, covering body/part targets, empty-hand collection, blood bottles, skulls and interaction restrictions.
