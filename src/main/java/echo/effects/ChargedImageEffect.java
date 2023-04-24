package echo.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public abstract class ChargedImageEffect extends AbstractGameEffect {

    private boolean firstFrame = true;
    private boolean flipHorizontal;
    private boolean flipVertical;
    private float drawX;
    private float drawY;
    private float height;

    public ChargedImageEffect() {
        this.color = new Color(1, 1, 1, 0);
        this.startingDuration = this.duration = 1.5F;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.color.a = this.duration / this.startingDuration;

        if (this.duration < 0.0F) {
            this.isDone = true;
            this.color.a = 0.0F;
        }

        if (firstFrame) {
            firstFrame = false;
            AbstractDungeon.player.tint.changeColor(AbstractDungeon.player.tint.color, this.startingDuration);
            AbstractDungeon.player.tint.color = new Color(1, 1, 1, 0);
            recordPositions();
            playSfx();
        }
    }

    protected void playSfx() {
    }

    protected void recordPositions() {
        AbstractPlayer p = AbstractDungeon.player;
        this.drawX = p.drawX;
        this.drawY = p.drawY;
        this.flipHorizontal = p.flipHorizontal;
        this.flipVertical = p.flipVertical;
        this.height = p.hb.height;
    }

    protected void drawImage(SpriteBatch sb, Texture img, int srcWidth, int srcHeight, int originX, int originY, float scale) {
        sb.setColor(this.color);
        sb.draw(img,
                flipHorizontal
                        ? drawX - (srcWidth - originX) * Settings.scale * scale
                        : drawX - originX * Settings.scale * scale,
                flipVertical
                        ? drawY + height - (srcHeight - originY) * Settings.scale * scale
                        : drawY - originY * Settings.scale * scale,
                img.getWidth() * Settings.scale * scale,
                img.getHeight() * Settings.scale * scale,
                0,
                0,
                img.getWidth(),
                img.getHeight(),
                flipHorizontal,
                flipVertical
        );
    }

    @Override
    public void dispose() {

    }
}
