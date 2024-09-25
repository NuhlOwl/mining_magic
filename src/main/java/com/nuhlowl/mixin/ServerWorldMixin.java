package com.nuhlowl.mixin;

import com.nuhlowl.spells.Spells;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
	@Shadow public abstract long getSeed();

	@Inject(at = @At("RETURN"), method = "<init>")
	private void init(CallbackInfo info) {
		Spells.createSpellMapForSeed(this.getSeed());
	}

	@Inject(at = @At("RETURN"), method = "close")
	private void close(CallbackInfo info) {
		Spells.removeSpellMapForSeed(this.getSeed());
	}

}