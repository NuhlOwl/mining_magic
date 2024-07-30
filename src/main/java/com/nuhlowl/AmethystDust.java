package com.nuhlowl;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AmethystDust extends Block {
    public static final double CENTER_HIT_RADIUS = .1;

    public static final MapCodec<AmethystDust> CODEC = createCodec(AmethystDust::new);

    public static final DirectionProperty FACING = DirectionProperty.of("facing");
    public static final BooleanProperty IS_CORNER = BooleanProperty.of("is_corner");
    public static final EnumProperty<DustConnection> NORTH_CONNECTION = EnumProperty.of("north", DustConnection.class);
    public static final EnumProperty<DustConnection> EAST_CONNECTION = EnumProperty.of("east", DustConnection.class);
    public static final EnumProperty<DustConnection> SOUTH_CONNECTION = EnumProperty.of("south", DustConnection.class);
    public static final EnumProperty<DustConnection> WEST_CONNECTION = EnumProperty.of("west", DustConnection.class);

    public static final EnumProperty<DustConnection> NORTH_TO_EAST_CONNECTION = EnumProperty.of("north_to_east", DustConnection.class);
    public static final EnumProperty<DustConnection> NORTH_TO_WEST_CONNECTION = EnumProperty.of("north_to_west", DustConnection.class);
    public static final EnumProperty<DustConnection> SOUTH_TO_EAST_CONNECTION = EnumProperty.of("south_to_east", DustConnection.class);
    public static final EnumProperty<DustConnection> SOUTH_TO_WEST_CONNECTION = EnumProperty.of("south_to_west", DustConnection.class);
    public static final EnumProperty<DustConnection> EAST_TO_WEST_CONNECTION = EnumProperty.of("east_to_west", DustConnection.class);
    public static final EnumProperty<DustConnection> NORTH_TO_SOUTH_CONNECTION = EnumProperty.of("north_to_south", DustConnection.class);

    private static final Map<Direction, Map<Direction, EnumProperty<DustConnection>>> FACING_NEIGHBOR_TO_CONNECTION = ImmutableMap.of(
            Direction.UP, ImmutableMap.of(Direction.NORTH, NORTH_CONNECTION, Direction.SOUTH, SOUTH_CONNECTION, Direction.EAST, EAST_CONNECTION, Direction.WEST, WEST_CONNECTION),
            Direction.DOWN, ImmutableMap.of(Direction.NORTH, NORTH_CONNECTION, Direction.SOUTH, SOUTH_CONNECTION, Direction.EAST, EAST_CONNECTION, Direction.WEST, WEST_CONNECTION),
            Direction.NORTH, ImmutableMap.of(Direction.UP, NORTH_CONNECTION, Direction.DOWN, SOUTH_CONNECTION, Direction.EAST, WEST_CONNECTION, Direction.WEST, EAST_CONNECTION),
            Direction.SOUTH, ImmutableMap.of(Direction.UP, NORTH_CONNECTION, Direction.DOWN, SOUTH_CONNECTION, Direction.EAST, EAST_CONNECTION, Direction.WEST, WEST_CONNECTION),
            Direction.EAST, ImmutableMap.of(Direction.UP, NORTH_CONNECTION, Direction.DOWN, SOUTH_CONNECTION, Direction.NORTH, EAST_CONNECTION, Direction.SOUTH, WEST_CONNECTION),
            Direction.WEST, ImmutableMap.of(Direction.UP, NORTH_CONNECTION, Direction.DOWN, SOUTH_CONNECTION, Direction.SOUTH, EAST_CONNECTION, Direction.NORTH, WEST_CONNECTION)
    );

    private static final Map<Direction, Map<Direction, List<Pair<Direction, EnumProperty<DustConnection>>>>> FACING_NEIGHBOR_TO_POSSIBLE_CONNECTIONS = ImmutableMap.of(
            Direction.UP, ImmutableMap.of(
                    Direction.NORTH, List.of(
                            new Pair<>(Direction.EAST, NORTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.WEST, NORTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.SOUTH, NORTH_TO_SOUTH_CONNECTION)
                    ),
                    Direction.SOUTH, List.of(
                            new Pair<>(Direction.EAST, SOUTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.WEST, SOUTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.NORTH, NORTH_TO_SOUTH_CONNECTION)
                    ),
                    Direction.EAST, List.of(
                            new Pair<>(Direction.NORTH, NORTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.WEST, EAST_TO_WEST_CONNECTION),
                            new Pair<>(Direction.SOUTH, SOUTH_TO_EAST_CONNECTION)
                    ),
                    Direction.WEST, List.of(
                            new Pair<>(Direction.EAST, EAST_TO_WEST_CONNECTION),
                            new Pair<>(Direction.NORTH, NORTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.SOUTH, SOUTH_TO_WEST_CONNECTION)
                    )
            ),
            Direction.DOWN, ImmutableMap.of(
                    Direction.NORTH, List.of(
                            new Pair<>(Direction.EAST, NORTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.WEST, NORTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.SOUTH, NORTH_TO_SOUTH_CONNECTION)
                    ),
                    Direction.SOUTH, List.of(
                            new Pair<>(Direction.EAST, SOUTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.WEST, SOUTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.NORTH, NORTH_TO_SOUTH_CONNECTION)
                    ),
                    Direction.EAST, List.of(
                            new Pair<>(Direction.NORTH, NORTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.WEST, EAST_TO_WEST_CONNECTION),
                            new Pair<>(Direction.SOUTH, SOUTH_TO_EAST_CONNECTION)
                    ),
                    Direction.WEST, List.of(
                            new Pair<>(Direction.EAST, EAST_TO_WEST_CONNECTION),
                            new Pair<>(Direction.NORTH, NORTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.SOUTH, SOUTH_TO_WEST_CONNECTION)
                    )
            ),
            Direction.NORTH, ImmutableMap.of(
                    Direction.UP, List.of(
                            new Pair<>(Direction.EAST, NORTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.WEST, NORTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.DOWN, NORTH_TO_SOUTH_CONNECTION)
                    ),
                    Direction.DOWN, List.of(
                            new Pair<>(Direction.EAST, SOUTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.WEST, SOUTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.NORTH, NORTH_TO_SOUTH_CONNECTION)
                    ),
                    Direction.EAST, List.of(
                            new Pair<>(Direction.NORTH, NORTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.WEST, EAST_TO_WEST_CONNECTION),
                            new Pair<>(Direction.SOUTH, SOUTH_TO_WEST_CONNECTION)
                    ),
                    Direction.WEST, List.of(
                            new Pair<>(Direction.EAST, EAST_TO_WEST_CONNECTION),
                            new Pair<>(Direction.NORTH, NORTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.SOUTH, SOUTH_TO_EAST_CONNECTION)
                    )
            ),
            Direction.SOUTH, ImmutableMap.of(
                    Direction.UP, List.of(
                            new Pair<>(Direction.EAST, NORTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.WEST, NORTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.DOWN, NORTH_TO_SOUTH_CONNECTION)
                    ),
                    Direction.DOWN, List.of(
                            new Pair<>(Direction.EAST, SOUTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.WEST, SOUTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.UP, NORTH_TO_SOUTH_CONNECTION)
                    ),
                    Direction.EAST, List.of(
                            new Pair<>(Direction.UP, NORTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.WEST, EAST_TO_WEST_CONNECTION),
                            new Pair<>(Direction.DOWN, SOUTH_TO_EAST_CONNECTION)
                    ),
                    Direction.WEST, List.of(
                            new Pair<>(Direction.EAST, EAST_TO_WEST_CONNECTION),
                            new Pair<>(Direction.UP, NORTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.DOWN, SOUTH_TO_EAST_CONNECTION)
                    )
            ),
            Direction.EAST, ImmutableMap.of(
                    Direction.UP, List.of(
                            new Pair<>(Direction.NORTH, NORTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.SOUTH, NORTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.DOWN, NORTH_TO_SOUTH_CONNECTION)
                    ),
                    Direction.DOWN, List.of(
                            new Pair<>(Direction.UP, NORTH_TO_SOUTH_CONNECTION),
                            new Pair<>(Direction.NORTH, SOUTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.SOUTH, SOUTH_TO_WEST_CONNECTION)
                    ),
                    Direction.NORTH, List.of(
                            new Pair<>(Direction.UP, NORTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.DOWN, SOUTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.SOUTH, EAST_TO_WEST_CONNECTION)
                    ),
                    Direction.SOUTH, List.of(
                            new Pair<>(Direction.DOWN, SOUTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.UP, NORTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.NORTH, EAST_TO_WEST_CONNECTION)
                    )
            ),
            Direction.WEST, ImmutableMap.of(
                    Direction.UP, List.of(
                            new Pair<>(Direction.NORTH, NORTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.SOUTH, NORTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.DOWN, NORTH_TO_SOUTH_CONNECTION)
                    ),
                    Direction.DOWN, List.of(
                            new Pair<>(Direction.UP, NORTH_TO_SOUTH_CONNECTION),
                            new Pair<>(Direction.NORTH, SOUTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.SOUTH, SOUTH_TO_WEST_CONNECTION)
                    ),
                    Direction.NORTH, List.of(
                            new Pair<>(Direction.UP, NORTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.DOWN, SOUTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.SOUTH, EAST_TO_WEST_CONNECTION)
                    ),
                    Direction.SOUTH, List.of(
                            new Pair<>(Direction.UP, NORTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.DOWN, SOUTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.NORTH, EAST_TO_WEST_CONNECTION)
                    )
            )
    );

    public AmethystDust(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(FACING, Direction.UP)
                .with(IS_CORNER, false)
                .with(NORTH_CONNECTION, DustConnection.NONE)
                .with(EAST_CONNECTION, DustConnection.NONE)
                .with(SOUTH_CONNECTION, DustConnection.NONE)
                .with(WEST_CONNECTION, DustConnection.NONE)
                .with(NORTH_TO_EAST_CONNECTION, DustConnection.NONE)
                .with(NORTH_TO_WEST_CONNECTION, DustConnection.NONE)
                .with(SOUTH_TO_EAST_CONNECTION, DustConnection.NONE)
                .with(SOUTH_TO_WEST_CONNECTION, DustConnection.NONE)
                .with(EAST_TO_WEST_CONNECTION, DustConnection.NONE)
                .with(NORTH_TO_SOUTH_CONNECTION, DustConnection.NONE)
        );
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING)
                .add(IS_CORNER)
                .add(NORTH_CONNECTION)
                .add(EAST_CONNECTION)
                .add(SOUTH_CONNECTION)
                .add(WEST_CONNECTION)
                .add(NORTH_TO_EAST_CONNECTION)
                .add(NORTH_TO_WEST_CONNECTION)
                .add(SOUTH_TO_EAST_CONNECTION)
                .add(SOUTH_TO_WEST_CONNECTION)
                .add(EAST_TO_WEST_CONNECTION)
                .add(NORTH_TO_SOUTH_CONNECTION);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getSide();
        List<Direction> neighbors = getDirectionOrderForFacing(ctx.getSide());

        BlockState state = super.getPlacementState(ctx);

        boolean northIsConnectable = isNeighborIsConnectable(ctx.getBlockPos(), neighbors.get(0), ctx.getWorld());
        boolean southIsConnectable = isNeighborIsConnectable(ctx.getBlockPos(), neighbors.get(1), ctx.getWorld());
        boolean eastIsConnectable = isNeighborIsConnectable(ctx.getBlockPos(), neighbors.get(2), ctx.getWorld());
        boolean westIsConnectable = isNeighborIsConnectable(ctx.getBlockPos(), neighbors.get(3), ctx.getWorld());


        if (northIsConnectable && eastIsConnectable) {
            state = state.with(NORTH_TO_EAST_CONNECTION, DustConnection.CONNECTED);
        }

        if (northIsConnectable && westIsConnectable) {
            state = state.with(NORTH_TO_WEST_CONNECTION, DustConnection.CONNECTED);
        }

        if (northIsConnectable && southIsConnectable) {
            state = state.with(NORTH_TO_SOUTH_CONNECTION, DustConnection.CONNECTED);
        }

        if (southIsConnectable && eastIsConnectable) {
            state = state.with(SOUTH_TO_EAST_CONNECTION, DustConnection.CONNECTED);
        }

        if (southIsConnectable && westIsConnectable) {
            state = state.with(SOUTH_TO_WEST_CONNECTION, DustConnection.CONNECTED);
        }

        if (eastIsConnectable && westIsConnectable) {
            state = state.with(EAST_TO_WEST_CONNECTION, DustConnection.CONNECTED);
        }

        for (Direction direction : neighbors) {
            BlockPos neighborPos = ctx.getBlockPos().offset(direction);
            BlockState neighborState = ctx.getWorld().getBlockState(neighborPos);
            EnumProperty<DustConnection> connection = FACING_NEIGHBOR_TO_CONNECTION.get(facing).get(direction);
            if (neighborState.isOf(MiningMagic.AMETHYST_DUST_BLOCK)) {
                state = state.with(connection, DustConnection.CONNECTED);
            } else {
                state = state.with(connection, DustConnection.NONE);
            }
        }

        return setIsCorner(state.with(FACING, facing));
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
        Hand hand = player.getActiveHand();
        ItemStack itemStack = player.getStackInHand(hand);
        Direction dustFacing = state.get(FACING);

        if (itemStack.isOf(MiningMagic.WAND)) {
            world.removeBlock(pos, false);


            Vec3d hitPos = hit.getPos();
            Vec3d truncated = new Vec3d((int) hitPos.x, (int) hitPos.y, (int) hitPos.z);
            Vec3d signs = new Vec3d(hitPos.x / Math.abs(hitPos.x), hitPos.y / Math.abs(hitPos.y), hitPos.z / Math.abs(hitPos.z));

            Vec3d blockMid = truncated.add(new Vec3d(.5, .5, .5).multiply(signs));
            hitPos = hitPos.subtract(blockMid);

            switch (dustFacing.getAxis()) {
                case X:
                    hitPos = new Vec3d(0, hitPos.y, hitPos.z);
                    break;
                case Y:
                    hitPos = new Vec3d(hitPos.x, 0, hitPos.z);
                    break;
                case Z:
                    hitPos = new Vec3d(hitPos.x, hitPos.y, 0);
                    break;
            }

            Direction closestDir = Direction.UP;
            if (hitPos.length() < CENTER_HIT_RADIUS) {
                closestDir = dustFacing;
            } else {
                double closestDistance = Double.MAX_VALUE;

                for (Direction dir : Direction.values()) {
                    double dif = Vec3d.of(dir.getVector()).subtract(hitPos).length();
                    if (dif < closestDistance) {
                        closestDir = dir;
                        closestDistance = dif;
                    }
                }
            }

            BlockState runeState = MiningMagic.AMETHYST_RUNE.getDefaultState()
                    .with(AmethystRune.FACING, dustFacing)
                    .with(AmethystRune.PLACEMENT, closestDir);
            world.setBlockState(pos, runeState);
        }
        return ActionResult.SUCCESS;
    }



    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.get(FACING);
        boolean lookingToConnect = neighborState.isOf(MiningMagic.AMETHYST_DUST_BLOCK);

        List<Pair<Direction, EnumProperty<DustConnection>>> possibleConnections = FACING_NEIGHBOR_TO_POSSIBLE_CONNECTIONS.get(facing).get(direction);
        if (possibleConnections == null) {
            return state;
        }

        for (Pair<Direction, EnumProperty<DustConnection>> pair : possibleConnections) {
            if (isNeighborIsConnectable(pos, pair.getLeft(), world) && lookingToConnect) {
                state = state.with(pair.getRight(), DustConnection.CONNECTED);
            } else {
                state = state.with(pair.getRight(), DustConnection.NONE);
            }
        }

        EnumProperty<DustConnection> connection = FACING_NEIGHBOR_TO_CONNECTION.get(facing).get(direction);
        if (connection == null) {
            return state;
        }

        if (neighborState.isOf(MiningMagic.AMETHYST_DUST_BLOCK)) {
            state = state.with(connection, DustConnection.CONNECTED);
        } else {
            state = state.with(connection, DustConnection.NONE);
        }
        return setIsCorner(state);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
    }

    private BlockState setIsCorner(BlockState state) {
        boolean isCorner = state.get(NORTH_CONNECTION).isConnected() && (state.get(EAST_CONNECTION).isConnected() || state.get(WEST_CONNECTION).isConnected())
                || state.get(SOUTH_CONNECTION).isConnected() && (state.get(EAST_CONNECTION).isConnected() || state.get(WEST_CONNECTION).isConnected());
        return state.with(IS_CORNER, isCorner);
    }

    private boolean isNeighborIsConnectable(BlockPos blockPos, Direction neighborDirection, WorldAccess world) {
        BlockPos neighborPos = blockPos.offset(neighborDirection);
        BlockState neighborState = world.getBlockState(neighborPos);
        return neighborState.isOf(MiningMagic.AMETHYST_DUST_BLOCK);
    }

    private List<Direction> getDirectionOrderForFacing(Direction facing) {
        return switch (facing) {
            case NORTH, SOUTH -> List.of(Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST);
            case EAST, WEST -> List.of(Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH);
            case UP, DOWN -> List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
        };
    }
}
