package echo.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import echo.EchoMod;
import echo.powers.FlightPower;

public class OverloadedCore extends AbstractBaseRelic {

    public static final String ID = EchoMod.makeID(OverloadedCore.class.getSimpleName());

    private static final LandingSound LANDING_SOUND = LandingSound.SOLID;
    private static final int MAX_HP_COST = 8;

    public OverloadedCore() {
        super(ID, LANDING_SOUND);
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.decreaseMaxHealth(MAX_HP_COST);
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
        CardCrawlGame.sound.play("BLUNT_FAST");
    }

    @Override
    public void atTurnStartPostDraw() {
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FlightPower(AbstractDungeon.player, 1)));
        flash();
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
