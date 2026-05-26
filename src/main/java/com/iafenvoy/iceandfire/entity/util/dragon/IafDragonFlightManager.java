package com.iafenvoy.iceandfire.entity.util.dragon;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.AmphithereEntity;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.IceDragonEntity;
import com.iafenvoy.iceandfire.entity.util.IFlyingMount;
import com.iafenvoy.iceandfire.util.IafMath;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.phys.Vec3;


public class IafDragonFlightManager {
    private final DragonBaseEntity dragon;
    private Vec3 target;
    private Vec3 startAttackVec;
    private Vec3 startPreyVec;
    private LivingEntity prevAttackTarget = null;

    public IafDragonFlightManager(DragonBaseEntity dragon) {
        this.dragon = dragon;
    }

    public static float approach(float number, float max, float min) {
        min = Math.abs(min);
        return number < max ? Mth.clamp(number + min, number, max) : Mth.clamp(number - min, max, number);
    }

    public static float approachDegrees(float number, float max, float min) {
        float add = Mth.wrapDegrees(max - number);
        return approach(number, number + add, min);
    }

    public static float degreesDifferenceAbs(float f1, float f2) {
        return Math.abs(Mth.wrapDegrees(f2 - f1));
    }

    public void update() {

        if (this.dragon.getTarget() != null && this.dragon.getTarget().isAlive()) {
            if (this.dragon instanceof IceDragonEntity && this.dragon.isInWater())
                this.dragon.airAttack = this.dragon.getTarget() == null ? IafDragonAttacks.Air.SCORCH_STREAM : IafDragonAttacks.Air.TACKLE;
            LivingEntity entity = this.dragon.getTarget();
            if (this.dragon.airAttack == IafDragonAttacks.Air.TACKLE)
                this.target = new Vec3(entity.getX(), entity.getY() + entity.getBbHeight(), entity.getZ());
            if (this.dragon.airAttack == IafDragonAttacks.Air.HOVER_BLAST) {
                float distY = 5 + this.dragon.getDragonStage() * 2;
                int randomDist = 20;
                if (this.dragon.distanceToSqr(entity.getX(), this.dragon.getY(), entity.getZ()) < 16 || this.dragon.distanceToSqr(entity.getX(), this.dragon.getY(), entity.getZ()) > 900)
                    this.target = new Vec3(entity.getX() + this.dragon.getRandom().nextInt(randomDist) - (double) randomDist / 2, entity.getY() + distY, entity.getZ() + this.dragon.getRandom().nextInt(randomDist) - (double) randomDist / 2);
                this.dragon.breathAttack(entity.getX(), entity.getY(), entity.getZ(), true);
            }
            if (this.dragon.airAttack == IafDragonAttacks.Air.SCORCH_STREAM && this.startPreyVec != null && this.startAttackVec != null) {
                float distX = (float) (this.startPreyVec.x - this.startAttackVec.x);
                float distY = 5 + this.dragon.getDragonStage() * 2;
                float distZ = (float) (this.startPreyVec.z - this.startAttackVec.z);
                this.target = new Vec3(entity.getX() + distX, entity.getY() + distY, entity.getZ() + distZ);
                this.dragon.tryScorchTarget();
                if (this.target != null && this.dragon.distanceToSqr(this.target.x, this.target.y, this.target.z) < 100)
                    this.target = new Vec3(entity.getX() - distX, entity.getY() + distY, entity.getZ() - distZ);
            }

        } else if (this.target == null || this.dragon.distanceToSqr(this.target.x, this.target.y, this.target.z) < 4
                || !this.dragon.level().isEmptyBlock(BlockPos.containing(this.target.x, this.target.y, this.target.z))
                && (this.dragon.isHovering() || this.dragon.isFlying())
                || this.dragon.getCommand() == 2 && this.dragon.shouldTPtoOwner()) {
            BlockPos viewBlock = null;

            if (this.dragon instanceof IceDragonEntity && this.dragon.isInWater())
                viewBlock = DragonUtils.getWaterBlockInView(this.dragon);
            if (this.dragon.getCommand() == 2 && this.dragon.useFlyingPathFinder())
                viewBlock = this.dragon instanceof IceDragonEntity && this.dragon.isInWater() ? DragonUtils.getWaterBlockInViewEscort(this.dragon) : DragonUtils.getBlockInViewEscort(this.dragon);
            else if (this.dragon.lookingForRoostAIFlag) {
                // FIXME :: Unused
                BlockPos upPos = this.dragon.getRestrictCenter();
                if (this.dragon.getDistanceSquared(Vec3.atCenterOf(this.dragon.getRestrictCenter())) > 200)
                    upPos = upPos.above(30);
                viewBlock = upPos;

            } else if (viewBlock == null) {
                viewBlock = DragonUtils.getBlockInView(this.dragon);
                if (this.dragon.isInWater())
                    // If the dragon is in water, take off to reach the air target
                    this.dragon.setHovering(true);
            }
            if (viewBlock != null)
                this.target = new Vec3(viewBlock.getX() + 0.5, viewBlock.getY() + 0.5, viewBlock.getZ() + 0.5);
        }
        if (this.target != null) {
            if (this.target.y > IafCommonConfig.INSTANCE.dragon.maxFlight.getValue()) {
                this.target = new Vec3(this.target.x, IafCommonConfig.INSTANCE.dragon.maxFlight.getValue(), this.target.z);
            }
            if (this.target.y >= this.dragon.getY() && !this.dragon.isModelDead())
                this.dragon.setDeltaMovement(this.dragon.getDeltaMovement().add(0, 0.1D, 0));
        }
    }

