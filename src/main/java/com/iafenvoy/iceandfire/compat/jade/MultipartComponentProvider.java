package com.iafenvoy.iceandfire.compat.jade;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.MultipartPartEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.impl.ui.ArmorElement;
import snownee.jade.impl.ui.HealthElement;

public enum MultipartComponentProvider implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "multipart");
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        if (entityAccessor.getEntity() instanceof MultipartPartEntity multipart) {
            assert Minecraft.getInstance().level != null;
            Entity parent = Minecraft.getInstance().level.entityStorage.getEntityGetter().get(multipart.getParentId());
            if (parent instanceof Mob mob) {
                iTooltip.clear();
                iTooltip.addAll(mob.getDisplayName().toFlatList(Style.EMPTY.withColor(ChatFormatting.WHITE)));
                iTooltip.add(new HealthElement(mob.getMaxHealth(), mob.getHealth()));
                iTooltip.add(new ArmorElement(mob.getArmorValue()));
                if (mob instanceof DragonBaseEntity dragon) {
                    iTooltip.add(Component.translatable("dragon.stage").withStyle(ChatFormatting.GRAY).append(Component.literal(" " + dragon.getDragonStage())));
                    iTooltip.add(Component.literal(dragon.getAgeInDays() + "d"));
                    iTooltip.add(Component.literal(dragon.isMale() ? "Male" : "Female"));
                }
            }
        }
    }
}
