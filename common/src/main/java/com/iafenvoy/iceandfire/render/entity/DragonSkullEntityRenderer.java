package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.DragonSkullEntity;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.iceandfire.registry.IafRenderers;
import com.iafenvoy.iceandfire.render.model.animator.FireDragonTabulaModelAnimator;
import com.iafenvoy.iceandfire.render.model.animator.IceDragonTabulaModelAnimator;
import com.iafenvoy.iceandfire.render.model.animator.LightningTabulaDragonAnimator;
import com.iafenvoy.iceandfire.render.model.animator.NetherDragonTabulaModelAnimator;
import com.iafenvoy.uranus.client.model.ITabulaModelAnimator;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import com.iafenvoy.uranus.client.model.util.TabulaModelHandlerHelper;
import com.iafenvoy.uranus.util.function.MemorizeSupplier;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.HashMap;
import java.util.Map;

public class DragonSkullEntityRenderer extends EntityRenderer<DragonSkullEntity> {
    public static final Map<DragonType, Pair<Identifier, MemorizeSupplier<ITabulaModelAnimator<? extends DragonBaseEntity>>>> MODELS = new HashMap<>();
    public static final float[] growth_stage_1 = new float[]{1F, 3F};
    public static final float[] growth_stage_2 = new float[]{3F, 7F};
    public static final float[] growth_stage_3 = new float[]{7F, 12.5F};
    public static final float[] growth_stage_4 = new float[]{12.5F, 20F};
    public static final float[] growth_stage_5 = new float[]{20F, 30F};

    static {
        MODELS.put(IafDragonTypes.FIRE, Pair.of(IafRenderers.FIRE_DRAGON, new MemorizeSupplier<>(FireDragonTabulaModelAnimator::new)));
        MODELS.put(IafDragonTypes.ICE, Pair.of(IafRenderers.ICE_DRAGON, new MemorizeSupplier<>(IceDragonTabulaModelAnimator::new)));
        MODELS.put(IafDragonTypes.LIGHTNING, Pair.of(IafRenderers.LIGHTNING_DRAGON, new MemorizeSupplier<>(LightningTabulaDragonAnimator::new)));
        MODELS.put(IafDragonTypes.NETHER, Pair.of(IafRenderers.NETHER_DRAGON, new MemorizeSupplier<>(NetherDragonTabulaModelAnimator::new)));
    }

    public final float[][] growth_stages;

    public DragonSkullEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.growth_stages = new float[][]{growth_stage_1, growth_stage_2, growth_stage_3, growth_stage_4, growth_stage_5};
    }

    private static void setRotationAngles(BasicModelPart cube, float rotX) {
        cube.rotateAngleX = rotX;
        cube.rotateAngleY = (float) 0;
        cube.rotateAngleZ = (float) 0;
    }

    @Override
    public void render(DragonSkullEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn) {
        Pair<Identifier, MemorizeSupplier<ITabulaModelAnimator<? extends DragonBaseEntity>>> p = MODELS.get(IafRegistries.DRAGON_TYPE.get(IceAndFire.id(entity.getDragonType())));
        if (p == null) return;
        TabulaModel<? extends DragonBaseEntity> model = TabulaModelHandlerHelper.getModel(p.getFirst());
        if (model == null) return;
        VertexConsumer consumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(entity)));
        matrixStackIn.push();
        matrixStackIn.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-180.0F));
        matrixStackIn.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(-180.0F - entity.getYaw()));
        matrixStackIn.scale(1.0F, 1.0F, 1.0F);
        float size = this.getRenderSize(entity) / 3;
        matrixStackIn.scale(size, size, size);
        matrixStackIn.translate(0, entity.isOnWall() ? -0.24F : -0.12F, entity.isOnWall() ? 0.4F : 0.5F);
        model.resetToDefaultPose();
        setRotationAngles(model.getCube("Head"), entity.isOnWall() ? (float) Math.toRadians(50F) : 0F);
        model.getCube("Head").render(matrixStackIn, consumer, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
        matrixStackIn.pop();
    }

    @Override
    public Identifier getTexture(DragonSkullEntity entity) {
        return IafRegistries.DRAGON_TYPE.get(IceAndFire.id(entity.getDragonType())).getSkeletonTexture(entity.getDragonStage());
    }

    public float getRenderSize(DragonSkullEntity skull) {
        float step = (this.growth_stages[skull.getDragonStage() - 1][1] - this.growth_stages[skull.getDragonStage() - 1][0]) / 25;
        if (skull.getDragonAge() > 125)
            return this.growth_stages[skull.getDragonStage() - 1][0] + ((step * 25));
        return this.growth_stages[skull.getDragonStage() - 1][0] + ((step * this.getAgeFactor(skull)));
    }

    private int getAgeFactor(DragonSkullEntity skull) {
        return (skull.getDragonStage() > 1 ? skull.getDragonAge() - (25 * (skull.getDragonStage() - 1)) : skull.getDragonAge());
    }
}
