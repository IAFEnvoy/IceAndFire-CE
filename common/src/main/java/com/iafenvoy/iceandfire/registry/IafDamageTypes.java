package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class IafDamageTypes {
    public static final RegistryKey<DamageType> BONUS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(IceAndFire.MOD_ID, "bonus"));
    public static final RegistryKey<DamageType> GORGON_DMG_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(IceAndFire.MOD_ID, "gorgon"));
    public static final RegistryKey<DamageType> DRAGON_FIRE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(IceAndFire.MOD_ID, "dragon_fire"));
    public static final RegistryKey<DamageType> DRAGON_ICE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(IceAndFire.MOD_ID, "dragon_ice"));
    public static final RegistryKey<DamageType> DRAGON_LIGHTNING_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(IceAndFire.MOD_ID, "dragon_lightning"));
    public static final RegistryKey<DamageType> DRAGON_SOUL_FIRE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(IceAndFire.MOD_ID, "dragon_soul_fire"));

    private static RegistryEntry<DamageType> get(Entity entity, RegistryKey<DamageType> key) {
        Registry<DamageType> registry = entity.getWorld().damageSources.registry;
        return registry.getEntry(key).orElse(registry.entryOf(DamageTypes.OUT_OF_WORLD));
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

    public static CustomEntityDamageSource causeDragonSoulFireDamage(Entity entity) {
        return new CustomEntityDamageSource(get(entity, DRAGON_SOUL_FIRE_TYPE), entity);
    }

    public static CustomIndirectEntityDamageSource causeIndirectDragonSoulFireDamage(Entity source, Entity indirectEntityIn) {
        return new CustomIndirectEntityDamageSource(get(indirectEntityIn, DRAGON_SOUL_FIRE_TYPE), source, indirectEntityIn);
    }

    public static class CustomEntityDamageSource extends DamageSource {
        public CustomEntityDamageSource(RegistryEntry<DamageType> damageType, Entity entity) {
            super(damageType, entity);
        }

        @Override
        public Text getDeathMessage(LivingEntity entityLivingBaseIn) {
            LivingEntity livingentity = entityLivingBaseIn.getPrimeAdversary();
            String s = "death.attack." + this.getName();
            int index = entityLivingBaseIn.getRandom().nextInt(2);
            String s1 = s + "." + index;
            String s2 = s + ".attacker_" + index;
            return livingentity != null ? Text.translatable(s2, entityLivingBaseIn.getDisplayName(), livingentity.getDisplayName()) : Text.translatable(s1, entityLivingBaseIn.getDisplayName());
        }
    }

    public static class CustomIndirectEntityDamageSource extends DamageSource {
        public CustomIndirectEntityDamageSource(RegistryEntry<DamageType> damageType, Entity source, Entity entity) {
            super(damageType, source, entity);
        }

        @Override
        public Text getDeathMessage(LivingEntity entityLivingBaseIn) {
            LivingEntity livingentity = entityLivingBaseIn.getPrimeAdversary();
            String s = "death.attack." + this.getName();
            int index = entityLivingBaseIn.getRandom().nextInt(2);
            String s1 = s + "." + index;
            String s2 = s + ".attacker_" + index;
            return livingentity != null ? Text.translatable(s2, entityLivingBaseIn.getDisplayName(), livingentity.getDisplayName()) : Text.translatable(s1, entityLivingBaseIn.getDisplayName());
        }
    }
}
