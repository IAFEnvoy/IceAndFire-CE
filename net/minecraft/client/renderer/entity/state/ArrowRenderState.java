package net.minecraft.client.renderer.entity.state;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArrowRenderState extends EntityRenderState {
    public float xRot;
    public float yRot;
    public float shake;
}
