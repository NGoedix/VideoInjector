package ngoedix.videoinjector.api.mediaPlayer;

import net.minecraft.client.renderer.texture.NativeImage;
import ngoedix.videoinjector.Constants;
import ngoedix.videoinjector.api.DefaultBufferFormatCallback;
import ngoedix.videoinjector.api.DynamicResourceLocation;
import ngoedix.videoinjector.api.MediaPlayerHandler;
import ngoedix.videoinjector.api.eventbus.VideoInjectorEventBus;
import ngoedix.videoinjector.api.eventbus.event.PlayerEvents;
import ngoedix.videoinjector.api.internal.utils.IntegerBuffer2D;
import uk.co.caprica.vlcj.player.component.CallbackMediaListPlayerComponent;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventAdapter;

import java.util.concurrent.Semaphore;

/**
 * For most use cases this implementation should be enough.
 *
 * @see MediaPlayerBase
 * @see AbstractMediaPlayer
 * @since 1.0.0
 */
public class SimpleMediaPlayer extends MediaPlayerBase {
    // Frame Holders
    protected final Semaphore semaphore = new Semaphore(1, true);
    // MediaPlayerCallback
    protected CallbackMediaPlayerComponent mediaPlayerComponent;
    protected IntegerBuffer2D videoFrame = new IntegerBuffer2D(1, 1);

    public SimpleMediaPlayer(DynamicResourceLocation resourceLocation) {
        super(resourceLocation);
        if (Constants.NO_LIBRARY_MODE) {
            mediaPlayerComponent = null;
        } else {
            mediaPlayerComponent = new CallbackMediaListPlayerComponent(MediaPlayerHandler.getInstance().getFactory(), null, null, true, null, callback, new DefaultBufferFormatCallback(this), null);

            mediaPlayerComponent.mediaPlayer().subitems().events().addMediaListPlayerEventListener(new MediaListPlayerEventAdapter() {
                @Override
                public void mediaListPlayerFinished(MediaListPlayer mediaList) {
                    Constants.LOG.debug("Called MLPF-Event.");
                    mediaList.submit(
                            () -> VideoInjectorEventBus.getInstance().runEvent(new PlayerEvents.PlayerFinishedEvent(resourceLocation))
                    );
                }
            });
        }
    }

    /**
     * @return The VLCJ API.
     * @since 1.0.0
     */
    @Override
    public EmbeddedMediaPlayer api() {
        return Constants.NO_LIBRARY_MODE ? null : mediaPlayerComponent.mediaPlayer();
    }

    @Override
    public void markToRemove() {
        super.markToRemove();
        MediaPlayerHandler.getInstance().flagPlayerRemoval(dynamicResourceLocation);
    }

    @Override
    public void cleanup() {
        if (Constants.LOG.isDebugEnabled()) {
            Constants.LOG.debug("Removing Player '{}'", dynamicResourceLocation.toWorkingString());
        }
        if (providesAPI()) {
            mediaPlayerComponent.mediaPlayer().controls().stop();
            mediaPlayerComponent.release();
        }
    }

    @Override
    public int[] getIntFrame() {
        try {
            semaphore.acquire();
            IntegerBuffer2D temp = new IntegerBuffer2D(videoFrame);
            semaphore.release();
            return temp.getArray();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return new int[0];
    }

    /**
     * This returns the width of the current video frame. <br>
     * Some useful math: <br>
     * int x = index % getWidth(); <br>
     * int y = index / getWidth(); <br>
     *
     * @since 1.0.0
     */
    @Override
    public int getWidth() {
        int width = 0;
        try {
            semaphore.acquire();
            width = videoFrame.getWidth();
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return width;
    }

    public IntegerBuffer2D getIntBuffer() {
        try {
            semaphore.acquire();
            IntegerBuffer2D currentFrame = new IntegerBuffer2D(videoFrame);
            semaphore.release();
            return currentFrame;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return super.getIntBuffer();
    }

    @Override
    public void setIntBuffer(IntegerBuffer2D in) {
        try {
            semaphore.acquire();
            videoFrame = new IntegerBuffer2D(in);
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public DynamicResourceLocation renderToResourceLocation() {
        IntegerBuffer2D buffer2D = getIntBuffer();
        int width = buffer2D.getWidth();
        if (width == 0) {
            return dynamicResourceLocation;
        }
        image = new NativeImage(width, buffer2D.getHeight(), true);
        for (int i = 0; i < buffer2D.getHeight(); i++) {
            for (int j = 0; j < width; j++) {
                image.setPixelRGBA(j, i, buffer2D.get(j, i));
            }
        }
        dynamicTexture.setPixels(image);
        return dynamicResourceLocation;
    }

    @Override
    public boolean providesAPI() {
        return mediaPlayerComponent != null;
    }
}
