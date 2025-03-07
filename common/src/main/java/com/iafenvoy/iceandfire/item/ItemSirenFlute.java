package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.component.IafEntityData;
import com.iafenvoy.iceandfire.entity.util.IBlacklistedFromStatues;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.registry.IafSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class ItemSirenFlute extends Item {
    public ItemSirenFlute() {
        super(new Settings().maxDamage(200));
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity player, Hand hand) {
        ItemStack itemStackIn = player.getStackInHand(hand);
        player.setCurrentHand(hand);
        player.getItemCooldownManager().set(this, 900);

        double dist = 32;
        Vec3d Vector3d = player.getCameraPosVec(1.0F);
        Vec3d Vector3d1 = player.getRotationVec(1.0F);
        Vec3d Vector3d2 = Vector3d.add(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist);

        Entity pointedEntity = null;
        List<Entity> list = player.getWorld().getOtherEntities(player, player.getBoundingBox().stretch(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist).expand(1.0D, 1.0D, 1.0D), entity -> {
            boolean blindness = entity instanceof LivingEntity living && living.hasStatusEffect(StatusEffects.BLINDNESS) || entity instanceof IBlacklistedFromStatues blacklisted && !blacklisted.canBeTurnedToStone();
            return entity != null && entity.canHit() && !blindness && (entity instanceof PlayerEntity || entity instanceof LivingEntity living && DragonUtils.isAlive(living));
        });

        double d2 = dist;
        for (Entity entity1 : list) {
            Box axisalignedbb = entity1.getBoundingBox().expand(entity1.getTargetingMargin());
            Optional<Vec3d> raytraceresult = axisalignedbb.raycast(Vector3d, Vector3d2);

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
            IafEntityData data = IafEntityData.get(livingEntity);
            data.miscData.setLoveTicks(10 * 20);
            itemStackIn.damage(2, player, EquipmentSlot.MAINHAND);
            player.getItemCooldownManager().set(itemStackIn.getItem(), 45 * 20);
        }

        player.playSound(IafSounds.SIREN_SONG.get(), 1, 1);
        return new TypedActionResult<>(ActionResult.PASS, itemStackIn);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.iceandfire.legendary_weapon.desc").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.siren_flute.desc_0").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.siren_flute.desc_1").formatted(Formatting.GRAY));
    }
}
