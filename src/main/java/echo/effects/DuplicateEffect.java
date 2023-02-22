package echo.effects;

import basemod.helpers.ScreenPostProcessorManager;
import basemod.interfaces.ScreenPostProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import echo.EchoMod;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DuplicateEffect extends AbstractGameEffect {
    public static final float DURATION = 1.25f;
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

    private final PostProcessor postProcessor;

    public DuplicateEffect() {
        this.duration = this.startingDuration = DURATION;
        this.postProcessor = new PostProcessor();
        AbstractDungeon.topLevelEffects.add(new BorderLongFlashEffect(EchoMod.ECHO_COLOR.cpy()));
    }

    @Override
    public void update() {
        if (this.duration == this.startingDuration) {
            ScreenPostProcessorManager.addPostProcessor(postProcessor);
        }
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
            ScreenPostProcessorManager.removePostProcessor(postProcessor);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
    }

    @Override
    public void dispose() {
    }

    public class PostProcessor implements ScreenPostProcessor {
        public PostProcessor() {
            gridShader.begin();
            gridShader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            gridShader.end();
        }

        @Override
        public void postProcess(SpriteBatch sb, TextureRegion frameTexture, OrthographicCamera camera) {
            ShaderProgram oldShader = sb.getShader();

            gridShader.begin();
            gridShader.setUniformf("time", duration);
            gridShader.setUniformf("alpha", 0.5f);
            gridShader.end();

            sb.draw(frameTexture, 0, 0);
            sb.enableBlending();
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            sb.setShader(gridShader);
            sb.draw(frameTexture, 0, 0);
            sb.setShader(oldShader);
        }
    }
}
