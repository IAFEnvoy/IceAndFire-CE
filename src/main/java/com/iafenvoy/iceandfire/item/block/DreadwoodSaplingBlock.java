package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.registry.IafFeatures;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class DreadwoodSaplingBlock extends SaplingBlock {
    public DreadwoodSaplingBlock() {
        super(new TreeGrower("dread_wood", 0.1F, Optional.empty(), Optional.empty(), Optional.of(IafFeatures.DREADWOOD), Optional.of(IafFeatures.DREADWOOD_LARGE), Optional.empty(), Optional.empty()), Properties.ofFullCopy(Blocks.OAK_SAPLING));
    }
}
