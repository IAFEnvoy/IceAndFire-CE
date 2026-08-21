package net.minecraft.client.model;

import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EntityModel<T extends EntityRenderState> extends Model<T> {
    public static final float MODEL_Y_OFFSET = -1.501F;

    protected EntityModel(ModelPart root) {
        this(root, RenderTypes::entityCutout);
    }

    protected EntityModel(ModelPart root, Function<Identifier, RenderType> renderType) {
        super(root, renderType);
    }
}
