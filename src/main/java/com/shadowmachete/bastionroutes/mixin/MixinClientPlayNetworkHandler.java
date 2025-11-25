package com.shadowmachete.bastionroutes.mixin;

import static com.shadowmachete.bastionroutes.BastionRoutes.LOGGER;

import com.shadowmachete.bastionroutes.BastionRoutes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {
    @Shadow
    private MinecraftClient client;

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void onPostGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        String tag = "";

        if (client.getServer() != null) {
            tag = ((ServerAccessor) client.getServer()).getSession().getDirectoryName();
        }

        tag = tag.toLowerCase();
        // This is probably LBP world
        // LBP is named LBP 3.14.0 in the directory
        LOGGER.info("World directory name: {}", tag);
        BastionRoutes.setInLBP(tag.contains("lbp"));
    }
}
