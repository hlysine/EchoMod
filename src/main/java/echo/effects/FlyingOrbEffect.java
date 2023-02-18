package echo.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;

public class FlyingOrbEffect extends AbstractGameEffect {
    private final TextureAtlas.AtlasRegion img;
    private final CatmullRomSpline<Vector2> crs = new CatmullRomSpline<>();
    private final ArrayList<Vector2> controlPoints = new ArrayList<>();
    private static final int TRAIL_ACCURACY = 60;
    private final Vector2[] points = new Vector2[TRAIL_ACCURACY];

    private final Vector2 pos;
    private final Vector2 target;
    private float currentSpeed;
    private static final float START_VELOCITY = 300f * Settings.scale;
    private static final float MAX_VELOCITY = 3000f * Settings.scale;
    private static final float VELOCITY_RAMP_RATE = 1000f * Settings.scale;
    private static final float DST_THRESHOLD = 10f * Settings.scale;
    private static final float HOME_IN_THRESHOLD = 10f * Settings.scale;
    private static final float SCALE = 1.5f;

    private float rotation;

    private final boolean rotateClockwise;
    private boolean stopRotating = false;
    private float rotationRate;

    public FlyingOrbEffect(float fromX, float fromY, float toX, float toY, Color color) {
        this.img = ImageMaster.GLOW_SPARK_2;
        this.pos = new Vector2(fromX, fromY);
        this.target = new Vector2(toX, toY);
        this.crs.controlPoints = new Vector2[1];
        this.rotateClockwise = MathUtils.randomBoolean();
        this.rotation = MathUtils.random(0, 359);
        this.controlPoints.clear();
        this.rotationRate = MathUtils.random(300.0F, 350.0F) * Settings.scale;
        this.currentSpeed = START_VELOCITY * MathUtils.random(0.2F, 1.0F);
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
        this.rotationRate += Gdx.graphics.getDeltaTime() * 700.0F;

        if (!this.stopRotating) {
            if (this.rotateClockwise) {
                this.rotation += Gdx.graphics.getDeltaTime() * this.rotationRate;
            } else {
                this.rotation -= Gdx.graphics.getDeltaTime() * this.rotationRate;
                if (this.rotation < 0.0F) {
                    this.rotation += 360.0F;
                }
            }

            this.rotation %= 360.0F;

            if (!this.stopRotating) {
                if (this.target.dst(this.pos) < HOME_IN_THRESHOLD) {
                    this.rotation = targetAngle;
                    this.stopRotating = true;
                } else if (Math.abs(this.rotation - targetAngle) < Gdx.graphics.getDeltaTime() * this.rotationRate) {
                    this.rotation = targetAngle;
                    this.stopRotating = true;
                }
            }
        }

        tmp.setAngle(this.rotation);

        tmp.x *= Gdx.graphics.getDeltaTime() * this.currentSpeed;
        tmp.y *= Gdx.graphics.getDeltaTime() * this.currentSpeed;
        this.pos.sub(tmp);

        if (this.stopRotating) {
            this.currentSpeed += Gdx.graphics.getDeltaTime() * VELOCITY_RAMP_RATE * 3.0F;
        } else {
            this.currentSpeed += Gdx.graphics.getDeltaTime() * VELOCITY_RAMP_RATE * 1.5F;
        }
        if (this.currentSpeed > MAX_VELOCITY) {
            this.currentSpeed = MAX_VELOCITY;
        }

        if ((this.target.x < Settings.WIDTH / 2.0F && this.pos.x < 0.0F) ||
                (this.target.x > Settings.WIDTH / 2.0F && this.pos.x > Settings.WIDTH) ||
                this.target.dst(this.pos) < DST_THRESHOLD ||
                this.target.dst(this.pos) < this.currentSpeed * Gdx.graphics.getDeltaTime()) {
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
            for (int i = 0; i < 60; i++) {
                this.points[i] = new Vector2();
                this.crs.valueAt(this.points[i], i / 59.0F);
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
