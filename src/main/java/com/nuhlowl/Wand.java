package com.nuhlowl;

import com.nuhlowl.spells.Spell;
import com.nuhlowl.spells.Spells;
import net.minecraft.SharedConstants;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class Wand extends Item {
    public static final int BASE_MAX_INCREMENTS = 3;
    public static final int WAND_BASE_USE_DAMAGE = 1;
    public static final int WAND_FEEDBACK_USE_MULTIPLIER = 5;
    public static final int WAND_FEEDBACK_CASTER_DAMAGE = 1;

    private Hand chargingWandHand = Hand.MAIN_HAND;
    private boolean charging = false;

    public Wand(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack item = user.getStackInHand(hand);
        Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack reagent = user.getStackInHand(otherHand);

        if (world instanceof ServerWorld serverWorld) {
            Spell spell = Spells.getSpellForItem(serverWorld.getSeed(), reagent.getItem());
            if (spell != null) {
                user.setCurrentHand(hand);
                this.chargingWandHand = hand;
                this.charging = true;
                return TypedActionResult.success(item);
            }
        }

        return TypedActionResult.fail(item);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (world instanceof ServerWorld serverWorld) {
            this.castSpell(remainingUseTicks, stack, user, serverWorld);
        }

        this.charging = false;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    public boolean isCharging() {
        return this.charging;
    }

    private void castSpell(
            int remainingUseTicks,
            ItemStack wand,
            LivingEntity user,
            ServerWorld world
    ) {
        int useTicks = this.getMaxUseTime(wand, user) - remainingUseTicks;
        Hand otherHand = this.chargingWandHand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack reagent = user.getStackInHand(otherHand);

        Spell spell = Spells.getSpellForItem(world.getSeed(), reagent.getItem());

        if (spell != null) {
            boolean miscast = false;
            int increments = useTicks / SharedConstants.TICKS_PER_SECOND;
            int cost = spell.cost();
            if (increments == 0) {
                miscast = true;
            } else {
                increments = increments - 1;
                cost += increments * spell.perIncrementCost();
            }

            if (increments > BASE_MAX_INCREMENTS) {
                double overCharge = increments - BASE_MAX_INCREMENTS;
                double miscastChance = overCharge / (double) BASE_MAX_INCREMENTS;
                double roll = Math.random();
                if (miscastChance > roll) {
                    miscast = true;
                }
            } else {
                if (reagent.getCount() < cost) {
                    miscast = true;
                }
            }

            if (miscast) {
                this.causeMagicFeedback(wand, user, world);
            } else {
                reagent.decrement(cost);
                spell.castSpell(user, world, reagent);
                wand.damage(WAND_BASE_USE_DAMAGE, user, this.chargingWandHand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }

            MiningMagic.LOGGER.info("cast details, use ticks = {}, cost = {}, increments = {}, miscast = {}", useTicks, cost, increments, miscast);
        }

        user.setStackInHand(otherHand, reagent);
    }

    private void causeMagicFeedback(ItemStack wand, LivingEntity caster, World world) {
        caster.damage(world.getDamageSources().magic(), WAND_FEEDBACK_CASTER_DAMAGE);
        wand.damage(WAND_BASE_USE_DAMAGE * WAND_FEEDBACK_USE_MULTIPLIER, caster, this.chargingWandHand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }
}
