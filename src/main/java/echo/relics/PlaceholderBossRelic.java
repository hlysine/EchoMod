package echo.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import echo.EchoMod;
import echo.subscribers.ChargedSubscriber;

public class PlaceholderBossRelic extends AbstractBaseRelic implements ChargedSubscriber {

    public static final String ID = EchoMod.makeID(PlaceholderBossRelic.class.getSimpleName());

    private static final LandingSound LANDING_SOUND = LandingSound.MAGICAL;

    public PlaceholderBossRelic() {
        super(ID, LANDING_SOUND);
    }

    @Override
    public void onEquip() {
        if (AbstractDungeon.player.energy.energyMaster > 0)
            AbstractDungeon.player.energy.energyMaster--;
    }

    @Override
    public void onUnequip() {
        AbstractDungeon.player.energy.energyMaster++;
    }

    @Override
    public boolean overrideChargedCheck() {
        return true;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
