package com.iafenvoy.iceandfire.particle;

import com.iafenvoy.iceandfire.IceAndFire;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ParticleDreadPortal extends SpriteBillboardParticle {
    private static final Identifier SNOWFLAKE = Identifier.of(IceAndFire.MOD_ID, "textures/particle/snowflake_0.png");
    private static final Identifier SNOWFLAKE_BIG = Identifier.of(IceAndFire.MOD_ID, "textures/particle/snowflake_1.png");
    private final boolean big;

    public ParticleDreadPortal(ClientWorld world, double x, double y, double z, double motX, double motY, double motZ) {
        super(world, x, y, z, motX, motY, motZ);
        this.setPos(x, y, z);
        this.big = this.random.nextBoolean();
    }

    @Override
    public void buildGeometry(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        this.scale = 0.125F * (this.maxAge - (this.age));
        this.scale = this.scale * 0.09F;
        if (this.age > this.getMaxAge())
            this.markDead();

        Vec3d Vector3d = renderInfo.getPos();
        float f = (float) (MathHelper.lerp(partialTicks, this.prevPosX, this.x) - Vector3d.getX());
        float f1 = (float) (MathHelper.lerp(partialTicks, this.prevPosY, this.y) - Vector3d.getY());
        float f2 = (float) (MathHelper.lerp(partialTicks, this.prevPosZ, this.z) - Vector3d.getZ());
        Quaternionf quaternion;
        if (this.angle == 0.0F)
            quaternion = renderInfo.getRotation();
        else {
            quaternion = new Quaternionf(renderInfo.getRotation());
            float f3 = MathHelper.lerp(partialTicks, this.prevAngle, this.angle);
            quaternion.mul(RotationAxis.POSITIVE_Z.rotation(f3));
        }

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        quaternion.transform(vector3f1);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = this.getSize(partialTicks);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f = quaternion.transform(vector3f);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }
        float f7 = 0;
        float f8 = 1;
        float f5 = 0;
        float f6 = 1;
        RenderSystem.setShaderTexture(0, this.big ? SNOWFLAKE_BIG : SNOWFLAKE);
        int j = this.getBrightness(partialTicks);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        builder.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).texture(f8, f6).color(this.red, this.green, this.blue, this.alpha).light(j);
        builder.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).texture(f8, f5).color(this.red, this.green, this.blue, this.alpha).light(j);
        builder.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).texture(f7, f5).color(this.red, this.green, this.blue, this.alpha).light(j);
        builder.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).texture(f7, f6).color(this.red, this.green, this.blue, this.alpha).light(j);
        BufferRenderer.drawWithGlobalProgram(builder.end());
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }

    @Override
    public int getBrightness(float partialTick) {
        return 240;
    }
}
