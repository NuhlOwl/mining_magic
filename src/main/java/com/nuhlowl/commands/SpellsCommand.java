package com.nuhlowl.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.nuhlowl.MiningMagic;
import com.nuhlowl.spells.Spell;
import com.nuhlowl.spells.Spells;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpellsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("spells").requires(source -> source.hasPermissionLevel(2)).executes(context -> {
            long l = context.getSource().getWorld().getSeed();

            List<Text> lines = new ArrayList<>();
            lines.add(Text.translatable("miningmagic.commands.spells.header"));
            for (Map.Entry<Item, Spell> entry : Spells.SPELL_MAPS_BY_SEED.get(l).entrySet()) {
                Text text = Text.translatable("miningmagic.commands.spells.spell_item_combination",
                        entry.getKey().getName(), entry.getValue().getName());
                lines.add(text);
            }
            context.getSource().sendFeedback(() -> Texts.join(lines, Text.of("\n")), false);
            return (int) l;
        }));
    }
}
