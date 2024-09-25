package com.nuhlowl.spells.arcane;

import com.nuhlowl.MiningMagic;
import com.nuhlowl.spells.ShotSpellEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;

public class ArcaneShotEntity extends ShotSpellEntity {
    private int damage;

    public ArcaneShotEntity(EntityType<? extends ArcaneShotEntity> entityType, World world) {
        super(entityType, world);
        this.accelerationPower = 0.0;
        this.damage = 0;
    }

    public ArcaneShotEntity(World world, double x, double y, double z, int damage) {
        super(MiningMagic.ARCANE_SHOT_ENTITY, world, x, y, z);
        this.damage = damage;
    }

    @Override
    public ParticleEffect getTrailEffect() {
        return new ArcaneParticleEffect(1, ArcaneParticleEffect.ArcaneType.TRAIL);
    }

    @Override
    public ParticleEffect getHitEffect() {
        return new ArcaneParticleEffect(
                1,
                ArcaneParticleEffect.ArcaneType.SPARK
        );
    }

    @Override
    public Identifier getTexture() {
        return Identifier.of(MiningMagic.MOD_ID, "textures/magic/spells/arcane_shot.png");
    }

    @Override
    public int getColor() {
        return ColorHelper.Argb.getArgb(255, 255, 255, 255);
    }

    @Override
    public void hitTarget(LivingEntity target) {
        DamageSource damageSource = this.getDamageSources().magic();
        if (target.damage(damageSource, this.damage)) {
            EnchantmentHelper.onTargetDamaged((ServerWorld) this.getWorld(), target, damageSource);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("spell_damage", this.damage);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("spell_damage", NbtElement.INT_TYPE)) {
            this.damage = nbt.getInt("spell_damage");
        }
    }
}
