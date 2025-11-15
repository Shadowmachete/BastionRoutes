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

public class AddWaypointCommand {
    public static void register(CommandDispatcher<ServerCommandSource> sourceCommandDispatcher) {
        sourceCommandDispatcher.register(
                literal("add")
                        .executes(context -> {
                            Coordinates coords = new Coordinates(
                                    context.getSource().getPlayer().getBlockPos().getX(),
                                    context.getSource().getPlayer().getBlockPos().getY(),
                                    context.getSource().getPlayer().getBlockPos().getZ()
                            );
                            RouteManager.recordWaypoint(coords);
                            context.getSource().getPlayer().sendMessage(
                                    new LiteralText("Added waypoint"), false
                            );
                            return 0;
                        })

                        .then(argument("waypointName", string())
                                .executes(context -> {
                                    String waypointName = getString(context, "waypointName");
                                    Coordinates coords = new Coordinates(
                                            context.getSource().getPlayer().getBlockPos().getX(),
                                            context.getSource().getPlayer().getBlockPos().getY(),
                                            context.getSource().getPlayer().getBlockPos().getZ()
                                    );
                                    RouteManager.recordWaypoint(coords, waypointName);
                                    context.getSource().getPlayer().sendMessage(
                                            new LiteralText("Added waypoint: " + waypointName), false
                                    );
                                    return 0;
                                })
                        )
        );
    }
}

