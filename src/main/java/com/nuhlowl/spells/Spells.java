package com.nuhlowl.spells;

import com.nuhlowl.MiningMagic;
import com.nuhlowl.spells.arcane.ArcaneShotSpell;
import com.nuhlowl.spells.status.StatusEffectSpell;
import net.minecraft.SharedConstants;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.CheckedRandom;

import java.util.*;
import java.util.stream.Collectors;

public class Spells {
    public static final HashMap<Long, HashMap<Item, Spell>> SPELL_MAPS_BY_SEED = new HashMap<>();
    public static final Spell DEFAULT_SPELL = new ArcaneShotSpell();
    public static final long CLIENT_SEED = 0;
    public static final HashMap<Identifier, Spell> SPELL_REGISTRY = new HashMap<>();

    public static final Identifier ARCANE_SHOT_SPELL = registerSpell("arcane_shot", DEFAULT_SPELL);
    public static final Identifier FIRE_BALL_SPELL = registerSpell("fire_ball", new FireBallSpell());
    public static final Identifier POISON_STATUS_SPELL = registerSpell("poison_status", new StatusEffectSpell(StatusEffectSpell.StatusId.POISON_ID));
    public static final Identifier SPEED_STATUS_SPELL = registerSpell("speed_status", new StatusEffectSpell(StatusEffectSpell.StatusId.SPEED_ID));
    public static final Identifier HASTE_STATUS_SPELL = registerSpell("haste_status", new StatusEffectSpell(StatusEffectSpell.StatusId.HASTE_ID));
    public static final Identifier INSTANT_DAMAGE_STATUS_SPELL = registerSpell("instant_damage_status", new StatusEffectSpell(StatusEffectSpell.StatusId.INSTANT_DAMAGE_ID, 64, 0, SharedConstants.TICKS_PER_SECOND * 3, SharedConstants.TICKS_PER_SECOND * 3));
    public static final Identifier INSTANT_HEALTH_STATUS_SPELL = registerSpell("instant_health_status", new StatusEffectSpell(StatusEffectSpell.StatusId.INSTANT_HEALTH_ID, 64, 0, SharedConstants.TICKS_PER_SECOND * 3, SharedConstants.TICKS_PER_SECOND * 3));
    public static final Identifier JUMP_BOOST_STATUS_SPELL = registerSpell("jump_boost", new StatusEffectSpell(StatusEffectSpell.StatusId.JUMP_BOOST_ID));
    public static final Identifier REGENERATION_STATUS_SPELL = registerSpell("regeneration_status", new StatusEffectSpell(StatusEffectSpell.StatusId.REGENERATION_ID));
    public static final Identifier RESISTANCE_STATUS_SPELL = registerSpell("resistance_status", new StatusEffectSpell(StatusEffectSpell.StatusId.RESISTANCE_ID));
    public static final Identifier FIRE_RESISTANCE_STATUS_SPELL = registerSpell("fire_resistance_status", new StatusEffectSpell(StatusEffectSpell.StatusId.FIRE_RESISTANCE_ID));
    public static final Identifier WATER_BREATHING_STATUS_SPELL = registerSpell("water_breathing_status", new StatusEffectSpell(StatusEffectSpell.StatusId.WATER_BREATHING_ID));
    public static final Identifier INVISIBILITY_STATUS_SPELL = registerSpell("invisibility_status", new StatusEffectSpell(StatusEffectSpell.StatusId.INVISIBILITY_ID));
    public static final Identifier BLINDNESS_STATUS_SPELL = registerSpell("blindness_status", new StatusEffectSpell(StatusEffectSpell.StatusId.BLINDNESS_ID));
    public static final Identifier NIGHT_VISION_STATUS_SPELL = registerSpell("night_vision_status", new StatusEffectSpell(StatusEffectSpell.StatusId.NIGHT_VISION_ID));
    public static final Identifier WEAKNESS_STATUS_SPELL = registerSpell("weakness_status", new StatusEffectSpell(StatusEffectSpell.StatusId.WEAKNESS_ID));
    public static final Identifier WITHER_STATUS_SPELL = registerSpell("wither_status", new StatusEffectSpell(StatusEffectSpell.StatusId.WITHER_ID, 3, 1));
    public static final Identifier HEALTH_BOOST_STATUS_SPELL = registerSpell("health_boost_status", new StatusEffectSpell(StatusEffectSpell.StatusId.HEALTH_BOOST_ID, 5, 1));
    public static final Identifier ABSORPTION_STATUS_SPELL = registerSpell("absorption_status", new StatusEffectSpell(StatusEffectSpell.StatusId.ABSORPTION_ID, 3, 1));
    public static final Identifier LEVITATION_STATUS_SPELL = registerSpell("levitation_status", new StatusEffectSpell(StatusEffectSpell.StatusId.LEVITATION_ID));
    public static final Identifier SLOW_FALLING_STATUS_SPELL = registerSpell("slow_falling_status", new StatusEffectSpell(StatusEffectSpell.StatusId.SLOW_FALLING_ID));
    public static final Identifier GLOWING_STATUS_SPELL = registerSpell("glowing_status", new StatusEffectSpell(StatusEffectSpell.StatusId.GLOWING_ID));
    public static final Identifier WEAVING_STATUS_SPELL = registerSpell("weaving_status", new StatusEffectSpell(StatusEffectSpell.StatusId.WEAVING_ID));
    public static final Identifier OOZING_STATUS_SPELL = registerSpell("oozing_status", new StatusEffectSpell(StatusEffectSpell.StatusId.OOZING_ID));
    public static final Identifier INFESTED_STATUS_SPELL = registerSpell("infested_status", new StatusEffectSpell(StatusEffectSpell.StatusId.INFESTED_ID));
    public static final Identifier WIND_CHARGED_STATUS_SPELL = registerSpell("wind_charged_status", new StatusEffectSpell(StatusEffectSpell.StatusId.WIND_CHARGED_ID));

