package com.nuhlowl.spells;

import com.nuhlowl.MiningMagic;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class ShotSpellEntity extends ExplosiveProjectileEntity {
    public static final float DAMAGE = 1.0F;

    public ShotSpellEntity(EntityType<? extends ShotSpellEntity> entityType, World world) {
        super(entityType, world);
        this.accelerationPower = 0.0;
    }

    public ShotSpellEntity(EntityType<? extends ShotSpellEntity> entityType, World world, double x, double y, double z) {
        super(entityType, x, y, z, world);
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
                this.getPos().x - (double) f,
                this.getPos().y - 0.15F,
                this.getPos().z - (double) f,
                this.getPos().x + (double) f,
                this.getPos().y - 0.15F + (double) g,
                this.getPos().z + (double) f
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
                EnchantmentHelper.onTargetDamaged((ServerWorld) this.getWorld(), livingEntity, damageSource);
            }

            this.createHitParticles(getPos());
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        if (!this.getWorld().isClient) {
            this.discard();

            Vec3i hitNormal = blockHitResult.getSide().getVector();
            Vec3d spawnNormal = Vec3d.of(hitNormal).multiply(0.25, 0.25, 0.25);
            Vec3d spawnPoint = blockHitResult.getPos().add(spawnNormal);
            this.createHitParticles(spawnPoint);
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
        super.tick();

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            if (this.getBlockY() > this.getWorld().getTopY() + 30) {
                this.discard();
                return;
            }

            ParticleEffect particleEffect = this.getTrailEffect();
            if (particleEffect != null) {
                Vec3d velocity = this.getVelocity();
                double particleX = this.getX() + velocity.x;
                double particleY = this.getY() + velocity.y;
                double particleZ = this.getZ() + velocity.z;
                velocity = velocity.multiply(.25);

                serverWorld.spawnParticles(
                        this.getHitEffect(),
                        particleX, particleY, particleZ,
                        10,
                        velocity.x, velocity.y, velocity.z,
                        velocity.length());
            }
        }
    }

    private void createHitParticles(Vec3d pos) {
        Vec3d velocity = this.getVelocity().multiply(.5);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    this.getHitEffect(),
                    pos.x, pos.y, pos.z,
                    20,
                    velocity.x, velocity.y, velocity.z,
                    velocity.length());
        }
    }

    abstract public ParticleEffect getTrailEffect();
    abstract public ParticleEffect getHitEffect();
    abstract public Identifier getTexture();
    abstract public int getColor();
}
