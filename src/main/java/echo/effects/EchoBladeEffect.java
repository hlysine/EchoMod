package echo.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import echo.EchoMod;
import echo.util.TextureLoader;

public class EchoBladeEffect extends ChargedImageEffect {
    private static final Texture IMG = TextureLoader.getTexture(EchoMod.makeEffectPath("echo_blade.png"));

    @Override
    public void render(SpriteBatch sb) {
        drawImage(sb, IMG, IMG.getWidth(), IMG.getHeight(), 543, 273, 0.7f);
    }

    @Override
    protected void playSfx() {
        SfxStore.GENJI_ULTIMATE_FIRE.play(0.1f);
    }
}
