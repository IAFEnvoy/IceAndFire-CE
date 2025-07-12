package com.iafenvoy.iceandfire.entity.util.dragon;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.EntityAmphithere;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.entity.EntityIceDragon;
import com.iafenvoy.iceandfire.entity.util.IFlyingMount;
import com.iafenvoy.iceandfire.util.IafMath;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


public class IafDragonFlightManager {
    private final EntityDragonBase dragon;
    private Vec3d target;
    private Vec3d startAttackVec;
    private Vec3d startPreyVec;
    private LivingEntity prevAttackTarget = null;

    public IafDragonFlightManager(EntityDragonBase dragon) {
        this.dragon = dragon;
    }

    public static float approach(float number, float max, float min) {
        min = Math.abs(min);
        return number < max ? MathHelper.clamp(number + min, number, max) : MathHelper.clamp(number - min, max, number);
    }

    public static float approachDegrees(float number, float max, float min) {
        float add = MathHelper.wrapDegrees(max - number);
        return approach(number, number + add, min);
    }

    public static float degreesDifferenceAbs(float f1, float f2) {
        return Math.abs(MathHelper.wrapDegrees(f2 - f1));
    }

    public void update() {

        if (this.dragon.getTarget() != null && this.dragon.getTarget().isAlive()) {
            if (this.dragon instanceof EntityIceDragon && this.dragon.isTouchingWater())
                this.dragon.airAttack = this.dragon.getTarget() == null ? IafDragonAttacks.Air.SCORCH_STREAM : IafDragonAttacks.Air.TACKLE;
            LivingEntity entity = this.dragon.getTarget();
            if (this.dragon.airAttack == IafDragonAttacks.Air.TACKLE)
                this.target = new Vec3d(entity.getX(), entity.getY() + entity.getHeight(), entity.getZ());
            if (this.dragon.airAttack == IafDragonAttacks.Air.HOVER_BLAST) {
                float distY = 5 + this.dragon.getDragonStage() * 2;
                int randomDist = 20;
                if (this.dragon.squaredDistanceTo(entity.getX(), this.dragon.getY(), entity.getZ()) < 16 || this.dragon.squaredDistanceTo(entity.getX(), this.dragon.getY(), entity.getZ()) > 900)
                    this.target = new Vec3d(entity.getX() + this.dragon.getRandom().nextInt(randomDist) - (double) randomDist / 2, entity.getY() + distY, entity.getZ() + this.dragon.getRandom().nextInt(randomDist) - (double) randomDist / 2);
                this.dragon.stimulateFire(entity.getX(), entity.getY(), entity.getZ(), 3);
            }
            if (this.dragon.airAttack == IafDragonAttacks.Air.SCORCH_STREAM && this.startPreyVec != null && this.startAttackVec != null) {
                float distX = (float) (this.startPreyVec.x - this.startAttackVec.x);
                float distY = 5 + this.dragon.getDragonStage() * 2;
                float distZ = (float) (this.startPreyVec.z - this.startAttackVec.z);
                this.target = new Vec3d(entity.getX() + distX, entity.getY() + distY, entity.getZ() + distZ);
                this.dragon.tryScorchTarget();
                if (this.target != null && this.dragon.squaredDistanceTo(this.target.x, this.target.y, this.target.z) < 100)
                    this.target = new Vec3d(entity.getX() - distX, entity.getY() + distY, entity.getZ() - distZ);
            }

        } else if (this.target == null || this.dragon.squaredDistanceTo(this.target.x, this.target.y, this.target.z) < 4
                || !this.dragon.getWorld().isAir(BlockPos.ofFloored(this.target.x, this.target.y, this.target.z))
                && (this.dragon.isHovering() || this.dragon.isFlying())
                || this.dragon.getCommand() == 2 && this.dragon.shouldTPtoOwner()) {
            BlockPos viewBlock = null;

            if (this.dragon instanceof EntityIceDragon && this.dragon.isTouchingWater())
                viewBlock = DragonUtils.getWaterBlockInView(this.dragon);
            if (this.dragon.getCommand() == 2 && this.dragon.useFlyingPathFinder())
                viewBlock = this.dragon instanceof EntityIceDragon && this.dragon.isTouchingWater() ? DragonUtils.getWaterBlockInViewEscort(this.dragon) : DragonUtils.getBlockInViewEscort(this.dragon);
            else if (this.dragon.lookingForRoostAIFlag) {
                // FIXME :: Unused
                BlockPos upPos = this.dragon.getPositionTarget();
                if (this.dragon.getDistanceSquared(Vec3d.ofCenter(this.dragon.getPositionTarget())) > 200)
                    upPos = upPos.up(30);
                viewBlock = upPos;

            } else if (viewBlock == null) {
                viewBlock = DragonUtils.getBlockInView(this.dragon);
                if (this.dragon.isTouchingWater())
                    // If the dragon is in water, take off to reach the air target
                    this.dragon.setHovering(true);
            }
            if (viewBlock != null)
                this.target = new Vec3d(viewBlock.getX() + 0.5, viewBlock.getY() + 0.5, viewBlock.getZ() + 0.5);
        }
        if (this.target != null) {
            if (this.target.y > IafCommonConfig.INSTANCE.dragon.maxFlight.getValue()) {
                this.target = new Vec3d(this.target.x, IafCommonConfig.INSTANCE.dragon.maxFlight.getValue(), this.target.z);
            }
            if (this.target.y >= this.dragon.getY() && !this.dragon.isModelDead())
                this.dragon.setVelocity(this.dragon.getVelocity().add(0, 0.1D, 0));
        }
    }

