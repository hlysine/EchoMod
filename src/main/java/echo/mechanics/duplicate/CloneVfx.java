package echo.mechanics.duplicate;

import basemod.BaseMod;
import basemod.interfaces.PreUpdateSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import echo.EchoMod;
import echo.characters.Echo;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CloneVfx implements PreUpdateSubscriber {
    private static final Logger logger = LogManager.getLogger(CloneVfx.class);
    private static final ShaderProgram gridShader;
    private static final ShaderProgram cardShader;
    public static float cloneVfxTimer = 0.0f;
    public static float visibleRadius = 0.0f;
    public static FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false);
    public static TextureRegion fboRegion = new TextureRegion(fbo.getColorBufferTexture());

    static {
        fboRegion.flip(false, true);

        gridShader = new ShaderProgram(
                Gdx.files.internal(EchoMod.makeShaderPath("grid_texture/grid_texture.vert")).readString(),
                Gdx.files.internal(EchoMod.makeShaderPath("grid_texture/grid_texture.frag")).readString()
        );

        if (gridShader.getLog().length() != 0) {
            logger.log(CloneVfx.gridShader.isCompiled() ? Level.WARN : Level.ERROR, CloneVfx.gridShader.getLog());
        }

        cardShader = new ShaderProgram(
                Gdx.files.internal(EchoMod.makeShaderPath("grid_card/grid_card.vert")).readString(),
                Gdx.files.internal(EchoMod.makeShaderPath("grid_card/grid_card.frag")).readString()
        );

        if (cardShader.getLog().length() != 0) {
            logger.log(CloneVfx.cardShader.isCompiled() ? Level.WARN : Level.ERROR, CloneVfx.cardShader.getLog());
        }

        BaseMod.subscribe(new CloneVfx());
    }

    private static ShaderProgram prevShader;

    public static void playerPreRender(AbstractPlayer player, SpriteBatch sb) {
        if (CloningModule.isCloning()) {
            boolean isBlendingEnabled = sb.isBlendingEnabled();
            int blendSrcFunc = sb.getBlendSrcFunc();
            int blendDstFunc = sb.getBlendDstFunc();
            Color color = sb.getColor();
            ShaderProgram shader = sb.getShader();
            sb.flush();

            sb.end();
            fbo.begin();
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
            sb.begin();
            if (isBlendingEnabled) {
                sb.enableBlending();
                sb.setBlendFunction(blendSrcFunc, blendDstFunc);
            } else {
                sb.disableBlending();
            }
            sb.setColor(color);
            sb.setShader(shader);
        }
    }

    public static void playerPostRender(AbstractPlayer player, SpriteBatch sb) {
        if (CloningModule.isCloning()) {
            sb.flush();

            sb.end();
            fbo.end();
            sb.begin();
            sb.setColor(0.44f, 0.33f, 0.86f, 1f);
            sb.enableBlending();
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            gridShader.begin();
            gridShader.setUniformf("res_x", Gdx.graphics.getWidth());
            gridShader.setUniformf("res_y", Gdx.graphics.getHeight());
            gridShader.setUniformf("time", cloneVfxTimer);
            gridShader.setUniformf("period", 5.0f);
            gridShader.setUniformi("grayscale", 0);
            gridShader.setUniformf("center", player.hb.cX, player.hb.cY);
            gridShader.setUniformf("radius", visibleRadius);
            visibleRadius = Math.min(10000f, visibleRadius + 5f);
            gridShader.end();

            ShaderProgram oldShader = sb.getShader();
            sb.setShader(gridShader);
            sb.draw(fboRegion, -Settings.VERT_LETTERBOX_AMT, -Settings.HORIZ_LETTERBOX_AMT);
            sb.setShader(oldShader);
        }
    }

    public static void relicPreRender(AbstractRelic relic, SpriteBatch sb) {
        if (CloningModule.relicTransformer != null && !CloningModule.relicTransformer.originalRelics.contains(relic)) {
            sb.flush();

            gridShader.begin();
            gridShader.setUniformf("res_x", 128);
            gridShader.setUniformf("res_y", 128);
            gridShader.setUniformf("time", cloneVfxTimer);
            gridShader.setUniformf("period", 5.0f);
            gridShader.setUniformi("grayscale", relic.grayscale ? 1 : 0);
            gridShader.setUniformf("radius", 10000f);
            gridShader.end();

            prevShader = sb.getShader();
            sb.setShader(gridShader);
        }
    }

    public static void relicPostRender(AbstractRelic relic, SpriteBatch sb) {
        if (CloningModule.relicTransformer != null && !CloningModule.relicTransformer.originalRelics.contains(relic)) {
            sb.flush();
            sb.setShader(prevShader);
        }
    }

    public static void cardPreRender(AbstractCard card, SpriteBatch sb) {
        if (card.color == Echo.Enums.CARD_COLOR) {
            sb.flush();

            cardShader.begin();
            cardShader.setUniformf("res_x", 512);
            cardShader.setUniformf("res_y", 512);
            cardShader.setUniformf("time", cloneVfxTimer);
            cardShader.setUniformf("period", 5.0f);
            cardShader.end();

            prevShader = sb.getShader();
            sb.setShader(cardShader);
        }
    }

    public static void cardPostRender(AbstractCard card, SpriteBatch sb) {
        if (card.color == Echo.Enums.CARD_COLOR) {
            sb.flush();
            sb.setShader(prevShader);
        }
    }

    @Override
    public void receivePreUpdate() {
        cloneVfxTimer += Gdx.graphics.getDeltaTime();
    }
}
