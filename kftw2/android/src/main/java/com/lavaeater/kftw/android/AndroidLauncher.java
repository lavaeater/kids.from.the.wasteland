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
        initialize(new KidsFromTheWastelandGame(new GameSettings(144f, 112f, 8, 20, "sv")), configuration);
    }
}