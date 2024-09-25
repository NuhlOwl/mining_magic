package com.nuhlowl.mixin.client;

import com.nuhlowl.spells.Spells;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Inject(at = @At("RETURN"), method = "disconnect")
    private void close(CallbackInfo info) {
        Spells.removeSpellMapForSeed(Spells.CLIENT_SEED);
    }
}