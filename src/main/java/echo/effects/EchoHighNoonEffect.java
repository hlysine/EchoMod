package echo.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import echo.EchoMod;
import echo.util.TextureLoader;

public class EchoHighNoonEffect extends ChargedImageEffect {
    private static final Texture IMG = TextureLoader.getTexture(EchoMod.makeEffectPath("echo_high_noon.png"));

    @Override
    public void render(SpriteBatch sb) {
        drawImage(sb, IMG, IMG.getWidth(), IMG.getHeight(), 518, 303, 0.6f);
    }
}
