package com.nuhlowl.mixin.client;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModelPredicateProviderRegistry.class)
public class ExampleClientMixin {
//	@Inject(at = @At(value = "HEAD"), method = "get", locals = LocalCapture.CAPTURE_FAILSOFT)
//	private static void onParticleReturn(ItemStack stack, Identifier id, CallbackInfoReturnable<ModelPredicateProvider> cir) {
//		// This code is injected into the start of MinecraftClient.run()V
//		MiningMagic.LOGGER.info("on get {} - {}", id, stack);
////		MiningMagic.LOGGER.error("error", throwable2);
//	}
}