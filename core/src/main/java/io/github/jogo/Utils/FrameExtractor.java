package io.github.jogo.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class FrameExtractor {
    private static FrameExtractor instance = null;

    private final Texture[] frames;
    private final int frameCount;

    // Construtor privado
    private FrameExtractor(String path, int totalFrames, int frameWidth, int frameHeight) {
        Pixmap fullPixmap = new Pixmap(Gdx.files.internal(path));
        frames = new Texture[totalFrames];

        for (int i = 0; i < totalFrames; i++) {
            int x = i * frameWidth;
            Pixmap framePixmap = new Pixmap(frameWidth, frameHeight, fullPixmap.getFormat());
            framePixmap.drawPixmap(fullPixmap, x, 0, frameWidth, frameHeight, 0, 0, frameWidth, frameHeight);
            frames[i] = new Texture(framePixmap);
            framePixmap.dispose();
        }

        fullPixmap.dispose();
        frameCount = totalFrames;
    }

    // Método de acesso Singleton
    public static FrameExtractor getInstance(String path, int totalFrames, int frameWidth, int frameHeight) {
        if (instance == null) {
            instance = new FrameExtractor(path, totalFrames, frameWidth, frameHeight);
        }
        return instance;
    }

    // Opcional: método para limpar e permitir recarregar (reset)
    public static void reset() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }

    public Texture getFrame(int index) {
        if (index < 0 || index >= frames.length) return null;
        return frames[index];
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void dispose() {
        for (Texture frame : frames) {
            if (frame != null) frame.dispose();
        }
    }
}
