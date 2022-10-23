package ngoedix.videoinjector;

import ngoedix.videoinjector.api.MediaPlayerHandler;

public class ShutdownHook extends Thread {

    private final MediaPlayerHandler instance = MediaPlayerHandler.getInstance();

    @Override
    public void run() {
        Constants.LOG.info("Running VideoInjector shutdown hook");
        instance.shutdown();
        Constants.LOG.info("Shutdown hook finished");
    }
}
