package com.nuhlowl.spells;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public interface Spell {
    Text getName();
    void castSpell(LivingEntity user, World world, ItemStack reagent, int increments);
    int cost();
    int perIncrementCost();
    int ticksToCast();
    int ticksPerIncrement();
    int maxIncrements();
}
