package com.iafenvoy.iceandfire.data;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.registry.IafHippogryphTypes;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public record HippogryphType(String name, boolean developer, TagKey<Biome> spawnBiomes) {
    public static HippogryphType[] getWildTypes() {
        return new HippogryphType[]{IafHippogryphTypes.BLACK, IafHippogryphTypes.BROWN, IafHippogryphTypes.GRAY, IafHippogryphTypes.CHESTNUT, IafHippogryphTypes.CREAMY, IafHippogryphTypes.DARK_BROWN, IafHippogryphTypes.WHITE};
    }

    public static HippogryphType getRandomType() {
        return getWildTypes()[ThreadLocalRandom.current().nextInt(getWildTypes().length - 1)];
    }

    public static HippogryphType getBiomeType(Holder<Biome> biome) {
        List<HippogryphType> types = IafRegistries.HIPPOGRYPH_TYPE.stream().filter(x -> x.allowSpawn(biome)).toList();
        if (types.isEmpty()) return getRandomType();
        else {
            if (types.contains(IafHippogryphTypes.GRAY) && types.contains(IafHippogryphTypes.CHESTNUT))
                return IafHippogryphTypes.GRAY;
            return types.get(ThreadLocalRandom.current().nextInt(types.size()));
        }
    }

    public boolean allowSpawn(Holder<Biome> biome) {
        return biome.is(this.spawnBiomes);
    }

    public ResourceLocation getTexture(boolean blink) {
        return ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/" + this.name.toLowerCase(Locale.ROOT) + (blink ? "_blink" : "") + ".png");
    }
}
