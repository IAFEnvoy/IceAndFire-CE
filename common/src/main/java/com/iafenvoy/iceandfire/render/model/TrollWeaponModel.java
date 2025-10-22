package com.iafenvoy.iceandfire.render.model;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import net.minecraft.entity.Entity;

public class TrollWeaponModel extends AdvancedEntityModel<Entity> {
    public final AdvancedModelBox log1;
    public final AdvancedModelBox log2;
    public final AdvancedModelBox handle;
    public final AdvancedModelBox column;
    public final AdvancedModelBox blade1;
    public final AdvancedModelBox blade2;
    public final AdvancedModelBox blade2_1;
    public final AdvancedModelBox block;
    public final AdvancedModelBox blade2_2;
    public final AdvancedModelBox bottom;
    public final AdvancedModelBox top;

    public TrollWeaponModel() {
        this.texWidth = 256;
        this.texHeight = 128;
        this.block = new AdvancedModelBox(this, 182, 19);
        this.block.setPos(0.0F, 0.0F, 0.0F);
        this.block.addBox(-2.0F, 11.0F, -8.0F, 4, 10, 15, 0.0F);
        this.bottom = new AdvancedModelBox(this, 177, 0);
        this.bottom.setPos(-1.0F, 0.0F, 0.0F);
        this.bottom.addBox(-1.5F, -21.0F, -3.5F, 10, 4, 10, 0.0F);
        this.blade2 = new AdvancedModelBox(this, 186, 66);
        this.blade2.setPos(0.0F, 7.0F, -1.0F);
        this.blade2.addBox(-1.0F, 1.0F, -1.0F, 2, 2, 9, 0.0F);
        this.column = new AdvancedModelBox(this, 220, 28);
        this.column.setPos(-2.0F, 0.0F, -3.0F);
        this.column.addBox(-1.5F, -20.0F, -2.5F, 8, 42, 8, 0.0F);
        this.blade1 = new AdvancedModelBox(this, 186, 84);
        this.blade1.setPos(0.0F, 0.0F, -2.0F);
        this.blade1.addBox(-1.0F, 12.0F, 0.0F, 2, 10, 10, 0.0F);
        this.handle = new AdvancedModelBox(this, 232, 80);
        this.handle.setPos(0.3F, 0.0F, 1.0F);
        this.handle.addBox(-1.5F, -20.0F, -1.5F, 3, 42, 3, 0.0F);
        this.blade2_1 = new AdvancedModelBox(this, 189, 69);
        this.blade2_1.setPos(0.0F, 2.0F, -1.0F);
        this.blade2_1.addBox(-1.0F, 2.0F, -1.0F, 2, 2, 6, 0.0F);
        this.top = new AdvancedModelBox(this, 177, 0);
        this.top.setPos(-1.0F, 0.0F, 0.0F);
        this.top.addBox(-1.5F, 20.0F, -3.5F, 10, 4, 10, 0.0F);
        this.log1 = new AdvancedModelBox(this, 10, 100);
        this.log1.setPos(-0.5F, 8.0F, -0.4F);
        this.log1.addBox(-1.5F, -17.9F, -3.5F, 6, 20, 6, 0.0F);
        this.setRotateAngle(this.log1, 3.141592653589793F, 0.0F, 0.0F);
        this.log2 = new AdvancedModelBox(this, 10, 70);
        this.log2.setPos(0.0F, 9.0F, -1.0F);
        this.log2.addBox(-2.0F, -6.9F, -3.0F, 7, 24, 6, 0.0F);
        this.setRotateAngle(this.log2, -0.045553093477052F, 0.0F, 0.0F);
        this.blade2_2 = new AdvancedModelBox(this, 161, 99);
        this.blade2_2.setPos(0.0F, 17.0F, 9.0F);
        this.blade2_2.addBox(-1.0F, -5.0F, -6.0F, 2, 5, 5, 0.0F);
        this.setRotateAngle(this.blade2_2, 3.141592653589793F, 0.0F, 0.0F);
        this.handle.addChild(this.block);
        this.column.addChild(this.bottom);
        this.handle.addChild(this.blade2);
        this.log1.addChild(this.column);
        this.handle.addChild(this.blade1);
        this.log1.addChild(this.handle);
        this.handle.addChild(this.blade2_1);
        this.column.addChild(this.top);
        this.log1.addChild(this.log2);
        this.blade1.addChild(this.blade2_2);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.log1);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.log1);
    }

}
