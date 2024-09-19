package com.nuhlowl;

import com.nuhlowl.villagers.Jobs;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
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
}