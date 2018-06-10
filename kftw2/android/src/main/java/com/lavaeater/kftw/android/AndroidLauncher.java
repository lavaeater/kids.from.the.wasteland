package com.lavaeater.kftw.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.lavaeater.kftw.KidsFromTheWastelandGame;

import data.GameSettings;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        initialize(new KidsFromTheWastelandGame(new GameSettings(128f, 72f, 8, 16, "sv")), configuration);
    }
}