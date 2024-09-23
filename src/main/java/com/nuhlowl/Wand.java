package com.nuhlowl;

import com.nuhlowl.spells.Spell;
import com.nuhlowl.spells.SpellCastResult;
import com.nuhlowl.spells.Spells;
import net.minecraft.component.ComponentMap;
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
        if (user instanceof PlayerEntity playerEntity && world instanceof ServerWorld serverWorld) {
            int useTicks = this.getMaxUseTime(stack, user) - remainingUseTicks;
            if (useTicks > 10) {
//                ItemStack item = user.getStackInHand(this.chargingWandHand);
                Hand otherHand = this.chargingWandHand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
                ItemStack reagent = user.getStackInHand(otherHand);

                Spell spell = Spells.getSpellForItem(serverWorld.getSeed(), reagent.getItem());

                if (spell != null) {
                    SpellCastResult result = spell.castSpell(playerEntity, world, reagent);
                    if (!user.isInCreativeMode()) {
                        reagent.decrement(result.cost);
                    }
                    user.setStackInHand(otherHand, reagent);
                }
            } else {
                MiningMagic.LOGGER.info("fail");
            }
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
}
