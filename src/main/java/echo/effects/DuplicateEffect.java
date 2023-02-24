package echo.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import echo.EchoMod;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DuplicateEffect extends AbstractGameEffect {
    public static final float DURATION = 1f;
    private static final Logger logger = LogManager.getLogger(DuplicateEffect.class);
    private static final ShaderProgram gridShader;

    static {
        gridShader = new ShaderProgram(
                Gdx.files.internal(EchoMod.makeShaderPath("grid_screen/grid_screen.vert")).readString(),
                Gdx.files.internal(EchoMod.makeShaderPath("grid_screen/grid_screen.frag")).readString()
        );

        if (gridShader.getLog().length() != 0) {
            logger.log(gridShader.isCompiled() ? Level.WARN : Level.ERROR, gridShader.getLog());
        }
    }

    private final FrameBuffer frameBuffer;
    private Runnable action;

    public DuplicateEffect(Runnable action) {
        this.action = action;
        this.duration = this.startingDuration = DURATION;
        this.frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false);
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < startingDuration / 2f && this.action != null) {
            this.action.run();
            this.action = null;
        }
        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.end();
        this.frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
        this.frameBuffer.end();
        sb.begin();

        gridShader.begin();
        float progress = 1 - Math.abs(duration - startingDuration / 2f) / (startingDuration / 2f);
        gridShader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (duration > startingDuration / 2f) {
            gridShader.setUniformf("foreground_time", Interpolation.pow2Out.apply(progress) * startingDuration / 2);
        } else {
            gridShader.setUniformf("foreground_time", Interpolation.pow5.apply(progress) * startingDuration / 2);
        }
        if (duration > startingDuration * 0.9f) {
            gridShader.setUniformf("alpha", Interpolation.sine.apply(1 - (duration - startingDuration * 0.9f) / (startingDuration * 0.1f)));
        } else if (duration < startingDuration * 0.1f) {
            gridShader.setUniformf("alpha", Interpolation.sine.apply(duration / startingDuration / 0.1f));
        } else {
            gridShader.setUniformf("alpha", 1);
        }
        if (duration > startingDuration * 0.8f) {
            gridShader.setUniformf("background_time", Interpolation.pow3Out.apply(1 - (duration - startingDuration * 0.8f) / (startingDuration * 0.2f)) * 0.8f);
        } else {
            gridShader.setUniformf("background_time", 0.8f + Interpolation.pow3.apply(1 - duration / startingDuration / 0.8f) * 0.2f);
        }
        gridShader.setUniformf("duration", startingDuration);
        gridShader.setUniformf("speed", 1f);
        if (duration > startingDuration / 2f) {
            gridShader.setUniformf("grid_radius", 200.0f);
            gridShader.setUniformf("grid_border", 50.0f);
            gridShader.setUniformf("grid_margin", 50.0f);
        } else {
            gridShader.setUniformf("grid_radius", (float) (10f * Math.pow(20f, progress)));
            gridShader.setUniformf("grid_border", (float) (2.5f * Math.pow(20f, progress)));
            gridShader.setUniformf("grid_margin", (float) (2.5f * Math.pow(20f, progress)));
        }
        gridShader.end();

        ShaderProgram oldShader = sb.getShader();
        sb.setShader(gridShader);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sb.draw(this.frameBuffer.getColorBufferTexture(), 0, 0);
        sb.setShader(oldShader);
    }

    @Override
    public void dispose() {
    }
}
