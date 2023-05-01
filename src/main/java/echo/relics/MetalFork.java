package echo.relics;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.subscribers.MonsterLostHPSubscriber;

public class MetalFork extends AbstractBaseRelic implements MonsterLostHPSubscriber {

    public static final String ID = EchoMod.makeID(MetalFork.class.getSimpleName());

    private static final LandingSound LANDING_SOUND = LandingSound.CLINK;

    public MetalFork() {
        super(ID, LANDING_SOUND);
    }

    @Override
    public void wasMonsterHPLost(AbstractMonster monster, DamageInfo info, int amount) {
        if (monster.currentHealth < monster.maxHealth / 2 && monster.currentHealth + amount >= monster.maxHealth / 2) {
            addToBot(new GainEnergyAction(1));
            flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
