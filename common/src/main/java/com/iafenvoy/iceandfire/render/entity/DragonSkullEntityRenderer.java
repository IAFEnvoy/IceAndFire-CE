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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.RotationAxis;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DragonSkullEntityRenderer extends EntityRenderer<DragonSkullEntity> {
    public static final Event<Consumer<BiConsumer<DragonType, net.minecraft.util.Pair<Identifier, MemorizeSupplier<ITabulaModelAnimator<? extends DragonBaseEntity>>>>>> COLLECT_DRAGON_SKULL_MODELS = new Event<>(callbacks -> consumer -> callbacks.forEach(x -> x.accept(consumer)));
    private final Map<DragonType, Pair<Identifier, MemorizeSupplier<ITabulaModelAnimator<? extends DragonBaseEntity>>>> models = new HashMap<>();

    static {
        COLLECT_DRAGON_SKULL_MODELS.register(consumer -> {
            consumer.accept(IafDragonTypes.FIRE, new Pair<>(IafRenderers.FIRE_DRAGON, new MemorizeSupplier<>(FireDragonTabulaModelAnimator::new)));
            consumer.accept(IafDragonTypes.ICE, new Pair<>(IafRenderers.ICE_DRAGON, new MemorizeSupplier<>(IceDragonTabulaModelAnimator::new)));
            consumer.accept(IafDragonTypes.LIGHTNING, new Pair<>(IafRenderers.LIGHTNING_DRAGON, new MemorizeSupplier<>(LightningTabulaDragonAnimator::new)));
        });
    }

    public DragonSkullEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        COLLECT_DRAGON_SKULL_MODELS.invoker().accept(this.models::put);
    }

    private static void setRotationAngles(BasicModelPart cube, float rotX) {
        cube.rotateAngleX = rotX;
        cube.rotateAngleY = (float) 0;
        cube.rotateAngleZ = (float) 0;
    }

    @Override
    public void render(DragonSkullEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn) {
        Pair<Identifier, MemorizeSupplier<ITabulaModelAnimator<? extends DragonBaseEntity>>> p = this.models.get(IafRegistries.DRAGON_TYPE.get(IceAndFire.id(entity.getDragonType())));
        if (p == null) return;
        TabulaModel<? extends DragonBaseEntity> model = TabulaModelHandlerHelper.getModel(p.getLeft());
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
        DragonSize size = DragonSize.getSize(skull.getDragonStage());
        float step = size.step() / 25;
        if (skull.getDragonAge() > 125) return size.x0() + ((step * 25));
        return size.x0() + ((step * this.getAgeFactor(skull)));
    }

    private int getAgeFactor(DragonSkullEntity skull) {
        return (skull.getDragonStage() > 1 ? skull.getDragonAge() - (25 * (skull.getDragonStage() - 1)) : skull.getDragonAge());
    }
}
