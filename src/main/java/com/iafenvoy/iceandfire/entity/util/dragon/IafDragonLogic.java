package com.iafenvoy.iceandfire.entity.util.dragon;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.DreadQueenEntity;
import com.iafenvoy.iceandfire.registry.IafSounds;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/*
    dragon logic separation for client, server and shared sides.
 */
public class IafDragonLogic {
    private final DragonBaseEntity dragon;
    long ticksAfterClearingTarget;

    public IafDragonLogic(DragonBaseEntity dragon) {
        this.dragon = dragon;
    }

    /*
    logic done exclusively on server.
    */
    public void updateDragonServer() {
        // Update dragon rider
        this.dragon.updateRider();

        // Update dragon pitch
        this.dragon.updatePitch(this.dragon.yo - this.dragon.getY());

        if (this.dragon.lookingForRoostAIFlag && this.dragon.getLastHurtByMob() != null || this.dragon.isSleeping()) {
            this.dragon.lookingForRoostAIFlag = false;
        }
        if (IafCommonConfig.INSTANCE.dragon.sleep.getValue() && !this.dragon.isSleeping() && !this.dragon.isTimeToWake() && this.dragon.getPassengers().isEmpty() && this.dragon.getCommand() != 2) {
            if (this.dragon.hasHomePosition
                    && this.dragon.getRestrictCenter() != null
                    && DragonUtils.isInHomeDimension(this.dragon)
                    && this.dragon.getDistanceSquared(Vec3.atCenterOf(this.dragon.getRestrictCenter())) > this.dragon.getBbWidth() * 10
                    && this.dragon.getCommand() != 2 && this.dragon.getCommand() != 1) {
                this.dragon.lookingForRoostAIFlag = true;
            } else {
                this.dragon.lookingForRoostAIFlag = false;
                if ((/* Avoid immediately sleeping after killing the target */ this.dragon.level().getGameTime() - this.ticksAfterClearingTarget >= 20) && !this.dragon.isInWater() && this.dragon.onGround() && !this.dragon.isFlying() && !this.dragon.isHovering() && this.dragon.getTarget() == null)
                    this.dragon.setInSittingPose(true);
            }
        } else
            this.dragon.lookingForRoostAIFlag = false;
        if (this.dragon.isSleeping() && (this.dragon.isFlying() || this.dragon.isHovering() || this.dragon.isInWater() || (this.dragon.level().canSeeSkyFromBelowWater(this.dragon.blockPosition()) && this.dragon.isTimeToWake() && !this.dragon.isTame() || this.dragon.isTimeToWake() && this.dragon.isTame()) || this.dragon.getTarget() != null || !this.dragon.getPassengers().isEmpty()))
            this.dragon.setInSittingPose(false);
        if (this.dragon.isOrderedToSit() && this.dragon.getControllingPassenger() != null)
            this.dragon.setOrderedToSit(false);
        if (this.dragon.blockBreakCounter <= 0)
            this.dragon.blockBreakCounter = IafCommonConfig.INSTANCE.dragon.breakBlockCooldown.getValue();
        this.dragon.updateBurnTarget();
        if (this.dragon.isOrderedToSit()) {
            if (this.dragon.getCommand() != 1 || this.dragon.getControllingPassenger() != null)
                this.dragon.setOrderedToSit(false);
        } else {
            if (this.dragon.getCommand() == 1 && this.dragon.getControllingPassenger() == null)
                this.dragon.setOrderedToSit(true);
        }
        if (this.dragon.isOrderedToSit())
            this.dragon.getNavigation().stop();
        if (this.dragon.isInLove())
            this.dragon.level().broadcastEntityEvent(this.dragon, (byte) 18);
        if (new Vec3i((int) this.dragon.xo, (int) this.dragon.yo, (int) this.dragon.zo).distSqr(this.dragon.blockPosition()) <= 0.5)
            this.dragon.ticksStill++;
        else this.dragon.ticksStill = 0;
        if (this.dragon.getControllingPassenger() == null && this.dragon.isTackling() && !this.dragon.isFlying() && this.dragon.onGround()) {
            this.dragon.tacklingTicks++;
            if (this.dragon.tacklingTicks == 40) {
                this.dragon.tacklingTicks = 0;
                this.dragon.setTackling(false);
                this.dragon.setFlying(false);
            }
        }
        if (this.dragon.getRandom().nextInt(500) == 0 && !this.dragon.isModelDead() && !this.dragon.isSleeping())
            this.dragon.roar();

        // In air tackle attack
        if (this.dragon.isFlying() && this.dragon.getTarget() != null) {
            if (this.dragon.airAttack == IafDragonAttacks.Air.TACKLE)
                this.dragon.setTackling(true);

            if (this.dragon.isTackling())
                if (this.dragon.getBoundingBox().expandTowards(2.0D, 2.0D, 2.0D).intersects(this.dragon.getTarget().getBoundingBox())) {
                    this.dragon.usingGroundAttack = true;
                    this.dragon.randomizeAttacks();
                    this.attackTarget(this.dragon.getTarget(), null, this.dragon.getDragonStage() * 3);
                    this.dragon.setFlying(false);
                    this.dragon.setHovering(false);
                }
        }

        if (this.dragon.getControllingPassenger() == null && this.dragon.isTackling() && (this.dragon.getTarget() == null || this.dragon.airAttack != IafDragonAttacks.Air.TACKLE)) {
            this.dragon.setTackling(false);
            this.dragon.randomizeAttacks();
        }
        if (this.dragon.isPassenger()) {
            this.dragon.setFlying(false);
            this.dragon.setHovering(false);
            this.dragon.setInSittingPose(false);
        }
        if (this.dragon.isFlying() && this.dragon.tickCount % 40 == 0 || this.dragon.isFlying() && this.dragon.isSleeping())
            this.dragon.setInSittingPose(false);
        if (!this.dragon.canMove()) {
            if (this.dragon.getTarget() != null) {
                this.dragon.setTarget(null);
                this.ticksAfterClearingTarget = this.dragon.level().getGameTime();
            }
            this.dragon.getNavigation().stop();
        }
        if (!this.dragon.isTame())
            this.dragon.updateCheckPlayer();
        if (this.dragon.isModelDead() && (this.dragon.isFlying() || this.dragon.isHovering())) {
            this.dragon.setFlying(false);
            this.dragon.setHovering(false);
        }
        if (this.dragon.getControllingPassenger() == null) {
            if ((this.dragon.useFlyingPathFinder() || this.dragon.isHovering()) && this.dragon.navigatorType != 1)
                this.dragon.switchNavigator(1);
        } else {
            if ((this.dragon.useFlyingPathFinder() || this.dragon.isHovering()) && this.dragon.navigatorType != 2)
                this.dragon.switchNavigator(2);
        }
        if (this.dragon.getControllingPassenger() == null && !this.dragon.useFlyingPathFinder() && !this.dragon.isHovering() && this.dragon.navigatorType != 0)
            this.dragon.switchNavigator(0);
        // Dragon landing
        if (this.dragon.getControllingPassenger() == null && !this.dragon.isOverAir() && this.dragon.doesWantToLand() && (this.dragon.isFlying() || this.dragon.isHovering()) && !this.dragon.isInWater()) {
            this.dragon.setFlying(false);
            this.dragon.setHovering(false);
        }
        if (this.dragon.isHovering()) {
            if (this.dragon.isFlying() && this.dragon.flyTicks > 40) {
                this.dragon.setHovering(false);
                this.dragon.setFlying(true);
            }
            this.dragon.hoverTicks++;
        } else this.dragon.hoverTicks = 0;
        if (this.dragon.isHovering() && !this.dragon.isFlying()) {
            if (this.dragon.isSleeping()) this.dragon.setHovering(false);
            // Slowly land the hovering dragon
            if (this.dragon.getControllingPassenger() == null && this.dragon.doesWantToLand() && !this.dragon.onGround() && !this.dragon.isInWater())
                this.dragon.setDeltaMovement(this.dragon.getDeltaMovement().add(0, -0.25, 0));
            else {
                if ((this.dragon.getControllingPassenger() == null || this.dragon.getControllingPassenger() instanceof DreadQueenEntity) && !this.dragon.isBeyondHeight()) {
                    double up = this.dragon.isInWater() ? 0.12D : 0.08D;
                    this.dragon.setDeltaMovement(this.dragon.getDeltaMovement().add(0, up, 0));
                }
                if (this.dragon.hoverTicks > 40) {
                    this.dragon.setHovering(false);
                    this.dragon.setFlying(true);
                    this.dragon.flyHovering = 0;
                    this.dragon.hoverTicks = 0;
                    this.dragon.flyTicks = 0;
                }
            }
        }
        if (this.dragon.isSleeping())
            this.dragon.getNavigation().stop();
        if ((this.dragon.onGround() || this.dragon.isInWater()) && this.dragon.flyTicks != 0)
            this.dragon.flyTicks = 0;
        if (this.dragon.isAllowedToTriggerFlight() && this.dragon.isFlying() && this.dragon.doesWantToLand()) {
            this.dragon.setFlying(false);
            this.dragon.setHovering(this.dragon.isOverAir());
            if (!this.dragon.isOverAir()) {
                this.dragon.flyTicks = 0;
                this.dragon.setFlying(false);
            }
        }
        if (this.dragon.isFlying())
            this.dragon.flyTicks++;
        if ((this.dragon.isHovering() || this.dragon.isFlying()) && this.dragon.isSleeping()) {
            this.dragon.setFlying(false);
            this.dragon.setHovering(false);
        }
        if (!this.dragon.isFlying() && !this.dragon.isHovering())
            if (this.dragon.isAllowedToTriggerFlight() || this.dragon.getY() < this.dragon.level().getMinBuildHeight())
                if (this.dragon.getRandom().nextInt(this.dragon.getFlightChancePerTick()) == 0 || this.dragon.getY() < this.dragon.level().getMinBuildHeight() || this.dragon.getTarget() != null && Math.abs(this.dragon.getTarget().getY() - this.dragon.getY()) > 5 || this.dragon.isInWater()) {
                    this.dragon.setHovering(true);
                    this.dragon.setInSittingPose(false);
                    this.dragon.setOrderedToSit(false);
                    this.dragon.flyHovering = 0;
                    this.dragon.hoverTicks = 0;
                    this.dragon.flyTicks = 0;
                }
        if (this.dragon.getTarget() != null)
            if (!DragonUtils.isAlive(this.dragon.getTarget())) {
                this.dragon.setTarget(null);
                this.ticksAfterClearingTarget = this.dragon.level().getGameTime();
            }
        if (!this.dragon.isAgingDisabled()) {
            this.dragon.setAgeInTicks(this.dragon.getAgeInTicks() + 1);
            if (this.dragon.getAgeInTicks() % 24000 == 0) {
                this.dragon.refreshDirtyAttributes();
                this.dragon.growDragon(0);
            }
        }
        if (IafCommonConfig.INSTANCE.dragon.hungerTickRate.getValue() > 0)
            if (this.dragon.tickCount % IafCommonConfig.INSTANCE.dragon.hungerTickRate.getValue() == 0)
                if (this.dragon.getHunger() > 0)
                    this.dragon.setHunger(this.dragon.getHunger() - 1);
        if ((this.dragon.groundAttack == IafDragonAttacks.Ground.FIRE) && this.dragon.getDragonStage() < 2) {
            this.dragon.usingGroundAttack = true;
            this.dragon.randomizeAttacks();
            this.dragon.playSound(this.dragon.getBabyFireSound(), 1, 1);
        }
        if (this.dragon.isBreathingFire()) {
            if (this.dragon.isSleeping() || this.dragon.isModelDead()) {
                this.dragon.setBreathingFire(false);
                this.dragon.randomizeAttacks();
                this.dragon.fireBreathTicks = 0;
            }
            if (this.dragon.burningTarget == null)
                if (this.dragon.getRemainingFireTicks() > this.dragon.getDragonStage() * 25 || this.dragon.getOwner() != null && this.dragon.getPassengers().contains(this.dragon.getOwner()) && this.dragon.fireStopTicks <= 0) {
                    this.dragon.setBreathingFire(false);
                    this.dragon.randomizeAttacks();
                    this.dragon.fireBreathTicks = 0;
                }

            if (this.dragon.fireStopTicks > 0 && this.dragon.getOwner() != null && this.dragon.getPassengers().contains(this.dragon.getOwner()))
                this.dragon.fireStopTicks--;
        }
        if (this.dragon.isFlying()) {
            if (this.dragon.getTarget() != null && this.dragon.getBoundingBox().expandTowards(3.0F, 3.0F, 3.0F).intersects(this.dragon.getTarget().getBoundingBox()))
                this.dragon.doHurtTarget(this.dragon.getTarget());
            if (this.dragon.airAttack == IafDragonAttacks.Air.TACKLE && (this.dragon.horizontalCollision || this.dragon.onGround())) {
                this.dragon.usingGroundAttack = true;
                if (this.dragon.getControllingPassenger() == null) {
                    this.dragon.setFlying(false);
                    this.dragon.setHovering(false);
                }
            }
            if (this.dragon.usingGroundAttack)
                this.dragon.airAttack = IafDragonAttacks.Air.TACKLE;
            if (this.dragon.airAttack == IafDragonAttacks.Air.TACKLE && this.dragon.getTarget() != null && this.dragon.isTargetBlocked(this.dragon.getTarget().position()))
                this.dragon.randomizeAttacks();
        }
    }

