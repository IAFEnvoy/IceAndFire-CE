package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.CockatriceEntity;
import com.iafenvoy.iceandfire.render.model.CockatriceChickModel;
import com.iafenvoy.iceandfire.render.model.CockatriceModel;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class CockatriceEntityRenderer extends EntityRenderer<CockatriceEntity, LegacyEntityRenderState<CockatriceEntity>> {
    public static final Identifier TEXTURE_ROOSTER = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cockatrice/cockatrice_0.png");
    public static final Identifier TEXTURE_HEN = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cockatrice/cockatrice_1.png");
    public static final Identifier TEXTURE_ROOSTER_CHICK = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cockatrice/cockatrice_0_chick.png");
    public static final Identifier TEXTURE_HEN_CHICK = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cockatrice/cockatrice_1_chick.png");
    public static final CockatriceModel ADULT_MODEL = new CockatriceModel();
    public static final CockatriceChickModel BABY_MODEL = new CockatriceChickModel();

    public CockatriceEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.6F;
    }

    private Vec3 getPosition(LivingEntity LivingEntityIn, double p_177110_2_) {
        double d0 = LivingEntityIn.xOld + (LivingEntityIn.getX() - LivingEntityIn.xOld);
        double d1 = p_177110_2_ + LivingEntityIn.yOld + (LivingEntityIn.getY() - LivingEntityIn.yOld);
        double d2 = LivingEntityIn.zOld + (LivingEntityIn.getZ() - LivingEntityIn.zOld);
        return new Vec3(d0, d1, d2);
    }

    @Override
    public boolean shouldRender(@NotNull CockatriceEntity livingEntityIn, @NotNull Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ))
            return true;
        else {
            if (livingEntityIn.hasTargetedEntity()) {
                LivingEntity livingentity = livingEntityIn.getTargetedEntity();
                if (livingentity != null) {
                    Vec3 Vector3d = this.getPosition(livingentity, (double) livingentity.getBbHeight() * 0.5D);
                    Vec3 Vector3d1 = this.getPosition(livingEntityIn, livingEntityIn.getEyeHeight());
                    return camera.isVisible(new AABB(Vector3d1.x, Vector3d1.y, Vector3d1.z, Vector3d.x, Vector3d.y, Vector3d.z));
                }
            }
            return false;
        }
    }

    protected void scale(CockatriceEntity entity, @NotNull PoseStack matrixStackIn) {
        if (entity.isBaby())
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
    }

    public @NotNull Identifier getTextureLocation(CockatriceEntity cockatrice) {
        return cockatrice.isBaby() ? cockatrice.isHen() ? TEXTURE_HEN_CHICK : TEXTURE_ROOSTER_CHICK : cockatrice.isHen() ? TEXTURE_HEN : TEXTURE_ROOSTER;
    }

    @Override
    public LegacyEntityRenderState<CockatriceEntity> createRenderState() {
        return new LegacyEntityRenderState<>();
    }

    @Override
    public void extractRenderState(CockatriceEntity entity, LegacyEntityRenderState<CockatriceEntity> state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.entity = entity;
    }

    @Override
    public void submit(LegacyEntityRenderState<CockatriceEntity> state, PoseStack poseStack, SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        CockatriceEntity entity = state.entity;
        AdvancedEntityModel<CockatriceEntity> model = entity.isBaby() ? BABY_MODEL : ADULT_MODEL;
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entity, poseStack);
        model.setupAnim(entity, 0.0F, 0.0F, state.ageInTicks, 0.0F, 0.0F);
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(this.getTextureLocation(entity)), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            model.renderToBuffer(modelStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor == 0 ? -1 : state.outlineColor);
        });
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }
}
