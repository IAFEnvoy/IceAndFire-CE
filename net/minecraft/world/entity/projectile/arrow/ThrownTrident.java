package net.minecraft.world.entity.projectile.arrow;

import java.util.Collection;
import java.util.List;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ThrownTrident extends AbstractArrow {
    private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.BOOLEAN);
    private static final float WATER_INERTIA = 0.99F;
    private static final boolean DEFAULT_DEALT_DAMAGE = false;
    private boolean dealtDamage = false;
    public int clientSideReturnTridentTickCount;

    public ThrownTrident(EntityType<? extends ThrownTrident> type, Level level) {
        super(type, level);
    }

    public ThrownTrident(Level level, LivingEntity owner, ItemStack tridentItem) {
        super(EntityType.TRIDENT, owner, level, tridentItem, null);
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(tridentItem));
        this.entityData.set(ID_FOIL, tridentItem.hasFoil());
    }

    public ThrownTrident(Level level, double x, double y, double z, ItemStack tridentItem) {
        super(EntityType.TRIDENT, x, y, z, level, tridentItem, tridentItem);
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(tridentItem));
        this.entityData.set(ID_FOIL, tridentItem.hasFoil());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(ID_LOYALTY, (byte)0);
        entityData.define(ID_FOIL, false);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity currentOwner = this.getOwner();
        int loyalty = this.entityData.get(ID_LOYALTY);
        if (loyalty > 0 && (this.dealtDamage || this.isNoPhysics()) && currentOwner != null) {
            if (!this.isAcceptibleReturnOwner()) {
                if (this.level() instanceof ServerLevel level && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(level, this.getPickupItem(), 0.1F);
                }

                this.discard();
            } else {
                if (!(currentOwner instanceof Player) && this.position().distanceTo(currentOwner.getEyePosition()) < currentOwner.getBbWidth() + 1.0) {
                    this.discard();
                    return;
                }

                this.setNoPhysics(true);
                Vec3 vec = currentOwner.getEyePosition().subtract(this.position());
                this.setPosRaw(this.getX(), this.getY() + vec.y * 0.015 * loyalty, this.getZ());
                double accel = 0.05 * loyalty;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec.normalize().scale(accel)));
                if (this.clientSideReturnTridentTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }

                this.clientSideReturnTridentTickCount++;
            }
        }

        super.tick();
    }

    private boolean isAcceptibleReturnOwner() {
        Entity currentOwner = this.getOwner();
        return currentOwner == null || !currentOwner.isAlive() ? false : !(currentOwner instanceof ServerPlayer) || !currentOwner.isSpectator();
    }

    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    @Override
    protected @Nullable EntityHitResult findHitEntity(Vec3 from, Vec3 to) {
        return this.dealtDamage ? null : super.findHitEntity(from, to);
    }

    @Override
    protected Collection<EntityHitResult> findHitEntities(Vec3 from, Vec3 to) {
        EntityHitResult e = this.findHitEntity(from, to);
        return e != null ? List.of(e) : List.of();
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        float dmg = 8.0F;
        Entity currentOwner = this.getOwner();
        DamageSource damageSource = this.damageSources().trident(this, (Entity)(currentOwner == null ? this : currentOwner));
        if (this.level() instanceof ServerLevel serverLevel) {
            dmg = EnchantmentHelper.modifyDamage(serverLevel, this.getWeaponItem(), entity, damageSource, dmg);
        }

        this.dealtDamage = true;
        if (entity.hurtOrSimulate(damageSource, dmg)) {
            if (entity.is(EntityType.ENDERMAN)) {
                return;
            }

            if (this.level() instanceof ServerLevel serverLevel) {
                EnchantmentHelper.doPostAttackEffectsWithItemSourceOnBreak(
                    serverLevel, entity, damageSource, this.getWeaponItem(), weapon -> this.kill(serverLevel)
                );
            }

            if (entity instanceof LivingEntity mob) {
                this.doKnockback(mob, damageSource);
                this.doPostHurtEffects(mob);
            }
        }

        this.deflect(ProjectileDeflection.REVERSE, entity, this.owner, false);
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.02, 0.2, 0.02));
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }

    @Override
    protected void hitBlockEnchantmentEffects(ServerLevel level, BlockHitResult hitResult, ItemStack weapon) {
        Vec3 compensatedHitPosition = hitResult.getBlockPos().clampLocationWithin(hitResult.getLocation());
        EnchantmentHelper.onHitBlock(
            level,
            weapon,
            this.getOwner() instanceof LivingEntity livingOwner ? livingOwner : null,
            this,
            null,
            compensatedHitPosition,
            level.getBlockState(hitResult.getBlockPos()),
            item -> this.kill(level)
        );
    }

    @Override
    public ItemStack getWeaponItem() {
        return this.getPickupItemStackOrigin();
    }

    @Override
    protected boolean tryPickup(Player player) {
        return super.tryPickup(player) || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.TRIDENT);
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void playerTouch(Player player) {
        if (this.ownedBy(player) || this.getOwner() == null) {
            super.playerTouch(player);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.dealtDamage = input.getBooleanOr("DealtDamage", false);
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(this.getPickupItemStackOrigin()));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("DealtDamage", this.dealtDamage);
    }

    private byte getLoyaltyFromItem(ItemStack tridentItem) {
        return this.level() instanceof ServerLevel serverLevel
            ? (byte)Mth.clamp(EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverLevel, tridentItem, this), 0, 127)
            : 0;
    }

    @Override
    public void tickDespawn() {
        int loyalty = this.entityData.get(ID_LOYALTY);
        if (this.pickup != AbstractArrow.Pickup.ALLOWED || loyalty <= 0) {
            super.tickDespawn();
        }
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }

    @Override
    public boolean shouldRender(double camX, double camY, double camZ) {
        return true;
    }
}
