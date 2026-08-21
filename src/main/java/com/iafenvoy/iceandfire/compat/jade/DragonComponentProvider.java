package com.iafenvoy.iceandfire.compat.jade;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum DragonComponentProvider implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public @NonNull Identifier getUid() {
        return Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "dragon");
    }

    @Override
    public void appendTooltip(@NonNull ITooltip iTooltip, EntityAccessor entityAccessor, @NonNull IPluginConfig iPluginConfig) {
        if (entityAccessor.getEntity() instanceof DragonBaseEntity dragon) {
            iTooltip.add(Component.translatable("dragon.stage").withStyle(ChatFormatting.GRAY).append(Component.literal(" " + dragon.getDragonStage())));
            iTooltip.add(Component.literal(dragon.getAgeInDays() + "d"));
            iTooltip.add(Component.literal(dragon.isMale() ? "Male" : "Female"));
        }
    }
}
