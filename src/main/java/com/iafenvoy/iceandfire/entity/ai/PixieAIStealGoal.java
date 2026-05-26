package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.PixieEntity;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class PixieAIStealGoal extends Goal {
    private final PixieEntity temptedEntity;
    private Player temptingPlayer;
    private int delayTemptCounter = 0;
    private boolean isRunning;

    public PixieAIStealGoal(PixieEntity temptedEntityIn) {
        this.temptedEntity = temptedEntityIn;
    }

    @Override
    public boolean canUse() {
        if (!IafCommonConfig.INSTANCE.pixie.stealItems.getValue() || !this.temptedEntity.getMainHandItem().isEmpty() || this.temptedEntity.stealCooldown > 0)
            return false;
        if (this.temptedEntity.getRandom().nextInt(200) == 0) return false;
        if (this.temptedEntity.isTame()) return false;
        if (this.delayTemptCounter > 0) {
            --this.delayTemptCounter;
            return false;
        } else {
            this.temptingPlayer = this.temptedEntity.level().getNearestPlayer(this.temptedEntity, 10.0D);
            return this.temptingPlayer != null && (this.temptedEntity.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.temptingPlayer.getInventory().isEmpty() && !this.temptingPlayer.isCreative());
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.temptedEntity.isTame() && this.temptedEntity.getMainHandItem().isEmpty() && this.delayTemptCounter == 0 && this.temptedEntity.stealCooldown == 0;
    }

    @Override
    public void start() {
        this.isRunning = true;
    }

    @Override
    public void stop() {
        this.temptingPlayer = null;
        if (this.delayTemptCounter < 10)
            this.delayTemptCounter += 10;
        this.isRunning = false;
    }

    @Override
    public void tick() {
        this.temptedEntity.getLookControl().setLookAt(this.temptingPlayer, this.temptedEntity.getMaxHeadYRot() + 20, this.temptedEntity.getMaxHeadXRot());
        ArrayList<Integer> slotlist = new ArrayList<>();
        if (this.temptedEntity.distanceToSqr(this.temptingPlayer) < 3D && !this.temptingPlayer.getInventory().isEmpty()) {

            for (int i = 0; i < this.temptingPlayer.getInventory().getContainerSize(); i++) {
                ItemStack targetStack = this.temptingPlayer.getInventory().getItem(i);
                if (!Inventory.isHotbarSlot(i) && !targetStack.isEmpty() && targetStack.isStackable() && !targetStack.is(IafItemTags.PIXIE_STOLEN_BLACKLIST))
                    slotlist.add(i);
            }
            if (!slotlist.isEmpty()) {
                final int slot = slotlist.size() == 1 ? slotlist.getFirst() : slotlist.get(ThreadLocalRandom.current().nextInt(slotlist.size()));
                ItemStack randomItem = this.temptingPlayer.getInventory().getItem(slot);
                this.temptedEntity.setItemInHand(InteractionHand.MAIN_HAND, randomItem);
                this.temptingPlayer.getInventory().removeItemNoUpdate(slot);
                this.temptedEntity.playSound(IafSounds.PIXIE_TAUNT.get(), 1F, 1F);

                for (PixieEntity pixie : this.temptingPlayer.level().getEntitiesOfClass(PixieEntity.class, this.temptedEntity.getBoundingBox().inflate(40)))
                    pixie.stealCooldown = 1000 + pixie.getRandom().nextInt(3000);
                if (this.temptingPlayer != null)
                    this.temptingPlayer.addEffect(new MobEffectInstance(this.temptedEntity.negativePotions[this.temptedEntity.getColor()], 100));
            } else//If the pixie couldn't steal anything
                this.delayTemptCounter = 10 * 20;
        } else
            this.temptedEntity.getMoveControl().setWantedPosition(this.temptingPlayer.getX(), this.temptingPlayer.getY() + 1.5F, this.temptingPlayer.getZ(), 1D);
    }

    public boolean isRunning() {
        return this.isRunning;
    }
}