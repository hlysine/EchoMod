package echo.relics;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.mechanics.focused.FocusedChecker;
import echo.subscribers.FocusedSubscriber;

public class CrystalBall extends AbstractBaseRelic implements FocusedSubscriber {

    public static final String ID = EchoMod.makeID(CrystalBall.class.getSimpleName());

    public static final float FOCUSED_PERCENTAGE = 0.7f;

    private static final LandingSound LANDING_SOUND = LandingSound.SOLID;

    public CrystalBall() {
        super(ID, LANDING_SOUND);
    }

    @Override
    public boolean overrideFocusedCheck(AbstractMonster target) {
        boolean originalFocused = FocusedChecker.baseFocused(target);
        boolean newFocused = FocusedChecker.baseFocused(target, FOCUSED_PERCENTAGE);

        if (newFocused && !originalFocused) {
            beginLongPulse();
        } else {
            stopPulse();
        }

        return newFocused;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
