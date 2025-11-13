package com.shadowmachete.bastionroutes;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.shadowmachete.bastionroutes.render.RenderQueue;
import com.shadowmachete.bastionroutes.waypoints.Coordinates;
import com.shadowmachete.bastionroutes.waypoints.Waypoint;
import com.shadowmachete.bastionroutes.waypoints.WaypointManager;
import net.fabricmc.api.ClientModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BastionRoutes implements ClientModInitializer {
	public static final String MOD_ID = "bastion-waypoints";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Starting up...");
        WaypointManager.addWaypoint(new Waypoint(new Coordinates(5, 53, 5), "Test"));
        RenderQueue.getInstance().add("hand", matrixStack -> {
            RenderSystem.pushMatrix();
            RenderSystem.multMatrix(matrixStack.peek().getModel());
            GlStateManager.disableTexture();
            GlStateManager.disableDepthTest();
            RenderSystem.defaultBlendFunc();

            WaypointManager.renderWaypoints();

            RenderSystem.popMatrix();
        });
	}
}