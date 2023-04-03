package echo.cards.attacks;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.FlightPower;
import echo.util.RunnableAction;

public class HitAndRun extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(HitAndRun.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public HitAndRun() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
        addToBot(new RunnableAction(() -> {
            if (m != null && m.getIntentBaseDmg() >= 0) {
                addToBot(new ApplyPowerAction(p, p, new FlightPower(p, magicNumber)));
            } else {
                AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, cardStrings.EXTENDED_DESCRIPTION[0], true));
            }
        }));
    }
}
