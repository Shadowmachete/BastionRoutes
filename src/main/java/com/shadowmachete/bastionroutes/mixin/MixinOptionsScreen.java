package com.shadowmachete.bastionroutes.mixin;

import com.shadowmachete.bastionroutes.BastionRoutes;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class MixinOptionsScreen {
    @Inject(at = @At("TAIL"), method = "init()V")
    public void init(CallbackInfo ci) {
        ((ScreenInvoker) this).invokeAddButton(new ButtonWidget(((Screen) (Object) this).width / 2 - 100, ((Screen) (Object) this).height / 6 + 140, 200, 20, new LiteralText("Bastion Waypoints Options..."), button -> {
            BastionRoutes.LOGGER.info("Opening Bastion Waypoints Options Screen");
        }));
    }
}