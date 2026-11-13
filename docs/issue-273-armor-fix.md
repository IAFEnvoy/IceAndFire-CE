# Armor plate fix for Minecraft 1.20.1 (#273)

Backports the Dragonsteel and Sea Serpent plate correction visually tried on Minecraft 26.1.2. This branch uses Yarn names and keeps its existing model hierarchy and equipment-slot selection.

## Changes

- Fire, ice and lightning Dragonsteel armor: apply the base armor dilation to both visor halves, shoulder sleeves and lower leg plates.
- Sea Serpent armor: apply the base armor dilation to both shoulder sleeves for every color.
- Keep the existing skin clearance and the authored horn, spike and fin dimensions.

Helmets still use the inner model (`0.38` dilation), with the separate `hat` shell retaining its additional `0.5`. The visor front now reaches `z=-5.08`, projecting `0.20` model pixels beyond the hat shell at `z=-4.88`. Dragonsteel shoulder front clearance is `0.40`; lower leg plate front/back clearance is `0.50`; Sea Serpent shoulder front clearance is `0.50`.

## Verification

On 2026-11-13:

- `build --offline --no-configuration-cache --console=plain` passed for common, Fabric and Forge, including production JAR remapping.
- `:fabric:runClientRegression --offline --no-configuration-cache --console=plain` passed. The actual registered Uranus armor providers passed 19 model cases: all four slots for three Dragonsteel sets and all seven Sea Serpent shoulder variants. Checks measure emitted, transformed vertices, cover plate clearance and preserve every Dragonsteel horn's bounds.
- The existing client startup/resource reload checks also passed.
- These are client geometry checks, not a separate visual acceptance of the 1.20.1 appearance. The Forge artifact was compiled and packaged; the automated client checks ran on Fabric against the shared model code.

Run both commands from the repository root with Java 17. The client test runs in its isolated development directory and exits automatically. Its result is `fabric/build/client-smoke-run/client-smoke-result.txt`, which must start with `PASS` and include `armor model cases=19`. Build/client logs for this run are `build/issue273-build.log` and `build/issue273-client.log`.

The test classes remain in `fabric/src/gametest` and are excluded from release JARs.

## Visual checks

Wear each Dragonsteel set and a Sea Serpent set, then compare helmets from the front, side and below and check shoulder sleeves and lower leg plates. Repeat on armor stands and while standing, crouching and walking with outer skin layers enabled. Check individual pieces and complete sets. Dragon Scale short spikes and Silver skirt clearance remain separate review candidates and are outside this backport.
