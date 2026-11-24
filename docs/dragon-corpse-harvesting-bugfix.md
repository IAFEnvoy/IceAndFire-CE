# Dragon corpse interaction after indicator rollback

The damage-indicator changes from #284, including the later optional Re-Retro Damage Indicators Mixin, have been withdrawn at the user's request. This applies to Minecraft 26.1.2, 1.21.1 and 1.20.1.

Corpses restore the original behavior: while `isModelDead()` is true and the entity is not removed, `isAlive()` returns true so vanilla's ordinary mob interaction entry remains available. Dragon parts inherit their original selectable behavior. The dedicated corpse interaction bypass and the indicator compatibility Mixin are removed. No damage-indicator targeting suppression is provided.

The existing client-side success response for valid NeoForge corpse actions is retained to prevent one click from harvesting through both hands. The original loot rules and build-permission checks remain in use. The unrelated dragonforge precision fix is retained; dragonforge eligibility explicitly rejects `isModelDead()` so the restored interaction-state semantics cannot make a corpse fuel a forge.

The client regression covers 72 cases per Minecraft version: three dragon types, four targets (body, head, outer wing, tail tip), and six actions (empty-hand loot, main-hand blood bottle, off-hand fallback prevention, final skull, unrelated item, adventure mode). It uses the real crosshair, use key, network packets and server-side loot checks. The server regression also checks empty-hand and bottle interaction through all ten parts per dragon. Indicator acquisition/suppression checks were removed along with the feature.

```powershell
# 26.1.2: Java 25 toolchain with Java 21 available for assets
.\gradlew.bat -PcorpseRegression runClient --offline --console=plain
.\gradlew.bat -PauditRegression runGameTestServer --offline --console=plain

# 1.21.1: Java 21
.\gradlew.bat runClientCorpseRegression --offline --no-configuration-cache --console=plain
.\gradlew.bat runGameTestServer --offline --no-configuration-cache --console=plain

# 1.20.1: Java 17, Fabric runtime
.\gradlew.bat :fabric:runClientCorpseRegression --offline --console=plain
.\gradlew.bat :fabric:runGametest --offline --console=plain
```

Build release JARs with `build` and without opt-in regression properties. The 1.20.1 Forge build shares the common fix, but runtime verification uses Fabric. The [dragonforge coordinate precision fix](dragonforge-coordinate-precision.md) remains in place.

Rollback validation: 1.20.1 passed 72/72 actual client interaction cases and 32/32 server GameTests. Release compilation passed. Indicator suppression has been removed.
