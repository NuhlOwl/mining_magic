package com.nuhlowl.mixin.client;

import com.nuhlowl.MiningMagic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class ExampleClientMixin {
//	@Inject(at = @At(value = "RETURN"), method = "onParticle", locals = LocalCapture.CAPTURE_FAILSOFT)
//	private void onParticleReturn(ParticleS2CPacket packet, CallbackInfo ci, int i, double g, double h, double j, double k, double l, double m, Throwable throwable2) {
//		// This code is injected into the start of MinecraftClient.run()V
//		MiningMagic.LOGGER.info("on particle return mixin");
//		MiningMagic.LOGGER.error("error", throwable2);
//	}
}