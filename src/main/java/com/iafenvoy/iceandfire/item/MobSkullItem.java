package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.IafSkullType;
import com.iafenvoy.iceandfire.entity.MobSkullEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class MobSkullItem extends Item {

    private final IafSkullType skull;

    public MobSkullItem(IafSkullType skull) {
        super(new Properties().stacksTo(1));
        this.skull = skull;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        MobSkullEntity skull = new MobSkullEntity(IafEntities.MOB_SKULL.get(), context.getLevel());
        assert player != null;
        ItemStack stack = player.getItemInHand(context.getHand());
        BlockPos offset = context.getClickedPos().relative(context.getClickedFace(), 1);
        skull.moveTo(offset.getX() + 0.5, offset.getY(), offset.getZ() + 0.5, 0, 0);
        float yaw = player.getYRot();
        if (context.getClickedFace() != Direction.UP)
            yaw = player.getDirection().toYRot();
        skull.setYRot(yaw);
        skull.setSkullType(this.skull);
        if (!context.getLevel().isClientSide)
            context.getLevel().addFreshEntity(skull);
        if (stack.has(DataComponents.CUSTOM_NAME))
            skull.setCustomName(stack.getHoverName());
        if (!player.isCreative())
            stack.shrink(1);
        return InteractionResult.SUCCESS;
    }
}
