# Ice and Fire Community Edition 1.3.1-bugfix — Minecraft 1.20.1

Changes since the 2026-11-05 release. Loader: Fabric and Forge.

## Hotfix

- Fixed fire, ice and lightning dragons aiming away from the dragonforge input at large world coordinates because of float rounding. Target block centers now retain double precision.

- Restored dragon corpse harvesting after the #284 death-state fix blocked vanilla's interaction entry. Empty-hand loot collection, glass-bottle blood collection and final skull harvesting now work again.
- Preserved the corpse death-state and damage-indicator targeting workaround. Aim at the corpse's central body to harvest it.
- Stopped separate corpse parts from intercepting right-clicks rejected by their server interaction guard.

## Added

- Added `worldgen.preventIceDragonStructuresInEnd`, enabled by default, to prevent new Ice Dragon Caves and Ice Dragon Roosts from generating in the End. Disable it to allow their previous generation behavior. Existing structures are unaffected.

## Fixed

- Fixed Dragonsteel visors, shoulder plates, and lower leg plates being hidden inside the armor shell. Also restored Sea Serpent shoulder plates for all colors ([#273](https://github.com/IAFEnvoy/IceAndFire-CE/issues/273)).
- Fixed dragon corpses being reported as alive, while preserving corpse selection and harvesting ([#284](https://github.com/IAFEnvoy/IceAndFire-CE/issues/284)).
- Fixed unridden hippogryphs rising indefinitely or dropping out of the sky. Improved takeoff, local flight destinations, descent, and landing, including behavior below Y=0 ([#291](https://github.com/IAFEnvoy/IceAndFire-CE/issues/291)).
- Fixed dragonforges selecting unavailable dragons or failing to receive breath. Corrected chained-dragon eligibility, element matching, range and line-of-sight checks, and stopping breath when the forge target becomes invalid ([#290](https://github.com/IAFEnvoy/IceAndFire-CE/issues/290)).
- Fixed a Stymphalian Bird flight crash when its flock had no valid leader. Improved leader re-election and removal of birds that leave the flock ([#295](https://github.com/IAFEnvoy/IceAndFire-CE/issues/295)).
- Fixed death worms being unable to move while ridden. Restored the original target-based turning, movement-key priorities, speed reductions, and rider jump handling ([#300](https://github.com/IAFEnvoy/IceAndFire-CE/issues/300)).

## Gameplay compatibility

- Death worms still require adulthood and a fishing rod in each hand to mount.
- Wild worms and worms owned by another player remain rideable, matching the original mod.

## Validation

- Full server regression suite: 31/31 passed. This includes real lightning-forge power transfer under 32 fixed random seeds, target precision checks for all three dragon types at positive and negative large coordinates, and corpse harvesting through the public interaction entry.
- Previously passed all 18 integrated-client corpse scenarios for this Minecraft version: fire, ice and lightning dragons; empty hand; blood bottles; off-hand fallback prevention; skull drops; unrelated held items; and adventure mode. These use the actual crosshair, use key, network packets and server-side inventory/loot checks.
- Release compilation passed. Release JARs exclude regression classes and test structures.
- The third-party damage-indicator HUD was not installed in these tests; its previous death-state and part-selection workaround is preserved.
- Runtime validation uses Fabric. Both Fabric and Forge release JARs compiled; Forge was not separately tested in game.
