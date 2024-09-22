package com.nuhlowl.spells.arcane;

import com.nuhlowl.spells.Spell;
import com.nuhlowl.spells.SpellCastResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class ArcaneShotSpell implements Spell {
    @Override
    public Text getName() {
        return Text.translatable("miningmagic.spells.arcane_shot");
    }

    @Override
    public SpellCastResult castSpell(PlayerEntity user, World world, ItemStack reagent) {
        ArcaneShotEntity arcaneShotEntity = new ArcaneShotEntity(world, user.getPos().getX(), user.getEyePos().getY(), user.getPos().getZ());
        arcaneShotEntity.setOwner(user);
        arcaneShotEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F , 1.5F, 1.0F);

        world.spawnEntity(arcaneShotEntity);

        return new SpellCastResult(1);
    }
}