    public Vec3 getFlightTarget() {
        return this.target == null ? Vec3.ZERO : this.target;
    }

    public void setFlightTarget(Vec3 target) {
        this.target = target;
    }

    private float getDistanceXZ(double x, double z) {
        float f = (float) (this.dragon.getX() - x);
        float f2 = (float) (this.dragon.getZ() - z);
        return f * f + f2 * f2;
    }

    public void onSetAttackTarget(LivingEntity LivingEntityIn) {
        if (this.prevAttackTarget != LivingEntityIn) {
            this.startPreyVec = LivingEntityIn != null ? new Vec3(LivingEntityIn.getX(), LivingEntityIn.getY(), LivingEntityIn.getZ()) : new Vec3(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
            this.startAttackVec = new Vec3(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        }
        this.prevAttackTarget = LivingEntityIn;
    }

    public static class GroundMoveHelper extends MoveControl {
        public GroundMoveHelper(Mob LivingEntityIn) {
            super(LivingEntityIn);
        }

        public float distance(float rotateAngleFrom, float rotateAngleTo) {
            return (float) IafMath.atan2_accurate(Mth.sin(rotateAngleTo - rotateAngleFrom), Mth.cos(rotateAngleTo - rotateAngleFrom));
        }

        @Override
        public void tick() {
            if (this.operation == Operation.STRAFE) {
                float f = (float) this.mob.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
                float f1 = (float) this.speedModifier * f;
                float f2 = this.strafeForwards;
                float f3 = this.strafeRight;
                float f4 = Mth.sqrt(f2 * f2 + f3 * f3);

                if (f4 < 1.0F) f4 = 1.0F;

                f4 = f1 / f4;
                f2 = f2 * f4;
                f3 = f3 * f4;
                float f5 = Mth.sin(this.mob.getYRot() * 0.017453292F);
                float f6 = Mth.cos(this.mob.getYRot() * 0.017453292F);
                float f7 = f2 * f6 - f3 * f5;
                float f8 = f3 * f6 + f2 * f5;
                PathNavigation pathnavigate = this.mob.getNavigation();
                NodeEvaluator nodeprocessor = pathnavigate.getNodeEvaluator();
                if (nodeprocessor.getPathType(new PathfindingContext(this.mob.level(), this.mob), Mth.floor(this.mob.getX() + (double) f7), Mth.floor(this.mob.getY()), Mth.floor(this.mob.getZ() + (double) f8)) != PathType.WALKABLE) {
                    this.strafeForwards = 1.0F;
                    this.strafeRight = 0.0F;
                    f1 = f;
                }
                this.mob.setSpeed(f1);
                this.mob.setZza(this.strafeForwards);
                this.mob.setXxa(this.strafeRight);
                this.operation = Operation.WAIT;
            } else if (this.operation == Operation.MOVE_TO) {
                this.operation = Operation.WAIT;
                DragonBaseEntity dragonBase = (DragonBaseEntity) this.mob;
                double d0 = this.getWantedX() - this.mob.getX();
                double d1 = this.getWantedZ() - this.mob.getZ();
                double d2 = this.getWantedY() - this.mob.getY();
                double d3 = d0 * d0 + d2 * d2 + d1 * d1;

                if (d3 < 2.500000277905201E-7D) {
                    this.mob.setZza(0.0F);
                    return;
                }
                float targetDegree = (float) (Mth.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
                float changeRange = 70F;
                if (Math.ceil(dragonBase.getBbWidth()) > 2F) {
                    float ageMod = 1F - Math.min(dragonBase.getAgeInDays(), 125) / 125F;
                    changeRange = 5 + ageMod * 10;
                }
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), targetDegree, changeRange));
                this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                if (d2 > (double) this.mob.maxUpStep() && d0 * d0 + d1 * d1 < (double) Math.max(1.0F, this.mob.getBbWidth() / 2)) {
                    this.mob.getJumpControl().jump();
                    this.operation = Operation.JUMPING;
                }
            } else if (this.operation == Operation.JUMPING) {
                this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                if (this.mob.onGround()) this.operation = Operation.WAIT;
            } else this.mob.setZza(0.0F);
        }
    }

    public static class FlightMoveHelper extends MoveControl {
        private final DragonBaseEntity dragon;

        public FlightMoveHelper(DragonBaseEntity dragonBase) {
            super(dragonBase);
            this.dragon = dragonBase;
        }

