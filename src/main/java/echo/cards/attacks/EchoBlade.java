package echo.cards.attacks;

import com.evacipated.cardcrawl.mod.stslib.actions.common.DamageCallbackAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;

public class EchoBlade extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(EchoBlade.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public EchoBlade() {
        super(ID, TARGET);

        this.isEthereal = true;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageCallbackAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY, damageTaken -> {
            if (m.isDying || m.currentHealth <= 0) {
                addToBot(new MakeTempCardInHandAction(this.makeStatEquivalentCopy()));
            }
        }));
    }
}
