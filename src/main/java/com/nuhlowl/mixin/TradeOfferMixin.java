package com.nuhlowl.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(TradeOffer.class)
public class TradeOfferMixin {
    @Shadow public boolean rewardingPlayerExperience;

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/village/TradedItem;Ljava/util/Optional;Lnet/minecraft/item/ItemStack;IIZIIFI)V")
    public void shouldRewardPlayerExperience(TradedItem firstBuyItem, Optional secondBuyItem, ItemStack sellItem, int _uses, int maxUses, boolean rewardingPlayerExperience, int specialPrice, int demandBonus, float priceMultiplier, int merchantExperience, CallbackInfo ci) {
        if (sellItem.isEmpty()) {
            this.rewardingPlayerExperience = false;
        }
    }
}
