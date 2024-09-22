package com.nuhlowl.spells.arcane;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;

public class ArcaneParticle extends SpriteBillboardParticle {
    protected ArcaneParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, 0.0, 0.0, 0.0);
        this.velocityMultiplier = 0.7F;
        this.gravityStrength = 0.5F;
        this.velocityX *= 0.1F;
        this.velocityY *= 0.1F;
        this.velocityZ *= 0.1F;
        this.velocityX += g * 0.4;
        this.velocityY += h * 0.4;
        this.velocityZ += i * 0.4;
        float j = (float)(Math.random() * 0.3F + 0.6F);
        this.red = j;
        this.green = j;
        this.blue = j;
        this.scale *= 0.75F;
        this.maxAge = Math.max((int)(6.0 / (Math.random() * 0.8 + 0.6)), 1);
        this.collidesWithWorld = false;
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
}
