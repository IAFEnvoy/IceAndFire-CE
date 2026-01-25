package com.iafenvoy.iceandfire.render.model;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.data.component.MiscData;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class DeathWormGauntletModel extends DragonBaseModel {
    public final AdvancedModelBox Head;
    public final AdvancedModelBox JawExtender;
    public final AdvancedModelBox HeadInner;
    public final AdvancedModelBox ToothB;
    public final AdvancedModelBox ToothT;
    public final AdvancedModelBox ToothL;
    public final AdvancedModelBox ToothL_1;
    public final AdvancedModelBox JawExtender2;
    public final AdvancedModelBox JawExtender3;
    public final AdvancedModelBox JawExtender4;
    public final AdvancedModelBox TopJaw;
    public final AdvancedModelBox BottomJaw;
    public final AdvancedModelBox JawHook;

    public DeathWormGauntletModel() {
        this.texWidth = 128;
        this.texHeight = 64;
        this.Head = new AdvancedModelBox(this, 0, 29);
        this.Head.setPos(0.0F, 0.0F, 1.5F);
        this.Head.addBox(-5.0F, -5.0F, -8.0F, 10, 10, 8, 0.0F);
        this.TopJaw = new AdvancedModelBox(this, 19, 7);
        this.TopJaw.setPos(0.0F, -0.2F, -11.4F);
        this.TopJaw.addBox(-2.0F, -1.5F, -6.4F, 4, 2, 6, 0.0F);
        this.setRotateAngle(this.TopJaw, 0.091106186954104F, 0.0F, 0.0F);
        this.JawHook = new AdvancedModelBox(this, 0, 7);
        this.JawHook.setPos(0.0F, -0.3F, -6.0F);
        this.JawHook.addBox(-0.5F, -0.7F, -2.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(this.JawHook, 1.730144887501979F, 0.0F, 0.0F);
        this.ToothL = new AdvancedModelBox(this, 52, 34);
        this.ToothL.setPos(4.5F, 0.0F, -7.5F);
        this.ToothL.addBox(-0.5F, -0.4F, 0.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(this.ToothL, -3.141592653589793F, 0.3490658503988659F, 0.0F);
        this.ToothB = new AdvancedModelBox(this, 52, 34);
        this.ToothB.setPos(0.0F, 4.5F, -7.5F);
        this.ToothB.addBox(-0.5F, -0.4F, 0.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(this.ToothB, 2.7930504019665254F, -0.0F, 0.0F);
        this.JawExtender = new AdvancedModelBox(this, 0, 7);
        this.JawExtender.setPos(0.0F, 0.0F, 10.0F);
        this.JawExtender.addBox(-1.5F, -1.5F, -13.0F, 3, 3, 13, 0.0F);
        this.BottomJaw = new AdvancedModelBox(this, 40, 7);
        this.BottomJaw.setPos(0.0F, 0.8F, -12.3F);
        this.BottomJaw.addBox(-2.0F, 0.2F, -4.9F, 4, 1, 5, 0.0F);
        this.setRotateAngle(this.BottomJaw, -0.045553093477052F, 0.0F, 0.0F);
        this.ToothT = new AdvancedModelBox(this, 52, 34);
        this.ToothT.setPos(0.0F, -4.5F, -7.5F);
        this.ToothT.addBox(-0.5F, -0.4F, 0.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(this.ToothT, -2.7930504019665254F, -0.0F, 0.0F);
        this.HeadInner = new AdvancedModelBox(this, 0, 48);
        this.HeadInner.setPos(0.0F, 0.0F, -0.3F);
        this.HeadInner.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, 0.0F);
        this.ToothL_1 = new AdvancedModelBox(this, 52, 34);
        this.ToothL_1.setPos(-4.5F, 0.0F, -7.5F);
        this.ToothL_1.addBox(-0.5F, -0.4F, 0.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(this.ToothL_1, -3.141592653589793F, -0.3490658503988659F, 0.0F);
        this.JawExtender2 = new AdvancedModelBox(this, 0, 7);
        this.JawExtender2.setPos(0.0F, 0.0F, 0.0F);
        this.JawExtender2.addBox(-1.5F, -1.5F, -13.0F, 3, 3, 13, 0.0F);
        this.JawExtender3 = new AdvancedModelBox(this, 0, 7);
        this.JawExtender3.setPos(0.0F, 0.0F, 0.0F);
        this.JawExtender3.addBox(-1.5F, -1.5F, -13.0F, 3, 3, 13, 0.0F);
        this.JawExtender4 = new AdvancedModelBox(this, 0, 7);
        this.JawExtender4.setPos(0.0F, 0.0F, 0.0F);
        this.JawExtender4.addBox(-1.5F, -1.5F, -13.0F, 3, 3, 13, 0.0F);
        this.TopJaw.addChild(this.JawHook);
        this.Head.addChild(this.ToothL);
        this.Head.addChild(this.ToothB);
        this.Head.addChild(this.ToothT);
        this.Head.addChild(this.HeadInner);
        this.Head.addChild(this.ToothL_1);
        this.JawExtender.addChild(this.JawExtender2);
        this.JawExtender2.addChild(this.JawExtender3);
        this.JawExtender3.addChild(this.JawExtender4);
        this.JawExtender4.addChild(this.TopJaw);
        this.JawExtender4.addChild(this.BottomJaw);
        this.updateDefaultPose();
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.Head, this.JawExtender);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.Head, this.JawExtender, this.HeadInner, this.ToothB, this.ToothT, this.ToothL, this.ToothL_1, this.JawExtender2, this.JawExtender3, this.JawExtender4, this.TopJaw, this.BottomJaw, this.JawHook);
    }

    public void animate(ItemStack stack, float partialTick) {
        this.resetToDefaultPose();
        assert MinecraftClient.getInstance().world != null;
        Entity holder = MinecraftClient.getInstance().world.getEntityById(stack.getOrDefault(IafDataComponents.USER_ID.get(), -1));
        if (!(holder instanceof LivingEntity livingEntity)) return;

        MiscData miscData = MiscData.get(livingEntity);
        float lungeTicks = miscData.lungeTicks == 0 ? 0 : miscData.lungeTicks + partialTick;
        this.progressRotation(this.TopJaw, lungeTicks, (float) Math.toRadians(-30), 0, 0);
        this.progressRotation(this.BottomJaw, lungeTicks, (float) Math.toRadians(30), 0, 0);
        this.progressPosition(this.JawExtender, lungeTicks, 0, 0, -4);
        this.progressPosition(this.JawExtender2, lungeTicks, 0, 0, -10);
        this.progressPosition(this.JawExtender3, lungeTicks, 0, 0, -10);
        this.progressPosition(this.JawExtender4, lungeTicks, 0, 0, -10);
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
    }
}
