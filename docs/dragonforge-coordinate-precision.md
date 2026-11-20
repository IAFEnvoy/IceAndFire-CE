# Dragonforge target coordinate precision

Applies to Minecraft 26.1.2 and 1.21.1 (`2.2.1-bugfix`), and Minecraft 1.20.1 (`1.3.1-bugfix`, Fabric and Forge).

## Cause

All three dragon types calculated the target with `blockCoordinate + 0.5F` before passing it to the double-precision breath API. At large world coordinates, float rounding discards the half-block offset and can move the target into a neighboring block.

For example, the original code aims at `(8388608.0, 100.5, 8388610.0)` for input block `(8388608, 100, 8388609)`, instead of its center `(8388608.5, 100.5, 8388609.5)`. Breath visibility and impact checks can then disagree with the input's eligibility raycast. The input selects the dragon, but the breath does not consistently reach the forge.

The GameTest server can choose distant fixture coordinates, explaining why the old lightning-forge test passed at some locations and failed at others.

## Change

Use `0.5D` when computing fire, ice and lightning dragons' forge targets on all three branches. The normal breath, line-of-sight, warm-up and power-transfer logic stays in use.

## Verification

Latest verified full server suites:

| Minecraft | Runtime | Passed |
| --- | --- | --- |
| 26.1.2 | NeoForge | 34/34 |
| 1.21.1 | NeoForge | 23/23 |
| 1.20.1 | Fabric | 31/31 |


The deterministic target regression runs the real `breathFireAtPos` method for fire, ice and lightning dragons at the origin, beyond positive and negative 8,388,608 coordinates, and near positive/negative 29 million coordinates. It captures the target passed to the breath event and cancels those captured events to avoid attacking unloaded blocks. The listener is unregistered afterward. The original 26.1.2 code failed this check before the fix.

The assembled lightning-forge test exercises 32 fixed dragon random seeds and checks actual receipt of breath power for every seed. Baby/sleeping/dead/wild dragon rejection, chained-dragon eligibility, and obstruction checks are retained.

The 1.21.1 flight test now keeps its arena chunks ticking, matching the existing 26.1.2 fixture. This prevents the test dragon from pausing at the test world's active chunk boundary; hippogryph production behavior is unchanged.

Run from each branch checkout:

```powershell
# Minecraft 26.1.2: Java 25 toolchain; Java 21 available for asset tasks
.\gradlew.bat -PauditRegression runGameTestServer --offline --console=plain
.\gradlew.bat build --offline --console=plain

# Minecraft 1.21.1: Java 21
.\gradlew.bat runGameTestServer --offline --no-configuration-cache --console=plain
.\gradlew.bat build --offline --no-configuration-cache --console=plain

# Minecraft 1.20.1: Java 17; Fabric runtime, both loaders compiled
.\gradlew.bat :fabric:runGametest --offline --console=plain
.\gradlew.bat build --offline --console=plain
```

Release builds exclude regression classes and fixtures. The 26.1.2 release build must omit opt-in regression properties. The 1.20.1 runtime uses Fabric; Forge is compiled from the same common fix and is not separately exercised in game. The third-party damage-indicator HUD is not installed in these tests.
