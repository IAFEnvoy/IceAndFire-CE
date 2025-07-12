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

public class ItemDragonFlute extends Item {
    public ItemDragonFlute() {
        super(new Settings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity player, Hand hand) {
        ItemStack itemStackIn = player.getStackInHand(hand);
        player.getItemCooldownManager().set(this, 60);

        float chunksize = 16 * IafCommonConfig.INSTANCE.dragon.fluteDistance.getValue();
        List<Entity> list = worldIn.getOtherEntities(player, (new Box(player.getX(), player.getY(), player.getZ(), player.getX() + 1.0D, player.getY() + 1.0D, player.getZ() + 1.0D)).expand(chunksize, 256, chunksize));
        list.sort(new Sorter(player));
        List<IDragonFlute> dragons = new ArrayList<>();
        for (Entity entity : list) {
            if (entity instanceof IDragonFlute) {
                dragons.add((IDragonFlute) entity);
            }
        }

        for (IDragonFlute dragon : dragons) {
            dragon.onHearFlute(player);
        }
        worldIn.playSound(player, player.getBlockPos(), IafSounds.DRAGONFLUTE.get(), SoundCategory.NEUTRAL, 1, 1.75F);

        return new TypedActionResult<>(ActionResult.SUCCESS, itemStackIn);
    }

    public static class Sorter implements Comparator<Entity> {
        private final Entity theEntity;

        public Sorter(Entity theEntityIn) {
            this.theEntity = theEntityIn;
        }

        @Override
        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.squaredDistanceTo(p_compare_1_);
            double d1 = this.theEntity.squaredDistanceTo(p_compare_2_);
            return Double.compare(d0, d1);
        }
    }
}