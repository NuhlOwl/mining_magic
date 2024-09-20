package com.nuhlowl.villagers;

import com.nuhlowl.MiningMagic;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static com.nuhlowl.villagers.Jobs.LOG_RACK_BLOCK_ENTITY;

public class LogRackBlockEntity extends AbstractGathererJobBlockEntity {
    public static final RegistryKey<LootTable> WORKER_LOOT_TABLE = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(MiningMagic.MOD_ID, "idle/log_rack_worker"));

    protected LogRackBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(LOG_RACK_BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("miningmagic.log_rack_container");
    }

    @Override
    protected LootTable getWorkerLootTable() {
        return world.getServer().getReloadableRegistries().getLootTable(WORKER_LOOT_TABLE);
    }

    @Override
    protected SoundEvent getOpenSound() {
        return SoundEvents.BLOCK_WOOD_BREAK;
    }

    @Override
    protected SoundEvent getCloseSound() {
        return SoundEvents.BLOCK_WOOD_BREAK;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return RestrictedContainerScreenHandler.createGeneric9x6(syncId, playerInventory, this, LogRackBlockEntity::isLog);
    }

    @Override
    public int size() {
        return 54;
    }

    public static Boolean isLog(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock().getDefaultState().isIn(BlockTags.LOGS);
    }
}
