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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AmethystDust extends Block {
    public static final double CENTER_HIT_RADIUS = .1;

    public static final MapCodec<AmethystDust> CODEC = createCodec(AmethystDust::new);

    public static final DirectionProperty FACING = DirectionProperty.of("facing");

    public static final EnumProperty<DustConnection> NORTH_TO_EAST_CONNECTION = EnumProperty.of("north_to_east", DustConnection.class);
    public static final EnumProperty<DustConnection> NORTH_TO_WEST_CONNECTION = EnumProperty.of("north_to_west", DustConnection.class);
    public static final EnumProperty<DustConnection> SOUTH_TO_EAST_CONNECTION = EnumProperty.of("south_to_east", DustConnection.class);
    public static final EnumProperty<DustConnection> SOUTH_TO_WEST_CONNECTION = EnumProperty.of("south_to_west", DustConnection.class);
    public static final EnumProperty<DustConnection> EAST_TO_WEST_CONNECTION = EnumProperty.of("east_to_west", DustConnection.class);
    public static final EnumProperty<DustConnection> NORTH_TO_SOUTH_CONNECTION = EnumProperty.of("north_to_south", DustConnection.class);

    public static final BooleanProperty CUSTOM_CONNECTIONS = BooleanProperty.of("custom_connections");


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
                            new Pair<>(Direction.UP, NORTH_TO_SOUTH_CONNECTION)
                    ),
                    Direction.EAST, List.of(
                            new Pair<>(Direction.UP, NORTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.WEST, EAST_TO_WEST_CONNECTION),
                            new Pair<>(Direction.DOWN, SOUTH_TO_WEST_CONNECTION)
                    ),
                    Direction.WEST, List.of(
                            new Pair<>(Direction.EAST, EAST_TO_WEST_CONNECTION),
                            new Pair<>(Direction.UP, NORTH_TO_EAST_CONNECTION),
                            new Pair<>(Direction.DOWN, SOUTH_TO_EAST_CONNECTION)
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
                            new Pair<>(Direction.DOWN, SOUTH_TO_WEST_CONNECTION)
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
                            new Pair<>(Direction.NORTH, SOUTH_TO_WEST_CONNECTION),
                            new Pair<>(Direction.SOUTH, SOUTH_TO_EAST_CONNECTION)
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
                .with(NORTH_TO_EAST_CONNECTION, DustConnection.NONE)
                .with(NORTH_TO_WEST_CONNECTION, DustConnection.NONE)
                .with(SOUTH_TO_EAST_CONNECTION, DustConnection.NONE)
                .with(SOUTH_TO_WEST_CONNECTION, DustConnection.NONE)
                .with(EAST_TO_WEST_CONNECTION, DustConnection.NONE)
                .with(NORTH_TO_SOUTH_CONNECTION, DustConnection.NONE)
                .with(CUSTOM_CONNECTIONS, false)
        );
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING)
                .add(NORTH_TO_EAST_CONNECTION)
                .add(NORTH_TO_WEST_CONNECTION)
                .add(SOUTH_TO_EAST_CONNECTION)
                .add(SOUTH_TO_WEST_CONNECTION)
                .add(EAST_TO_WEST_CONNECTION)
                .add(NORTH_TO_SOUTH_CONNECTION)
                .add(CUSTOM_CONNECTIONS);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getSide();
        List<Direction> neighbors = getDirectionOrderForFacing(ctx.getSide());

        BlockState state = super.getPlacementState(ctx);
        if (state == null) {
            state = getDefaultState();
        }

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

        return state.with(FACING, facing);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        double oneVoxel = 1.0 / 16.0;
        double lastVoxel = 1.0 - oneVoxel;

        return switch (dir) {
            case UP -> VoxelShapes.cuboid(oneVoxel, 0, oneVoxel, lastVoxel, oneVoxel, lastVoxel);
            case DOWN -> VoxelShapes.cuboid(oneVoxel, lastVoxel, oneVoxel, lastVoxel, 1.0, lastVoxel);
            case NORTH -> VoxelShapes.cuboid(oneVoxel, oneVoxel, lastVoxel, lastVoxel, lastVoxel, 1.0);
            case SOUTH -> VoxelShapes.cuboid(oneVoxel, oneVoxel, 0, lastVoxel, lastVoxel, oneVoxel);
            case EAST -> VoxelShapes.cuboid(0, oneVoxel, oneVoxel, oneVoxel, lastVoxel, lastVoxel);
            case WEST -> VoxelShapes.cuboid(lastVoxel, oneVoxel, oneVoxel, 1.0, lastVoxel, lastVoxel);
        };
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        Hand hand = player.getActiveHand();
        ItemStack itemStack = player.getStackInHand(hand);
        Direction dustFacing = state.get(FACING);

        Vec3d hitPos = hit.getPos();
        Vec3d truncated = new Vec3d((int) hitPos.x, (int) hitPos.y, (int) hitPos.z);
        Vec3d signs = new Vec3d(hitPos.x / Math.abs(hitPos.x), hitPos.y / Math.abs(hitPos.y), hitPos.z / Math.abs(hitPos.z));

        Vec3d blockMid = truncated.add(new Vec3d(.5, .5, .5).multiply(signs));
        hitPos = hitPos.subtract(blockMid);

        hitPos = switch (dustFacing.getAxis()) {
            case X -> new Vec3d(0, hitPos.y, hitPos.z);
            case Y -> new Vec3d(hitPos.x, 0, hitPos.z);
            case Z -> new Vec3d(hitPos.x, hitPos.y, 0);
        };

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

        if (itemStack.isOf(MiningMagic.WAND)) {
            world.removeBlock(pos, false);

            BlockState runeState = MiningMagic.AMETHYST_RUNE.getDefaultState()
                    .with(AmethystRune.FACING, dustFacing)
                    .with(AmethystRune.PLACEMENT, closestDir);
            world.setBlockState(pos, runeState);
        } else {
            if (!isNeighborIsConnectable(pos, closestDir, world)) {
                // no connection on clicked side
                return ActionResult.FAIL;
            }

            List<Pair<Direction, EnumProperty<DustConnection>>> possibleConnections = FACING_NEIGHBOR_TO_POSSIBLE_CONNECTIONS.get(dustFacing).get(closestDir);
            if (possibleConnections == null) {
                // catch any strange clicks that result in an invalid facing-direction combination
                return ActionResult.SUCCESS_NO_ITEM_USED;
            }

            // created fixed combination order
            List<List<Pair<Direction, EnumProperty<DustConnection>>>> combinations = List.of(
                    List.of(
                            possibleConnections.get(0),
                            possibleConnections.get(1),
                            possibleConnections.get(2)
                    ),
                    List.of(
                            possibleConnections.get(0),
                            possibleConnections.get(1)
                    ),
                    List.of(
                            possibleConnections.get(0),
                            possibleConnections.get(2)
                    ),
                    List.of(
                            possibleConnections.get(1),
                            possibleConnections.get(2)
                    ),
                    List.of(
                            possibleConnections.get(0)
                    ),
                    List.of(
                            possibleConnections.get(1)
                    ),
                    List.of(
                            possibleConnections.get(2)
                    )
            );

            List<List<Pair<Direction, EnumProperty<DustConnection>>>> available = combinations.stream()
                    .filter(combo -> combo.stream().allMatch(item -> isNeighborIsConnectable(pos, item.getLeft(), world)))
                    .collect(Collectors.toList());

            available.add(List.of());

            BlockState finalState = state;
            int current = available.stream()
                    .filter(combo -> combo.stream().allMatch(item -> finalState.get(item.getRight()).isConnected()))
                    .findFirst()
                    .map(available::indexOf)
                    .orElse(0);
            MiningMagic.LOGGER.info("available {}", available.size());

            current += 1;
            if (current >= available.size()) {
                current = 0;
            }
            MiningMagic.LOGGER.info("current {}", current);

            List<Pair<Direction, EnumProperty<DustConnection>>> next = available.get(current);
            List<Pair<Direction, EnumProperty<DustConnection>>> toDisconnect = new ArrayList<>() {{
                add(possibleConnections.get(0));
                add(possibleConnections.get(1));
                add(possibleConnections.get(2));
            }};

            for (Pair<Direction, EnumProperty<DustConnection>> pair : next) {
                MiningMagic.LOGGER.info("connecting {} to {}", closestDir, pair.getLeft());
                state = state.with(pair.getRight(), DustConnection.CONNECTED);
                toDisconnect.remove(pair);
            }

            for (Pair<Direction, EnumProperty<DustConnection>> pair : toDisconnect) {
                MiningMagic.LOGGER.info("disconnecting {} to {}", closestDir, pair.getLeft());
                state = state.with(pair.getRight(), DustConnection.NONE);
            }

            world.setBlockState(pos, state.with(CUSTOM_CONNECTIONS, true));
        }
        return ActionResult.SUCCESS;
    }


    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(CUSTOM_CONNECTIONS)) {
            // don't auto connect customized connections
            return state;
        }

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

        return state;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
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
