package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.render.entity.LegacyEntityFeature;
import com.iafenvoy.iceandfire.render.entity.LegacyMobRenderer;
import com.iafenvoy.iceandfire.render.model.BipedBaseModel;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

/**
 * Legacy biped armor layer submitted through the 26.1 render-state collector.
 */
public class BipedArmorFeatureRenderer<T extends Mob & IAnimatedEntity, M extends BipedBaseModel<T>, A extends BipedBaseModel<T>> implements LegacyEntityFeature<T> {
    private final M parentModel;
    private final A modelLeggings;
    private final A modelArmor;
    private final Identifier defaultLegArmor;
    private final Identifier defaultArmor;

    public BipedArmorFeatureRenderer(LegacyMobRenderer<T, M> renderer, A modelLeggings, A modelArmor, Identifier defaultArmor, Identifier defaultLegArmor) {
        this.parentModel = renderer.getLegacyModel();
        this.modelLeggings = modelLeggings;
        this.modelArmor = modelArmor;
        this.defaultLegArmor = defaultLegArmor;
        this.defaultArmor = defaultArmor;
    }

    @Override
    public void submit(T entity, float partialTick, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, int lightCoords, int outlineColor) {
        this.submitEquipment(entity, EquipmentSlot.CHEST, poseStack, collector, lightCoords);
        this.submitEquipment(entity, EquipmentSlot.LEGS, poseStack, collector, lightCoords);
        this.submitEquipment(entity, EquipmentSlot.FEET, poseStack, collector, lightCoords);
        this.submitEquipment(entity, EquipmentSlot.HEAD, poseStack, collector, lightCoords);
    }

    private void submitEquipment(T entity, EquipmentSlot slot, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords) {
        ItemStack stack = entity.getItemBySlot(slot);
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.slot() != slot) return;
        A model = this.getSlotModel(slot);
        this.parentModel.setModelAttributes(model);
        this.setModelSlotVisible(model, slot);
        Identifier texture = this.getArmorResource(entity, stack, slot, null);
        collector.submitCustomGeometry(poseStack, RenderTypes.armorCutoutNoCull(texture), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            model.renderToBuffer(modelStack, buffer, lightCoords, OverlayTexture.NO_OVERLAY, -1);
        });
    }

    protected void setModelSlotVisible(A model, EquipmentSlot slot) {
        model.setVisible(false);
        switch (slot) {
            case HEAD -> {
                model.head.invisible = false;
                model.headware.invisible = false;
            }
            case CHEST -> {
                model.body.invisible = false;
                model.armRight.invisible = false;
                model.armLeft.invisible = false;
            }
            case LEGS -> {
                model.body.invisible = false;
                model.legRight.invisible = false;
                model.legLeft.invisible = false;
            }
            case FEET -> {
                model.legRight.invisible = false;
                model.legLeft.invisible = false;
            }
        }
    }

    private A getSlotModel(EquipmentSlot slot) {
        return this.isLegSlot(slot) ? this.modelLeggings : this.modelArmor;
    }

    protected boolean isLegSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }

    public Identifier getArmorResource(T entity, ItemStack stack, EquipmentSlot slot, String type) {
        return this.isLegSlot(slot) ? this.defaultLegArmor : this.defaultArmor;
    }
}
