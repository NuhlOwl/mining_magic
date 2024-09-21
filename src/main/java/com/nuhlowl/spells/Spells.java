package com.nuhlowl.spells;

import com.google.common.collect.ImmutableList;
import com.nuhlowl.MiningMagic;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.CheckedRandom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Spells {
    public static final HashMap<Long, HashMap<Item, Spell>> SPELL_MAPS_BY_SEED = new HashMap<>();
    public static final List<Spell> SPELLS = ImmutableList.of(
        new FireBallSpell()
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
        MiningMagic.LOGGER.info("Generating {} spell combinations", max);

        for (int i = 0; i<max; i++) {
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
            MiningMagic.LOGGER.info("registered item {} to spell {}", item.getName(), spell.getName());
        }

        SPELL_MAPS_BY_SEED.put(seed, seedSpells);
    }

    public static void removeSpellMapForSeed(long seed) {
        SPELL_MAPS_BY_SEED.remove(seed);
    }

    public static Spell getSpellForItem(long seed, Item item) {
        MiningMagic.LOGGER.info("checking seed {} for item {}", seed, item);
        HashMap<Item, Spell> seedSpells = SPELL_MAPS_BY_SEED.get(seed);

        MiningMagic.LOGGER.info("spells: {}", seedSpells);
        if (seedSpells == null) {
            return null;
        }

        Spell spell = seedSpells.get(item);
        MiningMagic.LOGGER.info("spell: {}", spell);

        return spell;
    }
}
