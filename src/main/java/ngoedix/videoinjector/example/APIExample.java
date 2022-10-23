package ngoedix.videoinjector.example;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.OptionsScreen;
import ngoedix.videoinjector.Constants;
import ngoedix.videoinjector.api.DynamicResourceLocation;
import ngoedix.videoinjector.api.MediaPlayerHandler;
import ngoedix.videoinjector.api.eventbus.EventPhase;
import ngoedix.videoinjector.api.eventbus.VideoInjectorEvent;
import ngoedix.videoinjector.api.eventbus.event.PlayerEvents;
import ngoedix.videoinjector.api.eventbus.event.PlayerRegistryEvent;
import ngoedix.videoinjector.api.mediaPlayer.MediaPlayerBase;
import ngoedix.videoinjector.api.mediaPlayer.SimpleMediaPlayer;

public class APIExample {
    boolean init = false;
    DynamicResourceLocation resourceLocation;

    @VideoInjectorEvent
    @SuppressWarnings("unused")
    public void init(PlayerRegistryEvent.AddPlayerEvent event) {
        Constants.LOG.info("Setting up example media player");
        resourceLocation = new DynamicResourceLocation(Constants.MOD_ID, "example");
        event.handler().registerPlayerOnFreeResLoc(resourceLocation, SimpleMediaPlayer.class);
        if (event.handler().getMediaPlayer(resourceLocation).providesAPI()) {
            event.handler().getMediaPlayer(resourceLocation).api().media().prepare("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
            event.handler().getMediaPlayer(resourceLocation).api().audio().setVolume(200);
        } else {
            Constants.LOG.warn("Example running in NO_LIBRARY_MODE");
        }
    }

    @VideoInjectorEvent(player = Constants.MOD_ID + ":" + "example")
    @SuppressWarnings("unused")
    public void onFinished(PlayerEvents.PlayerFinishedEvent event) {
        Constants.LOG.info("Finished Event Fired");
        if (event.getMediaPlayer().providesAPI()) {
            event.getMediaPlayer().api().media().play("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        }
    }

    @VideoInjectorEvent
    @SuppressWarnings("unused")
    public void drawBackground(DrawBackgroundEvent event) {
        MediaPlayerBase mediaPlayer = (MediaPlayerBase) MediaPlayerHandler.getInstance().getMediaPlayer(resourceLocation);
        if (event.getScreen() instanceof OptionsScreen && resourceLocation != null && mediaPlayer != null) {
            if (MediaPlayerHandler.getInstance().getMediaPlayer(resourceLocation).providesAPI()) {
                if (!init) {
                    mediaPlayer.api().controls().play();
                    init = true;
                }
                // Generic Render Code for Screens
                int width = Minecraft.getInstance().screen.width;
                int height = Minecraft.getInstance().screen.height;

                Minecraft.getInstance().getTextureManager().bind(mediaPlayer.renderToResourceLocation());

                RenderSystem.enableBlend();
                RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
                AbstractGui.blit(event.getPoseStack(), 0, 0, 0.0F, 0.0F, width, height, width, height);
                RenderSystem.disableBlend();
            } else {
                // Generic Render Code for Screens
                int width = Minecraft.getInstance().screen.width;
                int height = Minecraft.getInstance().screen.height;

                int width2;

                if (width <= height) {
                    width2 = width / 3;
                } else {
                    width2 = height / 2;
                }

                Minecraft.getInstance().getTextureManager().bind(new DynamicResourceLocation(Constants.MOD_ID, "fallback"));

                RenderSystem.enableBlend();
                RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
                AbstractGui.blit(event.getPoseStack(), 0, 0, 0.0F, 0.0F, width, height, width2, width2);
                RenderSystem.disableBlend();
            }
        }
    }

    @VideoInjectorEvent(phase = EventPhase.PRE, player = Constants.MOD_ID + ":" + "example")
    @SuppressWarnings("unused")
    public void removePlayer(PlayerRegistryEvent.RemovePlayerEvent event) {
        resourceLocation = null;
    }
}

