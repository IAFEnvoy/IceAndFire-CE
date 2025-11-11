package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.item.component.DragonHornComponent;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DragonHornItem extends Item {
    public DragonHornItem() {
        super(new Settings().maxCount(1));
    }

    public static int getDragonType(ItemStack stack) {
        if (stack.contains(IafDataComponents.DRAGON_HORN.get())) {
            Optional<EntityType<?>> optional = Registries.ENTITY_TYPE.getOrEmpty(stack.get(IafDataComponents.DRAGON_HORN.get()).entityType());
            if (optional.isPresent()) {
                EntityType<?> entityType = optional.get();
                if (entityType == IafEntities.FIRE_DRAGON.get()) return 1;
                if (entityType == IafEntities.ICE_DRAGON.get()) return 2;
                if (entityType == IafEntities.LIGHTNING_DRAGON.get()) return 3;
            }
        }
        return 0;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        stack = player.getStackInHand(hand);
        if (stack.isOf(IafItems.DRAGON_HORN.get()) && !stack.contains(IafDataComponents.DRAGON_HORN.get())) {
            if (!player.getWorld().isClient && (Entity) target instanceof DragonBaseEntity dragon && dragon.isOwner(player)) {
                NbtCompound entityTag = new NbtCompound();
                target.saveNbt(entityTag);
                stack.set(IafDataComponents.DRAGON_HORN.get(), new DragonHornComponent(Registries.ENTITY_TYPE.getId(target.getType()), target.getUuid(), entityTag));
                player.swingHand(hand);
                player.getWorld().playSound(player, player.getBlockPos(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 3.0F, 0.75F);
                target.remove(Entity.RemovalReason.DISCARDED);
                return ActionResult.SUCCESS;
            }
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getSide() != Direction.UP) return ActionResult.PASS;
        ItemStack stack = context.getStack();
        if (stack.contains(IafDataComponents.DRAGON_HORN.get())) {
            DragonHornComponent component = stack.get(IafDataComponents.DRAGON_HORN.get());
            World world = context.getWorld();
            EntityType<?> type = Registries.ENTITY_TYPE.getOrEmpty(component.entityType()).orElse(null);
            if (type != null) {
                Entity entity = type.create(world);
                if (entity instanceof DragonBaseEntity dragon)
                    dragon.readNbt(component.entityData());
                //Still needed to allow for intercompatibility
                UUID uuid = component.entityUuid();
                if (uuid != null) {
                    assert entity != null;
                    entity.setUuid(uuid);
                }

                assert entity != null;
                entity.updatePositionAndAngles(context.getBlockPos().getX() + 0.5D, context.getBlockPos().getY() + 1, context.getBlockPos().getZ() + 0.5D, 180 + (context.getHorizontalPlayerFacing()).asRotation(), 0.0F);
                if (world.spawnEntity(entity))
                    stack.remove(IafDataComponents.DRAGON_HORN.get());
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (stack.contains(IafDataComponents.DRAGON_HORN.get())) {
            DragonHornComponent component = stack.get(IafDataComponents.DRAGON_HORN.get());
            NbtCompound entityTag = component.entityData();
            if (!entityTag.isEmpty()) {
                Optional<EntityType<?>> optional = Registries.ENTITY_TYPE.getOrEmpty(component.entityType());
                if (optional.isPresent()) {
                    EntityType<?> entityType = optional.get();
                    tooltip.add((Text.translatable(entityType.getTranslationKey())).formatted(this.getTextColorForEntityType(entityType)));
                    String name = Text.translatable("dragon.unnamed").getString();
                    if (!entityTag.getString("CustomName").isEmpty())
                        name = entityTag.getString("CustomName");

                    tooltip.add((Text.literal(name)).formatted(Formatting.GRAY));
                    String gender = (Text.translatable("dragon.gender")).getString() + " " + (Text.translatable(entityTag.getBoolean("Gender") ? "dragon.gender.male" : "dragon.gender.female")).getString();
                    tooltip.add((Text.literal(gender)).formatted(Formatting.GRAY));
                    int stagenumber = entityTag.getInt("AgeTicks") / 24000;
                    int stage1;
                    if (stagenumber >= 100) stage1 = 5;
                    else if (stagenumber >= 75) stage1 = 4;
                    else if (stagenumber >= 50) stage1 = 3;
                    else if (stagenumber >= 25) stage1 = 2;
                    else stage1 = 1;
                    tooltip.add(Text.translatable("dragon.stage").append(Text.literal(" " + stage1 + " ")).append(Text.translatable("dragon.days.front")).append(Text.literal(stagenumber + " ")).append(Text.translatable("dragon.days.back")).formatted(Formatting.GRAY));
                }
            }
        }
    }

    private Formatting getTextColorForEntityType(EntityType<?> type) {
        if (type == IafEntities.FIRE_DRAGON.get()) return Formatting.DARK_RED;
        if (type == IafEntities.ICE_DRAGON.get()) return Formatting.BLUE;
        if (type == IafEntities.LIGHTNING_DRAGON.get()) return Formatting.DARK_PURPLE;
        return Formatting.GRAY;
    }
}
