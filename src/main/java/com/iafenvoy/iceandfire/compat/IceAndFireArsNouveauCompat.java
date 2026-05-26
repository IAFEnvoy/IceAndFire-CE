package com.iafenvoy.iceandfire.compat;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.api.registry.JarBehaviorRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.iafenvoy.iceandfire.entity.SeaSerpentEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.world.phys.Vec3;

public class IceAndFireArsNouveauCompat {
    public static void init() {
        JarBehaviorRegistry.register(IafEntities.SEA_SERPENT.get(), new SeaSerpentBehavior());
    }

    private static class SeaSerpentBehavior extends JarBehavior<SeaSerpentEntity> {
        @Override
        public Vec3 scaleOffset(MobJarTile pBlockEntity) {
            return new Vec3(-0.85, -0.85, -0.85);
        }

        @Override
        public Vec3 translate(MobJarTile pBlockEntity) {
            return new Vec3(0, 0.4, 0);
        }
    }
}
