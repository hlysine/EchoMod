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

public class PostDuplicateEffect extends AbstractGameEffect {
    public static final float DURATION = 0.4f;
    private static final Logger logger = LogManager.getLogger(PostDuplicateEffect.class);
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

    public PostDuplicateEffect() {
        this.duration = this.startingDuration = DURATION;
        this.frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false);
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
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
        gridShader.setUniformf("foreground_time", 0);
        gridShader.setUniformf("alpha", Interpolation.pow3Out.apply(progress) * 0.65f);
        gridShader.setUniformf("background_time", 1f);
        gridShader.setUniformf("duration", startingDuration);
        gridShader.setUniformf("speed", 1f);
        gridShader.setUniformf("grid_radius", 30.0f);
        gridShader.setUniformf("grid_border", 35.0f);
        gridShader.setUniformf("grid_margin", 5.0f);
        gridShader.setUniformf("background_inner_color", 0.24f, 1f, 1f, 1f);
        gridShader.setUniformf("background_outer_color", 0.24f, 1f, 1f, 1f);
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
