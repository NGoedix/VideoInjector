package ngoedix.videoinjector;

import ngoedix.videoinjector.api.DynamicResourceLocation;
import ngoedix.videoinjector.api.eventbus.EventPriority;
import ngoedix.videoinjector.api.eventbus.VideoInjectorEvent;
import ngoedix.videoinjector.api.eventbus.event.PlayerRegistryEvent;
import ngoedix.videoinjector.api.internal.utils.IntegerBuffer2D;
import ngoedix.videoinjector.api.mediaPlayer.SimpleMediaPlayer;
import ngoedix.videoinjector.internal.SimpleTextRenderer;
import ngoedix.videoinjector.internal.Util;

public class VideoInjectorEvents {
    public static final DynamicResourceLocation fallback = new DynamicResourceLocation(Constants.MOD_ID, "fallback");

    public static final String UNSUPPORTED = "It seems you're using an unsupported platform.";
    public static final String UNSUPPORTED2 = "Head to bit.ly/vlcBeta and try installing the latest version for your OS.";

    @VideoInjectorEvent(priority = EventPriority.SURPREME)
    @SuppressWarnings("unused")
    public static void addDefaultPlayer(PlayerRegistryEvent.AddPlayerEvent event) {
        event.handler().registerPlayerOnFreeResLoc(fallback, SimpleMediaPlayer.class);
        IntegerBuffer2D buffer = Util.injectableTextureFromJar("VLCMissing.png", VideoInjectorEvent.class.getClassLoader(), 1024);

        buffer.bulkPut(SimpleTextRenderer.greatestSizedText(UNSUPPORTED, 1024, 310, -1), 0, 0, true); //1024 * 120
        buffer.bulkPut(SimpleTextRenderer.greatestSizedText(UNSUPPORTED2, 1024, 310, -1), 0, 710, true);

        ((SimpleMediaPlayer) event.handler().getMediaPlayer(fallback)).setIntBuffer(buffer);
        ((SimpleMediaPlayer) event.handler().getMediaPlayer(fallback)).renderToResourceLocation();
    }
}
