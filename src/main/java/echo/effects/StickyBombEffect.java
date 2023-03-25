package echo.effects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import echo.EchoMod;
import echo.util.TextureLoader;

public class StickyBombEffect extends AbstractGameEffect {
    private static final Texture[] BOMB_TEXTURES = new Texture[]{
            TextureLoader.getTexture(EchoMod.makeEffectPath("sticky_bomb0.png")),
            TextureLoader.getTexture(EchoMod.makeEffectPath("sticky_bomb1.png")),
            TextureLoader.getTexture(EchoMod.makeEffectPath("sticky_bomb2.png")),
            TextureLoader.getTexture(EchoMod.makeEffectPath("sticky_bomb3.png")),
            TextureLoader.getTexture(EchoMod.makeEffectPath("sticky_bomb4.png")),
            TextureLoader.getTexture(EchoMod.makeEffectPath("sticky_bomb5.png")),
    };
    private static final float FADE_KEY_FRAME = 0.9f;
    float x;
    float y;
    float rotation;
    Texture img;
    float delay;

    public StickyBombEffect(AbstractCreature target, float delay) {
        float offset = Math.min(target.hb.width, target.hb.height) / 2f;
        this.x = target.hb.cX + MathUtils.random(-offset, offset);
        this.y = target.hb.cY + MathUtils.random(-offset, offset);
        this.delay = delay;

        this.rotation = MathUtils.random(0.0F, 360.0F);
        this.duration = this.startingDuration = 1.0F;
        this.img = BOMB_TEXTURES[MathUtils.random(BOMB_TEXTURES.length - 1)];

        this.scale = 0.5F;
        this.color = new Color(1.0F, 1.0F, 1.0F, 0.0F);
        this.renderBehind = false;
    }

    public void update() {
        if (this.delay > 0) {
            this.delay -= Gdx.graphics.getDeltaTime();
            return;
        }

        this.duration -= Gdx.graphics.getDeltaTime();

        if (this.duration > this.startingDuration * FADE_KEY_FRAME) {
            this.color.a = Interpolation.fade.apply(0.01f, 1f, 1f - (this.duration - this.startingDuration * FADE_KEY_FRAME) / (this.startingDuration * FADE_KEY_FRAME));
        } else {
            this.color.a = Interpolation.fade.apply(1f, 0.01f, 1f - this.duration / (this.startingDuration * (1 - FADE_KEY_FRAME)));
        }

        this.scale = Interpolation.pow3Out.apply(0.5F, 1.0F, this.duration) * Settings.scale;

        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }


    public void render(SpriteBatch sb) {
        if (this.delay > 0) return;
        sb.setColor(new Color(1.0F, 1.0F, 1.0F, this.color.a));
        sb.setBlendFunction(770, 1);
        sb.draw(this.img, this.x - 64, this.y - 64, 64, 64, 128, 128, this.scale, this.scale, this.rotation, 0, 0, 128, 128, false, false);
        sb.setBlendFunction(770, 771);
    }

    public void dispose() {
    }
}
