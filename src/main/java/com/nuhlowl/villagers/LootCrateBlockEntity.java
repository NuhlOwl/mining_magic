package com.nuhlowl.villagers;

import com.nuhlowl.MiningMagic;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static com.nuhlowl.villagers.Jobs.LOOT_CRATE_BLOCK_ENTITY;

public class LootCrateBlockEntity extends AbstractGathererJobBlockEntity {
    public static final RegistryKey<LootTable> WORKER_LOOT_TABLE = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(MiningMagic.MOD_ID, "idle/loot_crate_worker"));

    protected LootCrateBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(LOOT_CRATE_BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("miningmagic.loot_crate_container");
    }

    @Override
    protected LootTable getWorkerLootTable() {
        return world.getServer().getReloadableRegistries().getLootTable(WORKER_LOOT_TABLE);
    }

    @Override
    protected SoundEvent getOpenSound() {
        return SoundEvents.BLOCK_BARREL_OPEN;
    }

    @Override
    protected SoundEvent getCloseSound() {
        return SoundEvents.BLOCK_BARREL_CLOSE;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return RestrictedContainerScreenHandler.createGeneric9x6(syncId, playerInventory, this, LootCrateBlockEntity::isNotBlockItem);
    }

    @Override
    public int size() {
        return 54;
    }

    public static Boolean isNotBlockItem(ItemStack stack) {
        return !(stack.getItem() instanceof BlockItem);
    }
}
