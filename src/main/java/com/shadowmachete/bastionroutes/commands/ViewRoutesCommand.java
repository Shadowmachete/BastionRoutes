package com.shadowmachete.bastionroutes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.shadowmachete.bastionroutes.bastion.BastionStorage;
import com.shadowmachete.bastionroutes.routes.RouteManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.util.Arrays;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ViewRoutesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> sourceCommandDispatcher) {
        sourceCommandDispatcher.register(
                literal("viewroutes")
                        .executes(context -> {
                            if (BastionStorage.getInstance().getCurrentBastion() == null) {
                                context.getSource().getPlayer().sendMessage(
                                        new LiteralText("No current bastion."), false
                                );
                                return 0;
                            }

                            String[] routes = RouteManager.getRoutes();
                            if (routes.length == 0) {
                                context.getSource().getPlayer().sendMessage(
                                        new LiteralText("No routes found for current bastion."), false
                                );
                                return 0;
                            }
                            context.getSource().getPlayer().sendMessage(
                                    new LiteralText("Routes: " + String.join(", ", routes)), false
                            );
                            return 0;
                        })

                        .then(argument("bastionType", string())
                                .executes(context -> {
                                    String bastionType = getString(context, "bastionType");
                                    String[] routes = RouteManager.getRoutes(bastionType);
                                    if (routes.length == 0) {
                                        context.getSource().getPlayer().sendMessage(
                                                new LiteralText("No routes found for bastion type: " + bastionType), false
                                        );
                                        return 0;
                                    }
                                    context.getSource().getPlayer().sendMessage(
                                            new LiteralText("Routes: " + String.join(", ", routes)), false
                                    );
                                    return 0;
                                })
                        )
        );
    }
}

