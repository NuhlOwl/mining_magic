package com.nuhlowl.spells.arcane;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nuhlowl.MiningMagic;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class ArcaneParticleEffect implements ParticleEffect {
    public static final MapCodec<ArcaneParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.INT.fieldOf("strength")
                    .forGetter(ArcaneParticleEffect::getStrength))
                    .apply(instance, ArcaneParticleEffect::new)
    );
    public static final PacketCodec<RegistryByteBuf, ArcaneParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            ArcaneParticleEffect::getStrength,
            ArcaneParticleEffect::new
    );

    private int strength;

    public ArcaneParticleEffect(int strength) {
        this.strength = strength;
    }

    @Override
    public ParticleType<ArcaneParticleEffect> getType() {
        return MiningMagic.ARCANE_TRAIL_PARTICLE;
    }

    public int getStrength() {
        return strength;
    }
}
