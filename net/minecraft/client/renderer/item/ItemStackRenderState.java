package net.minecraft.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class ItemStackRenderState {
    ItemDisplayContext displayContext = ItemDisplayContext.NONE;
    private int activeLayerCount;
    private boolean animated;
    private boolean oversizedInGui;
    private @Nullable AABB cachedModelBoundingBox;
    private ItemStackRenderState.LayerRenderState[] layers = new ItemStackRenderState.LayerRenderState[]{new ItemStackRenderState.LayerRenderState()};

    public void ensureCapacity(int requestedCount) {
        int currentCapacity = this.layers.length;
        int requiredNewCapacity = this.activeLayerCount + requestedCount;
        if (requiredNewCapacity > currentCapacity) {
            this.layers = Arrays.copyOf(this.layers, requiredNewCapacity);

            for (int i = currentCapacity; i < requiredNewCapacity; i++) {
                this.layers[i] = new ItemStackRenderState.LayerRenderState();
            }
        }
    }

    public ItemStackRenderState.LayerRenderState newLayer() {
        this.ensureCapacity(1);
        return this.layers[this.activeLayerCount++];
    }

    public void clear() {
        this.displayContext = ItemDisplayContext.NONE;

        for (int i = 0; i < this.activeLayerCount; i++) {
            this.layers[i].clear();
        }

        this.activeLayerCount = 0;
        this.animated = false;
        this.oversizedInGui = false;
        this.cachedModelBoundingBox = null;
    }

    public void setAnimated() {
        this.animated = true;
    }

    public boolean isAnimated() {
        return this.animated;
    }

    public void appendModelIdentityElement(Object element) {
    }

    private ItemStackRenderState.LayerRenderState firstLayer() {
        return this.layers[0];
    }

    public boolean isEmpty() {
        return this.activeLayerCount == 0;
    }

    public boolean usesBlockLight() {
        return this.firstLayer().usesBlockLight;
    }

    public Material.@Nullable Baked pickParticleMaterial(RandomSource randomSource) {
        return this.activeLayerCount == 0 ? null : this.layers[randomSource.nextInt(this.activeLayerCount)].particleMaterial;
    }

    public void visitExtents(Consumer<Vector3fc> output) {
        Vector3f scratch = new Vector3f();
        PoseStack.Pose pose = new PoseStack.Pose();

        for (int i = 0; i < this.activeLayerCount; i++) {
            ItemStackRenderState.LayerRenderState layer = this.layers[i];
            layer.applyTransform(pose);
            Matrix4f poseTransform = pose.pose();
            Vector3fc[] layerExtents = layer.extents.get();

            for (Vector3fc extent : layerExtents) {
                output.accept(scratch.set(extent).mulPosition(poseTransform));
            }

            pose.setIdentity();
        }
    }

    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, int outlineColor) {
        for (int i = 0; i < this.activeLayerCount; i++) {
            this.layers[i].submit(poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
        }
    }

    public AABB getModelBoundingBox() {
        if (this.cachedModelBoundingBox != null) {
            return this.cachedModelBoundingBox;
        } else {
            AABB.Builder collector = new AABB.Builder();
            this.visitExtents(collector::include);
            AABB aabb = collector.isDefined() ? collector.build() : AABB.ofSize(Vec3.ZERO, 0.0, 0.0, 0.0);
            this.cachedModelBoundingBox = aabb;
            return aabb;
        }
    }

    public void setOversizedInGui(boolean oversizedInGui) {
        this.oversizedInGui = oversizedInGui;
    }

    public boolean isOversizedInGui() {
        return this.oversizedInGui;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum FoilType {
        NONE,
        STANDARD,
        SPECIAL;
    }

    @OnlyIn(Dist.CLIENT)
    public class LayerRenderState {
        private static final Vector3fc[] NO_EXTENTS = new Vector3fc[0];
        public static final Supplier<Vector3fc[]> NO_EXTENTS_SUPPLIER = () -> NO_EXTENTS;
        public static final int[] EMPTY_TINTS = new int[0];
        private final List<BakedQuad> quads;
        private boolean usesBlockLight;
        private Material.@Nullable Baked particleMaterial;
        private ItemTransform itemTransform;
        private final Matrix4f localTransform;
        private ItemStackRenderState.FoilType foilType;
        private @Nullable IntList tintLayers;
        private @Nullable SpecialModelRenderer<Object> specialRenderer;
        private @Nullable Object argumentForSpecialRendering;
        private Supplier<Vector3fc[]> extents;

        public LayerRenderState() {
            Objects.requireNonNull(ItemStackRenderState.this);
            super();
            this.quads = new ArrayList<>();
            this.itemTransform = ItemTransform.NO_TRANSFORM;
            this.localTransform = new Matrix4f();
            this.foilType = ItemStackRenderState.FoilType.NONE;
            this.extents = NO_EXTENTS_SUPPLIER;
        }

        public void clear() {
            this.quads.clear();
            this.foilType = ItemStackRenderState.FoilType.NONE;
            this.specialRenderer = null;
            this.argumentForSpecialRendering = null;
            if (this.tintLayers != null) {
                this.tintLayers.clear();
            }

            this.usesBlockLight = false;
            this.particleMaterial = null;
            this.itemTransform = ItemTransform.NO_TRANSFORM;
            this.localTransform.identity();
            this.extents = NO_EXTENTS_SUPPLIER;
        }

        public List<BakedQuad> prepareQuadList() {
            return this.quads;
        }

        public void setUsesBlockLight(boolean usesBlockLight) {
            this.usesBlockLight = usesBlockLight;
        }

        public void setExtents(Supplier<Vector3fc[]> extents) {
            this.extents = extents;
        }

        public void setParticleMaterial(Material.Baked particleMaterial) {
            this.particleMaterial = particleMaterial;
        }

        public void setItemTransform(ItemTransform transform) {
            this.itemTransform = transform;
        }

        public void setLocalTransform(Matrix4fc transform) {
            this.localTransform.set(transform);
        }

        public <T> void setupSpecialModel(SpecialModelRenderer<T> renderer, @Nullable T argument) {
            this.specialRenderer = eraseSpecialRenderer(renderer);
            this.argumentForSpecialRendering = argument;
        }

        private static SpecialModelRenderer<Object> eraseSpecialRenderer(SpecialModelRenderer<?> renderer) {
            return (SpecialModelRenderer<Object>)renderer;
        }

        public void setFoilType(ItemStackRenderState.FoilType foilType) {
            this.foilType = foilType;
        }

        public IntList tintLayers() {
            if (this.tintLayers == null) {
                this.tintLayers = new IntArrayList();
            }

            return this.tintLayers;
        }

        private void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, int outlineColor) {
            poseStack.pushPose();
            this.applyTransform(poseStack.last());
            if (this.specialRenderer != null) {
                this.specialRenderer
                    .submit(
                        this.argumentForSpecialRendering,
                        poseStack,
                        submitNodeCollector,
                        lightCoords,
                        overlayCoords,
                        this.foilType != ItemStackRenderState.FoilType.NONE,
                        outlineColor
                    );
            } else {
                int[] tints = this.tintLayers != null ? this.tintLayers.toArray(EMPTY_TINTS) : EMPTY_TINTS;
                submitNodeCollector.submitItem(
                    poseStack, ItemStackRenderState.this.displayContext, lightCoords, overlayCoords, outlineColor, tints, this.quads, this.foilType
                );
            }

            poseStack.popPose();
        }

        private void applyTransform(PoseStack.Pose localPose) {
            this.itemTransform.apply(ItemStackRenderState.this.displayContext.leftHand(), localPose);
            localPose.mulPose(this.localTransform);
        }
    }
}
