package com.nuhlowl.spells.status;

import com.nuhlowl.spells.ShotSpellEntity;
import com.nuhlowl.spells.arcane.ArcaneShotSpell;
import net.minecraft.SharedConstants;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class StatusEffectSpell extends ArcaneShotSpell {

    public enum StatusId {
        POISON_ID("0001"),
        SPEED_ID("0002"),
        HASTE_ID("0003"),
        INSTANT_DAMAGE_ID("0004"),
        INSTANT_HEALTH_ID("0005"),
        JUMP_BOOST_ID("0006"),
        REGENERATION_ID("0007"),
        RESISTANCE_ID("0008"),
        FIRE_RESISTANCE_ID("0009"),
        WATER_BREATHING_ID("0010"),
        INVISIBILITY_ID("0011"),
        BLINDNESS_ID("0012"),
        NIGHT_VISION_ID("0013"),
        WEAKNESS_ID("0014"),
        WITHER_ID("0015"),
        HEALTH_BOOST_ID("0016"),
        ABSORPTION_ID("0017"),
        LEVITATION_ID("0018"),
        SLOW_FALLING_ID("0019"),
        GLOWING_ID("0020"),
        WEAVING_ID("0021"),
        OOZING_ID("0022"),
        INFESTED_ID("0023"),
        WIND_CHARGED_ID("0024");

        private String id;

        StatusId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static StatusId fromId(String id) {
            for (StatusId status : StatusId.values()) {
                if (status.getId().equals(id)) {
                    return status;
                }
            }

            return null;
        }
    }

    public static final HashMap<StatusId, RegistryEntry<StatusEffect>> uuidPartToStatusKey = new HashMap<>() {{
        put(StatusId.POISON_ID, StatusEffects.POISON);
        put(StatusId.SPEED_ID, StatusEffects.SPEED);
        put(StatusId.HASTE_ID, StatusEffects.HASTE);
        put(StatusId.INSTANT_DAMAGE_ID, StatusEffects.INSTANT_DAMAGE);
        put(StatusId.INSTANT_HEALTH_ID, StatusEffects.INSTANT_HEALTH);
        put(StatusId.JUMP_BOOST_ID, StatusEffects.JUMP_BOOST);
        put(StatusId.REGENERATION_ID, StatusEffects.REGENERATION);
        put(StatusId.RESISTANCE_ID, StatusEffects.RESISTANCE);
        put(StatusId.FIRE_RESISTANCE_ID, StatusEffects.FIRE_RESISTANCE);
        put(StatusId.WATER_BREATHING_ID, StatusEffects.WATER_BREATHING);
        put(StatusId.INVISIBILITY_ID, StatusEffects.INVISIBILITY);
        put(StatusId.BLINDNESS_ID, StatusEffects.BLINDNESS);
        put(StatusId.NIGHT_VISION_ID, StatusEffects.NIGHT_VISION);
        put(StatusId.WEAKNESS_ID, StatusEffects.WEAKNESS);
        put(StatusId.WITHER_ID, StatusEffects.WITHER);
        put(StatusId.HEALTH_BOOST_ID, StatusEffects.HEALTH_BOOST);
        put(StatusId.ABSORPTION_ID, StatusEffects.ABSORPTION);
        put(StatusId.LEVITATION_ID, StatusEffects.LEVITATION);
        put(StatusId.SLOW_FALLING_ID, StatusEffects.SLOW_FALLING);
        put(StatusId.GLOWING_ID, StatusEffects.GLOWING);
        put(StatusId.WEAVING_ID, StatusEffects.WEAVING);
        put(StatusId.OOZING_ID, StatusEffects.OOZING);
        put(StatusId.INFESTED_ID, StatusEffects.INFESTED);
        put(StatusId.WIND_CHARGED_ID, StatusEffects.WIND_CHARGED);
    }};

    private final StatusId statusId;

    public StatusEffectSpell(StatusId statusId) {
        super();
        this.statusId = statusId;
    }

    public StatusEffectSpell(StatusId statusId, int cost, int perIncrementCost) {
        super(cost, perIncrementCost, SharedConstants.TICKS_PER_SECOND, SharedConstants.TICKS_PER_SECOND, 3);
        this.statusId = statusId;
    }

    public StatusEffectSpell(StatusId statusId, int cost, int perIncrementCost, int ticksToCast, int ticksPerIncrement) {
        super(cost, perIncrementCost, ticksToCast, ticksPerIncrement, 3);
        this.statusId = statusId;
    }

    public RegistryEntry<StatusEffect> getEffect() {
        return uuidPartToStatusKey.get(statusId);
    }

    @Override
    public Text getName() {
        return Text.translatable(
                "miningmagic.spells.status_effect",
                Text.translatable(getEffect().value().getTranslationKey())
        );
    }

    @Override
    public void castSpell(LivingEntity user, World world, ItemStack reagent, int increments) {
        RegistryEntry<StatusEffect> effect = getEffect();

        int duration = getBaseDuration() + getDurationPerIncrement() * increments;

        if (effect.value().isBeneficial()) {
            user.addStatusEffect(new StatusEffectInstance(effect, duration, 0), user);
        } else {
            ShotSpellEntity arcaneShotEntity = new StatusEffectSpellEntity(
                    new StatusEffectInstance(effect, duration, 0),
                    world,
                    user.getPos().getX(),
                    user.getEyePos().getY(),
                    user.getPos().getZ()
            );
            // replace second section of entity UUID with status effect id
            String[] parts = arcaneShotEntity.getUuid().toString().split("-");
            parts[1] = statusId.getId();
            arcaneShotEntity.setUuid(UUID.fromString(String.join("-", parts)));

            arcaneShotEntity.setOwner(user);
            arcaneShotEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);

            world.spawnEntity(arcaneShotEntity);
        }
    }

    public int getBaseDuration() {
        if (this.getEffect().value().isBeneficial()) {
            return 10 * SharedConstants.TICKS_PER_SECOND;
        } else if (this.getEffect().value().isInstant()) {
            return 1;
        }

        return 3 * SharedConstants.TICKS_PER_SECOND;
    }

    public int getDurationPerIncrement() {
        if (this.getEffect().value().isBeneficial()) {
            return 5 * SharedConstants.TICKS_PER_SECOND;
        } else if (this.getEffect().value().isInstant()) {
            return 0;
        }

        return 1;
    }
}
