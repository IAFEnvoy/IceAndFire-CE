package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public interface OrderedSubmitNodeCollector extends net.neoforged.neoforge.client.extensions.OrderedSubmitNodeCollectorExtension {
    void submitShadow(PoseStack poseStack, float radius, List<EntityRenderState.ShadowPiece> pieces);

    void submitNameTag(
        PoseStack poseStack,
        @Nullable Vec3 nameTagAttachment,
        final int offset,
        Component name,
        boolean seeThrough,
        int lightCoords,
        double distanceToCameraSq,
        final CameraRenderState camera
    );

    void submitText(
        PoseStack poseStack,
        float x,
        float y,
        FormattedCharSequence string,
        boolean dropShadow,
        Font.DisplayMode displayMode,
        int lightCoords,
        int color,
        int backgroundColor,
        int outlineColor
    );

    void submitFlame(PoseStack poseStack, EntityRenderState renderState, Quaternionf rotation);

    void submitLeash(PoseStack poseStack, EntityRenderState.LeashState leashState);

    <S> void submitModel(
        Model<? super S> model,
        S state,
        PoseStack poseStack,
        RenderType renderType,
        int lightCoords,
        int overlayCoords,
        int tintedColor,
        @Nullable TextureAtlasSprite sprite,
        int outlineColor,
        final ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    );

    default <S> void submitModel(
        Model<? super S> model,
        S state,
        PoseStack poseStack,
        RenderType renderType,
        int lightCoords,
        int overlayCoords,
        int outlineColor,
        ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {
        this.submitModel(model, state, poseStack, renderType, lightCoords, overlayCoords, -1, null, outlineColor, crumblingOverlay);
    }

    default <S> void submitModel(
        Model<? super S> model,
        S state,
        PoseStack poseStack,
        Identifier texture,
        int lightCoords,
        int overlayCoords,
        int outlineColor,
        ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {
        this.submitModel(model, state, poseStack, model.renderType(texture), lightCoords, overlayCoords, -1, null, outlineColor, crumblingOverlay);
    }

    default <S> void submitModel(
        Model<S> model,
        S state,
        PoseStack poseStack,
        int lightCoords,
        int overlayCoords,
        int tintedColor,
        SpriteId sprite,
        SpriteGetter sprites,
        int outlineColor,
        ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {
        this.submitModel(
            model,
            state,
            poseStack,
            sprite.renderType(model.renderType()),
            lightCoords,
            overlayCoords,
            tintedColor,
            sprites.get(sprite),
            outlineColor,
            crumblingOverlay
        );
    }

    default void submitModelPart(
        ModelPart modelPart, PoseStack poseStack, RenderType renderType, int lightCoords, int overlayCoords, @Nullable TextureAtlasSprite sprite
    ) {
        this.submitModelPart(modelPart, poseStack, renderType, lightCoords, overlayCoords, sprite, false, false, -1, null, 0);
    }

    default void submitModelPart(
        ModelPart modelPart,
        PoseStack poseStack,
        RenderType renderType,
        int lightCoords,
        int overlayCoords,
        @Nullable TextureAtlasSprite sprite,
        int tintedColor,
        ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {
        this.submitModelPart(modelPart, poseStack, renderType, lightCoords, overlayCoords, sprite, false, false, tintedColor, crumblingOverlay, 0);
    }

    default void submitModelPart(
        ModelPart modelPart,
        PoseStack poseStack,
        RenderType renderType,
        int lightCoords,
        int overlayCoords,
        @Nullable TextureAtlasSprite sprite,
        boolean sheeted,
        boolean hasFoil
    ) {
        this.submitModelPart(modelPart, poseStack, renderType, lightCoords, overlayCoords, sprite, sheeted, hasFoil, -1, null, 0);
    }

    void submitModelPart(
        ModelPart modelPart,
        PoseStack poseStack,
        RenderType renderType,
        int lightCoords,
        int overlayCoords,
        @Nullable TextureAtlasSprite sprite,
        boolean sheeted,
        boolean hasFoil,
        int tintedColor,
        final ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay,
        final int outlineColor
    );

    void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState movingBlockRenderState);

    void submitBlockModel(
        PoseStack poseStack, RenderType renderType, List<BlockStateModelPart> parts, int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor
    );

    void submitBreakingBlockModel(PoseStack poseStack, BlockStateModel model, long seed, int progress);

    void submitItem(
        PoseStack poseStack,
        ItemDisplayContext displayContext,
        int lightCoords,
        int overlayCoords,
        int outlineColor,
        int[] tintLayers,
        List<BakedQuad> quads,
        ItemStackRenderState.FoilType foilType
    );

    void submitCustomGeometry(PoseStack poseStack, RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer);

    void submitParticleGroup(SubmitNodeCollector.ParticleGroupRenderer particleGroupRenderer);
}
