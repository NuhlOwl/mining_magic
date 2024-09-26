package com.nuhlowl.villagers;

import com.nuhlowl.MiningMagic;
import com.nuhlowl.MiningMagicRules;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public abstract class AbstractGathererJobBlockEntity extends LootableContainerBlockEntity {
    private DefaultedList<ItemStack> inventory;
    private final ViewerCountManager stateManager;
    private Optional<VillagerEntity> workerCache;

    protected AbstractGathererJobBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);

        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        this.workerCache = Optional.empty();

        this.stateManager = new ViewerCountManager() {
            protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
                AbstractGathererJobBlockEntity.this.playSound(state, AbstractGathererJobBlockEntity.this.getCloseSound());
                AbstractGathererJobBlockEntity.this.setOpen(state, true);
            }

            protected void onContainerClose(World world, BlockPos pos, BlockState state) {
                AbstractGathererJobBlockEntity.this.playSound(state, AbstractGathererJobBlockEntity.this.getCloseSound());
                AbstractGathererJobBlockEntity.this.setOpen(state, false);
            }

            protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
            }

            protected boolean isPlayerViewing(PlayerEntity player) {
                if (player.currentScreenHandler instanceof GenericContainerScreenHandler) {
                    Inventory inventory = ((GenericContainerScreenHandler) player.currentScreenHandler).getInventory();
                    return inventory == AbstractGathererJobBlockEntity.this;
                } else {
                    return false;
                }
            }
        };
    }

    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.writeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory, registryLookup);
        }
    }

    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.readLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory, registryLookup);
        }
    }

    @Override
    protected Text getContainerName() {
        return getDisplayName();
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    public void onOpen(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.openContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public void onClose(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.closeContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public void tick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public void generateGathererLoot() {
        generateWorkerLoot(this.world, this);
    }

    void setOpen(BlockState state, boolean open) {
//        this.world.setBlockState(this.getPos(), state.with(BarrelBlock.OPEN, open), Block.NOTIFY_ALL);
    }

    void playSound(BlockState state, SoundEvent soundEvent) {
        Vec3i vec3i = (state.get(Properties.HORIZONTAL_FACING)).getVector();
        double d = (double) this.pos.getX() + 0.5 + (double) vec3i.getX() / 2.0;
        double e = (double) this.pos.getY() + 0.5 + (double) vec3i.getY() / 2.0;
        double f = (double) this.pos.getZ() + 0.5 + (double) vec3i.getZ() / 2.0;
        this.world.playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
    }

    private void generateWorkerLoot(World world, AbstractGathererJobBlockEntity entity) {
        entity.findWorker(world).ifPresent((worker) -> {
            if (!Jobs.rollIdleLoot(world, MiningMagicRules.GATHERER_AVG_SUCCESSES_PER_DAY, MiningMagicRules.GATHERER_AVG_SUCCESSES_PER_NIGHT)) {
                return;
            }

            LootTable lootTable = getWorkerLootTable();
            LootContextParameterSet set = (new LootContextParameterSet.Builder((ServerWorld) this.getWorld()))
                    .luck(worker.getVillagerData().getLevel())
                    .build(LootContextTypes.EMPTY);
            List<ItemStack> loot = lootTable.generateLoot(set);
            entity.addLootToInventory(loot);

            TradeOffer trade = new TradeOffer(
                    null,
                    ItemStack.EMPTY,
                    0,
                    1,
                    0
            );
            worker.trade(trade);
            worker.setExperienceFromServer(worker.getExperience() + trade.getMerchantExperience());
        });
    }

    private Optional<VillagerEntity> findWorker(World world) {
        if (this.workerCache.filter(LivingEntity::isAlive).isPresent()) {
            return this.workerCache;
        }

        Box b = new Box(this.pos)
                .expand(
                        this.getHorizontalExpansion(),
                        this.getHeightExpansion(),
                        this.getHorizontalExpansion()
                );

        List<VillagerEntity> nearbyVillagers = world.getEntitiesByClass(
                VillagerEntity.class,
                b,
                LivingEntity::isAlive
        );

        this.workerCache = nearbyVillagers.stream()
                .filter(entity -> {
                    Optional<GlobalPos> memory = entity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
                    if (memory != null && memory.isPresent()) {
                        return memory.stream().allMatch(
                                (p) -> p.equals(GlobalPos.create(world.getRegistryKey(), this.pos))
                        );
                    }

                    return false;
                })
                .findFirst();

        return this.workerCache;
    }

    protected int getHorizontalExpansion() {
        return 64;
    }

    protected int getHeightExpansion() {
        return 64;
    }

    protected void addLootToInventory(List<ItemStack> loot) {
        for (ItemStack lootStack : loot) {
            if (lootStack.isEmpty()) {
                continue;
            }

            int toGive = lootStack.getCount();

            while (toGive > 0) {
                ItemStack slot = findEmptyOrMatchingSlot(lootStack.getItem());
                if (slot == null) {
                    // no available slots
                    break;
                }

                int available = slot.getMaxCount() - slot.getCount();
                int willGive = Math.min(available, toGive);
                toGive -= willGive;
                slot.increment(willGive);
            }
        }
        this.markDirty();
    }

    private ItemStack findEmptyOrMatchingSlot(Item item) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack inventoryStack = inventory.get(i);
            boolean hasRoom = inventoryStack.getCount() < inventoryStack.getMaxCount();
            if (item == inventoryStack.getItem() && hasRoom) {
                return inventoryStack;
            } else if (inventoryStack.isEmpty()) {
                ItemStack newStack = new ItemStack(item);
                inventory.set(i, newStack);
                return newStack;
            }
        }

        return null;
    }

    abstract protected LootTable getWorkerLootTable();
    abstract protected SoundEvent getOpenSound();
    abstract protected SoundEvent getCloseSound();

}
