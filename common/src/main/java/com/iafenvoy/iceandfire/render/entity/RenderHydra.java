package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityHydra;
import com.iafenvoy.iceandfire.render.entity.layer.LayerGenericGlowing;
import com.iafenvoy.iceandfire.render.entity.layer.LayerHydraHead;
import com.iafenvoy.iceandfire.render.model.ModelHydraBody;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RenderHydra extends MobEntityRenderer<EntityHydra, ModelHydraBody> {
    public static final Identifier TEXUTURE_0 = Identifier.of(IceAndFire.MOD_ID, "textures/entity/hydra/hydra_0.png");
    public static final Identifier TEXUTURE_1 = Identifier.of(IceAndFire.MOD_ID, "textures/entity/hydra/hydra_1.png");
    public static final Identifier TEXUTURE_2 = Identifier.of(IceAndFire.MOD_ID, "textures/entity/hydra/hydra_2.png");
    public static final Identifier TEXUTURE_EYES = Identifier.of(IceAndFire.MOD_ID, "textures/entity/hydra/hydra_eyes.png");

    public RenderHydra(EntityRendererFactory.Context context) {
        super(context, new ModelHydraBody(), 1.2F);
        this.addFeature(new LayerHydraHead(this));
        this.addFeature(new LayerGenericGlowing<>(this, TEXUTURE_EYES));
    }

    @Override
    public void scale(EntityHydra LivingEntityIn, MatrixStack stack, float partialTickTime) {
        stack.scale(1.75F, 1.75F, 1.75F);
    }

    @Override
    public Identifier getTexture(EntityHydra gorgon) {
        return switch (gorgon.getVariant()) {
            case 1 -> TEXUTURE_1;
            case 2 -> TEXUTURE_2;
            default -> TEXUTURE_0;
        };
    }
}
