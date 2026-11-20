# Death worm riding fix (#300)

Release: Ice and Fire CE 1.3.1 for Minecraft 1.20.1 (Fabric and Forge).

The server riding goal supplied movement destinations, but the player-controlled
vehicle was simulated on the client without rider input. The fix moves the
original target calculation into `getControlledMovementInput`, guarded by
`isLogicalSideForUpdatingMovement()`, and ticks the ground move/jump controls.
The normal `getSaddledSpeed` implementation uses the controller-produced speed.
The riding goal continues to stop autonomous navigation/target selection and
reserves move/look/jump controls. Explicit jumping runs on the controlling side.

The implementation is in the common source set, shared by Fabric and Forge.
Controls retain the original behavior: W selects the forward target, S the rear
target, and A/D the side target. S overrides A/D, while A/D override W. Sideways
input applies a 0.25 controller-speed multiplier and reverse input applies 0.15;
S+A/S+D apply both. The controller turns toward the target (up to 90 degrees
per tick) and walks forward, preserving the original slow movement and vertical
look stopping tolerance. The adult/two-fishing-rod mounting rule is unchanged;
wild worms and worms owned by another player remain rideable.

## Verification

```powershell
.\gradlew.bat :fabric:runClientRidingRegression --console=plain
.\gradlew.bat :fabric:build :forge:build copyJars --console=plain
```

On 2026-11-20, all 15 Fabric integrated-client riding scenarios passed.
The isolated Fabric client regression creates an integrated flat world, mounts
an adult worm through the double-fishing-rod interaction, and drives actual
client key bindings. Its 15 scenarios cover W/S/A/D, turning, the four forward/
reverse plus side combinations, vertical look up/down, idle, jumping, sand-surface
movement, and dismount. Assertions check headings, turn limits, key priorities,
speed multipliers, displacement, and server position synchronization.

Read `fabric/build/riding-regression-run/issue300-client-result.json`; success
requires `passed: 15, failed: 0`. The test mod is in the separate `gametest`
source set and must not be included in either release JAR. Both Fabric and Forge release builds passed; their versions are 1.3.1, and
neither includes regression classes or the regression test mod. Runtime riding checks
use Fabric; Forge shares the same implementation but requires a separate manual
runtime check. Dedicated multiplayer and modpack compatibility are not covered.

For a manual check, summon an adult worm with
`/summon iceandfire:deathworm ~ ~ ~ {WormAge:5,Scale:1.0f}`, hold a fishing rod
in each hand, and mount. Check S/A/D turning toward their target direction,
W+A/W+D selecting the side, and S+A/S+D selecting the rear with both reductions.
At small scales the combined slowdown can fall below the movement threshold,
producing no visible displacement. Also check Space, Shift, remounting, and
unmounted autonomous movement on grass and sand.
