package com.nuhlowl;

import com.nuhlowl.network.SpellPayload;
import com.nuhlowl.spells.Spell;
import com.nuhlowl.spells.Spells;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class Wand extends Item {
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

        if (world instanceof ServerWorld serverWorld && user instanceof ServerPlayerEntity serverPlayer) {
            Spell spell = Spells.getSpellForItem(
                    serverWorld.getSeed(),
                    reagent.getItem()
            );
            if (spell != null) {
                Identifier spellId = Spells.getId(spell);
                Identifier itemId = Registries.ITEM.getId(reagent.getItem());
                if (itemId == Registries.ITEM.getDefaultId() || spellId == null) {
                    MiningMagic.LOGGER.error("Failed to get ids for spell or item. Spell ({} = {}). Item ({} = {}).", spell, spellId, reagent.getItem(), itemId);
                } else {
                    ServerPlayNetworking.send(serverPlayer, new SpellPayload(
                            spellId,
                            itemId
                    ));
                }

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
            this.castSpell(stack, user, serverWorld);
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

    public boolean isCastReady(ItemStack wand, World world, LivingEntity user) {
        CurrentSpellInfo result = getCurrentSpellInfo(wand, world, user);
        if (result.spell() == null) {
            return false;
        }

        return result.incrementTicks() > 0;
    }

    public float getChargeLevel(ItemStack wand, World world, LivingEntity user) {
        CurrentSpellInfo result = getCurrentSpellInfo(wand, world, user);
        if (result.spell() == null) return 0;

        if (result.incrementTicks() > 0) {
            float maxExtraTicks = result.spell().ticksPerIncrement() * result.spell().maxIncrements();
            if (maxExtraTicks == 0) {
                return 1.0F; // no increments, always full charge
            } else {
                float charge = (float) result.incrementTicks() / maxExtraTicks;
                return Math.min(charge, 1.0F);
            }
        } else {
            return 0;
        }
    }

    private CurrentSpellInfo getCurrentSpellInfo(ItemStack wand, World world, LivingEntity user) {
        int remainingUseTicks = user.getItemUseTimeLeft();
        int useTicks = this.getMaxUseTime(wand, user) - remainingUseTicks;
        Hand otherHand = this.chargingWandHand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack reagent = user.getStackInHand(otherHand);
        Spell spell = Spells.getClientSpellForItem(reagent.getItem());
        if (world instanceof ServerWorld serverWorld) {
            spell = Spells.getSpellForItem(serverWorld.getSeed(), reagent.getItem());
        }

        return new CurrentSpellInfo(spell, spell == null ? 0 : useTicks - spell.ticksToCast(), otherHand, reagent);
    }

    private record CurrentSpellInfo(Spell spell, int incrementTicks, Hand reagentHand, ItemStack reagent) {
    }

    private void castSpell(
            ItemStack wand,
            LivingEntity user,
            ServerWorld world
    ) {
        CurrentSpellInfo result = getCurrentSpellInfo(wand, world, user);
        if (result.spell() != null) {
            int cost = result.spell().cost();
            int increments = 0;
            boolean miscast = false;
            int remainingTicks = result.incrementTicks();

            if (remainingTicks >= 0) {
                increments = remainingTicks / result.spell().ticksPerIncrement();
                cost += increments * result.spell().perIncrementCost();

                if (increments > result.spell().maxIncrements()) {
                    double overCharge = increments - result.spell().maxIncrements();
                    double miscastChance = overCharge / (double) result.spell().maxIncrements();
                    double roll = Math.random();
                    if (miscastChance > roll) {
                        miscast = true;
                    }
                } else {
                    if (result.reagent().getCount() < cost) {
                        miscast = true;
                    }
                }
            }

            result.reagent().decrement(cost);

            if (miscast) {
                this.causeMagicFeedback(wand, user, world);
            } else {
                result.spell().castSpell(user, world, result.reagent(), increments);
                wand.damage(WAND_BASE_USE_DAMAGE, user, this.wandEquipmentSlot());
            }

            user.setStackInHand(result.reagentHand(), result.reagent());
        }
    }

    private void causeMagicFeedback(ItemStack wand, LivingEntity caster, World world) {
        caster.damage(world.getDamageSources().magic(), WAND_FEEDBACK_CASTER_DAMAGE);
        wand.damage(WAND_BASE_USE_DAMAGE * WAND_FEEDBACK_USE_MULTIPLIER, caster, wandEquipmentSlot());
    }

    private EquipmentSlot wandEquipmentSlot() {
        return this.chargingWandHand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }
}
