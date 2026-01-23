package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.registry.IafParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class EntityDreadLichSkull extends PersistentProjectileEntity {
    public EntityDreadLichSkull(EntityType<? extends PersistentProjectileEntity> type, World worldIn) {
        super(type, worldIn);
        this.setDamage(6F);
    }

    public EntityDreadLichSkull(EntityType<? extends PersistentProjectileEntity> type, World worldIn, LivingEntity shooter, double dmg) {
        super(type, shooter, worldIn);
        this.setDamage(dmg);
    }

    @Override
    public boolean isTouchingWater() {
        return false;
    }

    @Override
    public void tick() {
        float sqrt = MathHelper.sqrt((float) (this.getVelocity().x * this.getVelocity().x + this.getVelocity().z * this.getVelocity().z));
        Entity shootingEntity = this.getOwner();
        if (shootingEntity instanceof MobEntity mob && mob.getTarget() != null) {
            LivingEntity target = mob.getTarget();
            double minusX = target.getX() - this.getX();
            double minusY = target.getY() - this.getY();
            double minusZ = target.getZ() - this.getZ();
            double speed = 0.15D;
            this.setVelocity(this.getVelocity().add(minusX * speed * 0.1D, minusY * speed * 0.1D, minusZ * speed * 0.1D));
        }
        if (shootingEntity instanceof PlayerEntity player) {
            LivingEntity target = player.getPrimeAdversary();
            if (target == null || !target.isAlive()) {
                double d0 = 10;
                List<Entity> list = this.getWorld().getOtherEntities(shootingEntity, (new Box(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0D, this.getY() + 1.0D, this.getZ() + 1.0D)).expand(d0, 10.0D, d0), EntityPredicates.VALID_ENTITY);
                LivingEntity closest = null;
                if (!list.isEmpty())
                    for (Entity e : list)
                        if (e instanceof LivingEntity living && !e.getUuid().equals(shootingEntity.getUuid()) && e instanceof Monster)
                            if (closest == null || closest.distanceTo(shootingEntity) > e.distanceTo(shootingEntity))
                                closest = living;
                target = closest;
            }
            if (target != null && target.isAlive()) {
                double minusX = target.getX() - this.getX();
                double minusY = target.getY() + target.getStandingEyeHeight() - this.getY();
                double minusZ = target.getZ() - this.getZ();
                double speed = 0.25D * Math.min(this.distanceTo(target), 10D) / 10D;
                this.setVelocity(this.getVelocity().add((Math.signum(minusX) * 0.5D - this.getVelocity().x) * 0.10000000149011612D, (Math.signum(minusY) * 0.5D - this.getVelocity().y) * 0.10000000149011612D, (Math.signum(minusZ) * 0.5D - this.getVelocity().z) * 0.10000000149011612D));
                this.setYaw((float) (MathHelper.atan2(this.getVelocity().x, this.getVelocity().z) * (180D / Math.PI)));
                this.setPitch((float) (MathHelper.atan2(this.getVelocity().y, sqrt) * (180D / Math.PI)));
            }
        }
        if ((sqrt < 0.1F || this.horizontalCollision || this.verticalCollision || this.inGround) && this.age > 5)
            this.remove(RemovalReason.DISCARDED);
        double d0 = 0;
        double d1 = 0.01D;
        double d2 = 0D;
        double x = this.getX() + this.random.nextFloat() * this.getWidth() * 2.0F - this.getWidth();
        double y = this.getY() + this.random.nextFloat() * this.getHeight() - this.getHeight();
        double z = this.getZ() + this.random.nextFloat() * this.getWidth() * 2.0F - this.getWidth();
        float f = (this.getWidth() + this.getHeight() + this.getWidth()) * 0.333F + 0.5F;
        if (this.particleDistSq(x, y, z) < f * f)
            this.getWorld().addParticle(IafParticles.DREAD_TORCH.get(), x, y + 0.5D, z, d0, d1, d2);
        super.tick();
    }

    public double particleDistSq(double toX, double toY, double toZ) {
        double d0 = this.getX() - toX;
        double d1 = this.getY() - toY;
        double d2 = this.getZ() - toZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch) {
        if (!this.isSilent() && soundIn != SoundEvents.ENTITY_ARROW_HIT && soundIn != SoundEvents.ENTITY_ARROW_HIT_PLAYER)
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), soundIn, this.getSoundCategory(), volume, pitch);
    }

    @Override
    protected void onEntityHit(EntityHitResult raytraceResultIn) {
        if (raytraceResultIn.getType() == HitResult.Type.ENTITY) {
            Entity entity = raytraceResultIn.getEntity();
            Entity shootingEntity = this.getOwner();
            if (entity != null && shootingEntity != null && entity.isTeammate(shootingEntity)) return;
        }
        super.onEntityHit(raytraceResultIn);
    }

    @Override
    protected void onHit(LivingEntity living) {
        super.onHit(living);
        Entity shootingEntity = this.getOwner();
        if (living instanceof PlayerEntity player && (shootingEntity == null || !living.isPartOf(shootingEntity)))
            this.damageShield(player, (float) this.getDamage());
    }

    protected void damageShield(PlayerEntity player, float damage) {
        if (damage >= 3.0F && player.getActiveItem().getItem() instanceof ShieldItem) {
            int i = 1 + MathHelper.floor(damage);
            player.getActiveItem().damage(i, player, (playerSheild) -> playerSheild.sendToolBreakStatus(playerSheild.getActiveHand()));

            if (player.getActiveItem().isEmpty()) {
                Hand hand = player.getActiveHand();
                if (hand == Hand.MAIN_HAND)
                    this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                else
                    this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                player.clearActiveItem();
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.getWorld().random.nextFloat() * 0.4F);
            }
        }
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }
}
