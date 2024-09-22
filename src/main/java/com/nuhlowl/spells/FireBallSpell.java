package com.nuhlowl.spells;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireBallSpell implements Spell {
    @Override
    public Text getName() {
        return Text.translatable("miningmagic.spells.fireball");
    }

    @Override
    public SpellCastResult castSpell(PlayerEntity user, World world, ItemStack reagent) {

        float x = -MathHelper.sin(user.getYaw() * (float) (Math.PI / 180.0)) * MathHelper.cos(user.getPitch() * (float) (Math.PI / 180.0));
        float y = -MathHelper.sin(user.getPitch() * (float) (Math.PI / 180.0));
        float z = MathHelper.cos(user.getYaw() * (float) (Math.PI / 180.0)) * MathHelper.cos(user.getPitch() * (float) (Math.PI / 180.0));
        Vec3d facing = new Vec3d(x, y, z);
        Vec3d pos = user.getPos().add(new Vec3d(facing.x, facing.y, facing.z));
        SmallFireballEntity smallFireballEntity = new SmallFireballEntity(world, pos.getX(), pos.getY() + 1.5F, pos.getZ(), Vec3d.ZERO);
        //            smallFireballEntity.setItem(stack);
        smallFireballEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F , 1.5F, 1.0F);
        world.spawnEntity(smallFireballEntity);

        return new SpellCastResult(1);
    }
}
