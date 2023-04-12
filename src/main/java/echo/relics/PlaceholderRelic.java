package echo.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import echo.EchoMod;
import echo.powers.UltimateChargePower;

public class PlaceholderRelic extends AbstractBaseRelic {

    public static final String ID = EchoMod.makeID(PlaceholderRelic.class.getSimpleName());

    private static final LandingSound LANDING_SOUND = LandingSound.MAGICAL;

    public PlaceholderRelic() {
        super(ID, LANDING_SOUND);
    }

    @Override
    public void onPlayerEndTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        flash();
        addToBot(new ApplyPowerAction(p, p, new UltimateChargePower(p, 2)));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
