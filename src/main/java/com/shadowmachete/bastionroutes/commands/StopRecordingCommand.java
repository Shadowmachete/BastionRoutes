package com.shadowmachete.bastionroutes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.shadowmachete.bastionroutes.routes.RouteManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import static net.minecraft.server.command.CommandManager.literal;

public class StopRecordingCommand {
    public static void register(CommandDispatcher<ServerCommandSource> sourceCommandDispatcher) {
        sourceCommandDispatcher.register(
                literal("save")
                        .executes(context -> {
                            RouteManager.stopRecordingRoute();
                            context.getSource().getPlayer().sendMessage(
                                    new LiteralText("Saved route: " + RouteManager.newRouteName), false
                            );
                            return 0;
                        })
        );
    }
}
