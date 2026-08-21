package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.HippogryphEntity;
import com.iafenvoy.iceandfire.render.model.HippogryphModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class HippogryphEntityRenderer extends LegacyMobRenderer<HippogryphEntity, HippogryphModel> {
    private static final RenderType SADDLE = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/saddle.png"));
    private static final RenderType BRIDLE = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/bridle.png"));
    private static final RenderType CHEST = RenderTypes.entityTranslucent(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/chest.png"));
    private static final RenderType ARMOR_IRON = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/armor_iron.png"));
    private static final RenderType ARMOR_GOLD = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/armor_gold.png"));
    private static final RenderType ARMOR_DIAMOND = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/armor_diamond.png"));
    private static final RenderType ARMOR_NETHERITE = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/armor_netherite.png"));

    public HippogryphEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new HippogryphModel(), 0.8F);
        this.addLayer(this::submitEquipment);
    }

    @Override
    protected void scale(@NotNull HippogryphEntity entity, PoseStack matrix, float partialTickTime) {
        matrix.scale(1.2F, 1.2F, 1.2F);
    }

    private void submitEquipment(HippogryphEntity entity, float partialTick, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, int light, int outline) {
        RenderType armor = switch (entity.getArmorValue()) {
            case 1 -> ARMOR_IRON;
            case 2 -> ARMOR_GOLD;
            case 3 -> ARMOR_DIAMOND;
            case 4 -> ARMOR_NETHERITE;
            default -> null;
        };
        if (armor != null) submit(this.model, poseStack, collector, armor, light);
        if (entity.isSaddled()) submit(this.model, poseStack, collector, SADDLE, light);
        if (entity.isSaddled() && entity.getControllingPassenger() != null)
            submit(this.model, poseStack, collector, BRIDLE, light);
        if (entity.isChested()) submit(this.model, poseStack, collector, CHEST, light);
    }

    private static void submit(HippogryphModel model, PoseStack poseStack, SubmitNodeCollector collector, RenderType type, int light) {
        collector.submitCustomGeometry(poseStack, type, (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            model.renderToBuffer(modelStack, buffer, light, OverlayTexture.NO_OVERLAY, -1);
        });
    }

    @Override
    public @NotNull Identifier getTextureLocation(HippogryphEntity entity) {
        return entity.getEnumVariant().getTexture(entity.isBlinking());
    }
}
