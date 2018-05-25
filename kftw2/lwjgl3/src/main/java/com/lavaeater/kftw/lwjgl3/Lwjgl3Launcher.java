package com.lavaeater.kftw.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.lavaeater.kftw.GameSettings;
import com.lavaeater.kftw.KidsFromTheWastelandGame;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        //return new Lwjgl3Application(new KidsFromTheWastelandGame(), getDefaultConfiguration());
        return new Lwjgl3Application(new KidsFromTheWastelandGame(new GameSettings(96f, 72f, 8, 16)), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("KidsFromTheWasteland");
        configuration.setWindowedMode(2560, 1440);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}