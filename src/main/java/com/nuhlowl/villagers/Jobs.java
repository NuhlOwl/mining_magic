package com.nuhlowl.villagers;

import com.google.common.collect.ImmutableSet;
import com.nuhlowl.MiningMagic;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.Set;

public class Jobs {
    public static final RegistryKey<PointOfInterestType> PROSPECTOR = registerPOI("prospector");
    public static final RegistryKey<PointOfInterestType> LUMBERJACK = registerPOI("lumberjack");
    public static final RegistryKey<PointOfInterestType> ADVENTURER = registerPOI("adventurer");

    public static final Block SLUICE_BLOCK = MiningMagic.registerBlock(
            new SluiceBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.OAK_TAN)
                    .ticksRandomly()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.5F)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()),
            "sluice",
            true
    );
    public static final Block LOG_RACK_BLOCK = MiningMagic.registerBlock(
            new LogRackBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.STONE_GRAY)
                    .ticksRandomly()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.5F)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()),
            "log_rack",
            true
    );
    public static final Block LOOT_CRATE_BLOCK = MiningMagic.registerBlock(
            new LootCrateBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.OAK_TAN)
                    .ticksRandomly()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.5F)
                    .sounds(BlockSoundGroup.WOOD)
                    .nonOpaque()
                    .burnable()),
            "loot_crate",
            true
    );

    public static final BlockEntityType<SluiceBlockEntity> SLUICE_BLOCK_ENTITY = MiningMagic.registerBlockEntity("sluice", BlockEntityType.Builder.create(SluiceBlockEntity::new, SLUICE_BLOCK));
    public static final BlockEntityType<LogRackBlockEntity> LOG_RACK_BLOCK_ENTITY = MiningMagic.registerBlockEntity("log_rack", BlockEntityType.Builder.create(LogRackBlockEntity::new, LOG_RACK_BLOCK));
    public static final BlockEntityType<LootCrateBlockEntity> LOOT_CRATE_BLOCK_ENTITY = MiningMagic.registerBlockEntity("loot_crate", BlockEntityType.Builder.create(LootCrateBlockEntity::new, LOOT_CRATE_BLOCK));

    public static final VillagerProfession PROSPECTOR_PROFESSION = registerProfession("prospector", PROSPECTOR);
    public static final VillagerProfession LUMBERJACK_PROFESSION = registerProfession("lumberjack", LUMBERJACK);
    public static final VillagerProfession ADVENTURER_PROFESSION = registerProfession("adventurer", ADVENTURER);

    public static void init() {
        registerPOIType(PROSPECTOR, getStatesOfBlock(SLUICE_BLOCK), 1, 1);
        registerPOIType(LUMBERJACK, getStatesOfBlock(LOG_RACK_BLOCK), 1, 1);
        registerPOIType(ADVENTURER, getStatesOfBlock(LOOT_CRATE_BLOCK), 1, 1);

    }

    public static VillagerProfession registerProfession(String id, RegistryKey<PointOfInterestType> heldWorkstation) {
        return Registry.register(
                Registries.VILLAGER_PROFESSION,
                Identifier.of(MiningMagic.MOD_ID, id),
                new VillagerProfession(
                        id,
                        (entry) -> entry.matchesKey(heldWorkstation),
                        (entry) -> entry.matchesKey(heldWorkstation),
                        ImmutableSet.of(),
                        ImmutableSet.of(),
                        SoundEvents.ENTITY_VILLAGER_WORK_ARMORER
                )
        );
    }

    public static RegistryKey<PointOfInterestType> registerPOI(String id) {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Identifier.of(MiningMagic.MOD_ID, id));
    }

    private static Set<BlockState> getStatesOfBlock(Block block) {
        return ImmutableSet.copyOf(block.getStateManager().getStates());
    }

    private static PointOfInterestType registerPOIType(
            RegistryKey<PointOfInterestType> key, Set<BlockState> states, int ticketCount, int searchDistance
    ) {
        PointOfInterestType pointOfInterestType = new PointOfInterestType(states, ticketCount, searchDistance);
        Registry.register(Registries.POINT_OF_INTEREST_TYPE, key, pointOfInterestType);
        registerStates(Registries.POINT_OF_INTEREST_TYPE.entryOf(key), states);
        return pointOfInterestType;
    }

    private static void registerStates(RegistryEntry<PointOfInterestType> poiTypeEntry, Set<BlockState> states) {
        states.forEach(state -> {
            PointOfInterestTypes.POI_STATES_TO_TYPE.put(state, poiTypeEntry);
//            if (registryEntry2 != null) {
//                throw (IllegalStateException) Util.throwOrPause(new IllegalStateException(String.format(Locale.ROOT, "%s is defined in more than one PoI type", state)));
//            }
        });
    }
}
