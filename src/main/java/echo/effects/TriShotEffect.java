package echo.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import echo.EchoMod;

public class TriShotEffect extends AbstractGameEffect {

    @Override
    public void update() {
        AbstractPlayer p = AbstractDungeon.player;
        AbstractDungeon.effectsQueue.add(new FlyingOrbEffect(p.drawX + 20, p.hb.cY + 50, p.drawX + 1000, p.hb.cY + 20, EchoMod.ECHO_COLOR.cpy()));
        AbstractDungeon.effectsQueue.add(new FlyingOrbEffect(p.drawX + 20, p.hb.cY + 50, p.drawX + 1000, p.hb.cY - 20, EchoMod.ECHO_COLOR.cpy()));
        AbstractDungeon.effectsQueue.add(new FlyingOrbEffect(p.drawX + 20, p.hb.cY + 50, p.drawX + 1000, p.hb.cY - 30, EchoMod.ECHO_COLOR.cpy()));
        this.isDone = true;
    }

    @Override
    public void render(SpriteBatch sb) {

    }

    @Override
    public void dispose() {

    }
}
