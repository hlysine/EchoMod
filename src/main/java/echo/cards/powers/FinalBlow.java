package echo.cards.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.FinalBlowPower;

public class FinalBlow extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(FinalBlow.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public FinalBlow() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new FinalBlowPower(p, magicNumber)));
    }
}
