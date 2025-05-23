package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityMyrmexEgg;
import com.iafenvoy.iceandfire.render.model.ModelDragonEgg;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;

public class RenderMyrmexEgg extends LivingEntityRenderer<EntityMyrmexEgg, AdvancedEntityModel<EntityMyrmexEgg>> {
    public static final Identifier EGG_JUNGLE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/myrmex/myrmex_jungle_egg.png");
    public static final Identifier EGG_DESERT = Identifier.of(IceAndFire.MOD_ID, "textures/entity/myrmex/myrmex_desert_egg.png");

    public RenderMyrmexEgg(EntityRendererFactory.Context context) {
        super(context, new ModelDragonEgg<>(), 0.3F);
    }

    @Override
    protected boolean hasLabel(EntityMyrmexEgg entity) {
        return entity.shouldRenderName() && entity.hasCustomName();
    }

    @Override
    public Identifier getTexture(EntityMyrmexEgg entity) {
        return entity.isJungle() ? EGG_JUNGLE : EGG_DESERT;
    }
}
