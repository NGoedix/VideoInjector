package ngoedix.videoinjector.example;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import ngoedix.videoinjector.api.eventbus.event.Event;

public class DrawBackgroundEvent extends Event {
    private final Screen screen;
    private MatrixStack poseStack;

    public DrawBackgroundEvent(Screen screen, MatrixStack matrixStack) {
        this.screen = screen;
        this.poseStack = matrixStack;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    public Screen getScreen() {
        return screen;
    }

    public MatrixStack getPoseStack() {
        return poseStack;
    }

    public void setPoseStack(MatrixStack poseStack) {
        this.poseStack = poseStack;
    }
}

