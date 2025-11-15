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

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Starting up...");
        registerRenderers();
	}

    private void registerRenderers() {
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