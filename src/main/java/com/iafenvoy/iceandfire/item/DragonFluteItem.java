package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.util.dragon.IDragonFlute;
import com.iafenvoy.iceandfire.registry.IafSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DragonFluteItem extends Item {
    public DragonFluteItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStackIn = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(this, 60);

        float range = 16 * IafCommonConfig.INSTANCE.dragon.fluteDistance.getValue();
        List<Entity> list = worldIn.getEntities(player, (new AABB(player.getX(), player.getY(), player.getZ(), player.getX() + 1.0D, player.getY() + 1.0D, player.getZ() + 1.0D)).inflate(range, 256, range));
        list.sort(new Sorter(player));
        List<IDragonFlute> dragons = new ArrayList<>();
        for (Entity entity : list)
            if (entity instanceof IDragonFlute flute)
                dragons.add(flute);
        for (IDragonFlute dragon : dragons)
            dragon.onHearFlute(player);
        worldIn.playSound(player, player.blockPosition(), IafSounds.DRAGONFLUTE.get(), SoundSource.NEUTRAL, 1, 1.75F);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStackIn);
    }

    public static class Sorter implements Comparator<Entity> {
        private final Entity entity;

        public Sorter(Entity entity) {
            this.entity = entity;
        }

        @Override
        public int compare(Entity entity1, Entity entity2) {
            double d0 = this.entity.distanceToSqr(entity1);
            double d1 = this.entity.distanceToSqr(entity2);
            return Double.compare(d0, d1);
        }
    }
}