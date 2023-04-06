package echo.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class NanoBoostAuraEffect extends AbstractGameEffect {
    public static boolean switcher = true;
    private final TextureAtlas.AtlasRegion img = ImageMaster.EXHAUST_L;
    private final float ALPHA = 0.1f;

    private final AbstractCreature creature;
    private float dx;
    private float dy;
    private final float rotationSpeed;
    private final float vY;

    public NanoBoostAuraEffect(AbstractCreature creature) {
        this.creature = creature;
        this.changeColor();

        this.dx = MathUtils.random(-creature.hb.width / 3f, creature.hb.width / 3f);
        this.dy = MathUtils.random(-creature.hb.height / 3f, creature.hb.height / 3f);

        this.dx -= this.img.packedWidth / 2.0f;
        this.dy -= this.img.packedHeight / 2.0f;

        this.scale *= 1.5;

        this.duration = 2f;

        switcher = !switcher;

        this.rotation = MathUtils.random(360.0f);
        this.renderBehind = switcher;
        this.rotationSpeed = MathUtils.random(20.0f, 40.0f);
        this.vY = MathUtils.random(20.0f, 40.0f);
    }

    public void update() {
        if (this.duration > 1.0F) {
            this.color.a = Interpolation.fade.apply(ALPHA, 0.0f, this.duration - 1.0f);
        } else {
            this.color.a = Interpolation.fade.apply(0.0f, ALPHA, this.duration);
        }
        this.rotation += Gdx.graphics.getDeltaTime() * this.rotationSpeed;
        this.dy += Gdx.graphics.getDeltaTime() * this.vY;
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.setBlendFunction(770, 1);
        sb.draw(
                this.img,
                creature.hb.cX + this.dx, creature.hb.cY + this.dy,
                this.img.packedWidth / 2.0f, this.img.packedHeight / 2.0f,
                this.img.packedWidth, this.img.packedHeight,
                this.scale, this.scale,
                this.rotation
        );
        sb.setBlendFunction(770, 771);
    }

    public void dispose() {
    }

    private void changeColor() {
        if (MathUtils.randomBoolean()) {
            this.color = new Color(MathUtils.random(0.6f, 0.7f), MathUtils.random(0.2f, 0.3f), MathUtils.random(0.9f, 1f), this.color == null ? 0.0f : this.color.a);
        } else {
            this.color = new Color(MathUtils.random(0.5f, 0.55f), MathUtils.random(0.5f, 0.6f), MathUtils.random(0.7f, 0.8f), this.color == null ? 0.0f : this.color.a);
        }
    }
}

