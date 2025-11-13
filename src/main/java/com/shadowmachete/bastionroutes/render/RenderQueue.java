package com.shadowmachete.bastionroutes.render;

import net.minecraft.client.util.math.MatrixStack;

import java.util.*;
import java.util.function.Consumer;

public class RenderQueue {
    private final static RenderQueue INSTANCE = new RenderQueue();

    private Map<String, List<Consumer<MatrixStack>>> typeRunnableMap = new HashMap<>();
    private MatrixStack matrixStack = null;

    public static RenderQueue getInstance() {
        return INSTANCE;
    }

    public void add(String type, Consumer<MatrixStack> renderRunnable) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(renderRunnable);

        this.typeRunnableMap.putIfAbsent(type, new ArrayList<>());
        this.typeRunnableMap.get(type).add(renderRunnable);
    }

    public void remove(String type, Consumer<MatrixStack> renderRunnable) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(renderRunnable);

        if (this.typeRunnableMap.containsKey(type)) {
            this.typeRunnableMap.get(type).remove(renderRunnable);
        }
    }

    public void setTrackRender(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
    }

    public void onRender(String type) {
        if(this.matrixStack == null || !this.typeRunnableMap.containsKey(type))return;
        this.typeRunnableMap.get(type).forEach(r -> r.accept(this.matrixStack));
    }
}
