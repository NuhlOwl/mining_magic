package com.nuhlowl;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AmethystRune extends Block {
    public static final int MAX_RUNE = 15;
    public static final double CENTER_HIT_RADIUS = .25;

    public static final MapCodec<AmethystRune> CODEC = createCodec(AmethystRune::new);

    public static final DirectionProperty PLACEMENT = DirectionProperty.of("placement");
    public static final DirectionProperty FACING = DirectionProperty.of("facing");
    public static final IntProperty RUNE_PROPERTY = IntProperty.of("rune", 0, MAX_RUNE);

    public AmethystRune(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(PLACEMENT, Direction.NORTH)
                .with(FACING, Direction.UP)
                .with(RUNE_PROPERTY, 0)
        );
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PLACEMENT)
                .add(FACING)
                .add(RUNE_PROPERTY);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        double oneVoxel = 1.0/16.0;
        double lastVoxel = 1.0 - oneVoxel;

        switch (dir) {
            case UP:
                return VoxelShapes.cuboid(oneVoxel, 0, oneVoxel, lastVoxel, oneVoxel, lastVoxel);
            case DOWN:
                return VoxelShapes.cuboid(oneVoxel, lastVoxel, oneVoxel, lastVoxel, 1.0, lastVoxel);
            case NORTH:
                return VoxelShapes.cuboid(oneVoxel, oneVoxel, lastVoxel, lastVoxel, lastVoxel, 1.0);
            case SOUTH:
                return VoxelShapes.cuboid(oneVoxel, oneVoxel, 0, lastVoxel, lastVoxel, oneVoxel);
            case EAST:
                return VoxelShapes.cuboid(0, oneVoxel, oneVoxel, oneVoxel, lastVoxel, lastVoxel);
            case WEST:
                return VoxelShapes.cuboid(lastVoxel, oneVoxel, oneVoxel, 1.0, lastVoxel, lastVoxel);
        }

        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        int rune = state.get(RUNE_PROPERTY);
        if (rune >= MAX_RUNE) {
            rune = 0;
        } else {
            rune += 1;
        }
        world.setBlockState(pos, state.with(RUNE_PROPERTY, rune), Block.NOTIFY_ALL);
        return ActionResult.SUCCESS;
    }
}
