# Hydra Heart in Curios (#298)

Minecraft 26.1.2 / NeoForge 26.1.2.97 / Curios 15.0.0+26.1.2.

## Cause and fix

The 26.1.2 port replaced the inventory slot index check with a check that the
heart's actual item stack is in the player's hotbar. The Curios compatibility
wrapper still forwarded equipped hearts to `inventoryTick`, so those hearts
failed the hotbar check and never applied regeneration.

The hotbar and the server-side Curios callback now call the same regeneration
method. The Curios callback applies the effect directly while equipped. The
obsolete inventory-tick wrapper has been removed. Health brackets, effect
duration, and the handling of existing stronger regeneration are preserved.

## Automated regression

Run from the project root with Java 25:

```powershell
.\gradlew.bat -PauditRegression runGameTestServer --console=plain
.\gradlew.bat clean build --console=plain
```

The two Hydra Heart tests cover the registered Curios item capability on a heart
equipped in a real charm inventory, full health and every health bracket in both
configuration modes, configured effect duration, the first and last hotbar slots,
ordinary inventory slots, and preservation of a stronger regeneration effect.
The Curios test invokes its equipped-item callback directly; it does not exercise
the client UI or Curios' automatic entity tick scheduling.

Run the final build without `auditRegression` to exclude test classes and the
test structure from the release JAR.

Validated on 2026-11-20: all 33 required GameTests passed, including both Hydra
Heart regressions. A clean release build also passed, and its JAR contains no
regression classes, test structure, or obsolete Curios wrapper. Client UI and
automatic Curios ticking have not been manually verified; use the steps below.

## In-game verification

1. Install this build with its required dependencies and Curios. Disable natural
   regeneration with `/gamerule minecraft:natural_health_regeneration false`.
2. Injure the player, clear existing regeneration, and equip the only Hydra Heart
   in the functional charm slot. Regeneration should appear and heal the player.
3. Move the heart into the ordinary inventory, clear regeneration again, and
   confirm it is not reapplied. Moving it to either end of the hotbar should
   enable regeneration again.
4. Repeat with `tools.hydraHeartNerfedBrackets` enabled. At or above 75% health,
   the heart should not apply regeneration; below that threshold, the configured
   weaker brackets should work from both locations.

Clear the previous effect between cases: unequipping the heart does not remove
an effect that has already been granted. Restore the gamerule after testing.
