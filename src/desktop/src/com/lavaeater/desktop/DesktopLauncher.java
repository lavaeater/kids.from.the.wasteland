package com.lavaeater.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.lavaeater.TurboRakettiUltra;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 1080;
        config.width = 1920;
        //config.fullscreen = true;
        new LwjglApplication(new TurboRakettiUltra(), config);
    }
}
