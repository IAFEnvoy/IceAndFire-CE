package com.iafenvoy.iceandfire.compat.jade;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.entity.EntityMultipartPart;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.impl.ui.ArmorElement;
import snownee.jade.impl.ui.HealthElement;

public enum MultipartComponentProvider implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public Identifier getUid() {
        return Identifier.of(IceAndFire.MOD_ID, "multipart");
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        if (entityAccessor.getEntity() instanceof EntityMultipartPart multipart) {
            assert MinecraftClient.getInstance().world != null;
            Entity parent =  MinecraftClient.getInstance().world.entityManager.getLookup().get(multipart.getParentId());
            if (parent instanceof MobEntity mob) {
                iTooltip.clear();
                iTooltip.addAll(mob.getDisplayName().getWithStyle(Style.EMPTY.withColor(Formatting.WHITE)));
                iTooltip.add(new HealthElement(mob.getMaxHealth(), mob.getHealth()));
                iTooltip.add(new ArmorElement(mob.getArmor()));
                if (mob instanceof EntityDragonBase dragon) {
                    iTooltip.add(Text.translatable("dragon.stage").formatted(Formatting.GRAY).append(Text.literal(" " + dragon.getDragonStage())));
                    iTooltip.add(Text.literal(dragon.getAgeInDays() + "d"));
                    iTooltip.add(Text.literal(dragon.isMale() ? "Male" : "Female"));
                }
            }
        }
    }
}
