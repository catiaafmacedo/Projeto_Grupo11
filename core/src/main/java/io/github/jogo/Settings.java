package io.github.jogo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Settings {
    private static final Preferences prefs = Gdx.app.getPreferences("GameSettings");

    public static boolean isSoundEnabled() {
        return prefs.getBoolean("soundEnabled", true);
    }

    public static void setSoundEnabled(boolean enabled) {
        prefs.putBoolean("soundEnabled", enabled);
        prefs.flush();
    }

    public static float getSpeed() {
        return prefs.getFloat("gameSpeed", 1.0f);
    }

    public static void setSpeed(float speed) {
        prefs.putFloat("gameSpeed", speed);
        prefs.flush();
    }
}
