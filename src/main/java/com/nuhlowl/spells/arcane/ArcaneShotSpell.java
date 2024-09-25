package com.nuhlowl.spells.arcane;

import com.nuhlowl.MiningMagic;
import com.nuhlowl.spells.Spell;
import com.nuhlowl.spells.Spells;
import net.minecraft.SharedConstants;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ArcaneShotSpell implements Spell {
    private int cost;
    private int perIncrementCost;
    private int ticksToCast;
    private int ticksPerIncrement;
    private int maxIncrements;

    public ArcaneShotSpell() {
        this(1, 1, SharedConstants.TICKS_PER_SECOND, SharedConstants.TICKS_PER_SECOND, 3);
    }

    public ArcaneShotSpell(int cost, int perIncrementCost, int ticksToCast, int ticksPerIncrement, int maxIncrements) {
        this.cost = cost;
        this.perIncrementCost = perIncrementCost;
        this.ticksToCast = ticksToCast;
        this.ticksPerIncrement = ticksPerIncrement;
        this.maxIncrements = maxIncrements;
    }

    @Override
    public Text getName() {
        return Text.translatable("miningmagic.spells.arcane_shot");
    }

    @Override
    public void castSpell(LivingEntity user, World world, ItemStack reagent, int increments) {
        ArcaneShotEntity arcaneShotEntity = new ArcaneShotEntity(
                world,
                user.getPos().getX(),
                user.getEyePos().getY(),
                user.getPos().getZ(),
                1 + increments
        );
        arcaneShotEntity.setOwner(user);
        arcaneShotEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F , 1.5F, 1.0F);

        world.spawnEntity(arcaneShotEntity);
    }

    @Override
    public int cost() {
        return this.cost;
    }

    @Override
    public int perIncrementCost() {
        return this.perIncrementCost;
    }

    @Override
    public int ticksToCast() {
        return ticksToCast;
    }

    @Override
    public int ticksPerIncrement() {
        return ticksPerIncrement;
    }

    @Override
    public int maxIncrements() {
        return maxIncrements;
    }
}
