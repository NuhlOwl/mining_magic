package com.nuhlowl;

import com.mojang.datafixers.types.Type;
import com.mojang.serialization.MapCodec;
import com.nuhlowl.commands.SpellsCommand;
import com.nuhlowl.spells.arcane.ArcaneParticleEffect;
import com.nuhlowl.spells.arcane.ArcaneShotEntity;
import com.nuhlowl.spells.status.StatusEffectSpellEntity;
import com.nuhlowl.villagers.Jobs;
import com.nuhlowl.villagers.RestrictedContainerScreenHandler;
import com.nuhlowl.villagers.SluiceBlockEntity;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class MiningMagic implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MOD_ID = "miningmagic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final AmethystRune AMETHYST_RUNE = registerBlock(
            new AmethystRune(AbstractBlock.Settings.create().noCollision().breakInstantly().pistonBehavior(PistonBehavior.DESTROY)),
            "amethyst_rune",
            false
    );
    public static final AmethystDust AMETHYST_DUST_BLOCK = registerBlock(
            new AmethystDust(AbstractBlock.Settings.create().noCollision().breakInstantly().pistonBehavior(PistonBehavior.DESTROY)),
            "amethyst_dust",
            true
    );
    public static final Item WAND = registerItem(new Wand(new Item.Settings().maxCount(1).maxDamage(100)), "wand");

    public static final TagKey<Item> REAGENT_ITEM_TAG = registerItemTag("magic/spell_reagent");

    public static final ScreenHandlerType<RestrictedContainerScreenHandler> RESTRICTED_9X6 = registerScreenType("restricted_9x6", RestrictedContainerScreenHandler::createGeneric9x6);

    public static final EntityType<ArcaneShotEntity> ARCANE_SHOT_ENTITY = registerEntityType(
            "arcane_shot",
            EntityType.Builder.create((EntityType.EntityFactory<ArcaneShotEntity>) ArcaneShotEntity::new, SpawnGroup.MISC)
                    .dimensions(0.3125F, 0.3125F)
                    .eyeHeight(0.0F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
    );

    public static final EntityType<StatusEffectSpellEntity> STATUS_EFFECT_SPELL_ENTITY = registerEntityType(
            "status_effect_spell",
            EntityType.Builder.create((EntityType.EntityFactory<StatusEffectSpellEntity>) StatusEffectSpellEntity::new, SpawnGroup.MISC)
                    .dimensions(0.3125F, 0.3125F)
                    .eyeHeight(0.0F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
    );

    public static final ParticleType<ArcaneParticleEffect> ARCANE_TRAIL_PARTICLE = registerParticleType(
            "arcane_trail",
            false,
            type -> ArcaneParticleEffect.CODEC,
            type -> ArcaneParticleEffect.PACKET_CODEC
    );

    public static final ParticleType<ArcaneParticleEffect> ARCANE_SPARK_PARTICLE = registerParticleType(
            "arcane_spark",
            false,
            type -> ArcaneParticleEffect.CODEC,
            type -> ArcaneParticleEffect.PACKET_CODEC
    );

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        Jobs.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SpellsCommand.register(dispatcher);
        });

        PointOfInterestHelper.register(
                Identifier.of(MiningMagic.MOD_ID, "prospector"),
                1,
                1,
                Jobs.SLUICE_BLOCK
        );

        PointOfInterestHelper.register(
                Identifier.of(MiningMagic.MOD_ID, "lumberjack"),
                1,
                1,
                Jobs.LOG_RACK_BLOCK
        );

        PointOfInterestHelper.register(
                Identifier.of(MiningMagic.MOD_ID, "adventurer"),
                1,
                1,
                Jobs.LOOT_CRATE_BLOCK
        );

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new LootTableResourceListener());
    }

    public static <T extends Item> T registerItem(T item, String id) {
        return Registry.register(
                Registries.ITEM,
                Identifier.of(MOD_ID, id),
                item
        );
    }

    public static <T extends Block> T registerBlock(T block, String id, boolean registerItem) {
        if (registerItem) {
            registerItem(new BlockItem(block, new Item.Settings()), id);
        }

        return Registry.register(
                Registries.BLOCK,
                Identifier.of(MOD_ID, id),
                block
        );
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String id, BlockEntityType.Builder<T> builder) {
        Identifier identifier = Identifier.of(MOD_ID, id);
        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, identifier, builder.build(type));
    }

    private static TagKey<Item> registerItemTag(String id) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of(MiningMagic.MOD_ID, id));
    }

    private static <T extends Entity> EntityType<T> registerEntityType(String id, EntityType.Builder<T> type) {

        return Registry.register(Registries.ENTITY_TYPE, Identifier.of(MiningMagic.MOD_ID, id), type.build(id));
    }

    private static <T extends ParticleEffect> ParticleType<T> registerParticleType(
            String name,
            boolean alwaysShow,
            Function<ParticleType<T>, MapCodec<T>> codecGetter,
            Function<ParticleType<T>, PacketCodec<? super RegistryByteBuf, T>> packetCodecGetter
    ) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MiningMagic.MOD_ID, name), new ParticleType<T>(alwaysShow) {
            @Override
            public MapCodec<T> getCodec() {
                return codecGetter.apply(this);
            }

            @Override
            public PacketCodec<? super RegistryByteBuf, T> getPacketCodec() {
                return packetCodecGetter.apply(this);
            }
        });
    }

    private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenType(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of(MiningMagic.MOD_ID, id),
                new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES)
        );
    }
}