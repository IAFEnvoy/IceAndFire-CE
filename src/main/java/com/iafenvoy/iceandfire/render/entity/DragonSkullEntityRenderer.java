package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.DragonSkullEntity;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonSize;
import com.iafenvoy.iceandfire.event.CollectDragonSkullModelEvent;
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
import com.iafenvoy.uranus.util.function.MemorizeSupplier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public class DragonSkullEntityRenderer extends EntityRenderer<DragonSkullEntity, LegacyEntityRenderState<DragonSkullEntity>> {
    private final Map<DragonType, Pair<Identifier, MemorizeSupplier<ITabulaModelAnimator<? extends DragonBaseEntity>>>> models = new HashMap<>();

    public DragonSkullEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.models.put(IafDragonTypes.FIRE, Pair.of(IafRenderers.FIRE_DRAGON, new MemorizeSupplier<>(FireDragonTabulaModelAnimator::new)));
        this.models.put(IafDragonTypes.ICE, Pair.of(IafRenderers.ICE_DRAGON, new MemorizeSupplier<>(IceDragonTabulaModelAnimator::new)));
        this.models.put(IafDragonTypes.LIGHTNING, Pair.of(IafRenderers.LIGHTNING_DRAGON, new MemorizeSupplier<>(LightningTabulaDragonAnimator::new)));
        NeoForge.EVENT_BUS.post(new CollectDragonSkullModelEvent(this.models));
    }

    private static void setRotationAngles(BasicModelPart cube, float rotX) {
        cube.rotateAngleX = rotX;
        cube.rotateAngleY = 0;
        cube.rotateAngleZ = 0;
    }

    @Override
    public LegacyEntityRenderState<DragonSkullEntity> createRenderState() {
        return new LegacyEntityRenderState<>();
    }

    @Override
    public void extractRenderState(DragonSkullEntity entity, LegacyEntityRenderState<DragonSkullEntity> state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.entity = entity;
    }

    @Override
    public void submit(LegacyEntityRenderState<DragonSkullEntity> state, @NonNull PoseStack matrixStackIn, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        DragonSkullEntity entity = state.entity;
        Pair<Identifier, MemorizeSupplier<ITabulaModelAnimator<? extends DragonBaseEntity>>> p = this.models.get(IafRegistries.DRAGON_TYPE.get(IceAndFire.id(entity.getDragonType())).map(Holder.Reference::value).orElse(IafDragonTypes.FIRE));
        if (p == null) return;
        TabulaModel<? extends DragonBaseEntity> model = TabulaModelHandlerHelper.getModel(p.getFirst());
        if (model == null) return;
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180.0F));
        matrixStackIn.mulPose(Axis.YN.rotationDegrees(-180.0F - entity.getYRot()));
        matrixStackIn.scale(1.0F, 1.0F, 1.0F);
        float size = this.getRenderSize(entity) / 3;
        matrixStackIn.scale(size, size, size);
        matrixStackIn.translate(0, entity.isOnWall() ? -0.24F : -0.12F, entity.isOnWall() ? 0.4F : 0.5F);
        model.resetToDefaultPose();
        setRotationAngles(model.getCube("Head"), entity.isOnWall() ? (float) Math.toRadians(50F) : 0F);
        collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityTranslucent(this.getTextureLocation(entity)), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            model.getCube("Head").render(modelStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
        });
        matrixStackIn.popPose();
        super.submit(state, matrixStackIn, collector, camera);
    }

    public @NotNull Identifier getTextureLocation(DragonSkullEntity entity) {
        return IafRegistries.DRAGON_TYPE.get(IceAndFire.id(entity.getDragonType())).map(Holder.Reference::value).orElse(IafDragonTypes.FIRE).getSkeletonTexture(entity.getDragonStage());
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
