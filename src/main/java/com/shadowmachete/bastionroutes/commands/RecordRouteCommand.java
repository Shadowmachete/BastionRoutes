package com.shadowmachete.bastionroutes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.shadowmachete.bastionroutes.routes.RouteManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RecordRouteCommand {
    public static void register(CommandDispatcher<ServerCommandSource> sourceCommandDispatcher) {
        sourceCommandDispatcher.register(
                literal("record")
                        .executes(context -> {
                            RouteManager.recordRoute();
                            context.getSource().getPlayer().sendMessage(
                                    new LiteralText("Started recording route: " + RouteManager.newRouteName), false
                            );
                            return 0;
                        })

                        .then(argument("routeName", string())
                                .executes(context -> {
                                    String routeName = getString(context, "routeName");
                                    RouteManager.recordRoute(routeName);
                                    context.getSource().getPlayer().sendMessage(
                                            new LiteralText("Started recording route: " + routeName), false
                                    );
                                    return 0;
                                })
                        )
        );
    }
}
