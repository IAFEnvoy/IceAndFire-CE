# Armor plate fix for Minecraft 1.21.1 (#273)

Backports the Dragonsteel and Sea Serpent plate correction visually tried on Minecraft 26.1.2. This branch retains its separate root-level `hat`, its existing armor providers and its existing equipment-slot selection.

## Changes

- Fire, ice and lightning Dragonsteel armor: apply the base armor deformation to both visor halves, shoulder sleeves and lower leg plates.
- Sea Serpent armor: apply the base armor deformation to both shoulder sleeves for every color.
- Keep the existing skin clearance and the authored horn, spike and fin dimensions.

Helmets still use the inner model (`0.38` deformation), with the separate `hat` shell retaining its additional `0.5`. The visor front now reaches `z=-5.08`, projecting `0.20` model pixels beyond the hat shell at `z=-4.88`. Dragonsteel shoulder front clearance is `0.40`; lower leg plate front/back clearance is `0.50`; Sea Serpent shoulder front clearance is `0.50`.

## Verification

On 2026-11-13, both commands passed from the repository root with Java 21:

```powershell
.\gradlew.bat build --offline --no-configuration-cache --console=plain
.\gradlew.bat runClientArmorRegression build --offline --no-configuration-cache --console=plain
```

The isolated client run passed 19 model cases through the actual Uranus renderer registry: all four slots for three Dragonsteel sets and all seven Sea Serpent shoulder variants. Checks measure emitted, transformed vertices, cover plate clearance and preserve every Dragonsteel horn's bounds. The result is `build/armor-regression-runtime/armor-regression-result.json`, with `passed=19` and `failed=0`. Build/client logs for this run are `build/issue273-build.log` and `build/issue273-client.log`.

The regression classes remain in the separate `src/regression` source set and are excluded from the release JAR. The dedicated `runClientArmorRegression` run loads that source set; normal client runs retain the main source set. The client exits automatically after the checks. These are client geometry checks, not a separate visual acceptance of the 1.21.1 appearance.

The original investigation and supplied-JAR comparisons are recorded in `issue-273-armor-model-analysis.md`.

## Visual checks

Wear each Dragonsteel set and a Sea Serpent set, then compare helmets from the front, side and below and check shoulder sleeves and lower leg plates. Repeat on armor stands and while standing, crouching and walking with outer skin layers enabled. Check individual pieces and complete sets. Dragon Scale short spikes and Silver skirt clearance remain separate review candidates and are outside this backport.
