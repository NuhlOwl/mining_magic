package com.nuhlowl.network;

import com.nuhlowl.MiningMagic;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SpellPayload(Identifier spell, Identifier item) implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of(MiningMagic.MOD_ID, "spell");
    public static final CustomPayload.Id<SpellPayload> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<PacketByteBuf, SpellPayload> CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, SpellPayload::spell,
            Identifier.PACKET_CODEC, SpellPayload::item,
            SpellPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
