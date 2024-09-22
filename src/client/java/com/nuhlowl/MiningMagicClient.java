package com.nuhlowl;

import com.nuhlowl.spells.arcane.ArcaneParticle;
import com.nuhlowl.spells.arcane.ArcaneShotEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class MiningMagicClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockRenderLayerMap.INSTANCE.putBlock(MiningMagic.AMETHYST_RUNE, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MiningMagic.AMETHYST_DUST_BLOCK, RenderLayer.getCutout());

		HandledScreens.register(MiningMagic.RESTRICTED_9X6, RestrictedContainerScreen::new);

		ParticleFactoryRegistry.getInstance()
				.register(MiningMagic.ARCANE_TRAIL_PARTICLE, ArcaneParticle.Factory::new);

		EntityRendererRegistry
				.register(MiningMagic.ARCANE_SHOT_ENTITY, ArcaneShotEntityRenderer::new);
	}
}