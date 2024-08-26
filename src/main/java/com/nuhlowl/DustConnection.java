package com.nuhlowl;

import net.minecraft.util.StringIdentifiable;

public enum DustConnection implements StringIdentifiable {
    CONNECTED("connected"),
    NONE("none");

    private final String name;

    private DustConnection(final String name) {
        this.name = name;
    }

    public String toString() {
        return this.asString();
    }

    @Override
    public String asString() {
        return this.name;
    }

    public boolean isConnected() {
        return this != NONE;
    }
}