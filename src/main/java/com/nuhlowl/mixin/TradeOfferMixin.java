package com.nuhlowl.mixin;

import com.nuhlowl.MiningMagic;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(TradeOffer.class)
public class TradeOfferMixin {

    @Shadow @Final private ItemStack sellItem;

    @Inject(at = @At("HEAD"), method = "shouldRewardPlayerExperience", cancellable = true)
    public void shouldRewardPlayerExperience(CallbackInfoReturnable<Boolean> cir) {
        if (this.sellItem.isEmpty()) {
            cir.setReturnValue(false);
        }
    }
}
