package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class ItemDragonSeeker extends Item {
    private final SeekerType type;

    public ItemDragonSeeker(SeekerType type) {
        super(new Settings().maxCount(1).rarity(Rarity.RARE));
        this.type = type;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return super.use(world, user, hand);
        if (!IafCommonConfig.INSTANCE.misc.enableDragonSeeker.getValue()) {
            user.sendMessage(Text.translatable("text.iceandfire.not_enable"), false);
            return super.use(world, user, hand);
        }
        ItemStack stack = user.getStackInHand(hand);
        EntityDragonBase dragon = world.getClosestEntity(EntityDragonBase.class, TargetPredicate.createAttackable().setPredicate(entity -> {
            if (!(entity instanceof EntityDragonBase d)) return false;
            if (d.isMobDead() && !this.type.trackDead) return false;
            return !d.isTamed() || this.type.trackTeamed;
        }), user, user.getX(), user.getY(), user.getZ(), new Box(this.type.add(user.getPos(), true), this.type.add(user.getPos(), false)));
        if (dragon == null) {
            user.sendMessage(Text.translatable("item.iceandfire.dragon_seeker.not_found"));
            return TypedActionResult.fail(stack);
        }
        if (this.type.admin) {
            String pos1 = String.format(Locale.ROOT, "[%d, %d, %d]", (int) dragon.getX(), (int) dragon.getY(), (int) dragon.getZ()), pos2 = String.format("/tp @s %d %d %d", (int) dragon.getX(), (int) dragon.getY(), (int) dragon.getZ());
            Text locationText = Text.literal(pos1).setStyle(Style.EMPTY.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, pos2)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.coordinates.tooltip"))));
            user.sendMessage(Text.translatable("item.iceandfire.dragon_seeker.found_location").append(locationText));
        } else
            user.sendMessage(Text.translatable("item.iceandfire.dragon_seeker.found"));
        return TypedActionResult.success(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        String name = Registries.ITEM.getId(this).getPath();
        tooltip.add(Text.translatable("item.iceandfire." + name + ".tooltip"));
        tooltip.add(Text.translatable("item.iceandfire.dragon_seeker.credit").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(true)));
    }

    public enum SeekerType {
        NORMAL(150, true, true, false),
        EPIC(200, false, true, false),
        LEGENDARY(300, false, false, false),
        GODLY(500, false, false, true);
        private final int trackRange;
        private final boolean trackDead;
        private final boolean trackTeamed;
        private final boolean admin;

        SeekerType(int trackRange, boolean trackDead, boolean trackTeamed, boolean admin) {
            this.trackRange = trackRange;
            this.trackDead = trackDead;
            this.trackTeamed = trackTeamed;
            this.admin = admin;
        }

        public Vec3d add(Vec3d origin, boolean reverse) {
            int range = this.trackRange;
            if (reverse) range *= -1;
            return origin.add(range, range, range);
        }
    }
}
