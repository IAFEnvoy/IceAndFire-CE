package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.entity.EntityDreadMob;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.sound.BlockSoundGroup;

public class BlockGeneric extends Block {
    public BlockGeneric(Settings props) {
        super(props);
    }

    public static BlockGeneric builder(float hardness, float resistance, BlockSoundGroup sound, MapColor color, NoteBlockInstrument instrument, PistonBehavior reaction, boolean ignited) {
        Settings props = Settings.create().mapColor(color).sounds(sound).strength(hardness, resistance).requiresTool();
        if (instrument != null) props.instrument(instrument);
        if (reaction != null) props.pistonBehavior(reaction);
        if (ignited) props.burnable();
        return new BlockGeneric(props);
    }

    public static BlockGeneric builder(float hardness, float resistance, BlockSoundGroup sound, boolean slippery, MapColor color, NoteBlockInstrument instrument, PistonBehavior reaction, boolean ignited) {
        Settings props = Settings.create().mapColor(color).sounds(sound).strength(hardness, resistance).slipperiness(0.98F);
        if (instrument != null) props.instrument(instrument);
        if (reaction != null) props.pistonBehavior(reaction);
        if (ignited) props.burnable();
        return new BlockGeneric(props);
    }

    @Deprecated
    public boolean canEntitySpawn(BlockState state, Entity entity) {
        return entity instanceof EntityDreadMob || !DragonUtils.isDreadBlock(state);
    }
}
