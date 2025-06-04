package io.github.jogo.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;

/**
 * Classe responsável por renderizar uma versão miniatura do jogo (PIP - Picture in Picture).
 */
public class PIPRenderer implements Disposable {

    private final FrameBuffer pipBuffer;
    private Texture pipTexture;
    private final SpriteBatch pipBatch;

    private final int pipWidth;
    private final int pipHeight;
    private final int screenMarginX;
    private final int screenMarginY;

    public PIPRenderer(int pipWidth, int pipHeight, int marginX, int marginY) {
        this.pipWidth = pipWidth;
        this.pipHeight = pipHeight;
        this.screenMarginX = marginX;
        this.screenMarginY = marginY;

        this.pipBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, pipWidth, pipHeight, false);
        this.pipBatch = new SpriteBatch();
    }

    public void capture(Runnable renderFunction) {
        pipBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderFunction.run(); // executa o método fornecido

        pipBuffer.end();
        pipTexture = pipBuffer.getColorBufferTexture();
    }

    /**
     * Desenha a versão miniatura no ecrã, no canto inferior direito.
     */
    public void drawPIP() {
        if (pipTexture == null) return;

        pipBatch.begin();
        // Desenha a textura invertida verticalmente (necessário para FrameBuffer)
        pipBatch.draw(
            pipTexture,
            Gdx.graphics.getWidth() - pipWidth - screenMarginX,
            screenMarginY,
            pipWidth,
            pipHeight,
            0, 0, 1, 1
        );
        pipBatch.end();
    }

    @Override
    public void dispose() {
        pipBuffer.dispose();
        pipBatch.dispose();
        if (pipTexture != null) pipTexture.dispose();
    }
    // ===== Builder Pattern =====

    public static class Builder {
        private int pipWidth = 320;
        private int pipHeight = 180;
        private int marginX = 20;
        private int marginY = 20;

        public Builder setSize(int width, int height) {
            this.pipWidth = width;
            this.pipHeight = height;
            return this;
        }

        public Builder setMargin(int x, int y) {
            this.marginX = x;
            this.marginY = y;
            return this;
        }

        public PIPRenderer build() {
            return new PIPRenderer(pipWidth, pipHeight, marginX, marginY);
        }
    }
}
