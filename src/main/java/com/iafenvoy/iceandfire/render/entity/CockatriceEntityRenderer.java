package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.CockatriceEntity;
import com.iafenvoy.iceandfire.entity.util.IafEntityUtil;
import com.iafenvoy.iceandfire.render.misc.CockatriceBeamRenderer;
import com.iafenvoy.iceandfire.render.model.CockatriceChickModel;
import com.iafenvoy.iceandfire.render.model.CockatriceModel;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CockatriceEntityRenderer extends MobRenderer<CockatriceEntity, AdvancedEntityModel<CockatriceEntity>> {
    public static final ResourceLocation TEXTURE_ROOSTER = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cockatrice/cockatrice_0.png");
    public static final ResourceLocation TEXTURE_HEN = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cockatrice/cockatrice_1.png");
    public static final ResourceLocation TEXTURE_ROOSTER_CHICK = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cockatrice/cockatrice_0_chick.png");
    public static final ResourceLocation TEXTURE_HEN_CHICK = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cockatrice/cockatrice_1_chick.png");
    public static final CockatriceModel ADULT_MODEL = new CockatriceModel();
    public static final CockatriceChickModel BABY_MODEL = new CockatriceChickModel();

    public CockatriceEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new CockatriceModel(), 0.6F);
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

    @Override
    public void render(CockatriceEntity entityIn, float entityYaw, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        this.model = entityIn.isBaby() ? BABY_MODEL : ADULT_MODEL;
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        LivingEntity livingentity = entityIn.getTargetedEntity();
        boolean blindness = entityIn.hasEffect(MobEffects.BLINDNESS) || livingentity != null && livingentity.hasEffect(MobEffects.BLINDNESS);
        if (!blindness && livingentity != null && IafEntityUtil.isEntityLookingAt(entityIn, livingentity, CockatriceEntity.VIEW_RADIUS) && IafEntityUtil.isEntityLookingAt(livingentity, entityIn, CockatriceEntity.VIEW_RADIUS))
            CockatriceBeamRenderer.render(entityIn, livingentity, matrixStackIn, bufferIn, partialTicks);
    }

    @Override
    protected void scale(CockatriceEntity entity, @NotNull PoseStack matrixStackIn, float partialTickTime) {
        if (entity.isBaby())
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(CockatriceEntity cockatrice) {
        return cockatrice.isBaby() ? cockatrice.isHen() ? TEXTURE_HEN_CHICK : TEXTURE_ROOSTER_CHICK : cockatrice.isHen() ? TEXTURE_HEN : TEXTURE_ROOSTER;
    }
}
