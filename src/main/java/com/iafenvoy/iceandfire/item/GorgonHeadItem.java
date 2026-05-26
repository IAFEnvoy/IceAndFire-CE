package com.iafenvoy.iceandfire.item;

import com.google.common.base.Predicate;
import com.iafenvoy.iceandfire.entity.StoneStatueEntity;
import com.iafenvoy.iceandfire.entity.util.BlacklistedFromStatues;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.registry.IafDamageTypes;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class GorgonHeadItem extends Item {
    public GorgonHeadItem() {
        super(new Properties().durability(1));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity user) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, Level worldIn, LivingEntity entity, int timeLeft) {
        double dist = 32;
        Vec3 Vector3d = entity.getEyePosition(1.0F);
        Vec3 Vector3d1 = entity.getViewVector(1.0F);
        Vec3 Vector3d2 = Vector3d.add(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist);
        Entity pointedEntity = null;
        List<Entity> list = worldIn.getEntities(entity, entity.getBoundingBox().expandTowards(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist).inflate(1.0D, 1.0D, 1.0D), (Predicate<Entity>) entity12 -> {
            if (entity12 instanceof LivingEntity livingEntity) {
                boolean isImmune = livingEntity instanceof BlacklistedFromStatues blacklisted && !blacklisted.canBeTurnedToStone() || entity12.getType().is(IafEntityTags.IMMUNE_TO_GORGON_STONE) || livingEntity.hasEffect(MobEffects.BLINDNESS);
                return !isImmune && entity12.isPickable() && !livingEntity.isDeadOrDying() && (entity12 instanceof Player || DragonUtils.isAlive(livingEntity));
            }
            return false;
        });
        double d2 = dist;
        for (Entity entity1 : list) {
            AABB axisalignedbb = entity1.getBoundingBox().inflate(entity1.getPickRadius());
            Optional<Vec3> optional = axisalignedbb.clip(Vector3d, Vector3d2);

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

                if (pointedEntity instanceof Player)
                    wasSuccesful = pointedEntity.hurt(IafDamageTypes.causeGorgonDamage(pointedEntity), Integer.MAX_VALUE);
                else {
                    if (!worldIn.isClientSide)
                        pointedEntity.remove(Entity.RemovalReason.KILLED);
                }

                if (wasSuccesful) {
                    pointedEntity.playSound(IafSounds.TURN_STONE.get(), 1, 1);
                    StoneStatueEntity statue = StoneStatueEntity.buildStatueEntity(livingEntity);
                    statue.absMoveTo(pointedEntity.getX(), pointedEntity.getY(), pointedEntity.getZ(), pointedEntity.getYRot(), pointedEntity.getXRot());
                    statue.yBodyRot = pointedEntity.getYRot();
                    if (!worldIn.isClientSide)
                        worldIn.addFreshEntity(statue);
                }

                if (entity instanceof Player player && !player.isCreative())
                    stack.shrink(1);
            }
        }
        stack.remove(IafDataComponents.ACTIVE.get());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand hand) {
        ItemStack itemStackIn = playerIn.getItemInHand(hand);
        playerIn.startUsingItem(hand);
        itemStackIn.set(IafDataComponents.ACTIVE.get(), Unit.INSTANCE);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStackIn);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity player, @NotNull ItemStack stack, int count) {
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
    }
}
