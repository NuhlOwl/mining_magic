package com.nuhlowl.spells;

import com.google.common.collect.ImmutableList;
import com.nuhlowl.MiningMagic;
import com.nuhlowl.spells.arcane.ArcaneShotSpell;
import com.nuhlowl.spells.status.StatusEffectSpell;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.CheckedRandom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Spells {
    public static final HashMap<Long, HashMap<Item, Spell>> SPELL_MAPS_BY_SEED = new HashMap<>();
    public static final Spell DEFAULT_SPELL = new ArcaneShotSpell();
    public static final List<Spell> SPELLS = ImmutableList.of(
            DEFAULT_SPELL,
            new FireBallSpell(),
            new StatusEffectSpell(StatusEffectSpell.StatusId.POISON_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.SPEED_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.HASTE_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.INSTANT_DAMAGE_ID, 2, 2),
            new StatusEffectSpell(StatusEffectSpell.StatusId.INSTANT_HEALTH_ID, 3, 1),
            new StatusEffectSpell(StatusEffectSpell.StatusId.JUMP_BOOST_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.REGENERATION_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.RESISTANCE_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.FIRE_RESISTANCE_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.WATER_BREATHING_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.INVISIBILITY_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.BLINDNESS_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.NIGHT_VISION_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.WEAKNESS_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.WITHER_ID, 3, 1),
            new StatusEffectSpell(StatusEffectSpell.StatusId.HEALTH_BOOST_ID, 5, 1),
            new StatusEffectSpell(StatusEffectSpell.StatusId.ABSORPTION_ID, 3, 1),
            new StatusEffectSpell(StatusEffectSpell.StatusId.LEVITATION_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.SLOW_FALLING_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.GLOWING_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.WEAVING_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.OOZING_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.INFESTED_ID),
            new StatusEffectSpell(StatusEffectSpell.StatusId.WIND_CHARGED_ID)
    );

    public static void createSpellMapForSeed(long seed) {
        if (SPELL_MAPS_BY_SEED.containsKey(seed)) {
            // called for overworld, nether and end with same seed
            // would result in same mapping
            // might have different mappings for each later
            return;
        }

        CheckedRandom random = new CheckedRandom(seed);

        List<Spell> spellOptions = new ArrayList<>(SPELLS.size());
        spellOptions.addAll(SPELLS);

        HashMap<Item, Spell> seedSpells = new HashMap<>(SPELLS.size());
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

        Spell spell = seedSpells.get(item);

        return spell;
    }
}
