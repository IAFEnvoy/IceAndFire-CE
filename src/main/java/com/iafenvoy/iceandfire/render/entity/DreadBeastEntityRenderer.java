package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DreadBeastEntity;
import com.iafenvoy.iceandfire.render.model.DreadBeastModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class DreadBeastEntityRenderer extends LegacyMobRenderer<DreadBeastEntity, DreadBeastModel> {
    public static final Identifier TEXTURE_EYES = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_beast_eyes.png");
    public static final Identifier TEXTURE_0 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_beast_1.png");
    public static final Identifier TEXTURE_1 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_beast_2.png");

    public DreadBeastEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DreadBeastModel(), 0.5F);
    }

    @Override
    protected void scale(DreadBeastEntity entity, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(entity.getSize(), entity.getSize(), entity.getSize());
    }

    @Override
    public @NotNull Identifier getTextureLocation(DreadBeastEntity beast) {
        return beast.getVariant() == 1 ? TEXTURE_1 : TEXTURE_0;
    }
}
