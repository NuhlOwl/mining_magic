package com.nuhlowl.villagers;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.function.Predicate;

public class RestrictedSlot extends Slot {
    private final Predicate<ItemStack> check;

    public RestrictedSlot(Inventory inventory, int index, int x, int y, Predicate<ItemStack> check) {
        super(inventory, index, x, y);
        this.check = check;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return this.check.test(stack);
    }
}