    public boolean attackTarget(Entity target, Player ridingPlayer, float damage) {
        if (ridingPlayer == null)
            return target.hurt(target.level().damageSources().mobAttack(this.dragon), damage);
        else
            return target.hurt(target.level().damageSources().indirectMagic(this.dragon, ridingPlayer), damage);
    }

    /*
    logic done exclusively on client.
    */
    public void updateDragonClient() {
        if (!this.dragon.isModelDead()) {
            this.dragon.turn_buffer.calculateChainSwingBuffer(50, 0, 4, this.dragon);
            this.dragon.tail_buffer.calculateChainSwingBuffer(90, 20, 5F, this.dragon);
            if (!this.dragon.onGround()) {
                this.dragon.roll_buffer.calculateChainFlapBuffer(55, 1, 2F, 0.5F, this.dragon);
                this.dragon.pitch_buffer.calculateChainWaveBuffer(90, 10, 1F, 0.5F, this.dragon);
                this.dragon.pitch_buffer_body.calculateChainWaveBuffer(80, 10, 1, 0.5F, this.dragon);
            }
        }
        if (this.dragon.walkCycle < 39) this.dragon.walkCycle++;
        else this.dragon.walkCycle = 0;

        if (this.dragon.getAnimation() == DragonBaseEntity.ANIMATION_WINGBLAST && (this.dragon.getAnimationTick() == 17 || this.dragon.getAnimationTick() == 22 || this.dragon.getAnimationTick() == 28))
            this.dragon.spawnGroundEffects();
        this.dragon.legSolver.update(this.dragon, this.dragon.getRenderSize() / 3F);

        if (this.dragon.flightCycle == 11)
            this.dragon.spawnGroundEffects();
        if (this.dragon.isModelDead() && this.dragon.flightCycle != 0)
            this.dragon.flightCycle = 0;
    }

