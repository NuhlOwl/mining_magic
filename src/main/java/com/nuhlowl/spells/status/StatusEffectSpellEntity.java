package com.nuhlowl.spells.status;

import com.nuhlowl.MiningMagic;
import com.nuhlowl.spells.ShotSpellEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;

public class StatusEffectSpellEntity extends ShotSpellEntity {
    private StatusEffectInstance effect;

    public StatusEffectSpellEntity(EntityType<? extends StatusEffectSpellEntity> entityType, World world) {
        super(entityType, world);
        this.accelerationPower = 0.0;
    }

    public StatusEffectSpellEntity(
            StatusEffectInstance statusEffect,
            World world,
            double x,
            double y,
            double z
    ) {
        super(MiningMagic.STATUS_EFFECT_SPELL_ENTITY, world, x, y, z);
        this.effect = statusEffect;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("status_effect", effect.writeNbt());
        MiningMagic.LOGGER.info("wrote nbt {}", this.effect);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.effect = StatusEffectInstance.fromNbt(nbt.getCompound("status_effect"));
        MiningMagic.LOGGER.info("read nbt {}", this.effect);
    }

    @Override
    public ParticleEffect getTrailEffect() {
        return effect.getEffectType().value().createParticle(effect);
    }

    @Override
    public ParticleEffect getHitEffect() {
        return effect.getEffectType().value().createParticle(effect);
    }

    @Override
    public Identifier getTexture() {
        return Identifier.of(MiningMagic.MOD_ID, "textures/magic/spells/arcane_shot_bw.png");
    }

    @Override
    public int getColor() {
        int color = effect.getEffectType().value().getColor();
        return ColorHelper.Argb.getArgb(255,
                ColorHelper.Argb.getRed(color),
                ColorHelper.Argb.getGreen(color),
                ColorHelper.Argb.getBlue(color)
        );
    }

    @Override
    public void hitTarget(LivingEntity target) {
        target.addStatusEffect(new StatusEffectInstance(effect), this.getOwner());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);

        String[] parts = this.uuid.toString().split("-");
        StatusEffectSpell.StatusId id = StatusEffectSpell.StatusId.fromId(parts[1]);
        if (id != null) {
            this.effect = new StatusEffectInstance(
                    StatusEffectSpell.uuidPartToStatusKey.get(id),
                    0,
                    0
            );
        }
    }
}
