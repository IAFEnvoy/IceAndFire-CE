package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.data.HippogryphType;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class HippogryphEggEntity extends EggEntity {

    private ItemStack itemstack;

    public HippogryphEggEntity(EntityType<? extends EggEntity> type, World world) {
        super(type, world);
    }

    public HippogryphEggEntity(EntityType<? extends EggEntity> type, World worldIn, double x, double y, double z, ItemStack stack) {
        this(type, worldIn);
        this.setPosition(x, y, z);
        this.itemstack = stack;
    }

    public HippogryphEggEntity(EntityType<? extends EggEntity> type, World worldIn, LivingEntity throwerIn, ItemStack stack) {
        this(type, worldIn);
        this.setPosition(throwerIn.getX(), throwerIn.getEyeY() - 0.1F, throwerIn.getZ());
        this.itemstack = stack;
        this.setOwner(throwerIn);
    }

    @Override
    public void handleStatus(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                this.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()), this.getX(), this.getY(), this.getZ(), (this.random.nextFloat() - 0.5D) * 0.08D, (this.random.nextFloat() - 0.5D) * 0.08D, (this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    @Override
    protected void onCollision(HitResult result) {
        Entity thrower = this.getOwner();
        if (result instanceof EntityHitResult hitResult)
            hitResult.getEntity().damage(this.getWorld().getDamageSources().thrown(this, thrower), 0.0F);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            HippogryphEntity hippogryph = new HippogryphEntity(IafEntities.HIPPOGRYPH.get(), this.getWorld());
            hippogryph.setBreedingAge(-24000);
            hippogryph.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
            hippogryph.initialize(serverWorld, serverWorld.getLocalDifficulty(this.getBlockPos()), SpawnReason.SPAWN_EGG, null);
            if (this.itemstack != null) {
                HippogryphType variant = this.itemstack.get(IafDataComponents.HIPPOGRYPH_EGG.get());
                if (variant != null) hippogryph.setVariant(variant);
            }
            if (thrower instanceof PlayerEntity player)
                hippogryph.setOwner(player);
            this.getWorld().spawnEntity(hippogryph);
        }

        this.getWorld().sendEntityStatus(this, (byte) 3);
        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    protected Item getDefaultItem() {
        return IafItems.HIPPOGRYPH_EGG.get();
    }
}
