# Dragon corpse harvesting hotfix

Applies to Minecraft 26.1.2 and 1.21.1 (`2.2.1-bugfix`), and Minecraft 1.20.1 (`1.3.1-bugfix`).

## Cause

The #284 fix made corpses correctly return `false` from `isAlive()`. Vanilla's mob interaction entry rejects dead mobs before reaching Ice and Fire's corpse-harvesting code. The old regression called `mobInteract` / `interactMob` directly and therefore missed this rejection.

NeoForge corpse parts intentionally remain unpickable because Re-Retro Damage Indicators resolves multipart targets to the parent. The corpse body remains selectable. In 1.20.1, the separate part entities were still selectable but their server interaction guard rejected dead parents, allowing parts to intercept clicks meant for the corpse body.

## Changes

- 26.1.2 routes corpse interactions from the public `interact` entry to harvesting. Live dragons retain vanilla interaction handling.
- 1.21.1 and 1.20.1 route corpse interactions through `interactAt`, which the real client sends before the ordinary interaction. Their vanilla mob interaction method is final.
- Valid corpse interactions now return success on the NeoForge client, preventing a single click from also harvesting with the off hand. Loot and bottle consumption remain server-side.
- 1.20.1 stops picking corpse parts so clicks reach the harvestable body, matching the NeoForge branches.
- Death state, the existing damage-indicator targeting workaround, loot rules and build-permission checks remain in place.

## Verification

The server regression now enters the public interaction method and checks both empty-hand harvesting and glass-bottle blood collection for fire, ice and lightning dragons.

The new integrated-client regression uses the actual crosshair raycast and use key. It sends real interaction packets, then checks server-side death stage, blood, bottle consumption and skull drops. Each dragon type exercises six scenarios: empty hand, main-hand bottle, empty main hand with a bottle in the off hand (no second harvest), final skull harvest, an unrelated held item, and adventure mode. Corpses must remain dead and selectable; their parts must not intercept the crosshair.

Run from the respective checkout with the correct JDK:

```powershell
# Minecraft 26.1.2 (Java 25 toolchain; Java 21 available for asset tasks)
.\gradlew.bat -PcorpseRegression runClient --offline --console=plain
.\gradlew.bat build --offline --console=plain

# Minecraft 1.21.1 (Java 21)
.\gradlew.bat runClientCorpseRegression --offline --no-configuration-cache --console=plain
.\gradlew.bat build --offline --no-configuration-cache --console=plain

# Minecraft 1.20.1 (Java 17)
.\gradlew.bat :fabric:runClientCorpseRegression --offline --console=plain
.\gradlew.bat build --offline --console=plain
```

Client results are written to `build/corpse-client-runtime/corpse-client-result.json` (under `fabric/` for 1.20.1). Regression classes and fixtures must be excluded from release JARs; build 26.1.2 without opt-in regression properties.

The third-party indicator HUD itself is not installed in these tests. Its previous death-state and part-selection workaround is preserved. Manually verify with the indicator installed by looking at a dead dragon until the HUD linger interval expires, then right-click its central body to collect blood and loot.

Results: 18/18 integrated-client scenarios passed on each Minecraft version (54 total). The 1.20.1 runtime uses Fabric; Forge is compiled from the same common harvesting fix but is not separately exercised in game. The third-party indicator HUD was not included.

The separate lightning-forge test failure was traced to float rounding of distant target block centers. The precision fix is now synchronized to all three Minecraft branches for all dragon types. See [the coordinate precision fix](dragonforge-coordinate-precision.md).
