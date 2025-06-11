package io.github.jogo.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;

/**
 * SoundManager centraliza toda a gestão de som e música do jogo.
 * Usa o padrão Singleton, inicializa sons só uma vez e permite fácil controlo global.
 */
public class SoundManager {
    private static SoundManager instance;

    private final Music damageSound;
    private final Music music;
    private final Music lifeMusic;

    private boolean soundEnabled;
    private boolean damageSoundPlaying = false;
    private boolean lifeSoundPlaying = false;

    private final Preferences prefs = Gdx.app.getPreferences("GameSettings");

    private SoundManager() {
        // Carregar sons (aconselhável usar AssetManager em projetos grandes!)
        damageSound = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/damage.wav"));
        music = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/musica.mp3"));
        lifeMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/energy-drink.mp3"));

        soundEnabled = prefs.getBoolean("soundEnabled", true);

        damageSound.setOnCompletionListener(music -> damageSoundPlaying = false);
        lifeMusic.setOnCompletionListener(music -> lifeSoundPlaying = false);

        music.setLooping(true);
        if (soundEnabled) {
            music.setVolume(1f);
            music.play();
        } else {
            music.setVolume(0f);
        }
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        prefs.putBoolean("soundEnabled", enabled);
        prefs.flush();

        music.setVolume(enabled ? 1f : 0f);
        if (enabled && !music.isPlaying()) music.play();
        if (!enabled && music.isPlaying()) music.pause();
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void playDamage() {
        if (soundEnabled && !damageSoundPlaying) {
            damageSound.play();
            damageSoundPlaying = true;
        }
    }

    public void playItem() {
        if (soundEnabled && !lifeSoundPlaying) {
            lifeMusic.play();
            lifeSoundPlaying = true;
        }
    }

    public void playMusic() {
        if (soundEnabled && !music.isPlaying()) music.play();
    }

    public void stopMusic() {
        music.stop();
    }
    public void setMusicVolume(float volume) {
        music.setVolume(volume);
    }
    public void dispose() {
        if (damageSound != null) damageSound.dispose();
        if (music != null) music.dispose();
        if (lifeMusic != null) lifeMusic.dispose();
    }
}