    /*
    logic done on server and client on parallel.
    */
    public void updateDragonCommon() {
        if (this.dragon.fireBreathTicks > this.dragon.getAgeInDays() * IafCommonConfig.INSTANCE.dragon.maxBreathTimeMul.getValue()) {
            this.dragon.fireBreathTicks = 0;
            this.dragon.setBreathingFire(false);
        }
        if (this.dragon.isBreathingFire()) {
            this.dragon.fireBreathTicks++;
            if (this.dragon.burnProgress < 40)
                this.dragon.burnProgress++;
        } else this.dragon.burnProgress = 0;

        if (this.dragon.flightCycle == 2)
            if (!this.dragon.isDiving() && (this.dragon.isFlying() || this.dragon.isHovering())) {
                float dragonSoundVolume = IafCommonConfig.INSTANCE.dragon.flapNoiseDistance.getValue();
                float dragonSoundPitch = this.dragon.getVoicePitch();
                this.dragon.playSound(IafSounds.DRAGON_FLIGHT.get(), dragonSoundVolume, dragonSoundPitch);
            }

        if (this.dragon.flightCycle < 58) this.dragon.flightCycle += 2;
        else this.dragon.flightCycle = 0;

        final boolean flying = this.dragon.isFlying();
        if (flying) {
            if (this.dragon.flyProgress < 20.0F)
                this.dragon.flyProgress += 0.5F;
        } else {
            if (this.dragon.flyProgress > 0.0F)
                this.dragon.flyProgress -= 2F;
        }

        final boolean sleeping = this.dragon.isSleeping() && !this.dragon.isHovering() && !flying;
        if (sleeping) {
            if (this.dragon.sleepProgress < 20.0F)
                this.dragon.sleepProgress += 0.5F;
        } else {
            if (this.dragon.sleepProgress > 0.0F)
                this.dragon.sleepProgress -= 0.5F;
        }

        final boolean sitting = this.dragon.isOrderedToSit() && !this.dragon.isModelDead() && !sleeping;
        if (sitting) {
            if (this.dragon.sitProgress < 20.0F)
                this.dragon.sitProgress += 0.5F;
        } else {
            if (this.dragon.sitProgress > 0.0F)
                this.dragon.sitProgress -= 0.5F;
        }

        final boolean fireBreathing = this.dragon.isBreathingFire();
        this.dragon.prevFireBreathProgress = this.dragon.fireBreathProgress;
        if (fireBreathing) {
            if (this.dragon.fireBreathProgress < 10.0F)
                this.dragon.fireBreathProgress += 0.5F;
        } else {
            if (this.dragon.fireBreathProgress > 0.0F)
                this.dragon.fireBreathProgress -= 0.5F;
        }

        final boolean hovering = this.dragon.isHovering() || this.dragon.isFlying() && this.dragon.airAttack == IafDragonAttacks.Air.HOVER_BLAST && this.dragon.getTarget() != null && this.dragon.distanceTo(this.dragon.getTarget()) < 17F;
        if (hovering) {
            if (this.dragon.hoverProgress < 20.0F)
                this.dragon.hoverProgress += 0.5F;
        } else {
            if (this.dragon.hoverProgress > 0.0F)
                this.dragon.hoverProgress -= 2F;
        }

        final boolean diving = this.dragon.isDiving();
        if (diving) {
            if (this.dragon.diveProgress < 10.0F)
                this.dragon.diveProgress += 1F;
        } else {
            if (this.dragon.diveProgress > 0.0F)
                this.dragon.diveProgress -= 2F;
        }

        final boolean tackling = this.dragon.isTackling() && this.dragon.isOverAir();
        if (tackling) {
            if (this.dragon.tackleProgress < 5F)
                this.dragon.tackleProgress += 0.5F;
        } else {
            if (this.dragon.tackleProgress > 0.0F)
                this.dragon.tackleProgress -= 1.5F;
        }

        final boolean modelDead = this.dragon.isModelDead();
        if (modelDead) {
            if (this.dragon.modelDeadProgress < 20.0F)
                this.dragon.modelDeadProgress += 0.5F;
        } else {
            if (this.dragon.modelDeadProgress > 0.0F)
                this.dragon.modelDeadProgress -= 0.5F;
        }

        final boolean riding = this.dragon.isPassenger() && this.dragon.getVehicle() != null && this.dragon.getVehicle() instanceof Player;
        if (riding) {
            if (this.dragon.ridingProgress < 20.0F)
                this.dragon.ridingProgress += 0.5F;
        } else {
            if (this.dragon.ridingProgress > 0.0F)
                this.dragon.ridingProgress -= 0.5F;
        }

        if (this.dragon.hasHadHornUse)
            this.dragon.hasHadHornUse = false;

        if ((this.dragon.groundAttack == IafDragonAttacks.Ground.FIRE) && this.dragon.getDragonStage() < 2) {
            if (this.dragon.level().isClientSide)
                this.dragon.spawnBabyParticles();
            this.dragon.randomizeAttacks();
        }
    }


