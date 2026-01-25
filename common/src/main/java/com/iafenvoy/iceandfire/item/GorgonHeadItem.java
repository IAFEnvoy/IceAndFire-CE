package com.iafenvoy.iceandfire.item;

import com.google.common.base.Predicate;
import com.iafenvoy.iceandfire.entity.StoneStatueEntity;
import com.iafenvoy.iceandfire.entity.util.BlacklistedFromStatues;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.registry.IafDamageTypes;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class GorgonHeadItem extends Item {
    public GorgonHeadItem() {
        super(new Settings().maxDamage(1));
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World worldIn, LivingEntity entity, int timeLeft) {
        double dist = 32;
        Vec3d Vector3d = entity.getCameraPosVec(1.0F);
        Vec3d Vector3d1 = entity.getRotationVec(1.0F);
        Vec3d Vector3d2 = Vector3d.add(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist);
        Entity pointedEntity = null;
        List<Entity> list = worldIn.getOtherEntities(entity, entity.getBoundingBox().stretch(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist).expand(1.0D, 1.0D, 1.0D), (Predicate<Entity>) entity12 -> {
            if (entity12 instanceof LivingEntity livingEntity) {
                boolean isImmune = livingEntity instanceof BlacklistedFromStatues blacklisted && !blacklisted.canBeTurnedToStone() || entity12.getType().isIn(IafEntityTags.IMMUNE_TO_GORGON_STONE) || livingEntity.hasStatusEffect(StatusEffects.BLINDNESS);
                return !isImmune && entity12.canHit() && !livingEntity.isDead() && (entity12 instanceof PlayerEntity || DragonUtils.isAlive(livingEntity));
            }
            return false;
        });
        double d2 = dist;
        for (Entity entity1 : list) {
            Box axisalignedbb = entity1.getBoundingBox().expand(entity1.getTargetingMargin());
            Optional<Vec3d> optional = axisalignedbb.raycast(Vector3d, Vector3d2);

            if (axisalignedbb.contains(Vector3d)) {
                if (d2 >= 0.0D) d2 = 0.0D;
            } else if (optional.isPresent()) {
                double d3 = Vector3d.distanceTo(optional.get());
                if (d3 < d2 || d2 == 0.0D) {
                    if (entity1.getRootVehicle() == entity.getRootVehicle()) {
                        if (d2 == 0.0D) pointedEntity = entity1;
                    } else {
                        pointedEntity = entity1;
                        d2 = d3;
                    }
                }
            }
        }
        if (pointedEntity != null) {
            if (pointedEntity instanceof LivingEntity livingEntity) {
                boolean wasSuccesful = true;

                if (pointedEntity instanceof PlayerEntity)
                    wasSuccesful = pointedEntity.damage(IafDamageTypes.causeGorgonDamage(pointedEntity), Integer.MAX_VALUE);
                else {
                    if (!worldIn.isClient)
                        pointedEntity.remove(Entity.RemovalReason.KILLED);
                }

                if (wasSuccesful) {
                    pointedEntity.playSound(IafSounds.TURN_STONE.get(), 1, 1);
                    StoneStatueEntity statue = StoneStatueEntity.buildStatueEntity(livingEntity);
                    statue.updatePositionAndAngles(pointedEntity.getX(), pointedEntity.getY(), pointedEntity.getZ(), pointedEntity.getYaw(), pointedEntity.getPitch());
                    statue.bodyYaw = pointedEntity.getYaw();
                    if (!worldIn.isClient)
                        worldIn.spawnEntity(statue);
                }

                if (entity instanceof PlayerEntity player && !player.isCreative())
                    stack.decrement(1);
            }
        }
        stack.remove(IafDataComponents.ACTIVE.get());
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackIn = playerIn.getStackInHand(hand);
        playerIn.setCurrentHand(hand);
        itemStackIn.set(IafDataComponents.ACTIVE.get(), Unit.INSTANCE);
        return new TypedActionResult<>(ActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public void usageTick(World level, LivingEntity player, ItemStack stack, int count) {
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.iceandfire.legendary_weapon.desc").formatted(Formatting.GRAY));
    }
}
