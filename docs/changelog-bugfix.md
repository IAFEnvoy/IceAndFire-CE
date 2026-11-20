# Ice and Fire Community Edition 2.2.1-bugfix — Minecraft 26.1.2

Changes since the 2026-11-05 release. Loader: NeoForge.

## Hotfix

- Fixed fire, ice and lightning dragons aiming away from the dragonforge input at large world coordinates because of float rounding. Target block centers now retain double precision.

- Restored dragon corpse harvesting after the #284 death-state fix blocked vanilla's interaction entry. Empty-hand loot collection, glass-bottle blood collection and final skull harvesting now work again.
- Preserved the corpse death-state and damage-indicator targeting workaround. Aim at the corpse's central body to harvest it.
- Prevented a single valid right-click from harvesting twice through both hands on NeoForge.

## Added

- Added `worldgen.preventIceDragonStructuresInEnd`, enabled by default, to prevent new Ice Dragon Caves and Ice Dragon Roosts from generating in the End. Disable it to allow their previous generation behavior. Existing structures are unaffected.

## Fixed

- Fixed Dragonsteel visors, shoulder plates, and lower leg plates being hidden inside the armor shell. Also restored Sea Serpent shoulder plates for all colors ([#273](https://github.com/IAFEnvoy/IceAndFire-CE/issues/273)).
- Fixed dead dragons and their multipart hitboxes continuing to be acquired by damage-indicator HUDs, while preserving corpse selection and harvesting ([#284](https://github.com/IAFEnvoy/IceAndFire-CE/issues/284)).
- Fixed unridden hippogryphs rising indefinitely or dropping out of the sky. Improved takeoff, local flight destinations, descent, and landing, including behavior below Y=0 ([#291](https://github.com/IAFEnvoy/IceAndFire-CE/issues/291)).
- Fixed dragonforges selecting unavailable dragons or failing to receive breath. Corrected chained-dragon eligibility, element matching, range and line-of-sight checks, and stopping breath when the forge target becomes invalid ([#290](https://github.com/IAFEnvoy/IceAndFire-CE/issues/290)).
- Hardened Stymphalian Bird flight and following logic when the flock leader changes or disappears ([#295](https://github.com/IAFEnvoy/IceAndFire-CE/issues/295)).
- Fixed death worms being unable to move while ridden. Restored the original target-based turning, movement-key priorities, speed reductions, and rider jump handling ([#300](https://github.com/IAFEnvoy/IceAndFire-CE/issues/300)).
- Fixed the Siren Charm visual effect crashing the client on Minecraft 26.1.2. Updated its post-processing effect for the current renderer (PR [#294](https://github.com/IAFEnvoy/IceAndFire-CE/pull/294); thanks to GnomoMuitoLoco).
- Fixed the Hydra Heart failing to grant regeneration while equipped in a Curios charm slot ([#298](https://github.com/IAFEnvoy/IceAndFire-CE/issues/298)).

## Gameplay compatibility

- Death worms still require adulthood and a fishing rod in each hand to mount.
- Wild worms and worms owned by another player remain rideable, matching the original mod.

## Validation

- Full server regression suite: 34/34 passed. This includes real lightning-forge power transfer under 32 fixed random seeds, target precision checks for all three dragon types at positive and negative large coordinates, and corpse harvesting through the public interaction entry.
- Previously passed all 18 integrated-client corpse scenarios for this Minecraft version: fire, ice and lightning dragons; empty hand; blood bottles; off-hand fallback prevention; skull drops; unrelated held items; and adventure mode. These use the actual crosshair, use key, network packets and server-side inventory/loot checks.
- Release compilation passed. Release JARs exclude regression classes and test structures.
- The third-party damage-indicator HUD was not installed in these tests; its previous death-state and part-selection workaround is preserved.
