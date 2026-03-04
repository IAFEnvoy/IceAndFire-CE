package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.util.dragon.IDragonFlute;
import com.iafenvoy.iceandfire.registry.IafSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DragonFluteItem extends Item {
    public DragonFluteItem() {
        super(new Settings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity player, Hand hand) {
        ItemStack itemStackIn = player.getStackInHand(hand);
        player.getItemCooldownManager().set(this, 60);

        float range = 16 * IafCommonConfig.INSTANCE.dragon.fluteDistance.getValue();
        List<Entity> list = worldIn.getOtherEntities(player, (new Box(player.getX(), player.getY(), player.getZ(), player.getX() + 1.0D, player.getY() + 1.0D, player.getZ() + 1.0D)).expand(range, 256, range));
        list.sort(new Sorter(player));
        List<IDragonFlute> dragons = new ArrayList<>();
        for (Entity entity : list)
            if (entity instanceof IDragonFlute flute)
                dragons.add(flute);
        for (IDragonFlute dragon : dragons)
            dragon.onHearFlute(player);
        worldIn.playSound(player, player.getBlockPos(), IafSounds.DRAGONFLUTE.get(), SoundCategory.NEUTRAL, 1, 1.75F);
        return new TypedActionResult<>(ActionResult.SUCCESS, itemStackIn);
    }

    public static class Sorter implements Comparator<Entity> {
        private final Entity entity;

        public Sorter(Entity entity) {
            this.entity = entity;
        }

        @Override
        public int compare(Entity entity1, Entity entity2) {
            double d0 = this.entity.squaredDistanceTo(entity1);
            double d1 = this.entity.squaredDistanceTo(entity2);
            return Double.compare(d0, d1);
        }
    }
}