package echo.relics;

import echo.EchoMod;

public class PlaceholderRelic extends AbstractBaseRelic {

    public static final String ID = EchoMod.makeID(PlaceholderRelic.class.getSimpleName());

    private static final LandingSound LANDING_SOUND = LandingSound.MAGICAL;

    public PlaceholderRelic() {
        super(ID, LANDING_SOUND);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
