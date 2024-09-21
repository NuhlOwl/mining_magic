package com.nuhlowl.villagers;

import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;

public class GathererTrade extends TradeOffer {
    public GathererTrade() {
        super(null, ItemStack.EMPTY, 0, 1, 0);
        this.rewardingPlayerExperience = false;
    }
}
