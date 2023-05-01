package echo.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import echo.EchoMod;
import echo.powers.FlightPower;

public class FlightCore extends AbstractBaseRelic {

    public static final String ID = EchoMod.makeID(FlightCore.class.getSimpleName());

    private static final LandingSound LANDING_SOUND = LandingSound.SOLID;
    private static final int FLIGHT_COUNT = 2;

    private boolean usedThisCombat = false;

    public FlightCore() {
        super(ID, LANDING_SOUND);
    }

    public void atPreBattle() {
        usedThisCombat = false;
        this.pulse = true;
        beginPulse();
    }

    @Override
    public void wasHPLost(int damageAmount) {
        if (damageAmount > 0 &&
                (AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT &&
                !usedThisCombat) {
            flash();
            this.pulse = false;
            addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FlightPower(AbstractDungeon.player, FLIGHT_COUNT)));
            addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            usedThisCombat = true;
            this.grayscale = true;
        }
    }

    @Override
    public void justEnteredRoom(AbstractRoom room) {
        this.grayscale = false;
    }

    @Override
    public void onVictory() {
        this.pulse = false;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
