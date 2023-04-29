package echo.cards.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.SelfLearningPower;

public class SelfLearningAI extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(SelfLearningAI.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public SelfLearningAI() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new SelfLearningPower(p)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.isInnate = true;
        }
        super.upgrade();
    }
}
