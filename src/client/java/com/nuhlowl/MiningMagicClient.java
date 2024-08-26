package com.nuhlowl;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class MiningMagicClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockRenderLayerMap.INSTANCE.putBlock(MiningMagic.AMETHYST_RUNE, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MiningMagic.AMETHYST_DUST_BLOCK, RenderLayer.getCutout());
	}
}