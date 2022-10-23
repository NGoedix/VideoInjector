package ngoedix.videoinjector.api;

import ngoedix.videoinjector.Constants;
import ngoedix.videoinjector.api.eventbus.EventPhase;
import ngoedix.videoinjector.api.eventbus.EventPriority;
import ngoedix.videoinjector.api.eventbus.VideoInjectorEvent;
import ngoedix.videoinjector.api.eventbus.VideoInjectorEventBus;
import ngoedix.videoinjector.api.eventbus.event.PlayerRegistryEvent;
import ngoedix.videoinjector.api.mediaPlayer.AbstractMediaPlayer;
import ngoedix.videoinjector.api.mediaPlayer.MediaPlayerBase;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class manages the media players. <br>
 * Use {@link #registerPlayerOnFreeResLoc(DynamicResourceLocation, Class, Object...)} to register a new {@link AbstractMediaPlayer}
 * to its unique {@link net.minecraft.util.ResourceLocation;}.
 * Use {@link #getMediaPlayer(DynamicResourceLocation)} to get your reference afterwards. <br>
 * If your player is not required anymore, use {@link AbstractMediaPlayer#markToRemove()} to free its resources. Calling {@link #getMediaPlayer(DynamicResourceLocation)} on a removed or
 * not existing player may <b> give you the fallback Player, effectively resulting in a {@link NullPointerException}</b>.
 * Use {@link #mediaPlayerExists(DynamicResourceLocation)} to check if your reference is valid. <br>
 * You <b>MUST</b> check if your reference is valid on a {@link PlayerRegistryEvent.RemovePlayerEvent},
 * best in PRE-Phase and then decide whether to {@link PlayerRegistryEvent.RemovePlayerEvent#setCanceled()}
 * or releasing all your instances of your player, so that it can be cleaned up. <br>
 * Your object may get removed on shutdown, as this {@link #shutdown()}s the library.
 *
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class MediaPlayerHandler {

    private static MediaPlayerHandler instance;

    // New System
    private final Map<DynamicResourceLocation, AbstractMediaPlayer> playerRegistry = new HashMap<>();
    private final List<DynamicResourceLocation> markedRelease = new ArrayList<>();

    private final MediaPlayerFactory factory;

    private MediaPlayerHandler(MediaPlayerFactory factory) {
        this.factory = factory;
    }

    /**
     * @return The Handler instance.
     * @since 1.0.0
     */
    public static synchronized MediaPlayerHandler getInstance() {
        if (MediaPlayerHandler.instance == null) {
            if (Constants.NO_LIBRARY_MODE) {
                MediaPlayerHandler.instance = new MediaPlayerHandler(null);
            } else {
                MediaPlayerHandler.instance = new MediaPlayerHandler(new MediaPlayerFactory("--no-metadata-network-access", "--file-logging", "--logfile", "logs/vlc.log", "--logmode", "text", "--verbose", "2", "--no-quiet"));
            }
        }
        return MediaPlayerHandler.instance;
    }

    public MediaPlayerFactory getFactory() {
        return factory;
    }

    /**
     * Registers a new Player to a ResourceLocation if it is available.
     *
     * @param resourceLocation The resourceLocation, both being rendered to and the key to get the player.
     * @param clazz            Class which player should be constructed.
     * @param arguments        Constructor arguments in the right order.
     * @return True if the Player was successfully registered, false otherwise.
     * @since 1.0.0
     */
    public synchronized boolean registerPlayerOnFreeResLoc(DynamicResourceLocation resourceLocation, Class<? extends AbstractMediaPlayer> clazz, Object... arguments) {
        if (playerRegistry.containsKey(resourceLocation)) {
            return false;
        }
        AbstractMediaPlayer player;
        try {
            player = createPlayerForMe(resourceLocation, clazz, arguments);
            registerPlayer(resourceLocation, player);
            return true;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Constants.LOG.error("Invalid instantiation", e);
        }
        return false;
    }

    /**
     * @param resourceLocation {@link DynamicResourceLocation} to register to.
     * @param mediaPlayer      {@link AbstractMediaPlayer} to register.
     * @return True if the player was registered, false if there was a conflict. In case of a conflict, you need to
     * immediately set you instance to null, otherwise you will cause issues. Using
     * {@link #registerPlayerOnFreeResLoc(DynamicResourceLocation, Class, Object...)} will take care of this for you,
     * so <b>please</b> use it instead.
     * @see #registerPlayerOnFreeResLoc(DynamicResourceLocation, Class, Object...)
     * @since 1.0.0
     * @deprecated Use {@link #registerPlayerOnFreeResLoc(DynamicResourceLocation, Class, Object...)} instead.
     */
    public synchronized boolean registerPlayerOnFreeResLoc(DynamicResourceLocation resourceLocation, AbstractMediaPlayer mediaPlayer) {
        if (playerRegistry.containsKey(resourceLocation)) {
            return false;
        }
        registerPlayer(resourceLocation, mediaPlayer);
        return true;
    }

    /**
     * Internal Player registering methode.
     *
     * @param resourceLocation Location to register to.
     * @param mediaPlayer      MediaPlayer to register.
     * @see #registerPlayerOnFreeResLoc(DynamicResourceLocation, Class, Object...)
     * @since 1.0.0
     */
    private synchronized void registerPlayer(DynamicResourceLocation resourceLocation, AbstractMediaPlayer mediaPlayer) {
        if (playerRegistry.containsKey(resourceLocation) && Constants.LOG.isWarnEnabled()) {
            Constants.LOG.warn("Registering MediaPlayer of type '{}' to ResourceLocation '{}', although one is already " +
                    "present with this ResourceLocation. If this is an intended overwrite, 'release()' should be called before " +
                    "re-registering the media player.", mediaPlayer.getClass().getName(), resourceLocation.toString());
        }
        playerRegistry.put(resourceLocation, mediaPlayer);
    }

    /**
     * Methode used to create instances of MediaPlayers
     *
     * @param resourceLocation resourceLocation of the new Player.
     * @param clazz            class from which an instance is created.
     * @param arguments        arguments for the constructor.
     * @param <T>              Type of the new Instance.
     * @return A new instance of the given Type.
     * @see #registerPlayerOnFreeResLoc(DynamicResourceLocation, Class, Object...)
     * @since 1.0.0
     */
    private <T extends AbstractMediaPlayer> T createPlayerForMe(DynamicResourceLocation resourceLocation, Class<T> clazz, Object... arguments) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] allArgClass;
        Object[] allArgs;
        int j = (MediaPlayerBase.class.isAssignableFrom(clazz)) ? 1 : 0;
        if (arguments != null) {
            int i = j;
            allArgClass = new Class[arguments.length + j];
            allArgs = new Object[arguments.length + j];
            for (Object o : arguments) {
                allArgClass[i] = o.getClass();
                allArgs[i] = o;
                i++;
            }
        } else {
            allArgClass = new Class[j];
            allArgs = new Object[j];
        }
        if (MediaPlayerBase.class.isAssignableFrom(clazz)) {
            allArgClass[0] = resourceLocation.getClass();
            allArgs[0] = resourceLocation;
        }

        return clazz.getDeclaredConstructor(allArgClass).newInstance(allArgs);
    }

    /**
     * @param resourceLocation ResourceLocation for which we want to get the MediaPlayer
     * @return The MediaPlayer for the given ResourceLocation
     * @since 1.0.0
     */
    public AbstractMediaPlayer getMediaPlayer(DynamicResourceLocation resourceLocation) {
        return playerRegistry.getOrDefault(resourceLocation, playerRegistry.get(new DynamicResourceLocation(Constants.MOD_ID, "fallback")));
    }

    /**
     * Checks if a Player exists for the given Location.
     *
     * @param resourceLocation The Location to check.
     * @return True if a player exists, false otherwise.
     * @since 1.0.0
     */
    public boolean mediaPlayerExists(DynamicResourceLocation resourceLocation) {
        return playerRegistry.get(resourceLocation) != null;
    }

    /**
     * Marks players for removal.
     *
     * @param resourceLocation Location of the players to be removed.
     * @since 1.0.0
     */
    public synchronized void flagPlayerRemoval(DynamicResourceLocation resourceLocation) {
        VideoInjectorEventBus.getInstance().runEvent(new PlayerRegistryEvent.RemovePlayerEvent(resourceLocation));
    }

    /**
     * Clears all internal references to removal merked players
     *
     * @see #flagPlayerRemoval(DynamicResourceLocation)
     * @since 1.0.0
     */
    @VideoInjectorEvent(priority = EventPriority.LOWEST, phase = EventPhase.EVENT)
    public synchronized void gc(PlayerRegistryEvent.RemovePlayerEvent event) {
        if (!event.isCanceled()) {
            getMediaPlayer(event.resourceLocation).cleanup();
            playerRegistry.remove(event.resourceLocation);
        }
    }

    /**
     * <b>DO NOT CALL THIS! NEVER! I'M SERIOUSLY! DO NOT CALL THIS!</b> <br>
     * This is for internal usage only. If you call this, <b>YOU WILL CAUSE PROBLEMS</b>!
     * Seriously, if you do, I'll take care of your calling class, for sure!
     * <p>
     * since 1.0.0
     */
    public void shutdown() {
        Constants.LOG.info("Running shutdown");
        playerRegistry.values().forEach(AbstractMediaPlayer::cleanup);
        Constants.LOG.info("Running shutdown step 1 finished");
        playerRegistry.clear();
        Constants.LOG.info("Running shutdown step 2 finished");
        if (factory != null) {
            factory.release();
        }
        Constants.LOG.info("Running shutdown completely finished");
    }
}
