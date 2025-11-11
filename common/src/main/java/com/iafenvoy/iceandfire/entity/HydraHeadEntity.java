package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;

public class HydraHeadEntity extends MultipartPartEntity {
    public int headIndex;
    public HydraEntity hydra;
    private boolean neck;

    public HydraHeadEntity(EntityType<?> t, World world) {
        super(t, world);
    }

    public HydraHeadEntity(HydraEntity entity, float radius, float angle, float y, float width, float height, float damageMulti, int headIndex, boolean neck) {
        super(IafEntities.HYDRA_MULTIPART.get(), entity, radius, angle, y, width, height, damageMulti);
        this.headIndex = headIndex;
        this.neck = neck;
        this.hydra = entity;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.hydra != null && this.hydra.getSeveredHead() != -1 && this.neck && !GorgonEntity.isStoneMob(this.hydra))
            if (this.hydra.getSeveredHead() == this.headIndex || this.getWorld().isClient)
                for (int k = 0; k < 5; ++k) {
                    double d2 = 0.4;
                    double d0 = 0.1;
                    double d1 = 0.1;
                    this.getWorld().addParticle(IafParticles.BLOOD.get(), this.getX() + (double) (this.random.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, this.getY() - 0.5D, this.getZ() + (double) (this.random.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, d2, d0, d1);
                }
    }

    @Override
    public boolean damage(DamageSource source, float damage) {
        Entity parent = this.getParent();
        if (parent instanceof HydraEntity h) {
            h.onHitHead(damage, this.headIndex);
            return h.damage(source, damage);
        } else return parent != null && parent.damage(source, damage);
    }
}
