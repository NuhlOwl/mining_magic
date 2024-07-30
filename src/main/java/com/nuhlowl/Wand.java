package com.nuhlowl;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Wand extends Item {
    public Wand(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack item = user.getStackInHand(hand);
        Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack reagent = user.getStackInHand(otherHand);

        if (reagent.isOf(Items.REDSTONE)) {
            float x = -MathHelper.sin(user.getYaw() * (float) (Math.PI / 180.0)) * MathHelper.cos(user.getPitch() * (float) (Math.PI / 180.0));
            float y = -MathHelper.sin(user.getPitch() * (float) (Math.PI / 180.0));
            float z = MathHelper.cos(user.getYaw() * (float) (Math.PI / 180.0)) * MathHelper.cos(user.getPitch() * (float) (Math.PI / 180.0));
            Vec3d facing = new Vec3d(x, y, z);
            Vec3d pos = user.getPos().add(new Vec3d(facing.x, facing.y, facing.z));
            SmallFireballEntity smallFireballEntity = new SmallFireballEntity(world, pos.getX(), pos.getY() + 1.5F, pos.getZ(), Vec3d.ZERO);
//            smallFireballEntity.setItem(stack);
            smallFireballEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F , 1.5F, 1.0F);
            world.spawnEntity(smallFireballEntity);

            reagent.decrement(1);
            user.setStackInHand(otherHand, reagent);

            return TypedActionResult.success(item);
        }

        return TypedActionResult.fail(item);
    }
}
