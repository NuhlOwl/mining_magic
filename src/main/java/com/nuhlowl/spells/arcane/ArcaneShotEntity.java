package com.nuhlowl.spells.arcane;

import com.nuhlowl.MiningMagic;
import com.nuhlowl.spells.ShotSpellEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;

public class ArcaneShotEntity extends ShotSpellEntity {

    public ArcaneShotEntity(EntityType<? extends ArcaneShotEntity> entityType, World world) {
        super(entityType, world);
        this.accelerationPower = 0.0;
    }

    public ArcaneShotEntity(World world, double x, double y, double z) {
        super(MiningMagic.ARCANE_SHOT_ENTITY, world, x, y, z);
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
}
