package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.registry.IafParticles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class HydraHeadEntity extends MultipartPartEntity<HydraEntity> {
    public int headIndex;
    public HydraEntity hydra;
    private boolean neck;

    public HydraHeadEntity(HydraEntity entity, float radius, float angle, float y, float width, float height, float damageMulti, int headIndex, boolean neck) {
        super(entity, radius, angle, y, width, height, damageMulti);
        this.headIndex = headIndex;
        this.neck = neck;
        this.hydra = entity;
    }

    public void setPartAngle(float angle) {
        this.angleYaw = (angle + 90.0F) * ((float) Math.PI / 180.0F);
    }

    @Override
    public void updatePosition() {
        if (this.headIndex >= this.hydra.getHeadCount()) {
            this.setOldPosAndRot();
            this.setPos(this.hydra.getX(), this.hydra.getY() - 64.0D, this.hydra.getZ());
            return;
        }
        super.updatePosition();
        if (this.hydra != null && this.hydra.getSeveredHead() != -1 && this.neck && !GorgonEntity.isStoneMob(this.hydra))
            if (this.hydra.getSeveredHead() == this.headIndex || this.level().isClientSide)
                for (int k = 0; k < 5; ++k) {
                    double d2 = 0.4;
                    double d0 = 0.1;
                    double d1 = 0.1;
                    this.level().addParticle(IafParticles.BLOOD.get(), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, this.getY() - 0.5D, this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, d2, d0, d1);
                }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float damage) {
        HydraEntity parent = this.getParent();
        if (this.headIndex >= parent.getHeadCount()) return false;
        parent.onHitHead(damage, this.headIndex);
        return parent.hurt(source, damage);
    }

    @Override
    public boolean isPickable() {
        return this.headIndex < this.getParent().getHeadCount();
    }
}
