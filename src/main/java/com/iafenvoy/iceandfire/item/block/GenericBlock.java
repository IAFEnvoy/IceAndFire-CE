package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.entity.DreadMobEntity;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class GenericBlock extends Block {
    public GenericBlock(Properties props) {
        super(props);
    }

    public static GenericBlock builder(float hardness, float resistance, SoundType sound, MapColor color, NoteBlockInstrument instrument, PushReaction reaction, boolean ignited) {
        Properties props = Properties.of().mapColor(color).sound(sound).strength(hardness, resistance).requiresCorrectToolForDrops();
        if (instrument != null) props.instrument(instrument);
        if (reaction != null) props.pushReaction(reaction);
        if (ignited) props.ignitedByLava();
        return new GenericBlock(props);
    }

    public static GenericBlock builder(float hardness, float resistance, SoundType sound, boolean slippery, MapColor color, NoteBlockInstrument instrument, PushReaction reaction, boolean ignited) {
        Properties props = Properties.of().mapColor(color).sound(sound).strength(hardness, resistance).friction(0.98F);
        if (instrument != null) props.instrument(instrument);
        if (reaction != null) props.pushReaction(reaction);
        if (ignited) props.ignitedByLava();
        return new GenericBlock(props);
    }

    @Deprecated
    public boolean canEntitySpawn(BlockState state, Entity entity) {
        return entity instanceof DreadMobEntity || !DragonUtils.isDreadBlock(state);
    }
}