        @Override
        public void tick() {
            if (this.dragon.horizontalCollision) {
                this.dragon.setYRot(this.dragon.getYRot() + 180.0F);
                this.speedModifier = 0.1F;
                this.dragon.flightManager.target = null;
                return;
            }
            float distX = (float) (this.dragon.flightManager.getFlightTarget().x - this.dragon.getX());
            float distY = (float) (this.dragon.flightManager.getFlightTarget().y - this.dragon.getY());
            float distZ = (float) (this.dragon.flightManager.getFlightTarget().z - this.dragon.getZ());
            double planeDist = Math.sqrt(distX * distX + distZ * distZ);
            double yDistMod = 1.0D - (double) Mth.abs(distY * 0.7F) / planeDist;
            distX = (float) ((double) distX * yDistMod);
            distZ = (float) ((double) distZ * yDistMod);
            planeDist = Mth.sqrt(distX * distX + distZ * distZ);
            double dist = Math.sqrt(distX * distX + distZ * distZ + distY * distY);
            if (dist > 1.0F) {
                float yawCopy = this.dragon.getYRot();
                float atan = (float) Mth.atan2(distZ, distX);
                float yawTurn = Mth.wrapDegrees(this.dragon.getYRot() + 90);
                float yawTurnAtan = Mth.wrapDegrees(atan * 57.295776F);
                this.dragon.setYRot(IafDragonFlightManager.approachDegrees(yawTurn, yawTurnAtan, this.dragon.airAttack == IafDragonAttacks.Air.TACKLE && this.dragon.getTarget() != null ? 10 : 4.0F) - 90.0F);
                this.dragon.yBodyRot = this.dragon.getYRot();
                if (IafDragonFlightManager.degreesDifferenceAbs(yawCopy, this.dragon.getYRot()) < 3.0F)
                    this.speedModifier = IafDragonFlightManager.approach((float) this.speedModifier, 1.8F, 0.005F * (1.8F / (float) this.speedModifier));
                else {
                    this.speedModifier = IafDragonFlightManager.approach((float) this.speedModifier, 0.2F, 0.025F);
                    if (dist < 100D && this.dragon.getTarget() != null)
                        this.speedModifier = this.speedModifier * (dist / 100D);
                }
                float finPitch = (float) (-(Mth.atan2(-distY, planeDist) * 57.2957763671875D));
                this.dragon.setXRot(finPitch);
                float yawTurnHead = this.dragon.getYRot() + 90.0F;
                this.speedModifier *= this.dragon.getFlightSpeedModifier();
                this.speedModifier *= Math.min(1, dist / 50 + 0.3);//Make the dragon fly slower when close to target
                double x = this.speedModifier * Mth.cos(yawTurnHead * 0.017453292F) * Math.abs((double) distX / dist);
                double y = this.speedModifier * Mth.sin(finPitch * 0.017453292F) * Math.abs((double) distY / dist);
                double z = this.speedModifier * Mth.sin(yawTurnHead * 0.017453292F) * Math.abs((double) distZ / dist);
                double motionCap = 0.2D;
                this.dragon.setDeltaMovement(this.dragon.getDeltaMovement().add(Math.min(x * 0.2D, motionCap), Math.min(y * 0.2D, motionCap), Math.min(z * 0.2D, motionCap)));
            }
        }
    }

    public static class PlayerFlightMoveHelper<T extends Mob & IFlyingMount> extends MoveControl {
        private final T dragon;

        public PlayerFlightMoveHelper(T dragon) {
            super(dragon);
            this.dragon = dragon;
        }

        @Override
        public void tick() {
            if (this.dragon instanceof DragonBaseEntity theDragon && theDragon.getControllingPassenger() != null)
                // New ride system doesn't need move controller
                // The flight move control is disabled here, the walking move controller will stay Operation.WAIT so nothing will happen too
                return;

            double flySpeed = this.speedModifier * this.speedMod() * 3;
            Vec3 dragonVec = this.dragon.position();
            Vec3 moveVec = new Vec3(this.wantedX, this.wantedY, this.wantedZ);
            Vec3 normalized = moveVec.subtract(dragonVec).normalize();
            double dist = dragonVec.distanceTo(moveVec);
            this.dragon.setDeltaMovement(normalized.x * flySpeed, normalized.y * flySpeed, normalized.z * flySpeed);
            if (dist > 2.5E-7) {
                float yaw = (float) Math.toDegrees(Math.PI * 2 - Math.atan2(normalized.x, normalized.y));
                this.dragon.setYRot(this.rotlerp(this.dragon.getYRot(), yaw, 5));
                this.dragon.setSpeed((float) (this.speedModifier));
            }
            this.dragon.move(MoverType.SELF, this.dragon.getDeltaMovement());
        }

        public double speedMod() {
            return (this.dragon instanceof AmphithereEntity ? 0.6D : 1.25D) * IafCommonConfig.INSTANCE.dragon.dragonFlightSpeedMod.getValue().floatValue() * this.dragon.getAttributeValue(Attributes.MOVEMENT_SPEED);
        }
    }
}