    /*
    logic handler for the dragon's melee attacks.
    */
    public void updateDragonAttack() {
        Player ridingPlayer = this.dragon.getRidingPlayer();
        if (this.dragon.isPlayingAttackAnimation() && this.dragon.getTarget() != null && this.dragon.hasLineOfSight(this.dragon.getTarget())) {
            LivingEntity target = this.dragon.getTarget();
            final double dist = this.dragon.distanceTo(target);
            if (dist < this.dragon.getRenderSize() * 0.2574 * 2 + 2) {
                if (this.dragon.getAnimation() == DragonBaseEntity.ANIMATION_BITE) {
                    if (this.dragon.getAnimationTick() > 15 && this.dragon.getAnimationTick() < 25) {
                        this.attackTarget(target, ridingPlayer, (int) this.dragon.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                        this.dragon.usingGroundAttack = this.dragon.getRandom().nextBoolean();
                        this.dragon.randomizeAttacks();
                    }
                } else if (this.dragon.getAnimation() == DragonBaseEntity.ANIMATION_TAILWHACK) {
                    if (this.dragon.getAnimationTick() > 20 && this.dragon.getAnimationTick() < 30) {
                        this.attackTarget(target, ridingPlayer, (int) this.dragon.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                        target.knockback(this.dragon.getDragonStage() * 0.6F, Mth.sin(this.dragon.getYRot() * 0.017453292F), -Mth.cos(this.dragon.getYRot() * 0.017453292F));
                        this.dragon.usingGroundAttack = this.dragon.getRandom().nextBoolean();
                        this.dragon.randomizeAttacks();
                    }
                } else if (this.dragon.getAnimation() == DragonBaseEntity.ANIMATION_WINGBLAST)
                    if ((this.dragon.getAnimationTick() == 15 || this.dragon.getAnimationTick() == 25 || this.dragon.getAnimationTick() == 35)) {
                        this.attackTarget(target, ridingPlayer, (int) this.dragon.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                        target.knockback(this.dragon.getDragonStage() * 0.6F, Mth.sin(this.dragon.getYRot() * 0.017453292F), -Mth.cos(this.dragon.getYRot() * 0.017453292F));
                        this.dragon.usingGroundAttack = this.dragon.getRandom().nextBoolean();
                        this.dragon.randomizeAttacks();
                    }
            }
        }
    }

    public void debug() {
        String side = this.dragon.level().isClientSide ? "CLIENT" : "SERVER";
        String owner = this.dragon.getOwner() == null ? "null" : this.dragon.getOwner().getName().getString();
        String attackTarget = this.dragon.getTarget() == null ? "null" : this.dragon.getTarget().getName().getString();
        IceAndFire.LOGGER.warn("DRAGON DEBUG[{}]:\nStage: {}\nAge: {}\nVariant: {}\nOwner: {}\nAttack Target: {}\nFlying: {}\nHovering: {}\nHovering Time: {}\nWidth: {}\nMoveHelper: {}\nGround Attack: {}\nAir Attack: {}\nTackling: {}", side, this.dragon.getDragonStage(), this.dragon.getAgeInDays(), this.dragon.getVariant(), owner, attackTarget, this.dragon.isFlying(), this.dragon.isHovering(), this.dragon.hoverTicks, this.dragon.getBbWidth(), this.dragon.getMoveControl(), this.dragon.groundAttack, this.dragon.airAttack, this.dragon.isTackling());
    }
}