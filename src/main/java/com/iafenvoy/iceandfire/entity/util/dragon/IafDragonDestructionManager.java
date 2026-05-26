package com.iafenvoy.iceandfire.entity.util.dragon;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.util.BlockLaunchExplosion;
import com.iafenvoy.iceandfire.event.DragonFireDamageWorldEvent;
import com.iafenvoy.iceandfire.item.block.CharedPathBlock;
import com.iafenvoy.iceandfire.item.block.FallingReturningStateBlock;
import com.iafenvoy.iceandfire.item.block.ReturningStateBlock;
import com.iafenvoy.iceandfire.item.block.entity.DragonForgeInputBlockEntity;
import com.iafenvoy.iceandfire.item.block.util.DragonProof;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafDamageTypes;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.registry.IafMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;

public class IafDragonDestructionManager {
    public static void destroyAreaBreath(final Level level, final BlockPos center, final DragonBaseEntity dragon) {
        if (NeoForge.EVENT_BUS.post(new DragonFireDamageWorldEvent(dragon, center.getX(), center.getY(), center.getZ())).isCanceled())
            return;

        int statusDuration;
        float damageScale;

        if (dragon.dragonType == IafDragonTypes.FIRE) {
            statusDuration = 5 + dragon.getDragonStage() * 5;
            damageScale = IafCommonConfig.INSTANCE.dragon.attackDamageFire.getValue().floatValue();
        } else if (dragon.dragonType == IafDragonTypes.ICE) {
            statusDuration = 50 * dragon.getDragonStage();
            damageScale = IafCommonConfig.INSTANCE.dragon.attackDamageIce.getValue().floatValue();
        } else if (dragon.dragonType == IafDragonTypes.LIGHTNING) {
            statusDuration = 3;
            damageScale = IafCommonConfig.INSTANCE.dragon.attackDamageLightning.getValue().floatValue();
        } else return;

        double damageRadius = 3.5;
        boolean canBreakBlocks = level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);

        if (dragon.getDragonStage() <= 3) {
            BlockPos.betweenClosedStream(center.offset(-1, -1, -1), center.offset(1, 1, 1)).forEach(position -> {
                if (level.getBlockEntity(position) instanceof DragonForgeInputBlockEntity forge) {
                    forge.onHitWithFlame(dragon);
                    return;
                }
                if (canBreakBlocks && DragonUtils.canGrief(dragon) && dragon.getRandom().nextBoolean())
                    attackBlock(level, dragon, position);
            });
        } else {
            final int radius = dragon.getDragonStage() == 4 ? 2 : 3;
            final int x = radius + level.random.nextInt(1);
            final int y = radius + level.random.nextInt(1);
            final int z = radius + level.random.nextInt(1);
            final float f = (float) (x + y + z) * 0.333F + 0.5F;
            final float ff = f * f;

            damageRadius = 2.5F + f * 1.2F;

            BlockPos.betweenClosedStream(center.offset(-x, -y, -z), center.offset(x, y, z)).forEach(position -> {
                if (level.getBlockEntity(position) instanceof DragonForgeInputBlockEntity forge) {
                    forge.onHitWithFlame(dragon);
                    return;
                }
                if (canBreakBlocks && center.distSqr(position) <= ff)
                    if (DragonUtils.canGrief(dragon) && level.random.nextFloat() > (float) center.distSqr(position) / ff)
                        attackBlock(level, dragon, position);
            });
        }

        DamageSource damageSource = getDamageSource(dragon);
        float stageDamage = dragon.getDragonStage() * damageScale;

