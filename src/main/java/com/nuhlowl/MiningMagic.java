package com.nuhlowl;

import com.mojang.datafixers.types.Type;
import com.nuhlowl.villagers.Jobs;
import com.nuhlowl.villagers.RestrictedContainerScreenHandler;
import com.nuhlowl.villagers.SluiceBlockEntity;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static final Item WAND = registerItem(new Wand(new Item.Settings()), "wand");

    public static final ScreenHandlerType<RestrictedContainerScreenHandler> RESTRICTED_9X6 = registerScreenType("restricted_9x6", RestrictedContainerScreenHandler::createGeneric9x6);

    private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenType(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of(MiningMagic.MOD_ID, id),
                new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES)
        );
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        Jobs.init();

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
//            LOGGER.info("block break");
            return true;
        });
//        RegistryKey<LootTable> key = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(MiningMagic.MOD_ID, "idle/sluice"));
//        LootTable lootTable = piglin.getWorld().getServer().getReloadableRegistries().getLootTable(LootTables.PIGLIN_BARTERING_GAMEPLAY);

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
}