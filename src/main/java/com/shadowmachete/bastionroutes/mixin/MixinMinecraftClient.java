package com.shadowmachete.bastionroutes.mixin;

import com.shadowmachete.bastionroutes.BastionRoutes;
import com.shadowmachete.bastionroutes.event.ClientTickHandler;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(method = "tick()V", at = @At("RETURN"))
    private void onPostKeyboardInput(CallbackInfo ci) {
        if (!BastionRoutes.inLBP()) {
            ClientTickHandler.onClientTick((MinecraftClient) (Object) this);
        }
    }
}
