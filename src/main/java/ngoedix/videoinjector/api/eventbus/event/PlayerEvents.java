package ngoedix.videoinjector.api.eventbus.event;

import ngoedix.videoinjector.api.DynamicResourceLocation;
import ngoedix.videoinjector.api.MediaPlayerHandler;
import ngoedix.videoinjector.api.mediaPlayer.AbstractMediaPlayer;

public class PlayerEvents {

    private PlayerEvents() {

    }

    /**
     * Fired when VLCJ fires a finished playback event.
     * @since 2.2.0.4
     */
    public static class PlayerFinishedEvent extends Event implements PlayerEvent {

        DynamicResourceLocation player;

        public PlayerFinishedEvent(DynamicResourceLocation player) {
            this.player = player;
        }

        @Override
        public boolean isCancelable() {
            return false;
        }

        @Override
        public DynamicResourceLocation getPlayer() {
            return player;
        }

        public AbstractMediaPlayer getMediaPlayer() {
            return MediaPlayerHandler.getInstance().getMediaPlayer(player);
        }
    }
}
