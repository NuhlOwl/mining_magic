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
import net.minecraft.util.StringIdentifiable;

public class ArcaneParticleEffect implements ParticleEffect {
    public enum ArcaneType implements StringIdentifiable {
        TRAIL("trail", MiningMagic.ARCANE_TRAIL_PARTICLE),
        SPARK("spark", MiningMagic.ARCANE_SPARK_PARTICLE),;

        private final String name;
        private final ParticleType<ArcaneParticleEffect> particleType;

        ArcaneType(String name, ParticleType<ArcaneParticleEffect> particleType) {
            this.name = name;
            this.particleType = particleType;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public ParticleType<ArcaneParticleEffect> getParticleType() {
            return this.particleType;
        }

        static ArcaneType fromString(String name) {
            for (ArcaneType type : ArcaneType.values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }

            return null;
        }
    }
    public static final MapCodec<ArcaneParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.INT.fieldOf("strength")
                    .forGetter(ArcaneParticleEffect::getStrength),
                            Codec.STRING.fieldOf("type")
                                    .forGetter(ArcaneParticleEffect::getArcaneType))
                    .apply(instance, ArcaneParticleEffect::new)
    );
    public static final PacketCodec<RegistryByteBuf, ArcaneParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            ArcaneParticleEffect::getStrength,
            PacketCodecs.STRING,
            ArcaneParticleEffect::getArcaneType,
            ArcaneParticleEffect::new
    );

    private final int strength;
    private final ArcaneType type;

    private ArcaneParticleEffect(int strength, String type) {
        this(strength, ArcaneType.fromString(type));
    }

    public ArcaneParticleEffect(int strength, ArcaneType type) {
        this.strength = strength;
        this.type = type;
    }

    @Override
    public ParticleType<ArcaneParticleEffect> getType() {
        return this.type.getParticleType();
    }

    public int getStrength() {
        return strength;
    }

    public String getArcaneType() {
        return type.asString();
    }
}
