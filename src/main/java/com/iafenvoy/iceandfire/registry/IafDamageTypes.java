package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public final class IafDamageTypes {
    public static final ResourceKey<DamageType> BONUS = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "bonus"));
    public static final ResourceKey<DamageType> GORGON_DMG_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "gorgon"));
    public static final ResourceKey<DamageType> DRAGON_FIRE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "dragon_fire"));
    public static final ResourceKey<DamageType> DRAGON_ICE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "dragon_ice"));
    public static final ResourceKey<DamageType> DRAGON_LIGHTNING_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "dragon_lightning"));

    private static Holder<DamageType> get(Entity entity, ResourceKey<DamageType> key) {
        Registry<DamageType> registry = entity.level().damageSources.damageTypes;
        return registry.getHolder(key).orElse(registry.getHolderOrThrow(DamageTypes.FELL_OUT_OF_WORLD));
    }

    public static DamageSource bonusDamage(Entity attacker) {
        return new DamageSource(get(attacker, BONUS), attacker);
    }

    public static CustomEntityDamageSource causeGorgonDamage(Entity entity) {
        return new CustomEntityDamageSource(get(entity, GORGON_DMG_TYPE), entity);
    }

    public static CustomEntityDamageSource causeDragonFireDamage(Entity entity) {
        return new CustomEntityDamageSource(get(entity, DRAGON_FIRE_TYPE), entity);
    }

    public static CustomIndirectEntityDamageSource causeIndirectDragonFireDamage(Entity source, Entity indirectEntityIn) {
        return new CustomIndirectEntityDamageSource(get(indirectEntityIn, DRAGON_FIRE_TYPE), source, indirectEntityIn);
    }

    public static CustomEntityDamageSource causeDragonIceDamage(Entity entity) {
        return new CustomEntityDamageSource(get(entity, DRAGON_ICE_TYPE), entity);
    }

    public static CustomIndirectEntityDamageSource causeIndirectDragonIceDamage(Entity source, Entity indirectEntityIn) {
        return new CustomIndirectEntityDamageSource(get(indirectEntityIn, DRAGON_ICE_TYPE), source, indirectEntityIn);
    }

    public static CustomEntityDamageSource causeDragonLightningDamage(Entity entity) {
        return new CustomEntityDamageSource(get(entity, DRAGON_LIGHTNING_TYPE), entity);
    }

    public static CustomIndirectEntityDamageSource causeIndirectDragonLightningDamage(Entity source, Entity indirectEntityIn) {
        return new CustomIndirectEntityDamageSource(get(indirectEntityIn, DRAGON_ICE_TYPE), source, indirectEntityIn);
    }

    public static class CustomEntityDamageSource extends DamageSource {
        public CustomEntityDamageSource(Holder<DamageType> damageType, Entity entity) {
            super(damageType, entity);
        }

        @Override
        public @NotNull Component getLocalizedDeathMessage(LivingEntity entityLivingBaseIn) {
            LivingEntity livingentity = entityLivingBaseIn.getKillCredit();
            String s = "death.attack." + this.getMsgId();
            int index = entityLivingBaseIn.getRandom().nextInt(2);
            String s1 = s + "." + index;
            String s2 = s + ".attacker_" + index;
            return livingentity != null ? Component.translatable(s2, entityLivingBaseIn.getDisplayName(), livingentity.getDisplayName()) : Component.translatable(s1, entityLivingBaseIn.getDisplayName());
        }
    }

    public static class CustomIndirectEntityDamageSource extends DamageSource {
        public CustomIndirectEntityDamageSource(Holder<DamageType> damageType, Entity source, Entity entity) {
            super(damageType, source, entity);
        }

        @Override
        public @NotNull Component getLocalizedDeathMessage(LivingEntity entityLivingBaseIn) {
            LivingEntity livingentity = entityLivingBaseIn.getKillCredit();
            String s = "death.attack." + this.getMsgId();
            int index = entityLivingBaseIn.getRandom().nextInt(2);
            String s1 = s + "." + index;
            String s2 = s + ".attacker_" + index;
            return livingentity != null ? Component.translatable(s2, entityLivingBaseIn.getDisplayName(), livingentity.getDisplayName()) : Component.translatable(s1, entityLivingBaseIn.getDisplayName());
        }
    }
}
