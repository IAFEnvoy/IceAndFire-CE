package com.iafenvoy.iceandfire.entity.util.dragon;

import com.iafenvoy.iceandfire.api.IafEvents;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.data.component.IafEntityData;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.entity.block.BlockEntityDragonForgeInput;
import com.iafenvoy.iceandfire.entity.util.BlockLaunchExplosion;
import com.iafenvoy.iceandfire.item.block.BlockCharedPath;
import com.iafenvoy.iceandfire.item.block.BlockFallingReturningState;
import com.iafenvoy.iceandfire.item.block.BlockReturningState;
import com.iafenvoy.iceandfire.item.block.util.IDragonProof;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.tag.CommonTags;
import com.iafenvoy.iceandfire.registry.IafDamageTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class IafDragonDestructionManager {
    public static void destroyAreaBreath(final World level, final BlockPos center, final EntityDragonBase dragon) {
        if (IafEvents.ON_DRAGON_DAMAGE_BLOCK.invoker().onDamageBlock(dragon, center.getX(), center.getY(), center.getZ()))
            return;

        int statusDuration;
        float damageScale;

        if (dragon.dragonType == DragonType.FIRE) {
            statusDuration = 5 + dragon.getDragonStage() * 5;
            damageScale = IafCommonConfig.INSTANCE.dragon.attackDamageFire.getValue().floatValue();
        } else if (dragon.dragonType == DragonType.ICE) {
            statusDuration = 50 * dragon.getDragonStage();
            damageScale = IafCommonConfig.INSTANCE.dragon.attackDamageIce.getValue().floatValue();
        } else if (dragon.dragonType == DragonType.LIGHTNING) {
            statusDuration = 3;
            damageScale = IafCommonConfig.INSTANCE.dragon.attackDamageLightning.getValue().floatValue();
        } else return;

        double damageRadius = 3.5;
        boolean canBreakBlocks = level.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);

        if (dragon.getDragonStage() <= 3) {
            BlockPos.stream(center.add(-1, -1, -1), center.add(1, 1, 1)).forEach(position -> {
                if (level.getBlockEntity(position) instanceof BlockEntityDragonForgeInput forge) {
                    forge.onHitWithFlame();
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

            BlockPos.stream(center.add(-x, -y, -z), center.add(x, y, z)).forEach(position -> {
                if (level.getBlockEntity(position) instanceof BlockEntityDragonForgeInput forge) {
                    forge.onHitWithFlame();
                    return;
                }
                if (canBreakBlocks && center.getSquaredDistance(position) <= ff)
                    if (DragonUtils.canGrief(dragon) && level.random.nextFloat() > (float) center.getSquaredDistance(position) / ff)
                        attackBlock(level, dragon, position);
            });
        }

        DamageSource damageSource = getDamageSource(dragon);
        float stageDamage = dragon.getDragonStage() * damageScale;

        level.getNonSpectatingEntities(
                LivingEntity.class,
                new Box(
                        (double) center.getX() - damageRadius,
                        (double) center.getY() - damageRadius,
                        (double) center.getZ() - damageRadius,
                        (double) center.getX() + damageRadius,
                        (double) center.getY() + damageRadius,
                        (double) center.getZ() + damageRadius
                )
        ).forEach(target -> {
            if (!DragonUtils.onSameTeam(dragon, target) && !dragon.isPartOf(target) && dragon.canSee(target)) {
                target.damage(damageSource, stageDamage);
                applyDragonEffect(target, dragon, statusDuration);
            }
        });
    }

    public static void destroyAreaCharge(final World level, final BlockPos center, final EntityDragonBase dragon) {
        if (dragon == null) return;
        if (IafEvents.ON_DRAGON_DAMAGE_BLOCK.invoker().onDamageBlock(dragon, center.getX(), center.getY(), center.getZ()))
            return;

        int x = 2;
        int y = 2;
        int z = 2;

        boolean canBreakBlocks = DragonUtils.canGrief(dragon) && level.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);

        if (canBreakBlocks) {
            if (dragon.getDragonStage() <= 3) {
                BlockPos.stream(center.add(-x, -y, -z), center.add(x, y, z)).forEach(position -> {
                    BlockState state = level.getBlockState(position);
                    if (state.getBlock() instanceof IDragonProof) return;
                    if (dragon.getRandom().nextFloat() * 3 > center.getSquaredDistance(position) && DragonUtils.canDragonBreak(state, dragon))
                        level.breakBlock(position, false);
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

                BlockPos.stream(center.add(-x, -y, -z), center.add(x, y, z)).forEach(position -> {
                    if (center.getSquaredDistance(position) <= ff)
                        attackBlock(level, dragon, position);
                });
            }
        }

        final int statusDuration;

        if (dragon.dragonType == DragonType.FIRE)
            statusDuration = 15;
        else if (dragon.dragonType == DragonType.ICE)
            statusDuration = 400;
        else if (dragon.dragonType == DragonType.LIGHTNING)
            statusDuration = 9;
        else return;

        final float stageDamage = Math.max(1, dragon.getDragonStage() - 1) * 2F;
        DamageSource damageSource = getDamageSource(dragon);

        level.getNonSpectatingEntities(
                LivingEntity.class,
                new Box(
                        (double) center.getX() - x,
                        (double) center.getY() - y,
                        (double) center.getZ() - z,
                        (double) center.getX() + x,
                        (double) center.getY() + y,
                        (double) center.getZ() + z
                )
        ).forEach(target -> {
            if (!dragon.isTeammate(target) && !dragon.isPartOf(target) && dragon.canSee(target)) {
                target.damage(damageSource, stageDamage);
                applyDragonEffect(target, dragon, statusDuration);
            }
        });

        if (IafCommonConfig.INSTANCE.dragon.explosiveBreath.getValue())
            causeExplosion(level, center, dragon, damageSource, dragon.getDragonStage());
    }

    private static DamageSource getDamageSource(final EntityDragonBase dragon) {
        PlayerEntity player = dragon.getRidingPlayer();

        if (dragon.dragonType == DragonType.FIRE)
            return player != null ? IafDamageTypes.causeIndirectDragonFireDamage(dragon, player) : IafDamageTypes.causeDragonFireDamage(dragon);
        else if (dragon.dragonType == DragonType.ICE)
            return player != null ? IafDamageTypes.causeIndirectDragonIceDamage(dragon, player) : IafDamageTypes.causeDragonIceDamage(dragon);
        else if (dragon.dragonType == DragonType.LIGHTNING)
            return player != null ? IafDamageTypes.causeIndirectDragonLightningDamage(dragon, player) : IafDamageTypes.causeDragonLightningDamage(dragon);
        else
            return dragon.getWorld().getDamageSources().mobAttack(dragon);
    }

    private static void attackBlock(final World level, final EntityDragonBase dragon, final BlockPos position, final BlockState state) {
        if (state.getBlock() instanceof IDragonProof || !DragonUtils.canDragonBreak(state, dragon))
            return;

        BlockState transformed;

        if (dragon.dragonType == DragonType.FIRE)
            transformed = transformBlockFire(state);
        else if (dragon.dragonType == DragonType.ICE)
            transformed = transformBlockIce(state);
        else if (dragon.dragonType == DragonType.LIGHTNING)
            transformed = transformBlockLightning(state);
        else return;

        if (!transformed.isOf(state.getBlock()))
            level.setBlockState(position, transformed);

        Block elementalBlock;
        boolean doPlaceBlock;

        if (dragon.dragonType == DragonType.FIRE) {
            elementalBlock = Blocks.FIRE;
            doPlaceBlock = dragon.getRandom().nextBoolean();
        } else if (dragon.dragonType == DragonType.ICE) {
            elementalBlock = IafBlocks.DRAGON_ICE_SPIKES.get();
            doPlaceBlock = dragon.getRandom().nextInt(9) == 0;
        } else return;

        BlockState stateAbove = level.getBlockState(position.up());
        if (doPlaceBlock && transformed.isSolid() && stateAbove.getFluidState().isEmpty() && !stateAbove.isOpaque() && state.isOpaque() && DragonUtils.canDragonBreak(stateAbove, dragon))
            level.setBlockState(position.up(), elementalBlock.getDefaultState());
    }

    private static void attackBlock(final World level, final EntityDragonBase dragon, final BlockPos position) {
        attackBlock(level, dragon, position, level.getBlockState(position));
    }

    private static void applyDragonEffect(final LivingEntity target, final EntityDragonBase dragon, int statusDuration) {
        if (dragon.dragonType == DragonType.FIRE)
            target.setOnFireFor(statusDuration);
        else if (dragon.dragonType == DragonType.ICE) {
            IafEntityData data = IafEntityData.get(target);
            data.frozenData.setFrozen(target, statusDuration);
        } else if (dragon.dragonType == DragonType.LIGHTNING) {
            double x = dragon.getX() - target.getX();
            double y = dragon.getZ() - target.getZ();
            target.takeKnockback((double) statusDuration / 10, x, y);
        }
    }

    private static void causeExplosion(World world, BlockPos center, EntityDragonBase destroyer, DamageSource source, int stage) {
        Explosion.DestructionType mode = world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.KEEP;
        BlockLaunchExplosion explosion = new BlockLaunchExplosion(world, destroyer, source, center.getX(), center.getY(), center.getZ(), Math.min(2, stage - 2), mode);
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(true);
    }

    private static void destroyBlocks(World world, BlockPos center, int x, int y, int z, double radius2, Entity destroyer) {
        BlockPos.stream(center.add(-x, -y, -z), center.add(x, y, z)).forEach(pos -> {
            if (center.getSquaredDistance(pos) <= radius2) {
                BlockState state = world.getBlockState(pos);
                if (state.getBlock() instanceof IDragonProof) return;
                if (world.random.nextFloat() * 3 > (float) center.getSquaredDistance(pos) / radius2 && DragonUtils.canDragonBreak(state, destroyer))
                    world.breakBlock(pos, false);
            }
        });
    }

    public static BlockState transformBlockFire(BlockState in) {
        if (in.getBlock() instanceof SpreadableBlock)
            return IafBlocks.CHARRED_GRASS.get().getDefaultState().with(BlockReturningState.REVERTS, true);
        else if (in.isOf(Blocks.DIRT))
            return IafBlocks.CHARRED_DIRT.get().getDefaultState().with(BlockReturningState.REVERTS, true);
        else if (in.isIn(CommonTags.Blocks.GRAVEL) || (in.isIn(BlockTags.SAND) && !in.hasBlockEntity()))
            return IafBlocks.CHARRED_GRAVEL.get().getDefaultState().with(BlockFallingReturningState.REVERTS, true);
        else if (in.isIn(CommonTags.Blocks.COBBLESTONE))
            return IafBlocks.CHARRED_COBBLESTONE.get().getDefaultState().with(BlockReturningState.REVERTS, true);
        else if (in.isIn(BlockTags.BASE_STONE_OVERWORLD) && in.getBlock() != IafBlocks.CHARRED_COBBLESTONE.get())
            return IafBlocks.CHARRED_STONE.get().getDefaultState().with(BlockReturningState.REVERTS, true);
        else if (in.getBlock() == Blocks.DIRT_PATH)
            return IafBlocks.CHARRED_DIRT_PATH.get().getDefaultState().with(BlockCharedPath.REVERTS, true);
        else if (in.isIn(BlockTags.LOGS) || in.isIn(BlockTags.PLANKS))
            return IafBlocks.ASH.get().getDefaultState();
        else if (in.isIn(BlockTags.LEAVES) || in.isIn(BlockTags.FLOWERS) || in.isIn(BlockTags.CROPS) || in.getBlock() == Blocks.SNOW)
            return Blocks.AIR.getDefaultState();
        return in;
    }

    public static BlockState transformBlockIce(BlockState in) {
        if (in.getBlock() instanceof SpreadableBlock)
            return IafBlocks.FROZEN_GRASS.get().getDefaultState().with(BlockReturningState.REVERTS, true);
        else if (in.isIn(BlockTags.DIRT) && in.getBlock() == Blocks.DIRT || in.isIn(BlockTags.SNOW))
            return IafBlocks.FROZEN_DIRT.get().getDefaultState().with(BlockReturningState.REVERTS, true);
        else if (in.isIn(CommonTags.Blocks.GRAVEL) || (in.isIn(BlockTags.SAND) && !in.hasBlockEntity()))
            return IafBlocks.FROZEN_GRAVEL.get().getDefaultState().with(BlockFallingReturningState.REVERTS, true);
        else if (in.isIn(CommonTags.Blocks.COBBLESTONE))
            return IafBlocks.FROZEN_COBBLESTONE.get().getDefaultState().with(BlockReturningState.REVERTS, true);
        else if (in.isIn(BlockTags.BASE_STONE_OVERWORLD) && in.getBlock() != IafBlocks.FROZEN_COBBLESTONE.get())
            return IafBlocks.FROZEN_STONE.get().getDefaultState().with(BlockReturningState.REVERTS, true);
        else if (in.getBlock() == Blocks.DIRT_PATH)
            return IafBlocks.FROZEN_DIRT_PATH.get().getDefaultState().with(BlockCharedPath.REVERTS, true);
        else if (in.isIn(BlockTags.LOGS) || in.isIn(BlockTags.PLANKS))
            return IafBlocks.FROZEN_SPLINTERS.get().getDefaultState();
        else if (in.isOf(Blocks.WATER))
            return Blocks.ICE.getDefaultState();
        else if (in.isIn(BlockTags.LEAVES) || in.isIn(BlockTags.FLOWERS) || in.isIn(BlockTags.CROPS) || in.getBlock() == Blocks.SNOW)
            return Blocks.AIR.getDefaultState();
        return in;
    }

    public static BlockState transformBlockLightning(BlockState in) {
        if (in.getBlock() instanceof SpreadableBlock)
            return IafBlocks.CRACKLED_GRASS.get().getDefaultState().with(BlockReturningState.REVERTS, true);
        else if (in.isIn(BlockTags.DIRT) && in.getBlock() == Blocks.DIRT)
            return IafBlocks.CRACKLED_DIRT.get().getDefaultState().with(BlockReturningState.REVERTS, true);
        else if (in.isIn(CommonTags.Blocks.GRAVEL) || (in.isIn(BlockTags.SAND) && !in.hasBlockEntity()))
            return IafBlocks.CRACKLED_GRAVEL.get().getDefaultState().with(BlockFallingReturningState.REVERTS, true);
        else if (in.isIn(CommonTags.Blocks.COBBLESTONE))
            return IafBlocks.CRACKLED_COBBLESTONE.get().getDefaultState().with(BlockReturningState.REVERTS, true);
        else if (in.isIn(BlockTags.BASE_STONE_OVERWORLD) && in.getBlock() != IafBlocks.CRACKLED_COBBLESTONE.get())
            return IafBlocks.CRACKLED_STONE.get().getDefaultState().with(BlockReturningState.REVERTS, true);
        else if (in.getBlock() == Blocks.DIRT_PATH)
            return IafBlocks.CRACKLED_DIRT_PATH.get().getDefaultState().with(BlockCharedPath.REVERTS, true);
        else if (in.isIn(BlockTags.LOGS) || in.isIn(BlockTags.PLANKS))
            return IafBlocks.ASH.get().getDefaultState();
        else if (in.isIn(BlockTags.LEAVES) || in.isIn(BlockTags.FLOWERS) || in.isIn(BlockTags.CROPS) || in.getBlock() == Blocks.SNOW)
            return Blocks.AIR.getDefaultState();
        return in;
    }
}
