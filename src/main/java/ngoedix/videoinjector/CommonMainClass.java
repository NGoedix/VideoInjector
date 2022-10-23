package ngoedix.videoinjector;

import com.sun.jna.NativeLibrary;
import ngoedix.videoinjector.api.MediaPlayerHandler;
import ngoedix.videoinjector.api.eventbus.EventException;
import ngoedix.videoinjector.api.eventbus.VideoInjectorEventBus;
import ngoedix.videoinjector.api.eventbus.event.PlayerRegistryEvent;
import ngoedix.videoinjector.config.SimpleConfig;
import ngoedix.videoinjector.example.APIExample;
import ngoedix.videoinjector.internal.Arch;
import ngoedix.videoinjector.internal.DLLHandler;
import ngoedix.videoinjector.internal.LibraryMapping;
import org.apache.commons.lang3.SystemUtils;
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.factory.discovery.strategy.NativeDiscoveryStrategy;
import uk.co.caprica.vlcj.support.version.LibVlcVersion;
import uk.co.caprica.vlcj.support.version.Version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import static uk.co.caprica.vlcj.binding.lib.LibVlc.libvlc_get_version;

public class CommonMainClass {

    private final NativeDiscovery discovery = new NativeDiscovery();

    public CommonMainClass(SimpleConfig config) {
        // Detect OS
        if (SystemUtils.IS_OS_LINUX) {
            Constants.OS = "linux";
        } else if (SystemUtils.IS_OS_MAC) {
            Constants.OS = "mac";
        } else if (SystemUtils.IS_OS_WINDOWS) {
            Constants.OS = "windows";
        } else {
            Constants.OS = "unknown";
        }

        Constants.ARCH = Arch.getArch().toString();

        Constants.LOG.info("Running on OS: {} {}", Constants.OS, Constants.ARCH);

        // Delete mismatched dlls
        if (config.getAsInt("dllVersion") != Constants.DLL_VERSION || Constants.DEBUG_NO_LIBRARY_MODE) {
            Constants.LOG.info("DLL Version did change, removing old files...");
            DLLHandler.clearDLL();
            config.properties.setProperty("dllVersion", String.valueOf(Constants.DLL_VERSION));
            config.write();
        }

        // Init natives
        if (!onInit()) {
            System.exit(-9515); // TODO Run in NO_LIBRARY mode instead of causing a "soft" crash
        }

        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        // Setup Example?
        if (config.getAsBool("example")) {
            try {
                APIExample example = new APIExample();
                VideoInjectorEventBus.getInstance().registerEvent(example);
            } catch (EventException e) {
                Constants.LOG.error("A critical error happened", e);
            }
        }
    }

    public void apiSetup() {
        // Setup API
        MediaPlayerHandler.getInstance();
        try {
            VideoInjectorEventBus.getInstance().registerEvent(MediaPlayerHandler.getInstance());
            VideoInjectorEventBus.getInstance().registerEvent(VideoInjectorEvents.class);
        } catch (EventException e) {
            Constants.LOG.error("A critical error happened", e);
        }
        VideoInjectorEventBus.getInstance().runEvent(new PlayerRegistryEvent.AddPlayerEvent());
    }

    private boolean onInit() {
        deleteOldLog();
        if (!new File(LibraryMapping.libVLC.linuxName).isFile() && !new File(LibraryMapping.libVLC.windowsName).isFile() && !new File(LibraryMapping.libVLC.macName).isFile()) {
            Constants.LOG.info("Unpacking natives...");
            if (!DLLHandler.unpack(this.getClass().getClassLoader())) {
                Constants.LOG.warn("We do not bundle natives for your os. You can try to manually install VLC Player or libVLC for your System. FancyVideo-API only runs with libVLC Versions 4.0.0+");
            }
        }
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "");
        try {
            String path = discoverNativeVLC();
            Constants.LOG.info("Native VLC Found at '{}'", path);
            return true;
        } catch (UnsatisfiedLinkError e1) {
            Constants.LOG.error("Couldn't load vlc binaries, loading in NO_LIBRARY mode...");
            return false;
        }
    }

    private String discoverNativeVLC() {
        String nativeLibraryPath;
        discovery.discover();
        NativeDiscoveryStrategy nativeDiscoveryStrategy = discovery.successfulStrategy();
        nativeLibraryPath = discovery.discoveredPath();
        Constants.LOG.info("Strategy: {}", nativeDiscoveryStrategy);
        Constants.LOG.info("Path: {}", nativeLibraryPath);

        try {
            checkVersion();
        } catch (LinkageError e) {
            Constants.LOG.error("Failed to properly initialise the native library");
            Constants.LOG.error("Stacktrace:", e);
            Constants.NO_LIBRARY_MODE = true;
        }
        return nativeLibraryPath;
    }

    private void checkVersion() throws LinkageError {
        LibVlcVersion version = new LibVlcVersion();
        Constants.LOG.info("Version: " + new Version(libvlc_get_version()));
        if (!version.isSupported()) {
            throw new LinkageError(String.format("Failed to find minimum required VLC version %s, found %s", version.getRequiredVersion(), version.getVersion()));
        }
    }

    private static void deleteOldLog() {
        try {
            Files.delete(new File("logs/vlc.log").toPath());
        } catch (NoSuchFileException ignored) {
            // Ignored
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
