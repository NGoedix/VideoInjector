package ngoedix.videoinjector.api.internal;

import ngoedix.videoinjector.api.internal.utils.IntegerBuffer2D;
import ngoedix.videoinjector.api.mediaPlayer.MediaPlayerBase;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallbackAdapter;

/**
 * This provides a simple RenderCallbackAdapter implementation
 *
 * @since 1.0.0
 */
public class MediaPlayerCallback extends RenderCallbackAdapter {
    private final MediaPlayerBase mediaPlayer;
    private int width;

    public MediaPlayerCallback(int width, MediaPlayerBase mediaPlayer) {
        this.width = width;
        this.mediaPlayer = mediaPlayer;
    }

    public void setBuffer(int sourceWidth, int sourceHeight) {
        this.width = sourceWidth;
        setBuffer(new int[sourceWidth * sourceHeight]);
    }

    @Override
    protected void onDisplay(uk.co.caprica.vlcj.player.base.MediaPlayer mediaPlayer, int[] buffer) {
        this.mediaPlayer.setIntBuffer(new IntegerBuffer2D(width, buffer));
    }
}
