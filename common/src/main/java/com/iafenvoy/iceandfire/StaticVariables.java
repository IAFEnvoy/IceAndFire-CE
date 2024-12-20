package com.iafenvoy.iceandfire;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

public class StaticVariables {
    public static final Identifier MYRMEX_SYNC = new Identifier(IceAndFire.MOD_ID, "myrmex_sync");
    public static final Identifier DRAGON_CONTROL = new Identifier(IceAndFire.MOD_ID, "dragon_control");
    public static final Identifier DRAGON_SET_BURN_BLOCK = new Identifier(IceAndFire.MOD_ID, "dragon_set_burn_block");
    public static final Identifier MULTIPART_INTERACT = new Identifier(IceAndFire.MOD_ID, "multipart_interact");
    public static final Identifier PLAYER_HIT_MULTIPART = new Identifier(IceAndFire.MOD_ID, "player_hit_multipart");
    public static final Identifier START_RIDING_MOB_C2S = new Identifier(IceAndFire.MOD_ID, "start_riding_mob_c2s");
    public static final Identifier START_RIDING_MOB_S2C = new Identifier(IceAndFire.MOD_ID, "start_riding_mob_s2c");
    public static final Identifier UPDATE_PIXIE_HOUSE = new Identifier(IceAndFire.MOD_ID, "update_pixie_house");
    public static final Identifier UPDATE_PIXIE_JAR = new Identifier(IceAndFire.MOD_ID, "update_pixie_jar");
    public static final Identifier UPDATE_PODIUM = new Identifier(IceAndFire.MOD_ID, "update_podium");
    public static MinecraftServer server = null;
}
