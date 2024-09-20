package com.nuhlowl.villagers;

import com.nuhlowl.MiningMagic;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static com.nuhlowl.villagers.Jobs.SLUICE_BLOCK_ENTITY;

public class SluiceBlockEntity extends AbstractGathererJobBlockEntity {
    public static final RegistryKey<LootTable> IDLE_LOOT_TABLE = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(MiningMagic.MOD_ID, "idle/sluice"));
    public static final RegistryKey<LootTable> WORKER_LOOT_TABLE = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(MiningMagic.MOD_ID, "idle/sluice_worker"));

    public SluiceBlockEntity(BlockPos pos, BlockState state) {
        super(SLUICE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("miningmagic.sluice_container");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, syncId, playerInventory, this, 1);
    }

    @Override
    protected LootTable getWorkerLootTable() {
        return world.getServer().getReloadableRegistries().getLootTable(WORKER_LOOT_TABLE);
    }

    @Override
    protected SoundEvent getCloseSound() {
        return SoundEvents.BLOCK_IRON_DOOR_CLOSE;
    }

    @Override
    protected SoundEvent getOpenSound() {
        return SoundEvents.BLOCK_IRON_DOOR_OPEN;
    }

    public void generateIdleLoot(boolean waterlogged) {
        if (waterlogged) {
            generateWaterLoggedLoot();
        }
    }

    private void generateWaterLoggedLoot() {
        LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(IDLE_LOOT_TABLE);
        LootContextParameterSet set = (new LootContextParameterSet.Builder((ServerWorld) this.getWorld())).build(LootContextTypes.EMPTY);
        List<ItemStack> loot = lootTable.generateLoot(set);
        this.addLootToInventory(loot);
    }
}
