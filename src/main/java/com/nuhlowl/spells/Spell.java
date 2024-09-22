package com.nuhlowl.spells;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public interface Spell {
    Text getName();

    SpellCastResult castSpell(PlayerEntity user, World world, ItemStack reagent);
}
