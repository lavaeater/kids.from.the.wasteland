package com.lavaeater.kftw.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.lavaeater.kftw.GameSettings;
import com.lavaeater.kftw.KidsFromTheWastelandGame;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        initialize(new KidsFromTheWastelandGame(new GameSettings(48f, 32f, 8, 12)), configuration);
    }
}