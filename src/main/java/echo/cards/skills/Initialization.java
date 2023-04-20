package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.UltimateChargePower;

public class Initialization extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(Initialization.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public Initialization() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new UltimateChargePower(p, magicNumber)));
    }
}
