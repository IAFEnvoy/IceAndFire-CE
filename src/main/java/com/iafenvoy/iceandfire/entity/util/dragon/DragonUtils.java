package com.iafenvoy.iceandfire.entity.util.dragon;

import com.google.common.base.Predicate;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.*;
import com.iafenvoy.iceandfire.entity.util.IDeadMob;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.tag.IafBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DragonUtils {
    public static BlockPos getBlockInViewEscort(DragonBaseEntity dragon) {
        BlockPos escortPos = dragon.getEscortPosition();
        BlockPos ground = dragon.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, escortPos);
        int distFromGround = escortPos.getY() - ground.getY();
        for (int i = 0; i < 10; i++) {
            BlockPos pos = new BlockPos(escortPos.getX() + dragon.getRandom().nextInt(IafCommonConfig.INSTANCE.dragon.wanderFromHomeDistance.getValue()) - IafCommonConfig.INSTANCE.dragon.wanderFromHomeDistance.getValue() / 2,
                    (distFromGround > 16 ? escortPos.getY() : escortPos.getY() + 8 + dragon.getRandom().nextInt(16)),
                    (escortPos.getZ() + dragon.getRandom().nextInt(IafCommonConfig.INSTANCE.dragon.wanderFromHomeDistance.getValue()) - IafCommonConfig.INSTANCE.dragon.wanderFromHomeDistance.getValue() / 2));
            if (dragon.getDistanceSquared(Vec3.atCenterOf(pos)) > 6 && !dragon.isTargetBlocked(Vec3.atCenterOf(pos)))
                return pos;
        }
        return null;
    }

    public static BlockPos getWaterBlockInViewEscort(DragonBaseEntity dragon) {
        // In water escort
        BlockPos inWaterEscortPos = dragon.getEscortPosition();
        // We don't need to get too close
        if (Math.abs(dragon.getX() - inWaterEscortPos.getX()) < dragon.getBoundingBox().getXsize()
                && Math.abs(dragon.getZ() - inWaterEscortPos.getZ()) < dragon.getBoundingBox().getZsize())
            return dragon.blockPosition();
        // Takes off if the escort position is no longer in water, mainly for using elytra to fly out of the water
        if (inWaterEscortPos.getY() - dragon.getY() > 8 + dragon.getYNavSize() && !dragon.level().getFluidState(inWaterEscortPos.below()).is(FluidTags.WATER))
            dragon.setHovering(true);
        // Swim directly to the escort position
        return inWaterEscortPos;
    }

    public static BlockPos getBlockInView(DragonBaseEntity dragon) {
        float radius = 12 * (0.7F * dragon.getRenderSize() / 3);
        float neg = dragon.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = dragon.yBodyRot;
        if (dragon.hasHomePosition && dragon.homePos != null) {
            BlockPos dragonPos = dragon.blockPosition();
            BlockPos ground = dragon.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, dragonPos);
            int distFromGround = (int) dragon.getY() - ground.getY();
            for (int i = 0; i < 10; i++) {
                BlockPos homePos = dragon.homePos.getPosition();
                BlockPos pos = new BlockPos(homePos.getX() + dragon.getRandom().nextInt(IafCommonConfig.INSTANCE.dragon.wanderFromHomeDistance.getValue() * 2) - IafCommonConfig.INSTANCE.dragon.wanderFromHomeDistance.getValue(), (distFromGround > 16 ? (int) Math.min(IafCommonConfig.INSTANCE.dragon.maxFlight.getValue(), dragon.getY() + dragon.getRandom().nextInt(16) - 8) : (int) dragon.getY() + dragon.getRandom().nextInt(16) + 1), (homePos.getZ() + dragon.getRandom().nextInt(IafCommonConfig.INSTANCE.dragon.wanderFromHomeDistance.getValue() * 2) - IafCommonConfig.INSTANCE.dragon.wanderFromHomeDistance.getValue()));
                if (dragon.getDistanceSquared(Vec3.atCenterOf(pos)) > 6 && !dragon.isTargetBlocked(Vec3.atCenterOf(pos)))
                    return pos;
            }
        }
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (dragon.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = BlockPos.containing(dragon.getX() + extraX, 0, dragon.getZ() + extraZ);
        BlockPos ground = dragon.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, radialPos);
        int distFromGround = (int) dragon.getY() - ground.getY();
        BlockPos newPos = radialPos.above(distFromGround > 16 ? (int) Math.min(IafCommonConfig.INSTANCE.dragon.maxFlight.getValue(), dragon.getY() + dragon.getRandom().nextInt(16) - 8) : (int) dragon.getY() + dragon.getRandom().nextInt(16) + 1);
        BlockPos pos = dragon.doesWantToLand() ? ground : newPos;
        if (dragon.getDistanceSquared(Vec3.atCenterOf(newPos)) > 6 && !dragon.isTargetBlocked(Vec3.atCenterOf(newPos)))
            return pos;
        return null;
    }

    public static BlockPos getWaterBlockInView(DragonBaseEntity dragon) {
        float radius = 0.75F * (0.7F * dragon.getRenderSize() / 3) * -7 - dragon.getRandom().nextInt(dragon.getDragonStage() * 6);
        float neg = dragon.getRandom().nextBoolean() ? 1 : -1;
        float angle = (0.01745329251F * dragon.yBodyRot) + 3.15F + (dragon.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = BlockPos.containing(dragon.getX() + extraX, 0, dragon.getZ() + extraZ);
        BlockPos ground = dragon.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, radialPos);
        int distFromGround = (int) dragon.getY() - ground.getY();
        BlockPos newPos = radialPos.above(distFromGround > 16 ? (int) Math.min(IafCommonConfig.INSTANCE.dragon.maxFlight.getValue(), dragon.getY() + dragon.getRandom().nextInt(16) - 8) : (int) dragon.getY() + dragon.getRandom().nextInt(16) + 1);
        BlockPos pos = dragon.doesWantToLand() ? ground : newPos;
        BlockPos surface = dragon.level().getFluidState(newPos.below(2)).is(FluidTags.WATER) ? newPos.below(dragon.getRandom().nextInt(10) + 1) : newPos;
        if (dragon.getDistanceSquared(Vec3.atCenterOf(surface)) > 6 && dragon.level().getFluidState(surface).is(FluidTags.WATER))
            return surface;
        return null;
    }

    public static LivingEntity riderLookingAtEntity(LivingEntity dragon, LivingEntity rider, double dist) {
        Vec3 Vector3d = rider.getEyePosition(1.0F);
        Vec3 Vector3d1 = rider.getViewVector(1.0F);
        Vec3 Vector3d2 = Vector3d.add(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist);
        Entity pointedEntity = null;
        List<Entity> list = rider.level().getEntities(rider, rider.getBoundingBox().expandTowards(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist).inflate(1.0D, 1.0D, 1.0D), (Predicate<Entity>) entity -> {
            if (onSameTeam(dragon, entity)) return false;
            return entity != null && entity.isPickable() && entity instanceof LivingEntity && !entity.is(dragon) && !entity.isAlliedTo(dragon) && (!(entity instanceof IDeadMob) || !((IDeadMob) entity).isMobDead());
        });
        double d2 = dist;
        for (Entity entity1 : list) {
            AABB axisalignedbb = entity1.getBoundingBox().inflate((double) entity1.getPickRadius() + 2F);
            Vec3 raytraceresult = axisalignedbb.clip(Vector3d, Vector3d2).orElse(Vec3.ZERO);

            if (axisalignedbb.contains(Vector3d)) {
                if (d2 >= 0.0D) {
                    pointedEntity = entity1;
                    d2 = 0.0D;
                }
            } else {
                double d3 = Vector3d.distanceTo(raytraceresult);
                if (d3 < d2 || d2 == 0.0D) {
                    if (entity1.getRootVehicle() == rider.getRootVehicle()) {
                        if (d2 == 0.0D) pointedEntity = entity1;
                    } else {
                        pointedEntity = entity1;
                        d2 = d3;
                    }
                }
            }
        }
        return (LivingEntity) pointedEntity;
    }

    public static BlockPos getBlockInViewStymphalian(StymphalianBirdEntity bird) {
        float radius = 0.75F * (0.7F * 6) * -3 - bird.getRandom().nextInt(24);
        float neg = bird.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = bird.flock != null && !bird.flock.isLeader(bird) ? getStymphalianFlockDirection(bird) : bird.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (bird.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = getStymphalianFearPos(bird, BlockPos.containing(bird.getX() + extraX, 0, bird.getZ() + extraZ));
        BlockPos ground = bird.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, radialPos);
        int distFromGround = (int) bird.getY() - ground.getY();
        int flightHeight = Math.min(IafCommonConfig.INSTANCE.stymphalianBird.flightHeight.getValue(), ground.getY() + bird.getRandom().nextInt(16));
        BlockPos newPos = radialPos.above(distFromGround > 16 ? flightHeight : (int) bird.getY() + bird.getRandom().nextInt(16) + 1);
        if (bird.getDistanceSquared(Vec3.atCenterOf(newPos)) > 6 && !bird.isTargetBlocked(Vec3.atCenterOf(newPos)))
            return newPos;
        return null;
    }

    private static BlockPos getStymphalianFearPos(StymphalianBirdEntity bird, BlockPos fallback) {
        if (bird.getVictor() != null && bird.getVictor() instanceof PathfinderMob pathAware) {
            Vec3 Vector3d = DefaultRandomPos.getPosAway(pathAware, 16, IafCommonConfig.INSTANCE.stymphalianBird.flightHeight.getValue(), new Vec3(bird.getVictor().getX(), bird.getVictor().getY(), bird.getVictor().getZ()));
            if (Vector3d != null) {
                BlockPos pos = BlockPos.containing(Vector3d);
                return new BlockPos(pos.getX(), 0, pos.getZ());
            }
        }
        return fallback;
    }

    private static float getStymphalianFlockDirection(StymphalianBirdEntity bird) {
        StymphalianBirdEntity leader = bird.flock.getLeader();
        if (bird.distanceToSqr(leader) > 2) {
            double d0 = leader.getX() - bird.getX();
            double d2 = leader.getZ() - bird.getZ();
            float f = (float) (Mth.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
            float degrees = Mth.wrapDegrees(f - bird.getYRot());

            return bird.getYRot() + degrees;
        } else return leader.yBodyRot;
    }

    public static BlockPos getBlockInTargetsViewCockatrice(CockatriceEntity cockatrice, LivingEntity target) {
        float radius = 10 + cockatrice.getRandom().nextInt(10);
        float angle = (0.01745329251F * target.yHeadRot);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = BlockPos.containing(target.getX() + extraX, 0, target.getZ() + extraZ);
        BlockPos ground = target.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, radialPos);
        if (cockatrice.distanceToSqr(Vec3.atCenterOf(ground)) > 30 && !cockatrice.isTargetBlocked(Vec3.atCenterOf(ground)))
            return ground;
        return target.blockPosition();
    }


    public static BlockPos getBlockInTargetsViewGhost(GhostEntity ghost, LivingEntity target) {
        float radius = 4 + ghost.getRandom().nextInt(5);
        float angle = (0.01745329251F * (target.yHeadRot + 90F + ghost.getRandom().nextInt(180)));
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        if (ghost.distanceToSqr(Vec3.atCenterOf(BlockPos.containing(target.getX() + extraX, target.getY(), target.getZ() + extraZ))) > 30)
            return BlockPos.containing(target.getX() + extraX, target.getY(), target.getZ() + extraZ);
        return ghost.blockPosition();
    }

    public static BlockPos getBlockInTargetsViewGorgon(GorgonEntity cockatrice, LivingEntity target) {
        float radius = 6;
        float angle = (0.01745329251F * target.yHeadRot);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = BlockPos.containing(target.getX() + extraX, target.getY(), target.getZ() + extraZ);
        if (cockatrice.distanceToSqr(Vec3.atCenterOf(radialPos)) < 300 && !cockatrice.isTargetBlocked(Vec3.atCenterOf(radialPos).add(0, 0.75, 0)))
            return radialPos;
        return target.blockPosition();
    }


    public static BlockPos getBlockInTargetsViewSeaSerpent(SeaSerpentEntity serpent, LivingEntity target) {
        float radius = 10 * serpent.getSeaSerpentScale() + serpent.getRandom().nextInt(10);
        float angle = (0.01745329251F * target.yHeadRot);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = BlockPos.containing(target.getX() + extraX, 0, target.getZ() + extraZ);
        BlockPos ground = target.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, radialPos);
        if (serpent.distanceToSqr(Vec3.atCenterOf(ground)) > 30)
            return ground;
        return target.blockPosition();
    }

    public static boolean canTameDragonAttack(TamableAnimal dragon, Entity entity) {
        if (isVillager(entity)) return false;
        if (entity instanceof AbstractVillager || entity instanceof AbstractGolem || entity instanceof Player)
            return false;
        if (entity instanceof TamableAnimal tameable) return !tameable.isTame();
        return true;
    }

    public static boolean isVillager(Entity entity) {
        return entity instanceof Villager;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isAnimaniaMob(Entity entity) {
        return false;
    }

    public static boolean isDragonTargetable(Entity entity, TagKey<EntityType<?>> tag) {
        return entity.getType().is(tag);
    }

    public static String getDimensionName(Level world) {
        return world.dimension().location().toString();
    }

    public static boolean isInHomeDimension(DragonBaseEntity dragonBase) {
        return (dragonBase.getHomeDimensionName() == null || getDimensionName(dragonBase.level()).equals(dragonBase.getHomeDimensionName()));
    }

    public static boolean canDragonBreak(final BlockState state, final Entity entity) {
        if (!entity.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return false;
        Block block = state.getBlock();
        return block.getExplosionResistance() < 1200 && !state.is(IafBlockTags.DRAGON_BLOCK_BREAK_BLACKLIST);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean hasSameOwner(TamableAnimal cockatrice, Entity entity) {
        if (entity instanceof TamableAnimal tameable)
            return tameable.getOwnerUUID() != null && cockatrice.getOwnerUUID() != null && tameable.getOwnerUUID().equals(cockatrice.getOwnerUUID());
        return false;
    }

    public static boolean isAlive(final LivingEntity entity) {
        if (entity instanceof DragonBaseEntity dragon && dragon.isMobDead())
            return false;
        return (!(entity instanceof IDeadMob deadMob) || !deadMob.isMobDead()) && !GorgonEntity.isStoneMob(entity);
    }


    public static boolean canGrief(DragonBaseEntity dragon) {
        if (dragon.isTame()) {
            if (!IafCommonConfig.INSTANCE.dragon.tamedGriefing.getValue()) return false;
        }
        return IafCommonConfig.INSTANCE.dragon.griefing.getValue();

    }

    public static boolean canHostilesTarget(Entity entity) {
        if (entity instanceof Player && (entity.level().getDifficulty() == Difficulty.PEACEFUL || ((Player) entity).isCreative()))
            return false;
        if (entity instanceof DragonBaseEntity dragonBase && dragonBase.isMobDead())
            return false;
        else
            return entity instanceof LivingEntity livingEntity && isAlive(livingEntity);
    }

    public static boolean onSameTeam(Entity entity1, Entity entity2) {
        Entity owner1 = null;
        Entity owner2 = null;
        boolean def = entity1.isAlliedTo(entity2);
        if (entity1 instanceof TamableAnimal tameable)
            owner1 = tameable.getOwner();
        if (entity2 instanceof TamableAnimal tameable)
            owner2 = tameable.getOwner();
        if (entity1 instanceof MultipartPartEntity mutlipartPart) {
            Entity multipart = mutlipartPart.getParent();
            if (multipart instanceof TamableAnimal tameable)
                owner1 = tameable.getOwner();
        }
        if (entity2 instanceof MultipartPartEntity mutlipartPart) {
            Entity multipart = mutlipartPart.getParent();
            if (multipart instanceof TamableAnimal tameable)
                owner2 = tameable.getOwner();
        }
        if (owner1 != null && owner2 != null)
            return owner1.is(owner2);
        return def;
    }

    public static boolean isDreadBlock(BlockState state) {
        Block block = state.getBlock();
        return block == IafBlocks.DREAD_STONE.get() || block == IafBlocks.DREAD_STONE_BRICKS.get() || block == IafBlocks.DREAD_STONE_BRICKS_CHISELED.get() ||
                block == IafBlocks.DREAD_STONE_BRICKS_CRACKED.get() || block == IafBlocks.DREAD_STONE_BRICKS_MOSSY.get() || block == IafBlocks.DREAD_STONE_TILE.get() ||
                block == IafBlocks.DREAD_STONE_FACE.get() || block == IafBlocks.DREAD_TORCH.get() || block == IafBlocks.DREAD_STONE_BRICKS_STAIRS.get() ||
                block == IafBlocks.DREAD_STONE_BRICKS_SLAB.get() || block == IafBlocks.DREADWOOD_LOG.get() ||
                block == IafBlocks.DREADWOOD_PLANKS.get() || block == IafBlocks.DREADWOOD_PLANKS_LOCK.get() || block == IafBlocks.DREAD_PORTAL.get() ||
                block == IafBlocks.DREAD_SPAWNER.get();
    }
}
