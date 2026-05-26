package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.world.DragonPosWorldData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class SummoningCrystalItem extends Item {
    public SummoningCrystalItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        //Data Fix
        if (stack.has(IafDataComponents.NBT_COMPOUND.get())) {
            stack.set(IafDataComponents.CRYSTAL_DRAGON_DATA.get(), stack.get(IafDataComponents.NBT_COMPOUND.get()));
            stack.remove(IafDataComponents.NBT_COMPOUND.get());
        }
    }

    public static boolean hasDragon(ItemStack stack) {
        CompoundTag nbt = stack.get(IafDataComponents.CRYSTAL_DRAGON_DATA.get());
        if (stack.getItem() instanceof SummoningCrystalItem && nbt != null)
            for (String tagInfo : nbt.getAllKeys())
                if (tagInfo.contains("Dragon"))
                    return true;
        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        boolean flag = false;
        String desc = "entity.iceandfire.fire_dragon";
        if (stack.getItem() == IafItems.SUMMONING_CRYSTAL_ICE.get()) desc = "entity.iceandfire.ice_dragon";
        if (stack.getItem() == IafItems.SUMMONING_CRYSTAL_LIGHTNING.get()) desc = "entity.iceandfire.lightning_dragon";
        CompoundTag nbt = stack.get(IafDataComponents.CRYSTAL_DRAGON_DATA.get());
        if (nbt != null)
            for (String tagInfo : nbt.getAllKeys())
                if (tagInfo.contains("Dragon")) {
                    CompoundTag dragonTag = nbt.getCompound(tagInfo);
                    String dragonName = I18n.get(desc);
                    if (!dragonTag.getString("CustomName").isEmpty())
                        dragonName = dragonTag.getString("CustomName");
                    tooltip.add(Component.translatable("item.iceandfire.summoning_crystal.bound", dragonName).withStyle(ChatFormatting.GRAY));
                    flag = true;
                }
        if (!flag) {
            tooltip.add(Component.translatable("item.iceandfire.summoning_crystal.desc_0").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("item.iceandfire.summoning_crystal.desc_1").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        assert context.getPlayer() != null;
        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
        boolean flag = false;
        BlockPos offsetPos = context.getClickedPos().relative(context.getClickedFace());
        float yaw = context.getPlayer().getYRot();
        boolean displayError = false;
        CompoundTag nbt = stack.get(IafDataComponents.CRYSTAL_DRAGON_DATA.get());
        if (nbt != null && stack.getItem() == this && hasDragon(stack)) {
            for (String tagInfo : nbt.getAllKeys()) {
                if (tagInfo.contains("Dragon")) {
                    CompoundTag dragonTag = nbt.getCompound(tagInfo);
                    UUID id = dragonTag.getUUID("DragonUUID");
                    if (id != null && !context.getLevel().isClientSide) {
                        try {
                            Entity entity = context.getLevel().getServer().getLevel(context.getPlayer().level().dimension()).getEntity(id);
                            if (entity != null) {
                                flag = true;
                                this.summonEntity(entity, context.getLevel(), offsetPos, yaw);
                            }
                        } catch (Exception e) {
                            IceAndFire.LOGGER.error(e);
                            displayError = true;
                        }
                        DragonPosWorldData data = DragonPosWorldData.get(context.getLevel());
                        BlockPos dragonChunkPos = null;
                        if (data != null)
                            dragonChunkPos = data.getDragonPos(id);
                        if (IafCommonConfig.INSTANCE.dragon.chunkLoadSummonCrystal.getValue()) {
                            try {
                                if (!flag && data != null && context.getLevel().isClientSide) {//server side but couldn't find dragon
                                    ServerLevel serverWorld = (ServerLevel) context.getLevel();
                                    ChunkPos pos = new ChunkPos(dragonChunkPos);
                                    serverWorld.setChunkForced(pos.x, pos.z, true);
                                }
                            } catch (Exception e) {
                                IceAndFire.LOGGER.warn("Could not load chunk when summoning dragon", e);
                            }
                        }
                    }
                }
            }
            if (flag) {
                context.getPlayer().playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);
                context.getPlayer().playSound(SoundEvents.GLASS_BREAK, 1, 1);
                context.getPlayer().swing(context.getHand());
                context.getPlayer().displayClientMessage(Component.translatable("message.iceandfire.dragonTeleport"), true);
                stack.remove(IafDataComponents.CRYSTAL_DRAGON_DATA.get());
            } else if (displayError)
                context.getPlayer().displayClientMessage(Component.translatable("message.iceandfire.noDragonTeleport"), true);
        }
        return InteractionResult.PASS;
    }

    public void summonEntity(Entity entity, Level worldIn, BlockPos offsetPos, float yaw) {
        entity.moveTo(offsetPos.getX() + 0.5D, offsetPos.getY() + 0.5D, offsetPos.getZ() + 0.5D, yaw, 0);
        if (entity instanceof DragonBaseEntity dragon)
            dragon.setCrystalBound(false);
        if (IafCommonConfig.INSTANCE.dragon.chunkLoadSummonCrystal.getValue()) {
            DragonPosWorldData data = DragonPosWorldData.get(worldIn);
            if (data != null)
                data.removeDragon(entity.getUUID());
        }
    }
}