    public Vec3d getFlightTarget() {
        return this.target == null ? Vec3d.ZERO : this.target;
    }

    public void setFlightTarget(Vec3d target) {
        this.target = target;
    }

    private float getDistanceXZ(double x, double z) {
        float f = (float) (this.dragon.getX() - x);
        float f2 = (float) (this.dragon.getZ() - z);
        return f * f + f2 * f2;
    }

    public void onSetAttackTarget(LivingEntity LivingEntityIn) {
        if (this.prevAttackTarget != LivingEntityIn) {
            this.startPreyVec = LivingEntityIn != null ? new Vec3d(LivingEntityIn.getX(), LivingEntityIn.getY(), LivingEntityIn.getZ()) : new Vec3d(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
            this.startAttackVec = new Vec3d(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        }
        this.prevAttackTarget = LivingEntityIn;
    }

    public static class GroundMoveHelper extends MoveControl {
        public GroundMoveHelper(MobEntity LivingEntityIn) {
            super(LivingEntityIn);
        }

        public float distance(float rotateAngleFrom, float rotateAngleTo) {
            return (float) IafMath.atan2_accurate(MathHelper.sin(rotateAngleTo - rotateAngleFrom), MathHelper.cos(rotateAngleTo - rotateAngleFrom));
        }

        @Override
        public void tick() {
            if (this.state == State.STRAFE) {
                float f = (float) this.entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue();
                float f1 = (float) this.speed * f;
                float f2 = this.forwardMovement;
                float f3 = this.sidewaysMovement;
                float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);

                if (f4 < 1.0F) f4 = 1.0F;

                f4 = f1 / f4;
                f2 = f2 * f4;
                f3 = f3 * f4;
                float f5 = MathHelper.sin(this.entity.getYaw() * 0.017453292F);
                float f6 = MathHelper.cos(this.entity.getYaw() * 0.017453292F);
                float f7 = f2 * f6 - f3 * f5;
                float f8 = f3 * f6 + f2 * f5;
                EntityNavigation pathnavigate = this.entity.getNavigation();
                if (pathnavigate != null) {
                    PathNodeMaker nodeprocessor = pathnavigate.getNodeMaker();
                    if (nodeprocessor != null && nodeprocessor.getDefaultNodeType(this.entity.getWorld(), MathHelper.floor(this.entity.getX() + (double) f7), MathHelper.floor(this.entity.getY()), MathHelper.floor(this.entity.getZ() + (double) f8)) != PathNodeType.WALKABLE) {
                        this.forwardMovement = 1.0F;
                        this.sidewaysMovement = 0.0F;
                        f1 = f;
                    }
                }
                this.entity.setMovementSpeed(f1);
                this.entity.setForwardSpeed(this.forwardMovement);
                this.entity.setSidewaysSpeed(this.sidewaysMovement);
                this.state = State.WAIT;
            } else if (this.state == State.MOVE_TO) {
                this.state = State.WAIT;
                EntityDragonBase dragonBase = (EntityDragonBase) this.entity;
                double d0 = this.getTargetX() - this.entity.getX();
                double d1 = this.getTargetZ() - this.entity.getZ();
                double d2 = this.getTargetY() - this.entity.getY();
                double d3 = d0 * d0 + d2 * d2 + d1 * d1;

                if (d3 < 2.500000277905201E-7D) {
                    this.entity.setForwardSpeed(0.0F);
                    return;
                }
                float targetDegree = (float) (MathHelper.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
                float changeRange = 70F;
                if (Math.ceil(dragonBase.getWidth()) > 2F) {
                    float ageMod = 1F - Math.min(dragonBase.getAgeInDays(), 125) / 125F;
                    changeRange = 5 + ageMod * 10;
                }
                this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), targetDegree, changeRange));
                this.entity.setMovementSpeed((float) (this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
                if (d2 > (double) this.entity.getStepHeight() && d0 * d0 + d1 * d1 < (double) Math.max(1.0F, this.entity.getWidth() / 2)) {
                    this.entity.getJumpControl().setActive();
                    this.state = State.JUMPING;
                }
            } else if (this.state == State.JUMPING) {
                this.entity.setMovementSpeed((float) (this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
                if (this.entity.isOnGround()) this.state = State.WAIT;
            } else this.entity.setForwardSpeed(0.0F);
        }
    }

    public static class FlightMoveHelper extends MoveControl {
        private final EntityDragonBase dragon;

        public FlightMoveHelper(EntityDragonBase dragonBase) {
            super(dragonBase);
            this.dragon = dragonBase;
        }

        @Override
        public void tick() {
            if (this.dragon.horizontalCollision) {
                this.dragon.setYaw(this.dragon.getYaw() + 180.0F);
                this.speed = 0.1F;
                this.dragon.flightManager.target = null;
                return;
            }
            float distX = (float) (this.dragon.flightManager.getFlightTarget().x - this.dragon.getX());
            float distY = (float) (this.dragon.flightManager.getFlightTarget().y - this.dragon.getY());
            float distZ = (float) (this.dragon.flightManager.getFlightTarget().z - this.dragon.getZ());
            double planeDist = Math.sqrt(distX * distX + distZ * distZ);
            double yDistMod = 1.0D - (double) MathHelper.abs(distY * 0.7F) / planeDist;
            distX = (float) ((double) distX * yDistMod);
            distZ = (float) ((double) distZ * yDistMod);
            planeDist = MathHelper.sqrt(distX * distX + distZ * distZ);
            double dist = Math.sqrt(distX * distX + distZ * distZ + distY * distY);
            if (dist > 1.0F) {
                float yawCopy = this.dragon.getYaw();
                float atan = (float) MathHelper.atan2(distZ, distX);
                float yawTurn = MathHelper.wrapDegrees(this.dragon.getYaw() + 90);
                float yawTurnAtan = MathHelper.wrapDegrees(atan * 57.295776F);
                this.dragon.setYaw(IafDragonFlightManager.approachDegrees(yawTurn, yawTurnAtan, this.dragon.airAttack == IafDragonAttacks.Air.TACKLE && this.dragon.getTarget() != null ? 10 : 4.0F) - 90.0F);
                this.dragon.bodyYaw = this.dragon.getYaw();
                if (IafDragonFlightManager.degreesDifferenceAbs(yawCopy, this.dragon.getYaw()) < 3.0F)
                    this.speed = IafDragonFlightManager.approach((float) this.speed, 1.8F, 0.005F * (1.8F / (float) this.speed));
                else {
                    this.speed = IafDragonFlightManager.approach((float) this.speed, 0.2F, 0.025F);
                    if (dist < 100D && this.dragon.getTarget() != null)
                        this.speed = this.speed * (dist / 100D);
                }
                float finPitch = (float) (-(MathHelper.atan2(-distY, planeDist) * 57.2957763671875D));
                this.dragon.setPitch(finPitch);
                float yawTurnHead = this.dragon.getYaw() + 90.0F;
                this.speed *= this.dragon.getFlightSpeedModifier();
                this.speed *= Math.min(1, dist / 50 + 0.3);//Make the dragon fly slower when close to target
                double x = this.speed * MathHelper.cos(yawTurnHead * 0.017453292F) * Math.abs((double) distX / dist);
                double y = this.speed * MathHelper.sin(finPitch * 0.017453292F) * Math.abs((double) distY / dist);
                double z = this.speed * MathHelper.sin(yawTurnHead * 0.017453292F) * Math.abs((double) distZ / dist);
                double motionCap = 0.2D;
                this.dragon.setVelocity(this.dragon.getVelocity().add(Math.min(x * 0.2D, motionCap), Math.min(y * 0.2D, motionCap), Math.min(z * 0.2D, motionCap)));
            }
        }
    }

    public static class PlayerFlightMoveHelper<T extends MobEntity & IFlyingMount> extends MoveControl {
        private final T dragon;

        public PlayerFlightMoveHelper(T dragon) {
            super(dragon);
            this.dragon = dragon;
        }

        @Override
        public void tick() {
            if (this.dragon instanceof EntityDragonBase theDragon && theDragon.getControllingPassenger() != null)
                // New ride system doesn't need move controller
                // The flight move control is disabled here, the walking move controller will stay Operation.WAIT so nothing will happen too
                return;

            double flySpeed = this.speed * this.speedMod() * 3;
            Vec3d dragonVec = this.dragon.getPos();
            Vec3d moveVec = new Vec3d(this.targetX, this.targetY, this.targetZ);
            Vec3d normalized = moveVec.subtract(dragonVec).normalize();
            double dist = dragonVec.distanceTo(moveVec);
            this.dragon.setVelocity(normalized.x * flySpeed, normalized.y * flySpeed, normalized.z * flySpeed);
            if (dist > 2.5E-7) {
                float yaw = (float) Math.toDegrees(Math.PI * 2 - Math.atan2(normalized.x, normalized.y));
                this.dragon.setYaw(this.wrapDegrees(this.dragon.getYaw(), yaw, 5));
                this.dragon.setMovementSpeed((float) (this.speed));
            }
            this.dragon.move(MovementType.SELF, this.dragon.getVelocity());
        }

        public double speedMod() {
            return (this.dragon instanceof EntityAmphithere ? 0.6D : 1.25D) * IafCommonConfig.INSTANCE.dragon.dragonFlightSpeedMod.getValue().floatValue() * this.dragon.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        }
    }
}
