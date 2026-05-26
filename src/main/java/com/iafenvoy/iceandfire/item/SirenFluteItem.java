package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.component.MiscData;
import com.iafenvoy.iceandfire.entity.util.BlacklistedFromStatues;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.registry.IafSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class SirenFluteItem extends Item {
    public SirenFluteItem() {
        super(new Properties().durability(200));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStackIn = player.getItemInHand(hand);
        player.startUsingItem(hand);
        player.getCooldowns().addCooldown(this, 900);

        double dist = 32;
        Vec3 Vector3d = player.getEyePosition(1.0F);
        Vec3 Vector3d1 = player.getViewVector(1.0F);
        Vec3 Vector3d2 = Vector3d.add(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist);

        Entity pointedEntity = null;
        List<Entity> list = player.level().getEntities(player, player.getBoundingBox().expandTowards(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist).inflate(1.0D, 1.0D, 1.0D), entity -> {
            boolean blindness = entity instanceof LivingEntity living && living.hasEffect(MobEffects.BLINDNESS) || entity instanceof BlacklistedFromStatues blacklisted && !blacklisted.canBeTurnedToStone();
            return entity != null && entity.isPickable() && !blindness && (entity instanceof Player || entity instanceof LivingEntity living && DragonUtils.isAlive(living));
        });

        double d2 = dist;
        for (Entity entity1 : list) {
            AABB axisalignedbb = entity1.getBoundingBox().inflate(entity1.getPickRadius());
            Optional<Vec3> raytraceresult = axisalignedbb.clip(Vector3d, Vector3d2);

            if (axisalignedbb.contains(Vector3d)) {
                if (d2 >= 0.0D) {
                    pointedEntity = entity1;
                    d2 = 0.0D;
                }
            } else if (raytraceresult.isPresent()) {
                double d3 = Vector3d.distanceTo(raytraceresult.get());
                if (d3 < d2 || d2 == 0.0D)
                    if (entity1.getRootVehicle() == player.getRootVehicle()) {
                        if (d2 == 0.0D) pointedEntity = entity1;
                    } else {
                        pointedEntity = entity1;
                        d2 = d3;
                    }
            }
        }

        if (pointedEntity instanceof LivingEntity livingEntity) {
            MiscData.get(livingEntity).setLoveTicks(10 * 20);
            itemStackIn.hurtAndBreak(2, player, EquipmentSlot.MAINHAND);
            player.getCooldowns().addCooldown(itemStackIn.getItem(), 45 * 20);
        }

        player.playSound(IafSounds.SIREN_SONG.get(), 1, 1);
        return new InteractionResultHolder<>(InteractionResult.PASS, itemStackIn);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.iceandfire.siren_flute.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.iceandfire.siren_flute.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
