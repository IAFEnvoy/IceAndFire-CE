package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class MobRenderer<T extends Mob, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends LivingEntityRenderer<T, S, M> {
    public MobRenderer(EntityRendererProvider.Context context, M model, float shadow) {
        super(context, model, shadow);
    }

    protected boolean shouldShowName(T entity, double distanceToCameraSq) {
        return super.shouldShowName(entity, distanceToCameraSq)
            && (entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity);
    }

    @Override
    protected float getShadowRadius(S state) {
        return super.getShadowRadius(state) * state.ageScale;
    }

    protected static boolean checkMagicName(Entity entity, String magicName) {
        Component customName = entity.getCustomName();
        return customName != null && magicName.equals(customName.getString());
    }
}
