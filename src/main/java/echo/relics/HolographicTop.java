package echo.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.UnceasingTop;
import echo.EchoMod;
import echo.subscribers.RelicTransformSubscriber;

import java.util.Collections;
import java.util.List;

public class HolographicTop extends AbstractBaseRelic implements RelicTransformSubscriber {

    public static final String ID = EchoMod.makeID(HolographicTop.class.getSimpleName());

    private static final LandingSound LANDING_SOUND = LandingSound.MAGICAL;

    public HolographicTop() {
        super(ID, LANDING_SOUND);
    }

    @Override
    public List<AbstractRelic> transform() {
        return Collections.singletonList(new UnceasingTop());
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