    public static Identifier registerSpell(String id, Spell spell) {
        Identifier i = Identifier.of(MiningMagic.MOD_ID, "spells/" + id);
        SPELL_REGISTRY.put(i, spell);
        return i;
    }

    public static void createSpellMapForSeed(long seed) {
//        MiningMagic.LOGGER.info("Creating spell map for seed: {}", seed);
        if (SPELL_MAPS_BY_SEED.containsKey(seed)) {
            // called for overworld, nether and end with same seed
            // would result in same mapping
            // might have different mappings for each later
            return;
        }

        CheckedRandom random = new CheckedRandom(seed);

        List<Spell> spellOptions = new ArrayList<>(SPELL_REGISTRY.size());
        spellOptions.addAll(SPELL_REGISTRY.values());

        HashMap<Item, Spell> seedSpells = new HashMap<>(SPELL_REGISTRY.size());
        List<Item> reagentOptions = Registries.ITEM.stream()
                .filter((item) -> item.getDefaultStack().isIn(MiningMagic.REAGENT_ITEM_TAG))
                .collect(Collectors.toCollection(ArrayList::new));

        int max = Math.min(spellOptions.size(), reagentOptions.size());

        for (int i = 0; i < max; i++) {
            if (reagentOptions.isEmpty()) {
                // no more reagent spells
                // remaining spells will be added to spell scroll/ritual spell lists
                break;
            }

            int itemRoll = random.nextBetween(0, reagentOptions.size() - 1);
            Item item = reagentOptions.get(itemRoll);
            reagentOptions.remove(item);

            if (spellOptions.isEmpty()) {
                // remaining reagents will have default spell
                break;
            }

            int spellRoll = random.nextBetween(0, spellOptions.size() - 1);
            Spell spell = spellOptions.get(spellRoll);
            spellOptions.remove(spell);

            seedSpells.put(item, spell);
        }

        // any remaining reagents will be assigned to DEFAULT_SPELL
        for (Item item : reagentOptions) {
            seedSpells.put(item, DEFAULT_SPELL);
        }

        SPELL_MAPS_BY_SEED.put(seed, seedSpells);
    }

    public static void removeSpellMapForSeed(long seed) {
        SPELL_MAPS_BY_SEED.remove(seed);
    }

    public static Spell getSpellForItem(long seed, Item item) {
        HashMap<Item, Spell> seedSpells = SPELL_MAPS_BY_SEED.get(seed);

        if (seedSpells == null) {
            return null;
        }

        return seedSpells.get(item);
    }

    public static Spell getClientSpellForItem(Item item) {
        return getSpellForItem(CLIENT_SEED, item);
    }

    public static Identifier getId(Spell spell) {
        return SPELL_REGISTRY.entrySet().stream()
                .filter((entry) -> entry.getValue() == spell)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public static Optional<Spell> getOrEmpty(Identifier id) {
        return Optional.ofNullable(SPELL_REGISTRY.get(id));
    }

    public static void registerClientSpellItemAssociation(Spell spell, Item item) {
        HashMap<Item, Spell> spellMap = SPELL_MAPS_BY_SEED.computeIfAbsent(CLIENT_SEED, k -> new HashMap<>());
        spellMap.put(item, spell);
    }
}