        level.getEntitiesOfClass(
                LivingEntity.class,
                new AABB(
                        (double) center.getX() - damageRadius,
                        (double) center.getY() - damageRadius,
                        (double) center.getZ() - damageRadius,
                        (double) center.getX() + damageRadius,
                        (double) center.getY() + damageRadius,
                        (double) center.getZ() + damageRadius
                )
        ).forEach(target -> {
            if (!DragonUtils.onSameTeam(dragon, target) && !dragon.is(target) && dragon.hasLineOfSight(target)) {
                target.hurt(damageSource, stageDamage);
                applyDragonEffect(target, dragon, statusDuration);
            }
        });
    }

    public static void destroyAreaCharge(final Level level, final BlockPos center, final DragonBaseEntity dragon) {
        if (dragon == null) return;
        if (NeoForge.EVENT_BUS.post(new DragonFireDamageWorldEvent(dragon, center.getX(), center.getY(), center.getZ())).isCanceled())
            return;

        int x = 2;
        int y = 2;
        int z = 2;

        boolean canBreakBlocks = DragonUtils.canGrief(dragon) && level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);

        if (canBreakBlocks) {
            if (dragon.getDragonStage() <= 3) {
                BlockPos.betweenClosedStream(center.offset(-x, -y, -z), center.offset(x, y, z)).forEach(position -> {
                    BlockState state = level.getBlockState(position);
                    if (state.getBlock() instanceof DragonProof) return;
                    if (dragon.getRandom().nextFloat() * 3 > center.distSqr(position) && DragonUtils.canDragonBreak(state, dragon))
                        level.destroyBlock(position, false);
                    if (dragon.getRandom().nextBoolean()) attackBlock(level, dragon, position, state);
                });
            } else {
                final int radius = dragon.getDragonStage() == 4 ? 2 : 3;
                x = radius + level.random.nextInt(2);
                y = radius + level.random.nextInt(2);
                z = radius + level.random.nextInt(2);
                final float f = (float) (x + y + z) * 0.333F + 0.5F;
                final float ff = f * f;

                destroyBlocks(level, center, x, y, z, ff, dragon);

                x++;
                y++;
                z++;

                BlockPos.betweenClosedStream(center.offset(-x, -y, -z), center.offset(x, y, z)).forEach(position -> {
                    if (center.distSqr(position) <= ff)
                        attackBlock(level, dragon, position);
                });
            }
        }

        final int statusDuration;

        if (dragon.dragonType == IafDragonTypes.FIRE)
            statusDuration = 15;
        else if (dragon.dragonType == IafDragonTypes.ICE)
            statusDuration = 400;
        else if (dragon.dragonType == IafDragonTypes.LIGHTNING)
            statusDuration = 9;
        else return;

        final float stageDamage = Math.max(1, dragon.getDragonStage() - 1) * 2F;
        DamageSource damageSource = getDamageSource(dragon);

        level.getEntitiesOfClass(
                LivingEntity.class,
                new AABB(
                        (double) center.getX() - x,
                        (double) center.getY() - y,
                        (double) center.getZ() - z,
                        (double) center.getX() + x,
                        (double) center.getY() + y,
                        (double) center.getZ() + z
                )
        ).forEach(target -> {
            if (!dragon.isAlliedTo(target) && !dragon.is(target) && dragon.hasLineOfSight(target)) {
                target.hurt(damageSource, stageDamage);
                applyDragonEffect(target, dragon, statusDuration);
            }
        });

        if (IafCommonConfig.INSTANCE.dragon.explosiveBreath.getValue())
            causeExplosion(level, center, dragon, damageSource, dragon.getDragonStage());
    }

    private static DamageSource getDamageSource(final DragonBaseEntity dragon) {
        Player player = dragon.getRidingPlayer();

        if (dragon.dragonType == IafDragonTypes.FIRE)
            return player != null ? IafDamageTypes.causeIndirectDragonFireDamage(dragon, player) : IafDamageTypes.causeDragonFireDamage(dragon);
        else if (dragon.dragonType == IafDragonTypes.ICE)
            return player != null ? IafDamageTypes.causeIndirectDragonIceDamage(dragon, player) : IafDamageTypes.causeDragonIceDamage(dragon);
        else if (dragon.dragonType == IafDragonTypes.LIGHTNING)
            return player != null ? IafDamageTypes.causeIndirectDragonLightningDamage(dragon, player) : IafDamageTypes.causeDragonLightningDamage(dragon);
        else
            return dragon.level().damageSources().mobAttack(dragon);
    }

    @SuppressWarnings("deprecation")
    private static void attackBlock(final Level level, final DragonBaseEntity dragon, final BlockPos position, final BlockState state) {
        if (state.getBlock() instanceof DragonProof || !DragonUtils.canDragonBreak(state, dragon))
            return;

        BlockState transformed;

        if (dragon.dragonType == IafDragonTypes.FIRE)
            transformed = transformBlockFire(state);
        else if (dragon.dragonType == IafDragonTypes.ICE)
            transformed = transformBlockIce(state);
        else if (dragon.dragonType == IafDragonTypes.LIGHTNING)
            transformed = transformBlockLightning(state);
        else return;

        if (!transformed.is(state.getBlock()))
            level.setBlockAndUpdate(position, transformed);

        Block elementalBlock;
        boolean doPlaceBlock;

        if (dragon.dragonType == IafDragonTypes.FIRE) {
            elementalBlock = Blocks.FIRE;
            doPlaceBlock = dragon.getRandom().nextBoolean();
        } else if (dragon.dragonType == IafDragonTypes.ICE) {
            elementalBlock = IafBlocks.DRAGON_ICE_SPIKES.get();
            doPlaceBlock = dragon.getRandom().nextInt(9) == 0;
        } else return;

        BlockState stateAbove = level.getBlockState(position.above());
        if (doPlaceBlock && transformed.isSolid() && stateAbove.getFluidState().isEmpty() && !stateAbove.canOcclude() && state.canOcclude() && DragonUtils.canDragonBreak(stateAbove, dragon))
            level.setBlockAndUpdate(position.above(), elementalBlock.defaultBlockState());
    }

    private static void attackBlock(final Level level, final DragonBaseEntity dragon, final BlockPos position) {
        attackBlock(level, dragon, position, level.getBlockState(position));
    }

    private static void applyDragonEffect(final LivingEntity target, final DragonBaseEntity dragon, int statusDuration) {
        if (dragon.dragonType == IafDragonTypes.FIRE)
            target.igniteForSeconds(statusDuration);
        else if (dragon.dragonType == IafDragonTypes.ICE)
            target.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(IafMobEffects.FROZEN.get()), statusDuration));
        else if (dragon.dragonType == IafDragonTypes.LIGHTNING) {
            double x = dragon.getX() - target.getX();
            double y = dragon.getZ() - target.getZ();
            target.knockback((double) statusDuration / 10, x, y);
        }
    }

    private static void causeExplosion(Level world, BlockPos center, DragonBaseEntity destroyer, DamageSource source, int stage) {
        Explosion.BlockInteraction mode = world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP;
        BlockLaunchExplosion explosion = new BlockLaunchExplosion(world, destroyer, source, center.getX(), center.getY(), center.getZ(), Math.min(2, stage - 2), mode);
        explosion.explode();
        explosion.finalizeExplosion(true);
    }

    private static void destroyBlocks(Level world, BlockPos center, int x, int y, int z, double radius2, Entity destroyer) {
        BlockPos.betweenClosedStream(center.offset(-x, -y, -z), center.offset(x, y, z)).forEach(pos -> {
            if (center.distSqr(pos) <= radius2) {
                BlockState state = world.getBlockState(pos);
                if (state.getBlock() instanceof DragonProof) return;
                if (world.random.nextFloat() * 3 > (float) center.distSqr(pos) / radius2 && DragonUtils.canDragonBreak(state, destroyer))
                    world.destroyBlock(pos, false);
            }
        });
    }

    public static BlockState transformBlockFire(BlockState in) {
        if (in.getBlock() instanceof SpreadingSnowyDirtBlock)
            return IafBlocks.CHARRED_GRASS.get().defaultBlockState().setValue(ReturningStateBlock.REVERTS, true);
        else if (in.is(Blocks.DIRT))
            return IafBlocks.CHARRED_DIRT.get().defaultBlockState().setValue(ReturningStateBlock.REVERTS, true);
        else if (in.is(BlockTags.SAND) && in.getBlock() == Blocks.GRAVEL)
            return IafBlocks.CHARRED_GRAVEL.get().defaultBlockState().setValue(FallingReturningStateBlock.REVERTS, true);
        else if (in.is(BlockTags.BASE_STONE_OVERWORLD) && (in.getBlock() == Blocks.COBBLESTONE || in.getBlock().getDescriptionId().contains("cobblestone")))
            return IafBlocks.CHARRED_COBBLESTONE.get().defaultBlockState().setValue(ReturningStateBlock.REVERTS, true);
        else if (in.is(BlockTags.BASE_STONE_OVERWORLD) && in.getBlock() != IafBlocks.CHARRED_COBBLESTONE.get())
            return IafBlocks.CHARRED_STONE.get().defaultBlockState().setValue(ReturningStateBlock.REVERTS, true);
        else if (in.getBlock() == Blocks.DIRT_PATH)
            return IafBlocks.CHARRED_DIRT_PATH.get().defaultBlockState().setValue(CharedPathBlock.REVERTS, true);
        else if (in.is(BlockTags.LOGS) || in.is(BlockTags.PLANKS))
            return IafBlocks.ASH.get().defaultBlockState();
        else if (in.is(BlockTags.LEAVES) || in.is(BlockTags.FLOWERS) || in.is(BlockTags.CROPS) || in.getBlock() == Blocks.SNOW)
            return Blocks.AIR.defaultBlockState();
        return in;
    }

    public static BlockState transformBlockIce(BlockState in) {
        if (in.getBlock() instanceof SpreadingSnowyDirtBlock)
            return IafBlocks.FROZEN_GRASS.get().defaultBlockState().setValue(ReturningStateBlock.REVERTS, true);
        else if (in.is(BlockTags.DIRT) && in.getBlock() == Blocks.DIRT || in.is(BlockTags.SNOW))
            return IafBlocks.FROZEN_DIRT.get().defaultBlockState().setValue(ReturningStateBlock.REVERTS, true);
        else if (in.is(BlockTags.SAND) && in.getBlock() == Blocks.GRAVEL)
            return IafBlocks.FROZEN_GRAVEL.get().defaultBlockState().setValue(FallingReturningStateBlock.REVERTS, true);
        else if (in.is(BlockTags.SAND) && in.getBlock() != Blocks.GRAVEL)
            return in;
        else if (in.is(BlockTags.BASE_STONE_OVERWORLD) && (in.getBlock() == Blocks.COBBLESTONE || in.getBlock().getDescriptionId().contains("cobblestone")))
            return IafBlocks.FROZEN_COBBLESTONE.get().defaultBlockState().setValue(ReturningStateBlock.REVERTS, true);
        else if (in.is(BlockTags.BASE_STONE_OVERWORLD) && in.getBlock() != IafBlocks.FROZEN_COBBLESTONE.get())
            return IafBlocks.FROZEN_STONE.get().defaultBlockState().setValue(ReturningStateBlock.REVERTS, true);
        else if (in.getBlock() == Blocks.DIRT_PATH)
            return IafBlocks.FROZEN_DIRT_PATH.get().defaultBlockState().setValue(CharedPathBlock.REVERTS, true);
        else if (in.is(BlockTags.LOGS) || in.is(BlockTags.PLANKS))
            return IafBlocks.FROZEN_SPLINTERS.get().defaultBlockState();
        else if (in.is(Blocks.WATER))
            return Blocks.ICE.defaultBlockState();
        else if (in.is(BlockTags.LEAVES) || in.is(BlockTags.FLOWERS) || in.is(BlockTags.CROPS) || in.getBlock() == Blocks.SNOW)
            return Blocks.AIR.defaultBlockState();
        return in;
    }

    public static BlockState transformBlockLightning(BlockState in) {
        if (in.getBlock() instanceof SpreadingSnowyDirtBlock)
            return IafBlocks.CRACKLED_GRASS.get().defaultBlockState().setValue(ReturningStateBlock.REVERTS, true);
        else if (in.is(BlockTags.DIRT) && in.getBlock() == Blocks.DIRT)
            return IafBlocks.CRACKLED_DIRT.get().defaultBlockState().setValue(ReturningStateBlock.REVERTS, true);
        else if (in.is(BlockTags.SAND) && in.getBlock() == Blocks.GRAVEL)
            return IafBlocks.CRACKLED_GRAVEL.get().defaultBlockState().setValue(FallingReturningStateBlock.REVERTS, true);
        else if (in.is(BlockTags.BASE_STONE_OVERWORLD) && (in.getBlock() == Blocks.COBBLESTONE || in.getBlock().getDescriptionId().contains("cobblestone")))
            return IafBlocks.CRACKLED_COBBLESTONE.get().defaultBlockState().setValue(ReturningStateBlock.REVERTS, true);
        else if (in.is(BlockTags.BASE_STONE_OVERWORLD) && in.getBlock() != IafBlocks.CRACKLED_COBBLESTONE.get())
            return IafBlocks.CRACKLED_STONE.get().defaultBlockState().setValue(ReturningStateBlock.REVERTS, true);
        else if (in.getBlock() == Blocks.DIRT_PATH)
            return IafBlocks.CRACKLED_DIRT_PATH.get().defaultBlockState().setValue(CharedPathBlock.REVERTS, true);
        else if (in.is(BlockTags.LOGS) || in.is(BlockTags.PLANKS))
            return IafBlocks.ASH.get().defaultBlockState();
        else if (in.is(BlockTags.LEAVES) || in.is(BlockTags.FLOWERS) || in.is(BlockTags.CROPS) || in.getBlock() == Blocks.SNOW)
            return Blocks.AIR.defaultBlockState();
        return in;
    }
}
