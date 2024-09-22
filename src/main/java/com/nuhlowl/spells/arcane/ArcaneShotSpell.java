package com.nuhlowl.spells.arcane;

import com.nuhlowl.spells.Spell;
import com.nuhlowl.spells.SpellCastResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ArcaneShotSpell implements Spell {
    @Override
    public Text getName() {
        return Text.translatable("miningmagic.spells.arcane_shot");
    }

    @Override
    public SpellCastResult castSpell(PlayerEntity user, World world, ItemStack reagent) {
        float x = -MathHelper.sin(user.getYaw() * (float) (Math.PI / 180.0)) * MathHelper.cos(user.getPitch() * (float) (Math.PI / 180.0));
        float y = -MathHelper.sin(user.getPitch() * (float) (Math.PI / 180.0));
        float z = MathHelper.cos(user.getYaw() * (float) (Math.PI / 180.0)) * MathHelper.cos(user.getPitch() * (float) (Math.PI / 180.0));
        Vec3d facing = new Vec3d(x, y, z);
        Vec3d pos = user.getPos().add(new Vec3d(facing.x, facing.y, facing.z));
        ArcaneShotEntity arcaneShotEntity = new ArcaneShotEntity(world, pos.getX(), pos.getY() + 1.5F, pos.getZ(), Vec3d.ZERO);
        arcaneShotEntity.setOwner(user);

        arcaneShotEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F , 1.5F, 1.0F);
        world.spawnEntity(arcaneShotEntity);

        return new SpellCastResult(1);
    }
}
