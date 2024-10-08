package com.nuhlowl;

import com.nuhlowl.network.SpellPayload;
import com.nuhlowl.spells.Spell;
import com.nuhlowl.spells.Spells;
import com.nuhlowl.spells.arcane.ArcaneParticle;
import com.nuhlowl.spells.arcane.ArcaneShotEntityRenderer;
import com.nuhlowl.spells.arcane.StatusEffectSpellEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static com.nuhlowl.MiningMagic.MOD_ID;

public class MiningMagicClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(
                SpellPayload.ID,
                new ClientPlayNetworking.PlayPayloadHandler<SpellPayload>() {
                    @Override
                    public void receive(SpellPayload payload, ClientPlayNetworking.Context context) {
                        Optional<Item> item = Registries.ITEM.getOrEmpty(payload.item());
                        Optional<Spell> spell = Spells.getOrEmpty(payload.spell());

                        if (item.isEmpty() || spell.isEmpty()) {
                            MiningMagic.LOGGER.error("Could not register server received spell combo. Spell ({} = {}). Item ({} = {})", payload.spell(), spell, payload.item(), item);
                            return;
                        }

                        Spells.registerClientSpellItemAssociation(spell.get(), item.get());
                    }
                }
        );

        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        BlockRenderLayerMap.INSTANCE.putBlock(MiningMagic.AMETHYST_RUNE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MiningMagic.AMETHYST_DUST_BLOCK, RenderLayer.getCutout());

        HandledScreens.register(MiningMagic.RESTRICTED_9X6, RestrictedContainerScreen::new);

        ParticleFactoryRegistry.getInstance()
                .register(MiningMagic.ARCANE_TRAIL_PARTICLE, ArcaneParticle.Factory::new);
        ParticleFactoryRegistry.getInstance()
                .register(MiningMagic.ARCANE_SPARK_PARTICLE, ArcaneParticle.Factory::new);

        EntityRendererRegistry
                .register(MiningMagic.ARCANE_SHOT_ENTITY, ArcaneShotEntityRenderer::new);

        EntityRendererRegistry
                .register(MiningMagic.STATUS_EFFECT_SPELL_ENTITY, StatusEffectSpellEntityRenderer::new);

        ModelPredicateProviderRegistry.register(
                MiningMagic.WAND,
                Identifier.of(MOD_ID, "charging"),
                ((stack, world, entity, seed) ->
                    entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
                )
        );

        ModelPredicateProviderRegistry.register(
                MiningMagic.WAND,
                Identifier.of(MOD_ID, "ready"),
                ((stack, world, entity, seed) -> {
                    if (stack.getItem() instanceof Wand wand && entity != null) {
                        return wand.isCastReady(stack, world, entity) ? 1.0F : 0.0F;
                    }
                    return 0.0F;
                })
        );

        ModelPredicateProviderRegistry.register(
                MiningMagic.WAND,
                Identifier.of(MOD_ID, "charge_level"),
                ((stack, world, entity, seed) -> {
                    if (stack.getItem() instanceof Wand wand && entity != null) {
                        return wand.getChargeLevel(stack, world, entity);
                    }
                    return 0.0F;
                })
        );
    }
}