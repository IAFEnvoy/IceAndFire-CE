package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.data.HippogryphType;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class HippogryphEggEntity extends ThrownEgg {

    private ItemStack itemstack;

    public HippogryphEggEntity(EntityType<? extends ThrownEgg> type, Level world) {
        super(type, world);
    }

    public HippogryphEggEntity(EntityType<? extends ThrownEgg> type, Level worldIn, double x, double y, double z, ItemStack stack) {
        this(type, worldIn);
        this.setPos(x, y, z);
        this.itemstack = stack;
    }

    public HippogryphEggEntity(EntityType<? extends ThrownEgg> type, Level worldIn, LivingEntity throwerIn, ItemStack stack) {
        this(type, worldIn);
        this.setPos(throwerIn.getX(), throwerIn.getEyeY() - 0.1F, throwerIn.getZ());
        this.itemstack = stack;
        this.setOwner(throwerIn);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), (this.random.nextFloat() - 0.5D) * 0.08D, (this.random.nextFloat() - 0.5D) * 0.08D, (this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        Entity thrower = this.getOwner();
        if (result instanceof EntityHitResult hitResult)
            hitResult.getEntity().hurt(this.level().damageSources().thrown(this, thrower), 0.0F);

        if (this.level() instanceof ServerLevel serverWorld) {
            HippogryphEntity hippogryph = new HippogryphEntity(IafEntities.HIPPOGRYPH.get(), this.level());
            hippogryph.setAge(-24000);
            hippogryph.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            hippogryph.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.SPAWN_EGG, null);
            if (this.itemstack != null) {
                HippogryphType variant = this.itemstack.get(IafDataComponents.HIPPOGRYPH_EGG.get());
                if (variant != null) hippogryph.setVariant(variant);
            }
            if (thrower instanceof Player player)
                hippogryph.tame(player);
            this.level().addFreshEntity(hippogryph);
        }

        this.level().broadcastEntityEvent(this, (byte) 3);
        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return IafItems.HIPPOGRYPH_EGG.get();
    }
}
