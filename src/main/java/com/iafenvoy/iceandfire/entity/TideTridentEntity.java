package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.uranus.object.RegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class TideTridentEntity extends ThrownTrident {
    private static final int ADDITIONALPIERCING = 2;
    private int entitiesHit = 0;

    public TideTridentEntity(EntityType<? extends ThrownTrident> type, Level worldIn) {
        super(type, worldIn);
        this.pickupItemStack = new ItemStack(IafItems.TIDE_TRIDENT.get());
    }

    public TideTridentEntity(Level worldIn, LivingEntity thrower, ItemStack thrownStackIn) {
        this(IafEntities.TIDE_TRIDENT.get(), worldIn);
        this.setPos(thrower.getX(), thrower.getEyeY() - 0.1F, thrower.getZ());
        this.setOwner(thrower);
        this.pickupItemStack = thrownStackIn;
        this.entityData.set(ID_LOYALTY, (byte) EnchantmentHelper.getItemEnchantmentLevel(RegistryHelper.getEnchantment(worldIn.registryAccess(), Enchantments.LOYALTY), thrownStackIn));
        this.entityData.set(ID_FOIL, thrownStackIn.hasFoil());
        int piercingLevel = EnchantmentHelper.getItemEnchantmentLevel(RegistryHelper.getEnchantment(worldIn.registryAccess(), Enchantments.PIERCING), thrownStackIn);
        this.setPierceLevel((byte) piercingLevel);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        float f = 12.0F;
        Entity entity2 = this.getOwner();
        DamageSource damageSource = this.damageSources().trident(this, entity2 == null ? this : entity2);
        if (entity instanceof LivingEntity && this.level() instanceof ServerLevel serverWorld) {
            f = EnchantmentHelper.modifyDamage(serverWorld, this.getPickupItemStackOrigin(), entity, damageSource, f);
        }

        Entity entity1 = this.getOwner();
        DamageSource damagesource = this.level().damageSources().trident(this, entity1 == null ? this : entity1);
        this.entitiesHit++;
        if (this.entitiesHit >= this.getMaxPiercing())
            this.dealtDamage = true;
        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
        if (entity.hurt(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) return;

            if (entity instanceof LivingEntity livingentity1) {
                if (entity1 instanceof LivingEntity && this.level() instanceof ServerLevel serverWorld)
                    EnchantmentHelper.doPostAttackEffectsWithItemSource(serverWorld, entity, damageSource, this.getWeaponItem());
                this.doPostHurtEffects(livingentity1);
            }
        }

        float f1 = 1.0F;
        if (this.level() instanceof ServerLevel && this.level().isThundering() && EnchantmentHelper.getItemEnchantmentLevel(RegistryHelper.getEnchantment(this.level().registryAccess(), Enchantments.CHANNELING), this.getPickupItemStackOrigin()) > 0) {
            BlockPos blockpos = entity.blockPosition();
            if (this.level().canSeeSky(blockpos)) {
                LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(this.level());
                assert lightningboltentity != null;
                lightningboltentity.moveTo(Vec3.atCenterOf(blockpos));
                lightningboltentity.setCause(entity1 instanceof ServerPlayer ? (ServerPlayer) entity1 : null);
                this.level().addFreshEntity(lightningboltentity);
                soundevent = SoundEvents.TRIDENT_THUNDER.value();
                f1 = 5.0F;
            }
        }

        this.playSound(soundevent, f1, 1.0F);
    }

    private int getMaxPiercing() {
        return ADDITIONALPIERCING + this.getPierceLevel();
    }

}