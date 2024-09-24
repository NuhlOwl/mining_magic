package com.nuhlowl.spells;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public interface Spell {
    Text getName();
    SpellCastResult castSpell(LivingEntity user, World world, ItemStack reagent);
    int cost();
    int perIncrementCost();
}
