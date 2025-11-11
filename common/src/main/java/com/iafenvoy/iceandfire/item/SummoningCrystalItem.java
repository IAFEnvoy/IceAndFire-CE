package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.world.DragonPosWorldData;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class SummoningCrystalItem extends Item {
    public SummoningCrystalItem() {
        super(new Settings().maxCount(1));
    }

    public static boolean hasDragon(ItemStack stack) {
        NbtCompound nbt = stack.get(IafDataComponents.CRYSTAL_DRAGON_DATA.get());
        if (stack.getItem() instanceof SummoningCrystalItem && nbt != null)
            for (String tagInfo : nbt.getKeys())
                if (tagInfo.contains("Dragon"))
                    return true;
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        boolean flag = false;
        String desc = "entity.iceandfire.fire_dragon";
        if (stack.getItem() == IafItems.SUMMONING_CRYSTAL_ICE.get()) desc = "entity.iceandfire.ice_dragon";
        if (stack.getItem() == IafItems.SUMMONING_CRYSTAL_LIGHTNING.get()) desc = "entity.iceandfire.lightning_dragon";
        NbtCompound nbt = stack.get(IafDataComponents.CRYSTAL_DRAGON_DATA.get());
        if (nbt != null)
            for (String tagInfo : nbt.getKeys())
                if (tagInfo.contains("Dragon")) {
                    NbtCompound dragonTag = nbt.getCompound(tagInfo);
                    String dragonName = I18n.translate(desc);
                    if (!dragonTag.getString("CustomName").isEmpty())
                        dragonName = dragonTag.getString("CustomName");
                    tooltip.add(Text.translatable("item.iceandfire.summoning_crystal.bound", dragonName).formatted(Formatting.GRAY));
                    flag = true;
                }
        if (!flag) {
            tooltip.add(Text.translatable("item.iceandfire.summoning_crystal.desc_0").formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("item.iceandfire.summoning_crystal.desc_1").formatted(Formatting.GRAY));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        assert context.getPlayer() != null;
        ItemStack stack = context.getPlayer().getStackInHand(context.getHand());
        boolean flag = false;
        BlockPos offsetPos = context.getBlockPos().offset(context.getSide());
        float yaw = context.getPlayer().getYaw();
        boolean displayError = false;
        NbtCompound nbt = stack.get(IafDataComponents.CRYSTAL_DRAGON_DATA.get());
        if (nbt != null && stack.getItem() == this && hasDragon(stack)) {
            for (String tagInfo : nbt.getKeys()) {
                if (tagInfo.contains("Dragon")) {
                    NbtCompound dragonTag = nbt.getCompound(tagInfo);
                    UUID id = dragonTag.getUuid("DragonUUID");
                    if (id != null && !context.getWorld().isClient) {
                        try {
                            Entity entity = context.getWorld().getServer().getWorld(context.getPlayer().getWorld().getRegistryKey()).getEntity(id);
                            if (entity != null) {
                                flag = true;
                                this.summonEntity(entity, context.getWorld(), offsetPos, yaw);
                            }
                        } catch (Exception e) {
                            IceAndFire.LOGGER.error(e);
                            displayError = true;
                        }
                        DragonPosWorldData data = DragonPosWorldData.get(context.getWorld());
                        BlockPos dragonChunkPos = null;
                        if (data != null)
                            dragonChunkPos = data.getDragonPos(id);
                        if (IafCommonConfig.INSTANCE.dragon.chunkLoadSummonCrystal.getValue()) {
                            try {
                                if (!flag && data != null && context.getWorld().isClient) {//server side but couldn't find dragon
                                    ServerWorld serverWorld = (ServerWorld) context.getWorld();
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
                context.getPlayer().playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                context.getPlayer().playSound(SoundEvents.BLOCK_GLASS_BREAK, 1, 1);
                context.getPlayer().swingHand(context.getHand());
                context.getPlayer().sendMessage(Text.translatable("message.iceandfire.dragonTeleport"), true);
                stack.remove(IafDataComponents.CRYSTAL_DRAGON_DATA.get());
            } else if (displayError)
                context.getPlayer().sendMessage(Text.translatable("message.iceandfire.noDragonTeleport"), true);
        }
        return ActionResult.PASS;
    }

    public void summonEntity(Entity entity, World worldIn, BlockPos offsetPos, float yaw) {
        entity.refreshPositionAndAngles(offsetPos.getX() + 0.5D, offsetPos.getY() + 0.5D, offsetPos.getZ() + 0.5D, yaw, 0);
        if (entity instanceof DragonBaseEntity dragon)
            dragon.setCrystalBound(false);
        if (IafCommonConfig.INSTANCE.dragon.chunkLoadSummonCrystal.getValue()) {
            DragonPosWorldData data = DragonPosWorldData.get(worldIn);
            if (data != null)
                data.removeDragon(entity.getUuid());
        }
    }
}
