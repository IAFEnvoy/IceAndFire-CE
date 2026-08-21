package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.HippocampusEntity;
import com.iafenvoy.iceandfire.render.model.HippocampusModel;
import com.iafenvoy.iceandfire.util.Color4i;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class HippocampusEntityRenderer extends LegacyMobRenderer<HippocampusEntity, HippocampusModel> {
    private static final Identifier VARIANT_0 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/hippocampus_0.png");
    private static final Identifier VARIANT_0_BLINK = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/hippocampus_0_blinking.png");
    private static final Identifier VARIANT_1 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/hippocampus_1.png");
    private static final Identifier VARIANT_1_BLINK = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/hippocampus_1_blinking.png");
    private static final Identifier VARIANT_2 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/hippocampus_2.png");
    private static final Identifier VARIANT_2_BLINK = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/hippocampus_2_blinking.png");
    private static final Identifier VARIANT_3 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/hippocampus_3.png");
    private static final Identifier VARIANT_3_BLINK = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/hippocampus_3_blinking.png");
    private static final Identifier VARIANT_4 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/hippocampus_4.png");
    private static final Identifier VARIANT_4_BLINK = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/hippocampus_4_blinking.png");
    private static final Identifier VARIANT_5 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/hippocampus_5.png");
    private static final Identifier VARIANT_5_BLINK = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/hippocampus_5_blinking.png");
    private static final RenderType SADDLE = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/saddle.png"));
    private static final RenderType BRIDLE = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/bridle.png"));
    private static final RenderType CHEST = RenderTypes.entityTranslucent(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/chest.png"));
    private static final RenderType ARMOR_DIAMOND = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/armor_diamond.png"));
    private static final RenderType ARMOR_GOLD = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/armor_gold.png"));
    private static final RenderType ARMOR_IRON = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/armor_iron.png"));
    private static final RenderType RAINBOW = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/rainbow.png"));
    private static final RenderType RAINBOW_BLINK = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippocampus/rainbow_blink.png"));

    public HippocampusEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new HippocampusModel(), 0.8F);
        this.addLayer(this::submitRainbow);
        this.addLayer(this::submitSaddle);
    }

    private void submitSaddle(HippocampusEntity entity, float partialTick, PoseStack poseStack, SubmitNodeCollector collector, net.minecraft.client.renderer.state.level.CameraRenderState camera, int light, int outline) {
        if (entity.isSaddled()) submit(this.model, poseStack, collector, SADDLE, light, -1);
        if (entity.isSaddled() && entity.getControllingPassenger() != null) submit(this.model, poseStack, collector, BRIDLE, light, -1);
        if (entity.isChested()) submit(this.model, poseStack, collector, CHEST, light, -1);
        RenderType armor = switch (entity.getArmorValue()) {
            case 1 -> ARMOR_IRON;
            case 2 -> ARMOR_GOLD;
            case 3 -> ARMOR_DIAMOND;
            default -> null;
        };
        if (armor != null) submit(this.model, poseStack, collector, armor, light, -1);
    }

    private void submitRainbow(HippocampusEntity entity, float partialTick, PoseStack poseStack, SubmitNodeCollector collector, net.minecraft.client.renderer.state.level.CameraRenderState camera, int light, int outline) {
        if (!entity.hasCustomName() || !entity.getCustomName().getString().toLowerCase(Locale.ROOT).contains("rainbow")) return;
        int first = (entity.tickCount / 25 + entity.getId()) % DyeColor.values().length;
        int second = (first + 1) % DyeColor.values().length;
        float blend = (entity.tickCount % 25 + partialTick) / 25.0F;
        Color4i start = new Color4i(0xFF000000 | DyeColor.byId(first).getTextureDiffuseColor());
        Color4i end = new Color4i(0xFF000000 | DyeColor.byId(second).getTextureDiffuseColor());
        int color = new Color4i(start.r() * (1.0F - blend) + end.r() * blend, start.g() * (1.0F - blend) + end.g() * blend, start.b() * (1.0F - blend) + end.b() * blend, 1.0F).getIntValue();
        submit(this.model, poseStack, collector, entity.isBlinking() ? RAINBOW_BLINK : RAINBOW, light, color);
    }

    private static void submit(HippocampusModel model, PoseStack poseStack, SubmitNodeCollector collector, RenderType type, int light, int color) {
        collector.submitCustomGeometry(poseStack, type, (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            model.renderToBuffer(modelStack, buffer, light, OverlayTexture.NO_OVERLAY, color);
        });
    }

    @Override
    public @NotNull Identifier getTextureLocation(HippocampusEntity entity) {
        return switch (entity.getVariant()) {
            case 1 -> entity.isBlinking() ? VARIANT_1_BLINK : VARIANT_1;
            case 2 -> entity.isBlinking() ? VARIANT_2_BLINK : VARIANT_2;
            case 3 -> entity.isBlinking() ? VARIANT_3_BLINK : VARIANT_3;
            case 4 -> entity.isBlinking() ? VARIANT_4_BLINK : VARIANT_4;
            case 5 -> entity.isBlinking() ? VARIANT_5_BLINK : VARIANT_5;
            default -> entity.isBlinking() ? VARIANT_0_BLINK : VARIANT_0;
        };
    }
}
