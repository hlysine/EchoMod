package echo.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import echo.EchoMod;
import echo.powers.UltimateChargePower;

public class OverloadedModule extends AbstractBaseRelic {

    public static final String ID = EchoMod.makeID(OverloadedModule.class.getSimpleName());

    private static final LandingSound LANDING_SOUND = LandingSound.SOLID;

    public OverloadedModule() {
        super(ID, LANDING_SOUND);
    }

    @Override
    public void onEquip() {
        if (AbstractDungeon.player.energy.energyMaster > 0)
            AbstractDungeon.player.energy.energyMaster--;
    }

    @Override
    public void onPlayerEndTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        flash();
        addToBot(new ApplyPowerAction(p, p, new UltimateChargePower(p, 10)));
    }

    @Override
    public void onUnequip() {
        AbstractDungeon.player.energy.energyMaster++;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
