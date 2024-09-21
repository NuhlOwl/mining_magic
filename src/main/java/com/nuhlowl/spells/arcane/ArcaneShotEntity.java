package com.nuhlowl.spells.arcane;

import com.nuhlowl.MiningMagic;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ArcaneShotEntity extends ExplosiveProjectileEntity {
    public static final float DAMAGE = 1.0F;

    private ArcaneShotEntity(EntityType<ArcaneShotEntity> entityType, World world) {
        super(entityType, world);
        this.accelerationPower = 0.0;
    }

    public ArcaneShotEntity(World world, double x, double y, double z, Vec3d velocity) {
        super(MiningMagic.ARCANE_SHOT_ENTITY, x, y, z, velocity, world);
    }

    public static ArcaneShotEntity create(EntityType<ArcaneShotEntity> entityType, World world) {
        return new ArcaneShotEntity(entityType, world);
    }

    @Override
    protected boolean isBurning() {
        return false;
    }

    @Override
    protected @Nullable ParticleEffect getParticleType() {
        return null;
    }

    @Override
    protected Box calculateBoundingBox() {
        float f = this.getType().getDimensions().width() / 2.0F;
        float g = this.getType().getDimensions().height();
        return new Box(
                this.getPos().x - (double)f,
                this.getPos().y - 0.15F,
                this.getPos().z - (double)f,
                this.getPos().x + (double)f,
                this.getPos().y - 0.15F + (double)g,
                this.getPos().z + (double)f
        );
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!this.getWorld().isClient) {
            LivingEntity caster = this.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null;
            Entity hitEntity = entityHitResult.getEntity();
            if (caster != null) {
                caster.onAttacking(hitEntity);
            }

            DamageSource damageSource = this.getDamageSources().magic();
            if (hitEntity.damage(damageSource, DAMAGE) && hitEntity instanceof LivingEntity livingEntity) {
                EnchantmentHelper.onTargetDamaged((ServerWorld)this.getWorld(), livingEntity, damageSource);
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.getWorld().isClient) {
//            Vec3i vec3i = blockHitResult.getSide().getVector();
//            Vec3d vec3d = Vec3d.of(vec3i).multiply(0.25, 0.25, 0.25);
//            Vec3d vec3d2 = blockHitResult.getPos().add(vec3d);
            this.discard();
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.discard();
        }
    }

    @Override
    public void tick() {
        if (!this.getWorld().isClient && this.getBlockY() > this.getWorld().getTopY() + 30) {
            this.discard();
        } else {
            super.tick();
        }
    }
}
