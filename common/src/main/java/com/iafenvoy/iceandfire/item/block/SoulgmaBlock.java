package com.iafenvoy.iceandfire.item.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.sound.BlockSoundGroup;

public class SoulgmaBlock extends Block {
    public SoulgmaBlock() {
        super(Settings.create()
            .mapColor(MapColor.BLUE) // Soul fire blue color
            .sounds(BlockSoundGroup.STONE)
            .strength(0.5F, 0.5F) // Same as magma block
            .luminance(state -> 3) // Soul fire glow
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresTool()
        );
    }
}
