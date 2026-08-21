package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.SeaSerpentEntity;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.iceandfire.registry.IafRenderers;
import com.iafenvoy.iceandfire.render.entity.feature.SeaSerpentAncientFeatureRenderer;
import com.iafenvoy.iceandfire.render.model.animator.SeaSerpentTabulaModelAnimator;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.iafenvoy.uranus.client.model.util.TabulaModelHandlerHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class SeaSerpentEntityRenderer extends LegacyMobRenderer<SeaSerpentEntity, AdvancedEntityModel<SeaSerpentEntity>> {
    public SeaSerpentEntityRenderer(EntityRendererProvider.Context context) {
        super(context, () -> TabulaModelHandlerHelper.getModel(IafRenderers.SEA_SERPENT, SeaSerpentTabulaModelAnimator::new), 1.6F);
    }

    @Override
    protected void onModelAvailable() {
        this.addLayer(new SeaSerpentAncientFeatureRenderer(this));
    }

    @Override
    protected void scale(SeaSerpentEntity entity, PoseStack matrixStackIn, float partialTickTime) {
        this.shadowRadius = entity.getSeaSerpentScale();
        matrixStackIn.scale(this.shadowRadius, this.shadowRadius, this.shadowRadius);
    }

    @Override
    public @NotNull Identifier getTextureLocation(SeaSerpentEntity serpent) {
        return IafRegistries.SEA_SERPENT_TYPE.getValue(IceAndFire.id(serpent.getVariant())).getTexture(serpent.isBlinking());
    }
}
