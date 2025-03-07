package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.item.block.util.IDragonProof;
import net.minecraft.block.MapColor;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.sound.BlockSoundGroup;

public class BlockDragonBone extends PillarBlock implements IDragonProof {
    public BlockDragonBone() {
        super(Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sounds(BlockSoundGroup.WOOD).strength(30F, 500F).requiresTool());
    }
}
