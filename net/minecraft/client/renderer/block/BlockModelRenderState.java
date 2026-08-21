package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.MatrixUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class BlockModelRenderState {
    public static final int[] EMPTY_TINTS = new int[0];
    private @Nullable List<BlockStateModelPart> modelParts;
    private @Nullable Matrix4fc transformation;
    private @Nullable RenderType renderType;
    private @Nullable SpecialModelRenderer<?> specialRenderer;
    private @Nullable Matrix4fc specialRendererTransformation;
    private @Nullable IntList tintLayers;
    private @Nullable RandomSource randomSource;
    private boolean hasTranslucency;

    public void clear() {
        this.hasTranslucency = false;
        this.modelParts = null;
        this.transformation = null;
        this.renderType = null;
        this.specialRenderer = null;
        this.specialRendererTransformation = null;
        if (this.tintLayers != null) {
            this.tintLayers.clear();
        }
    }

    public IntList tintLayers() {
        if (this.tintLayers == null) {
            this.tintLayers = new IntArrayList();
        }

        return this.tintLayers;
    }

    public <T> void setupSpecialModel(SpecialModelRenderer<T> renderer, Matrix4fc transformation) {
        this.specialRenderer = renderer;
        this.specialRendererTransformation = identityToNull(transformation);
    }

    public List<BlockStateModelPart> setupModel(Matrix4fc transformation, boolean hasTranslucency) {
        this.transformation = identityToNull(transformation);
        this.renderType = hasTranslucency ? Sheets.translucentBlockSheet() : Sheets.cutoutBlockSheet();
        this.hasTranslucency = hasTranslucency;
        if (this.modelParts == null) {
            this.modelParts = new ObjectArrayList<>();
        } else {
            this.modelParts.clear();
        }

        return this.modelParts;
    }

    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, int outlineColor) {
        this.submitModel(this.renderType, poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
        this.submitSpecialRenderer(poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
    }

    /// Neo: submit this render state with full support for per-quad render types
    ///
    /// @param poseStack           The transformations to apply to the model
    /// @param submitNodeCollector The collector to submit this render state to
    /// @param lightCoords         The packed light coordinates to render the model with
    /// @param overlayCoords       The overlay texture coordinates to render the model with
    /// @param outlineColor        The outline color to render the model with, or `0` to render no outline
    public void submitMultiLayer(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, int outlineColor) {
        if (this.modelParts != null && !this.modelParts.isEmpty()) {
            List<BlockStateModelPart> modelPartsCopy = new ObjectArrayList<>(this.modelParts);
            int[] tints = this.tintLayers != null ? this.tintLayers.toArray(EMPTY_TINTS) : EMPTY_TINTS;
            if (this.transformation != null) {
                poseStack.pushPose();
                poseStack.mulPose(this.transformation);
                submitNodeCollector.submitMultiLayerBlockModel(poseStack, modelPartsCopy, hasTranslucency, tints, lightCoords, overlayCoords, outlineColor);
                poseStack.popPose();
            } else {
                submitNodeCollector.submitMultiLayerBlockModel(poseStack, modelPartsCopy, hasTranslucency, tints, lightCoords, overlayCoords, outlineColor);
            }
        }
        this.submitSpecialRenderer(poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
    }

    private void submitSpecialRenderer(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, int outlineColor) {
        if (this.specialRenderer != null) {
            if (this.specialRendererTransformation != null) {
                poseStack.pushPose();
                poseStack.mulPose(this.specialRendererTransformation);
                submitSpecialRenderer(this.specialRenderer, poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
                poseStack.popPose();
            } else {
                submitSpecialRenderer(this.specialRenderer, poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
            }
        }
    }

    private static @Nullable Matrix4fc identityToNull(Matrix4fc transformation) {
        return MatrixUtil.checkPropertyRaw(transformation, 4) ? null : transformation;
    }

    private void submitModel(
        RenderType renderType, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, int outlineColor
    ) {
        if (this.modelParts != null && !this.modelParts.isEmpty()) {
            List<BlockStateModelPart> modelPartsCopy = new ObjectArrayList<>(this.modelParts);
            int[] tints = this.tintLayers != null ? this.tintLayers.toArray(EMPTY_TINTS) : EMPTY_TINTS;
            if (this.transformation != null) {
                poseStack.pushPose();
                poseStack.mulPose(this.transformation);
                submitNodeCollector.submitBlockModel(poseStack, renderType, modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor);
                poseStack.popPose();
            } else {
                submitNodeCollector.submitBlockModel(poseStack, renderType, modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor);
            }
        }
    }

    private static void submitSpecialRenderer(
        SpecialModelRenderer<?> renderer, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, int outlineColor
    ) {
        renderer.submit(null, poseStack, submitNodeCollector, lightCoords, overlayCoords, false, outlineColor);
    }

    public void submitOnlyOutline(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, int outlineColor) {
        this.submitModel(RenderTypes.outline(TextureAtlas.LOCATION_BLOCKS), poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
    }

    public void submitWithZOffset(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, int outlineColor) {
        this.submitModel(
            RenderTypes.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS), poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor
        );
    }

    public boolean isEmpty() {
        return this.modelParts == null && this.specialRenderer == null;
    }

    public RandomSource scratchRandomSource(long seed) {
        if (this.randomSource == null) {
            this.randomSource = RandomSource.create(seed);
        } else {
            this.randomSource.setSeed(seed);
        }

        return this.randomSource;
    }
}
