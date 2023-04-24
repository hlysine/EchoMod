package echo.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import echo.EchoMod;
import echo.util.TextureLoader;

public class EchoOverclockEffect extends ChargedImageEffect {
    private static final Texture IMG = TextureLoader.getTexture(EchoMod.makeEffectPath("echo_overclock.png"));

    @Override
    public void render(SpriteBatch sb) {
        drawImage(sb, IMG, IMG.getWidth(), IMG.getHeight(), 293, 402, 1.2f);
    }
}
