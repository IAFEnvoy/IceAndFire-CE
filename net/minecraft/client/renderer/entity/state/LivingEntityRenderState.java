package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class LivingEntityRenderState extends EntityRenderState {
    public float bodyRot;
    public float yRot;
    public float xRot;
    public float deathTime;
    public float walkAnimationPos;
    public float walkAnimationSpeed;
    public float scale = 1.0F;
    public float ageScale = 1.0F;
    public float ticksSinceKineticHitFeedback;
    public boolean isUpsideDown;
    public boolean isFullyFrozen;
    public boolean isBaby;
    public boolean isInWater;
    public boolean isAutoSpinAttack;
    public boolean hasRedOverlay;
    public boolean isInvisibleToPlayer;
    public @Nullable Direction bedOrientation;
    public Pose pose = Pose.STANDING;
    public final ItemStackRenderState headItem = new ItemStackRenderState();
    public float wornHeadAnimationPos;
    public SkullBlock.@Nullable Type wornHeadType;
    public @Nullable ResolvableProfile wornHeadProfile;

    public boolean hasPose(Pose pose) {
        return this.pose == pose;
    }
}
