package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.DragonSkullEntity;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonSize;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.iceandfire.registry.IafRenderers;
import com.iafenvoy.iceandfire.render.model.animator.FireDragonTabulaModelAnimator;
import com.iafenvoy.iceandfire.render.model.animator.IceDragonTabulaModelAnimator;
import com.iafenvoy.iceandfire.render.model.animator.LightningTabulaDragonAnimator;
import com.iafenvoy.uranus.client.model.ITabulaModelAnimator;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import com.iafenvoy.uranus.client.model.util.TabulaModelHandlerHelper;
import com.iafenvoy.uranus.event.Event;
import com.iafenvoy.uranus.util.function.MemorizeSupplier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DragonSkullEntityRenderer extends EntityRenderer<DragonSkullEntity> {
    public static final Event<Consumer<BiConsumer<DragonType, net.minecraft.util.Tuple<ResourceLocation, MemorizeSupplier<ITabulaModelAnimator<? extends DragonBaseEntity>>>>>> COLLECT_DRAGON_SKULL_MODELS = new Event<>(callbacks -> consumer -> callbacks.forEach(x -> x.accept(consumer)));
    private final Map<DragonType, Tuple<ResourceLocation, MemorizeSupplier<ITabulaModelAnimator<? extends DragonBaseEntity>>>> models = new HashMap<>();

    static {
        COLLECT_DRAGON_SKULL_MODELS.register(consumer -> {
            consumer.accept(IafDragonTypes.FIRE, new Tuple<>(IafRenderers.FIRE_DRAGON, new MemorizeSupplier<>(FireDragonTabulaModelAnimator::new)));
            consumer.accept(IafDragonTypes.ICE, new Tuple<>(IafRenderers.ICE_DRAGON, new MemorizeSupplier<>(IceDragonTabulaModelAnimator::new)));
            consumer.accept(IafDragonTypes.LIGHTNING, new Tuple<>(IafRenderers.LIGHTNING_DRAGON, new MemorizeSupplier<>(LightningTabulaDragonAnimator::new)));
        });
    }

    public DragonSkullEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        COLLECT_DRAGON_SKULL_MODELS.invoker().accept(this.models::put);
    }

    private static void setRotationAngles(BasicModelPart cube, float rotX) {
        cube.rotateAngleX = rotX;
        cube.rotateAngleY = (float) 0;
        cube.rotateAngleZ = (float) 0;
    }

    @Override
    public void render(DragonSkullEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        Tuple<ResourceLocation, MemorizeSupplier<ITabulaModelAnimator<? extends DragonBaseEntity>>> p = this.models.get(IafRegistries.DRAGON_TYPE.get(IceAndFire.id(entity.getDragonType())));
        if (p == null) return;
        TabulaModel<? extends DragonBaseEntity> model = TabulaModelHandlerHelper.getModel(p.getA());
        if (model == null) return;
        VertexConsumer consumer = bufferIn.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180.0F));
        matrixStackIn.mulPose(Axis.YN.rotationDegrees(-180.0F - entity.getYRot()));
        matrixStackIn.scale(1.0F, 1.0F, 1.0F);
        float size = this.getRenderSize(entity) / 3;
        matrixStackIn.scale(size, size, size);
        matrixStackIn.translate(0, entity.isOnWall() ? -0.24F : -0.12F, entity.isOnWall() ? 0.4F : 0.5F);
        model.resetToDefaultPose();
        setRotationAngles(model.getCube("Head"), entity.isOnWall() ? (float) Math.toRadians(50F) : 0F);
        model.getCube("Head").render(matrixStackIn, consumer, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(DragonSkullEntity entity) {
        return IafRegistries.DRAGON_TYPE.get(IceAndFire.id(entity.getDragonType())).getSkeletonTexture(entity.getDragonStage());
    }

    public float getRenderSize(DragonSkullEntity skull) {
        DragonSize size = DragonSize.getSize(skull.getDragonStage());
        float step = size.step() / 25;
        if (skull.getDragonAge() > 125) return size.x0() + ((step * 25));
        return size.x0() + ((step * this.getAgeFactor(skull)));
    }

    private int getAgeFactor(DragonSkullEntity skull) {
        return (skull.getDragonStage() > 1 ? skull.getDragonAge() - (25 * (skull.getDragonStage() - 1)) : skull.getDragonAge());
    }
}
