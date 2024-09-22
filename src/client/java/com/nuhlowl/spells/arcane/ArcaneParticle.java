package com.nuhlowl.spells.arcane;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

public class ArcaneParticle extends SpriteBillboardParticle {
    protected ArcaneParticle(
            ClientWorld clientWorld,
            double x,
            double y,
            double z,
            double velocityX,
            double velocityY,
            double velocityZ
    ) {
        super(clientWorld, x, y, z, 0.0, 0.0, 0.0);
        this.velocityMultiplier = 0.7F;
        this.gravityStrength = 0F;

        double speed = new Vec3d(velocityX, velocityY, velocityZ).length();
        Vec3d velocity = new Vec3d(unitRandom(), unitRandom(), unitRandom()).multiply(speed);
        this.velocityX = velocity.x;
        this.velocityY = velocity.y;
        this.velocityZ = velocity.z;
        this.scale *= 0.75F;
        this.maxAge = 7;
        this.collidesWithWorld = true;
        this.tick();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    public static class Factory implements ParticleFactory<ArcaneParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(ArcaneParticleEffect arcaneParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ArcaneParticle damageParticle = new ArcaneParticle(clientWorld, d, e, f, g, h, i);
            damageParticle.setSprite(this.spriteProvider);
            return damageParticle;
        }
    }

    public static double unitRandom() {
        return Math.random() * 2.0 - 1.0;
    }
}
