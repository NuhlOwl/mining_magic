package com.nuhlowl.common;

import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.Direction;

public class Properties {
    public static final EnumProperty<Direction> FACING = EnumProperty.of(
            "facing",
            Direction.class,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.EAST,
            Direction.WEST
    );
}
