package com.shadowmachete.bastionroutes.mixin;

import com.shadowmachete.bastionroutes.BastionRoutes;
import com.shadowmachete.bastionroutes.bastion.BastionStorage;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(StructureBlock.class)
public abstract class MixinStructureBlock {
    @Inject(method = "doAction", at = @At("HEAD"))
    private void doAction(StructureBlockBlockEntity blockEntity, CallbackInfo ci) {
        if (Objects.requireNonNull(blockEntity.getMode()) == StructureBlockMode.LOAD && blockEntity.getStructureName().contains("bastionbuilder") && !blockEntity.getStructureName().contains("processors")) {
            // This is also probably an indicator that we're in a Bastion/LBP world, but we already check that on player connect
            // Just in case lets update here too
            // Sorry if that's not the intention
            BastionRoutes.setInLBP(true);
            BastionStorage.getInstance().addBastionDataFromStructureBlock(blockEntity);
        }
    }
}
