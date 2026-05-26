package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.render.model.BipedBaseModel;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

//TODO: Consider support for default minecraft armors/ dynamically selecting custom armors
//Base code from minecraft's ArmorBipedLayer
public class BipedArmorFeatureRenderer<T extends LivingEntity & IAnimatedEntity, M extends BipedBaseModel<T>, A extends BipedBaseModel<T>> extends RenderLayer<T, M> {
    private final A modelLeggings;
    private final A modelArmor;
    private final ResourceLocation defaultLegArmor;
    private final ResourceLocation defaultArmor;

    public BipedArmorFeatureRenderer(RenderLayerParent<T, M> mobRenderer, A modelLeggings, A modelArmor, ResourceLocation defaultArmor, ResourceLocation defaultLegArmor) {
        super(mobRenderer);
        this.modelLeggings = modelLeggings;
        this.modelArmor = modelArmor;
        this.defaultLegArmor = defaultLegArmor;
        this.defaultArmor = defaultArmor;
    }

    @Override
    public void render(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, @NotNull T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderEquipment(matrixStackIn, bufferIn, entitylivingbaseIn, EquipmentSlot.CHEST, packedLightIn, this.getSlotModel(EquipmentSlot.CHEST));
        this.renderEquipment(matrixStackIn, bufferIn, entitylivingbaseIn, EquipmentSlot.LEGS, packedLightIn, this.getSlotModel(EquipmentSlot.LEGS));
        this.renderEquipment(matrixStackIn, bufferIn, entitylivingbaseIn, EquipmentSlot.FEET, packedLightIn, this.getSlotModel(EquipmentSlot.FEET));
        this.renderEquipment(matrixStackIn, bufferIn, entitylivingbaseIn, EquipmentSlot.HEAD, packedLightIn, this.getSlotModel(EquipmentSlot.HEAD));
    }

    private void renderEquipment(PoseStack matrixStackIn, MultiBufferSource bufferIn, T entityIn, EquipmentSlot slotType, int packedLightIn, A modelIn) {
        ItemStack itemstack = entityIn.getItemBySlot(slotType);
        if (itemstack.getItem() instanceof ArmorItem armoritem)
            if (armoritem.getEquipmentSlot() == slotType) {
                this.getParentModel().setModelAttributes(modelIn);
                this.setModelSlotVisible(modelIn, slotType);
                boolean flag1 = itemstack.hasFoil();
                this.renderArmorItem(matrixStackIn, bufferIn, packedLightIn, flag1, modelIn, this.getArmorResource(entityIn, itemstack, slotType, null));
            }
    }

    protected void setModelSlotVisible(A modelIn, EquipmentSlot slotIn) {
        modelIn.setVisible(false);
        switch (slotIn) {
            case HEAD -> {
                modelIn.head.invisible = false;
                modelIn.headware.invisible = false;
            }
            case CHEST -> {
                modelIn.body.invisible = false;
                modelIn.armRight.invisible = false;
                modelIn.armLeft.invisible = false;
            }
            case LEGS -> {
                modelIn.body.invisible = false;
                modelIn.legRight.invisible = false;
                modelIn.legLeft.invisible = false;
            }
            case FEET -> {
                modelIn.legRight.invisible = false;
                modelIn.legLeft.invisible = false;
            }
        }
    }

    private void renderArmorItem(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, boolean glint, A modelIn, ResourceLocation armorResource) {
        VertexConsumer ivertexbuilder = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(armorResource), glint);
        modelIn.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
    }

    private A getSlotModel(EquipmentSlot equipmentSlotType) {
        return this.isLegSlot(equipmentSlotType) ? this.modelLeggings : this.modelArmor;
    }

    protected boolean isLegSlot(EquipmentSlot slotIn) {
        return slotIn == EquipmentSlot.LEGS;
    }

    public ResourceLocation getArmorResource(T entity, ItemStack stack, EquipmentSlot slot, String type) {
        if (this.isLegSlot(slot)) return this.defaultLegArmor;
        return this.defaultArmor;
    }
}
