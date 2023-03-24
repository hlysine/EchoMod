package echo.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;

public class FlyingOrbEffect extends AbstractGameEffect {
    private final TextureAtlas.AtlasRegion img;
    private final CatmullRomSpline<Vector2> crs = new CatmullRomSpline<>();
    private final ArrayList<Vector2> controlPoints = new ArrayList<>();
    private static final int TRAIL_ACCURACY = 100;
    private final Vector2[] points = new Vector2[TRAIL_ACCURACY];

    private final Vector2 pos;
    private final Vector2 target;
    private static final float VELOCITY = 4000f * Settings.scale;
    private static final float DST_THRESHOLD = 5f * Settings.scale;
    private static final float SCALE = 0.5f;

    private final float rotation;

    public FlyingOrbEffect(float fromX, float fromY, float toX, float toY, Color color) {
        this.img = ImageMaster.GLOW_SPARK_2;
        this.pos = new Vector2(fromX, fromY);
        this.target = new Vector2(toX, toY);
        this.crs.controlPoints = new Vector2[1];
        Vector2 tmp = new Vector2(this.pos.x - this.target.x, this.pos.y - this.target.y);
        tmp.nor();
        this.rotation = tmp.angle();
        this.controlPoints.clear();
        this.color = color;
        this.duration = 1.3F;
    }

    public void update() {
        updateMovement();
    }

    private void updateMovement() {
        Vector2 tmp = new Vector2(this.pos.x - this.target.x, this.pos.y - this.target.y);
        tmp.nor();
        float targetAngle = tmp.angle();

        tmp.setAngle(this.rotation);

        tmp.x *= Gdx.graphics.getDeltaTime() * VELOCITY;
        tmp.y *= Gdx.graphics.getDeltaTime() * VELOCITY;
        this.pos.sub(tmp);

        if ((this.target.x < Settings.WIDTH / 2.0F && this.pos.x < 0.0F) ||
                (this.target.x > Settings.WIDTH / 2.0F && this.pos.x > Settings.WIDTH) ||
                this.target.dst(this.pos) < DST_THRESHOLD ||
                this.target.dst(this.pos) < VELOCITY * Gdx.graphics.getDeltaTime()) {
            this.pos.x = this.target.x;
            this.pos.y = this.target.y;
            this.isDone = true;
            return;
        }
        if (!this.controlPoints.isEmpty()) {
            if (!this.controlPoints.get(0).equals(this.pos)) {
                this.controlPoints.add(this.pos.cpy());
            }
        } else {
            this.controlPoints.add(this.pos.cpy());
        }

        if (this.controlPoints.size() > 3) {
            Vector2[] vec2Array = new Vector2[0];
            this.crs.set(this.controlPoints.toArray(vec2Array), false);
            for (int i = 0; i < TRAIL_ACCURACY; i++) {
                this.points[i] = new Vector2();
                this.crs.valueAt(this.points[i], i / (TRAIL_ACCURACY - 1f));
            }
        }

        if (this.controlPoints.size() > 10) {
            this.controlPoints.remove(0);
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        if (!this.isDone) {
            sb.setBlendFunction(770, 1);
            sb.setColor(this.color);
            float scale = Settings.scale * SCALE;
            for (int i = this.points.length - 1; i > 0; i--) {
                if (this.points[i] != null) {
                    sb.draw(
                            this.img,
                            this.points[i].x - (this.img.packedWidth / 2f),
                            this.points[i].y - (this.img.packedHeight / 2f),
                            this.img.packedWidth / 2.0F,
                            this.img.packedHeight / 2.0F,
                            this.img.packedWidth,
                            this.img.packedHeight,
                            scale,
                            scale,
                            this.rotation
                    );
                    scale *= 0.975F;
                }
            }
            sb.setBlendFunction(770, 771);
        }
    }

    public void dispose() {
    }
}
