package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.DragonSkullEntity;
import com.iafenvoy.iceandfire.item.component.DragonSkullComponent;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DragonSkullItem extends Item {
    private final DragonType dragonType;

    public DragonSkullItem(DragonType dragonType) {
        super(new Properties().stacksTo(1).component(IafDataComponents.DRAGON_SKULL.get(), new DragonSkullComponent(4, 75)));
        this.dragonType = dragonType;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        String s = "dragon." + this.dragonType.name();
        tooltip.add(Component.translatable(s).withStyle(ChatFormatting.GRAY));
        if (stack.has(IafDataComponents.DRAGON_SKULL.get()))
            tooltip.add(Component.translatable("dragon.stage").withStyle(ChatFormatting.GRAY).append(Component.literal(" " + stack.get(IafDataComponents.DRAGON_SKULL.get()).stage())));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        assert context.getPlayer() != null;
        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
        /*
         * DragonEggEntity egg = new DragonEggEntity(worldIn);
         * egg.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() +
         * 0.5); if(!worldIn.isRemote){ worldIn.spawnEntityInWorld(egg); }
         */
        if (stack.has(IafDataComponents.DRAGON_SKULL.get())) {
            DragonSkullComponent component = stack.get(IafDataComponents.DRAGON_SKULL.get());
            DragonSkullEntity skull = new DragonSkullEntity(IafEntities.DRAGON_SKULL.get(), context.getLevel());
            skull.setDragonType(this.dragonType.name());
            skull.setStage(component.stage());
            skull.setDragonAge(component.dragonAge());
            BlockPos offset = context.getClickedPos().relative(context.getClickedFace(), 1);
            skull.moveTo(offset.getX() + 0.5, offset.getY(), offset.getZ() + 0.5, 0, 0);
            float yaw = context.getPlayer().getYRot();
            if (context.getClickedFace() != Direction.UP)
                yaw = context.getPlayer().getDirection().toYRot();
            skull.setYRot(yaw);
            if (stack.has(DataComponents.CUSTOM_NAME))
                skull.setCustomName(stack.getHoverName());
            if (!context.getLevel().isClientSide)
                context.getLevel().addFreshEntity(skull);
            if (!context.getPlayer().isCreative())
                stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
