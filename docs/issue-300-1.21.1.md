# Death worm riding fix (#300)

Release: Ice and Fire CE 2.2.1 for Minecraft 1.21.1 / NeoForge 21.1.248.

The riding AI supplied destinations on the server, while the riding player
simulated the vehicle on the client. The client received no useful movement
input. The fix runs the original target calculation and ground move/jump
controllers through `getRiddenInput` on `isControlledByLocalInstance()`.
The existing ridden speed method uses the move controller's speed. The riding
goal reserves movement/look/jump and stops autonomous navigation/targeting.
The explicit jump key runs only on the controlling instance.

W selects the forward target, S the rear target, and A/D the side target.
S takes priority over A/D, and A/D over W. Side input multiplies controller
speed by 0.25; reverse input multiplies it by 0.15; S+A/S+D apply both.
The worm turns up to 90 degrees per tick and walks forward toward the target.
The original slow movement, vertical-look stopping tolerance, double-fishing-rod
mounting requirement, and permission to ride wild or other players' worms
are preserved.

## Verification

```powershell
.\gradlew.bat runClientRidingRegression --console=plain
.\gradlew.bat clean build --console=plain
```

On 2026-11-20, all 15 integrated-client riding scenarios passed: W/S/A/D,
turning, W+A/W+D/S+A/S+D, vertical look up/down, idle, jump, sand-surface
movement, and dismount. The test drives actual key mappings and verifies
heading, the turn limit, speed multipliers, key priority, and server position
synchronization. See `build/riding-regression-runtime/issue300-client-result.json`.
Copy results before `clean`. Test sources use the separate regression source
set and are excluded from the release JAR. Dedicated multiplayer and modpack
compatibility have not been tested.

For a manual check, summon an adult worm with
`/summon iceandfire:deathworm ~ ~ ~ {WormAge:5,Scale:1.0f}`, hold a fishing rod
in each hand, mount, and check the directions/combinations above. S+A/S+D can
produce no visible displacement at small scales because the original combined
slowdown falls below the movement threshold. Also check Space, Shift, remounting,
and autonomous behavior after dismount.
