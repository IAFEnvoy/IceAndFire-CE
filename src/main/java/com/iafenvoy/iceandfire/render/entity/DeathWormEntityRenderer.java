package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DeathWormEntity;
import com.iafenvoy.iceandfire.render.model.DeathWormModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DeathWormEntityRenderer extends MobRenderer<DeathWormEntity, DeathWormModel> {
    public static final ResourceLocation TEXTURE_RED = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/deathworm/deathworm_red.png");
    public static final ResourceLocation TEXTURE_WHITE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/deathworm/deathworm_white.png");
    public static final ResourceLocation TEXTURE_YELLOW = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/deathworm/deathworm_yellow.png");

    public DeathWormEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DeathWormModel(), 0);
    }

    @Override
    protected void scale(DeathWormEntity entity, PoseStack matrixStackIn, float partialTickTime) {
        this.shadowRadius = entity.getAgeScale() / 3;
        matrixStackIn.scale(entity.getAgeScale(), entity.getAgeScale(), entity.getAgeScale());
    }



    @Override
    protected int getBlockLightLevel(DeathWormEntity entityIn, @NotNull BlockPos partialTicks) {
        return entityIn.isOnFire() ? 15 : entityIn.getWormBrightness(false);
    }

    @Override
    protected int getSkyLightLevel(DeathWormEntity entity, @NotNull BlockPos pos) {
        return entity.getWormBrightness(true);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DeathWormEntity entity) {
        return entity.getVariant() == 2 ? TEXTURE_WHITE : entity.getVariant() == 1 ? TEXTURE_RED : TEXTURE_YELLOW;
    }
}
