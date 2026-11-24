# Dragon block conversion (issue #302)

Minecraft branch: `26.1.2`. Fire, ice and lightning transformations use the existing shared gravel and cobblestone block tags. Cobblestone no longer needs the unrelated vanilla base-stone tag, and block translation names no longer determine its material.

The shared tags are `c:gravels` and `c:cobblestones`. This reuses the material classification already used by dragon roost generation and accepts tagged third-party blocks. Untagged slabs and walls are not converted just because their names contain "cobblestone". Natural base stone continues to produce elemental stone.

No charred, frozen or crackled sand blocks are registered in this branch. A leftover `frozen_sand.png` texture does not define a block. Sand and red sand, plus other blocks in `minecraft:sand` without a block entity, therefore produce the corresponding charred, frozen or crackled gravel. Suspicious sand is preserved so its archaeology data is not discarded.

All elemental gravel and cobblestone outputs keep `revert=true`. Their existing fixed return states remain unchanged: elemental gravel eventually returns to vanilla gravel (including gravel produced from sand), and elemental cobblestone returns to vanilla cobblestone. Original modded block identities and sand colors are not stored. No new block, configuration or mandatory dependency is added.

Loader-backed regressions verify all three elements, vanilla gravel/cobblestone, sand and red sand, test-only tagged gravel/sand/cobblestone stand-ins, return properties, natural-stone classification, archaeology blocks and unrelated blocks. On 1.20.1, stand-ins exercise both `c` and `forge` compatibility tag sources. Test entries and regression classes must not ship in release JARs.

Issue #302 block-conversion validation: 26.1.2 passed 36/36 server GameTests, including all three elements, gravel/cobblestone tags, sand/red sand, tagged stand-ins, reversion flags and protected archaeology controls. Release compilation passed.
