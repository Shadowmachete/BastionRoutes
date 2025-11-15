package com.shadowmachete.bastionroutes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.shadowmachete.bastionroutes.routes.RouteManager;
import com.shadowmachete.bastionroutes.waypoints.Coordinates;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetCurrentRouteCommand {
    public static void register(CommandDispatcher<ServerCommandSource> sourceCommandDispatcher) {
        sourceCommandDispatcher.register(
                literal("setcurrentroute")
                        .then(argument("routeName", string())
                                .executes(context -> {
                                    String routeName = getString(context, "routeName");
                                    RouteManager.setCurrentRoute(routeName);
                                    context.getSource().getPlayer().sendMessage(
                                            new LiteralText("Set current route to: " + routeName), false
                                    );
                                    return 0;
                                })
                        )
        );
    }
}
